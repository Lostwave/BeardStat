package me.tehbeard.BeardStat;

import java.io.File;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.tehbeard.BeardStat.DataProviders.FlatFileStatDataProvider;
import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import me.tehbeard.BeardStat.DataProviders.MysqlStatDataProvider;

import me.tehbeard.BeardStat.commands.*;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.BeardStat.listeners.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * BeardStat Statistic's tracking for the gentleman server
 * @author James
 *
 */
public class BeardStat extends JavaPlugin {



    public static  BeardStat self(){
        return self;
    }
    private static BeardStat self;
    private int runner;
    private PlayerStatManager playerStatManager;
    private HashMap<String,Long> loginTimes;
    private static final String PERM_PREFIX = "stat";

    public static boolean isVersionUpdated = false;

    /**
     * Returns the stat manager for use by other plugins
     * @return
     */
    public PlayerStatManager getStatManager(){
        return playerStatManager;
    }

    private int topPlayerCount = 0;
    public int getTopPlayerCount(){
        if(topPlayerCount == 0){
            topPlayerCount = getConfig().getInt("stats.topplayedcount", 25); 
        }
        if(topPlayerCount > 100){
            topPlayerCount = 100;
        }
        return topPlayerCount;
    }


    public static boolean hasPermission(Permissible player,String node){

        return player.hasPermission(PERM_PREFIX + "." + node);


    }
    public static void printCon(String line){
        System.out.println("[BeardStat] " + line);
    }

    public static void printDebugCon(String line){

        if(self != null && self.getConfig().getBoolean("general.debug", false)){
            System.out.println("[BeardStat][DEBUG] " + line);

        }

    }

    public void onDisable() {
        //flush database to cache

        printCon("Stopping auto flusher");
        getServer().getScheduler().cancelTask(runner);
        if(playerStatManager != null){
            printCon("Flushing cache to database");
            playerStatManager.saveCache();
            printCon("Cache flushed to database");
        }
        self = null;
    }

    public void onEnable() {


        self = this;
        loginTimes = new HashMap<String,Long>();
        printCon("Starting BeardStat");


        updateConfig();

        YamlConfiguration versions = YamlConfiguration.loadConfiguration(getResource("version.yml"));
        if(!new File(getDataFolder(),"config.yml").exists()
                ||
                
                (getConfig().getInt("stats.version") < versions.getInt("configversion"))
                ){
            getConfig().options().copyDefaults(true);
            
        }
        System.out.println("Config Version: ${project.config.version}");
        
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
        if(getConfig().getString("stats.database.type").equalsIgnoreCase("mysql")){
            try {
                db = new MysqlStatDataProvider(
                        getConfig().getString("stats.database.host"),
                        getConfig().getString("stats.database.database"),
                        getConfig().getString("stats.database.username"),
                        getConfig().getString("stats.database.password")
                        );
            } catch (SQLException e) {
                db = null;
                mysqlError(e);
            }
        }


        if(getConfig().getString("stats.database.type").equalsIgnoreCase("file")){
            db = new FlatFileStatDataProvider(new File(getDataFolder(),"stats.yml"));	
        }


        if(db==null){
            printCon(" Error loading database, disabling plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }
        playerStatManager = new PlayerStatManager(db);




        printCon("Registering events and collectors");


        //register event listeners

        //block listener
        List<String> worldList = getConfig().getStringList("stats.blacklist");
        StatBlockListener sbl = new StatBlockListener(worldList,playerStatManager);
        StatPlayerListener spl = new StatPlayerListener(worldList,playerStatManager);
        StatEntityListener sel = new StatEntityListener(worldList,playerStatManager);
        StatVehicleListener svl = new StatVehicleListener(worldList, playerStatManager);
        StatCraftListener scl = new StatCraftListener(worldList,playerStatManager);

        getServer().getPluginManager().registerEvents(sbl, this);
        getServer().getPluginManager().registerEvents(spl, this);
        getServer().getPluginManager().registerEvents(sel, this);
        getServer().getPluginManager().registerEvents(svl, this);
        getServer().getPluginManager().registerEvents(scl, this);



        printCon("Starting flush, defaulting to every 2 Minutes");
        runner = getServer().getScheduler().scheduleSyncRepeatingTask(this, new dbFlusher(), 2400L, 2400L);

        printCon("Loading commands");

        getCommand("stats").setExecutor(new StatCommand(playerStatManager));
        getCommand("played").setExecutor(new playedCommand(playerStatManager));
        getCommand("statsget").setExecutor(new StatGetCommand(playerStatManager));
        getCommand("statpage").setExecutor(new StatPageCommand(this));
        getCommand("laston").setExecutor(new LastOnCommand(playerStatManager));
        getCommand("topplayer").setExecutor(new TopPlayerCommand(playerStatManager));

        for(Player player: getServer().getOnlinePlayers()){
            loginTimes.put(player.getName(), (new Date()).getTime());
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

        //convert old world lists over to blacklist
        if(getConfig().contains("stats.worlds")){
            printCon("Moving blacklist to new location");
            getConfig().set("stats.blacklist", getConfig().getStringList("stats.worlds"));
            getConfig().set("stats.worlds",null);
        }



        if(!getConfig().get("stats.version","").equals(getDescription().getVersion())){
            printCon("WARNING! CONFIG LOADING FROM PREVIOUS VERSION");
            isVersionUpdated = true;
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
            if(getConfig().getBoolean("general.verbose",false)){
                BeardStat.printCon("Flushing to database.");
            }
            List<String> players = new ArrayList<String>(Bukkit.getOnlinePlayers().length);
            for(Player p: Bukkit.getOnlinePlayers()){
                players.add(p.getName());
            }
            playerStatManager.clearCache(players,true);
            if(getConfig().getBoolean("general.verbose",false)){
                BeardStat.printCon("flush completed");
            }
        }

    }
    /**
     * Returns length of current session in memory
     * @param player
     * @return
     */
    public int getSessionTime(String player){
        if(loginTimes.containsKey(player)){
            return Integer.parseInt("" + ((System.currentTimeMillis()  - loginTimes.get(player))/1000L));

        }
        return 0;
    }

    public Long getLoginTime(String player){
        if(!loginTimes.containsKey(player)){
            setLoginTime(player,System.currentTimeMillis());
        }
        return loginTimes.get(player);

    }

    public void setLoginTime(String player,long time){
        loginTimes.put(player,time);

    }

    public void wipeLoginTime(String player){
        loginTimes.remove(player);
    }

    public static void sendNoPermissionError(CommandSender sender){
        sendNoPermissionError(sender, "You don't have permission to use that command.");
    }

    public static void sendNoPermissionError(CommandSender sender, String message){
        sender.sendMessage(ChatColor.RED + message);
    }

    public static void mysqlError(SQLException e){
        self.getLogger().severe("=========================================");
        self.getLogger().severe("              DATABASE ERROR             ");
        self.getLogger().severe("=========================================");
        self.getLogger().severe("An error occured while trying to connect to the BeardStat database");
        self.getLogger().severe("Mysql error code: "+ e.getErrorCode());

        switch(e.getErrorCode()){
        case 1042:self.getLogger().severe("Cannot find hostname provided");break;
        case 1044:
        case 1045:self.getLogger().severe("Cannot connect to database, check user credentials, database exists and that user is able to log in from this remote machine");break;


        default:self.getLogger().severe("Error code not found, either check the error code online, or post on the dev.bukkit.org/server-mods/beardstat page");break; 
        }

        self.getLogger().severe("=========================================");
        self.getLogger().severe("            Begin error dump             ");
        self.getLogger().severe("=========================================");
        e.printStackTrace();
        self.getLogger().severe("=========================================");
        self.getLogger().severe("             End error dump              ");
        self.getLogger().severe("=========================================");

    }
}

