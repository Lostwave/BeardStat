package me.tehbeard.BeardStat.DataProviders;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Properties;

import org.bukkit.Bukkit;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.TopPlayer;

/**
 * Provides backend storage to a mysql database
 * @author James
 *
 */
public class MysqlStatDataProvider implements IStatDataProvider {

	protected Connection conn;
    private static final String PLAYEDFORPROCNAME = "topplayed";

	private String host;
	private String database;
	private String username;
	private String password;

	//protected static PreparedStatement prepGetPlayerStat;
	protected static PreparedStatement prepGetAllPlayerStat;
	protected static PreparedStatement prepSetPlayerStat;
	protected static PreparedStatement keepAlive;
    protected static PreparedStatement prepTopPlayersStat;
    protected static PreparedStatement prepAllStatsInCategory;

	private static HashMap<String,PlayerStatBlob> writeCache = new HashMap<String,PlayerStatBlob>();

	public MysqlStatDataProvider(String host,String database,String username,String password) throws SQLException{

		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			BeardStat.printCon("MySQL Library not found!");
		}
		createConnection();
		checkAndMakeTable();
		prepareStatements();
	}

	protected void createConnection(){
		String conUrl = String.format("jdbc:mysql://%s/%s",
				host, 
				database);

		BeardStat.printCon("Configuring....");
		Properties conStr = new Properties();
		conStr.put("user",username);
		conStr.put("password",password);
		BeardStat.printCon("Connecting....");
		try {
			conn = DriverManager.getConnection(conUrl,conStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void checkAndMakeTable(){
		BeardStat.printCon("Checking for table");
		try{
			ResultSet rs = conn.getMetaData().getTables(null, null, "stats", null);
			if (!rs.next()) {
				BeardStat.printCon("Stats table not found, creating table");
				PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `stats` ("+
						" `player` varchar(32) NOT NULL DEFAULT '-',"+
						" `category` varchar(32) NOT NULL DEFAULT 'stats',"+
						" `stat` varchar(32) NOT NULL DEFAULT '-',"+
						" `value` int(11) NOT NULL DEFAULT '0',"+
						" PRIMARY KEY (`player`,`category`,`stat`)"+
						") ENGINE=InnoDB DEFAULT CHARSET=latin1;");

				ps.executeUpdate();
				ps.close();
				BeardStat.printCon("created table");
			}
			else
			{
				BeardStat.printCon("Table found");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	      BeardStat.printCon("Checking for " + PLAYEDFORPROCNAME + " routine");
	        try{
	            ResultSet rs = conn.getMetaData().getProcedures(null, "stats", PLAYEDFORPROCNAME);
	            if (!rs.next()) {
	                BeardStat.printCon("topPlayers routine not found, creating routine");
	                CreateTopPlayersRoutine();
	                BeardStat.printCon("created routine");
	            }
	            else
	            {
	                BeardStat.printCon("Routine found");
	                if(BeardStat.isVersionUpdated){
	                    BeardStat.printCon("Version Updated, Altering Routine");
	                    CreateTopPlayersRoutine();
	                }
	            }
	            rs.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	}

   protected void CreateTopPlayersRoutine(){
	    try{
    	    PreparedStatement ps = conn.prepareStatement("DROP PROCEDURE IF EXISTS `" + PLAYEDFORPROCNAME + "';CREATE PROCEDURE `" + PLAYEDFORPROCNAME + "`(maxrows int, stat varchar(20)) " +
                    "BEGIN " +
                    "SET @currow = 0; " +
                    "SELECT @currow := @currow + 1 as Rank " +
                    "    , p.player as Player " +
                    "    , CASE WHEN stat='playedfor' THEN Concat(LPAD(floor(p.value / 60 / 60 / 24), 2, '0'), 'd ', LPAD(floor(p.value / 60 / 60 % 24), 2, '0'), 'h ', LPAD(floor(p.value / 60 % 24 % 60), 2, '0'), 'm ', LPAD(floor(p.value / 60 % 24 % 60 * 60 % 60), 2, '0'), 's') " + 
                    "       WHEN stat='firstlogin' THEN FROM_UNIXTIME(value) " + 
                    "       WHEN stat='lastlogin' THEN FROM_UNIXTIME(value) " + 
                    "       ELSE FORMAT(value, 0) " + 
                    "      END as TimeOnServer " +
                    "    , (SELECT FROM_UNIXTIME(value) FROM stats ll WHERE ll.player = p.player AND ll.stat='firstlogin') as FirstLogin " +
                    "    , (SELECT FROM_UNIXTIME(value) FROM stats ll WHERE ll.player = p.player AND ll.stat='lastlogin') as LastLogin " +
                    "FROM stats p " +
                    "WHERE p.stat=stat " +
                    "ORDER BY p.value DESC " +
                    "LIMIT 0,maxrows; " +
                    "END");

            ps.executeBatch();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    
	}
	

	protected void prepareStatements(){
		try{
			BeardStat.printDebugCon("Preparing statements");

			keepAlive = conn.prepareStatement("SELECT COUNT(*) from `stats`");
			prepGetAllPlayerStat = conn.prepareStatement("SELECT * FROM stats WHERE player=?");
			BeardStat.printDebugCon("Player stat statement created");
			prepSetPlayerStat = conn.prepareStatement("INSERT INTO `stats`" +
					"(`player`,`category`,`stat`,`value`) " +
					"values (?,?,?,?) ON DUPLICATE KEY UPDATE `value`=?;",Statement.RETURN_GENERATED_KEYS);
			prepTopPlayersStat = conn.prepareStatement("Call " + PLAYEDFORPROCNAME + "(?, ?);");
			prepAllStatsInCategory = conn.prepareStatement("SELECT DISTINCT stat FROM stats WHERE category=?");
			BeardStat.printDebugCon("Set player stat statement created");
			BeardStat.printCon("Initaised MySQL Data Provider.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public PlayerStatBlob pullPlayerStatBlob(String player) {
		return pullPlayerStatBlob(player,true);
	}


	public void pushPlayerStatBlob(PlayerStatBlob player) {

		//create a copy of the player stat blob to write to the 
		synchronized(writeCache){
			PlayerStatBlob copy = null;
			//grab it if it's still in the cache
			if(writeCache.containsKey(player.getName())){
				copy = writeCache.get(player.getName());
			} else {
				copy = new PlayerStatBlob(player.getName(),player.getPlayerID());
			}
			//copy playerstats that need changing
			for(PlayerStat ps:player.getStats()){
				//update or create
				if(copy.hasStat(ps.getCat(),ps.getName())){
					copy.getStat(ps.getCat(),ps.getName()).setValue(ps.getValue());
				}else{
					if(ps.isArchive()){
						BeardStat.printDebugCon("Caching stat " + ps.getName() + " as new");
						PlayerStat nps = new PlayerStat(ps.getCat(),ps.getName(),ps.getValue());

						copy.addStat(nps);
						ps.clearArchive();
					}
				}

			}
			//push to cache if it doesn't already exist there
			if(!writeCache.containsKey(player.getName())){
				writeCache.put(copy.getName(),copy);
			}
		}
	}


	public void flush() {
		//run SQL in async thread
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(BeardStat.self(), new sqlFlusher(pullCacheToThread()));

	}

	public PlayerStatBlob pullPlayerStatBlob(String player, boolean create) {
		try {
			long t1 = (new Date()).getTime();
			PlayerStatBlob pb = null;

			//try to pull it from the db
			prepGetAllPlayerStat.setString(1, player);
			ResultSet rs = prepGetAllPlayerStat.executeQuery();
			pb = new PlayerStatBlob(player,0);
			while(rs.next()){
				//`category`,`stat`,`value`
				PlayerStat ps = new PlayerStat(rs.getString(2),rs.getString(3),rs.getInt(4));
				pb.addStat(ps);
			}
			rs.close();

			BeardStat.printDebugCon("time taken to retrieve: "+((new Date()).getTime() - t1) +" Milliseconds");
			if(pb.getStats().size()==0 && create==false){return null;}

			return pb;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
   public List<TopPlayer> pullTopPlayers(String category){
        long t1 = (new Date()).getTime();
        List<TopPlayer> topPlayed = new ArrayList<TopPlayer>();

        try {
            //try to pull it from the db
            prepTopPlayersStat.setInt(1, BeardStat.self().getTopPlayerCount());
            prepTopPlayersStat.setString(2, category);
            ResultSet rs = prepTopPlayersStat.executeQuery();
            while(rs.next()){
                TopPlayer item = new TopPlayer(rs.getInt("Rank"),rs.getString("Player"),rs.getString("TimeOnServer"), rs.getDate("FirstLogin"), rs.getDate("LastLogin"));
                topPlayed.add(item);
            }
            rs.close();
    
            BeardStat.printDebugCon("time taken to retrieve: "+((new Date()).getTime() - t1) +" Milliseconds");
            if(topPlayed.size()==0){return null;}
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return topPlayed;
    }

    public List<String> pullAllStatsInCategory(String category){
        long t1 = (new Date()).getTime();

        if(category.length() == 0)
        { return null; }

        List<String> cats = new ArrayList<String>();
        try{
            prepAllStatsInCategory.setString(1, category);
            
            ResultSet rs = prepAllStatsInCategory.executeQuery();
            while(rs.next()){
                cats.add(rs.getString(1));
            }
            rs.close();
    
            BeardStat.printDebugCon("time taken to retrieve: "+((new Date()).getTime() - t1) +" Milliseconds");
            if(cats.size()==0){return null;}
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cats;
    }

	private HashMap<String,PlayerStatBlob> pullCacheToThread(){
		synchronized(writeCache){
			HashMap<String,PlayerStatBlob> tmp = writeCache;
			writeCache = new HashMap<String,PlayerStatBlob>();
			return tmp;
		}
	}

	class sqlFlusher implements Runnable {

		HashMap<String,PlayerStatBlob> toWrite = null;
		sqlFlusher(HashMap<String,PlayerStatBlob> toWrite){
			this.toWrite = toWrite;
		}
		public void run() {
			BeardStat.printDebugCon("[Writing to database]");
			try {
				//KEEP ALIVE  
				keepAlive.clearBatch();
				keepAlive.executeQuery();

				Long t1 = (new Date()).getTime();
				int objects = 0;
				prepSetPlayerStat.clearBatch();
				for(PlayerStatBlob pb:toWrite.values()){
					BeardStat.printDebugCon("Packing stats for "+pb.getName());
					BeardStat.printDebugCon("[");
					for(PlayerStat ps:pb.getStats()){
						BeardStat.printDebugCon("stat: " + ps.getCat() + "->"+ ps.getName() + " = " + ps.getValue());
						prepSetPlayerStat.setString(1, pb.getName());
						prepSetPlayerStat.setString(2, ps.getCat());
						prepSetPlayerStat.setString(3, ps.getName());
						prepSetPlayerStat.setInt(4, ps.getValue());
						prepSetPlayerStat.setInt(5, ps.getValue());
						prepSetPlayerStat.addBatch();
						objects+=1;
					}
					BeardStat.printDebugCon("]");
				}

				int[] r = prepSetPlayerStat.executeBatch();
				for(int rr :r){
					BeardStat.printDebugCon(":: " +rr);
				}

				long t2 = (new Date()).getTime();
				BeardStat.printDebugCon("[Database write Completed]");
				BeardStat.printDebugCon("Objects written to database: " + objects);
				BeardStat.printDebugCon("Time taken to write to Database: " + (t2-t1) + "milliseconds");
				if(objects > 0){
					BeardStat.printDebugCon("Average time per object: " + (t2-t1)/objects + "milliseconds");
				}

			} catch (SQLException e) {
				BeardStat.printCon("Connection Could not be established, attempting to reconnect...");
				createConnection();
				prepareStatements();
			}
		}

	}

	public void flushNow(){
		System.out.println("Saving in same thread");
		(new sqlFlusher(pullCacheToThread())).run();
	}
}
