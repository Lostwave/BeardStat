package me.tehbeard.BeardStat.DataProviders;

import java.sql.*;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;

public class SqliteStatDataProvider extends IStatDataProvider {

	Connection conn;
	PreparedStatement prepAddPlayer;
	PreparedStatement prepAddPlayerStat;
	PreparedStatement prepUpdPlayerStat;
	SqliteStatDataProvider(){
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:test.db");
			
			prepAddPlayer = conn.prepareStatement("INSERT INTO stat_players VALUES(null,?)");
			prepAddPlayerStat = conn.prepareStatement("INSERT INTO stat_statistic VALUES(?,?,?)");
			//prepUpdPlayerStat = conn.prepareStatement("UPDATE stat_statistic set VALUES(?,?,?)");
		} catch (SQLException e) {
			BeardStat.printCon("Failed to initaise Sqlite Data Provider. Dumping error.");
			e.printStackTrace();
		}
	}
	void test(){

		/*
	   PreparedStatement prep = conn.prepareStatement("insert into people values (?, ?);");
	   conn.setAutoCommit(false);
	    prep.setString(1, "Gandhi");
	    prep.setString(2, "politics");
	    prep.addBatch();
	    prep.executeBatch();
	    conn.setAutoCommit(true);


//SELECTION
 ResultSet rs = stat.executeQuery("select * from people;");
	    while (rs.next()) {
	      System.out.println("name = " + rs.getString("name"));
	      System.out.println("job = " + rs.getString("occupation"));
	    }
	    rs.close();
	    conn.close();
		 */

	}

	/**
	 * Create a new instance of this Data Provider
	 * @return
	 */
	public static IStatDataProvider newInstance(){
		//Attempt to load the SQLite library
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			BeardStat.printCon("SQLite Library not found!");
			return null;
		}

		return new SqliteStatDataProvider();
	}

	@Override
	public PlayerStatBlob pullPlayerStatBlob(String player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pushPlayerStatBlob(PlayerStatBlob player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}
	@Override
	public PlayerStatBlob pullPlayerStatBlob(String player, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


}
