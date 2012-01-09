package me.tehbeard.BeardStat;

import java.io.File;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import me.tehbeard.BeardStat.DataProviders.FlatFileStatDataProvider;
import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import me.tehbeard.BeardStat.DataProviders.MysqlStatDataProvider;
import me.tehbeard.BeardStat.StatCollectors.*;
import me.tehbeard.BeardStat.commands.*;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.BeardStat.listeners.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permissible;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * BeardStat Statistic's tracking for the gentleman server
 * @author James
 *
 */
public class BeardStat extends JavaPlugin {



	private static BeardStat self;
	private int runner;
	public static HashMap<String,Long> loginTimes = new HashMap<String,Long>();
	private static final String PERM_PREFIX = "stat";

	public static boolean hasPermission(Permissible player,String node){

		return player.hasPermission(PERM_PREFIX + "." + node);


	}
	public static void printCon(String line){
		System.out.println("[BeardStat] " + line);
	}

	public static void printDebugCon(String line){

		if(self.getConfig().getBoolean("general.debug", false)){
			System.out.println("[BeardStat][DEBUG] " + line);

		}

	}

	public void onDisable() {
		//flush database to cache

		printCon("Stopping auto flusher");
		getServer().getScheduler().cancelTask(runner);
		printCon("Flushing cache to database");
		PlayerStatManager.saveCache();
		printCon("Cache flushed to database");

		self = null;
	}

	public void onEnable() {


		self = this;

		printCon("Starting BeardStat");


		updateConfig();
		
		getConfig().options().copyDefaults(true);
		saveConfig();

		

		//set DB HERE
		printCon("Connecting to database");
		printCon("Using " + getConfig().getString("stats.database.type") + " Adpater");
		if(getConfig().getString("stats.database.type")==null){
			printCon("INVALID ADAPTER SELECTED");
			getPluginLoader().disablePlugin(this);
			return;
		}
		IStatDataProvider db =null;
		if(getConfig().getString("stats.database.type").equals("mysql")){
			try {
				db = new MysqlStatDataProvider(
						getConfig().getString("stats.database.host"),
						getConfig().getString("stats.database.database"),
						getConfig().getString("stats.database.username"),
						getConfig().getString("stats.database.password")
						);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(getConfig().getString("stats.database.type").equals("file")){
			db = new FlatFileStatDataProvider(new File(getDataFolder(),"stats.yml"));	
		}


		if(db==null){
			printCon(" Error loading database, disabling plugin");
			getPluginLoader().disablePlugin(this);
			return;
		}
		PlayerStatManager.setDatabase(db);




		printCon("Registering events and collectors");
		/**
		 * REGISTER STAT COLLECTORS
		 */
		BlockStatCollector bsc = new BlockStatCollector();
		StatCollectorManager.registerStatCollector(Type.BLOCK_BREAK,bsc);
		StatCollectorManager.registerStatCollector(Type.BLOCK_PLACE,bsc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_BUCKET_FILL,bsc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_BUCKET_EMPTY,bsc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_INTERACT,bsc);


		PlayerStatCollector psc = new PlayerStatCollector();			
		StatCollectorManager.registerStatCollector(Type.PLAYER_CHAT,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_DROP_ITEM,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_FISH,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_INTERACT_ENTITY,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_KICK,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_LOGIN,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_QUIT,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_MOVE,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_PICKUP_ITEM,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_PORTAL,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_TELEPORT,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_TOGGLE_SNEAK,psc);
		StatCollectorManager.registerStatCollector(Type.PLAYER_JOIN,psc);

		EntityStatCollector esc = new EntityStatCollector();
		StatCollectorManager.registerStatCollector(Type.ENTITY_DAMAGE,esc);
		StatCollectorManager.registerStatCollector(Type.ENTITY_DEATH,esc);
		StatCollectorManager.registerStatCollector(Type.ENTITY_REGAIN_HEALTH,esc);
		StatCollectorManager.registerStatCollector(Type.ENTITY_TAME,esc);


		//register event listeners

		//block listener
		StatBlockListener sbl = new StatBlockListener();
		getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, sbl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_PLACE, sbl,Priority.Monitor,this);

		//player listener
		StatPlayerListener spl = new StatPlayerListener();
		getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_DROP_ITEM, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_FISH, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT_ENTITY, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_LOGIN, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_PRELOGIN, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_MOVE, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_PICKUP_ITEM, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_PORTAL, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_TELEPORT, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_TOGGLE_SNEAK, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_BUCKET_EMPTY, spl,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_BUCKET_FILL, spl,Priority.Monitor,this);





		//entity listener (Damage/taming)
		StatEntityListener sel = new StatEntityListener();
		getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE,sel,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_DEATH,sel,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_REGAIN_HEALTH,sel,Priority.Monitor,this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_TAME,sel,Priority.Monitor,this);


		//entity listener

		printCon("Starting flush, defaulting to every 2 Minutes");
		runner = getServer().getScheduler().scheduleSyncRepeatingTask(this, new dbFlusher(), 2400L, 2400L);

		printCon("Loading commands");
		getCommand("stats").setExecutor(new StatCommand());
		getCommand("played").setExecutor(new playedCommand());
		getCommand("playedother").setExecutor(new playedOtherCommand());
		getCommand("statsget").setExecutor(new StatGetCommand());

		for(Player player: getServer().getOnlinePlayers()){
			BeardStat.loginTimes.put(player.getName(), (new Date()).getTime());
		}


		printCon("BeardStat Loaded");
	}

	/**
	 * Creates the inital config
	 */
	private void updateConfig() {
		//Transfer config if nessecary
		File f = new File(getDataFolder(),"BeardStat.yml");
		if(f.exists()){
			printCon("OLD CONFIG FILE FOUND, TRANSFERING TO NEW CONFIG");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			config.set("stats.version", getDescription().getVersion());
			getConfig().setDefaults(config);
			getConfig().options().copyDefaults(true);
			saveConfig();
			f.delete();
		}
		
		if(!getConfig().get("stats.version","").equals(getDescription().getVersion())){
			printCon("WARNING! CONFIG LOADING FROM PREVIOUS VERSION");
			getConfig().set("stats.version", getDescription().getVersion());
			saveConfig();
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		sender.sendMessage("Command not implemented!");
		return false;

	}

	public class dbFlusher implements Runnable{

		public void run() {
			BeardStat.printCon("Flushing to database.");
			PlayerStatManager.clearCache(true);
			BeardStat.printCon("flush completed");
		}

	}
	/**
	 * Returns length of current session in memory
	 * @param player
	 * @return
	 */
	public int sessionTime(String player){
		if( BeardStat.loginTimes.containsKey(player)){
			return Integer.parseInt(""+BeardStat.loginTimes.get(player)/1000L);

		}
		return 0;
	}
}
