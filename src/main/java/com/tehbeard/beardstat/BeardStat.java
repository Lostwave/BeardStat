package com.tehbeard.beardstat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Plotter;

import com.tehbeard.beardstat.commands.LastOnCommand;
import com.tehbeard.beardstat.commands.StatAdmin;
import com.tehbeard.beardstat.commands.StatCommand;
import com.tehbeard.beardstat.commands.StatPageCommand;
import com.tehbeard.beardstat.commands.StatScriptExecCommand;
import com.tehbeard.beardstat.commands.playedCommand;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.JDBCStatDataProvider;
import com.tehbeard.beardstat.dataproviders.MysqlStatDataProvider;
import com.tehbeard.beardstat.dataproviders.SQLiteStatDataProvider;
import com.tehbeard.beardstat.dataproviders.identifier.HomebrewIdentifierGenerator;
import com.tehbeard.beardstat.dataproviders.identifier.IdentifierService;
import com.tehbeard.beardstat.listeners.StatBlockListener;
import com.tehbeard.beardstat.listeners.StatCraftListener;
import com.tehbeard.beardstat.listeners.StatEntityListener;
import com.tehbeard.beardstat.listeners.StatPlayerListener;
import com.tehbeard.beardstat.listeners.StatVehicleListener;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.manager.OnlineTimeManager;
import com.tehbeard.beardstat.utils.BukkitHumanNameGenerator;
import com.tehbeard.beardstat.utils.LanguagePack;
import com.tehbeard.beardstat.utils.StatUtils;
import com.tehbeard.utils.mojang.api.profiles.HttpProfileRepository;
import com.tehbeard.utils.syringe.configInjector.YamlConfigInjector;


/**
 * BeardStat Statistic's tracking for the gentleman server
 *
 * @author James
 *
 */
public class BeardStat extends JavaPlugin implements DbPlatform {

    public static final String PERM_COMMAND_PLAYED_OTHER = "stat.command.played.other";
    public static final String PERM_COMMAND_STAT_OTHER = "stat.command.stat.other";
    // Default values for domain and world
    public static final String DEFAULT_DOMAIN = "default";
    public static final String GLOBAL_WORLD = "__global__";
    private int saveTaskId;
    private EntityStatManager statManager;
    public static StatConfiguration configuration;
    public static WorldManager worldManager;

    /**
     * Returns the stat manager for use by other plugins
     *
     * @return
     */
    public EntityStatManager getStatManager() {
        return this.statManager;
    }

    @Override
    public void onDisable() {
        /*
         * Shut down auto flusher, force cache to be saved, then kill static
         * reference to this plugin
         */
        getLogger().info("Stopping auto flusher");
        getServer().getScheduler().cancelTask(this.saveTaskId);
        if (this.statManager != null) {
            getLogger().info("Flushing cache to database");
            this.statManager.saveCache();
            this.statManager.flush();
            getLogger().info("Cache flushed to database");
        }
    }

    @Override
    public void onEnable() {

        getLogger().info("Starting BeardStat");

        // Read in the metadata file from jar and from data folder
        HomebrewIdentifierGenerator.readData(getResource("metadata.txt"));
        
        try {
            HomebrewIdentifierGenerator.readData(new FileInputStream(new File(getDataFolder(), "metadata.txt")));
        } catch (FileNotFoundException e) {
            getLogger().warning("No External metadata file detected");
        }
        BukkitHumanNameGenerator.init();

        // load language file from jar and from data folder
        try {
            getLogger().info("Loading default language pack");
            LanguagePack.load(getResource("messages.lang"));
            File extLangPack = new File(getDataFolder(), "messages.lang");
            if (extLangPack.exists()) {
                getLogger().info("External language pack detected! Loading...");
                LanguagePack.overlay(new FileInputStream(extLangPack));
            }
        } catch (IOException e1) {
            getLogger().severe("Failed to load language pack.");

        }

        // run config updater
        try {
            updateConfig();
            saveConfig();
            reloadConfig();
        } catch (Exception e) {
            handleError(new BeardStatRuntimeException("An error occured while loading or updating the config", e, false));
            return;
        }
        reloadConfig();
        getConfig();
        configuration = new StatConfiguration();
        new YamlConfigInjector(getConfig()).inject(configuration);

        getLogger().config(configuration.toString());

        //Hopefully this pleases Diemex
        Level level = Level.parse(configuration.logLevel);
        getLogger().setLevel(level);
        for (Handler handler : getLogger().getHandlers()) {
            handler.setLevel(level);
        }

        File worldsFile = new File(getDataFolder(), "worlds.yml");
        worldManager = new WorldManager(YamlConfiguration.loadConfiguration(worldsFile).getConfigurationSection("worlds"));

        // setup our data provider, fail out if it's not found
        getLogger().info("Connecting to database");
        getLogger().info("Using " + configuration.dbType + " Adpater");
        DatabaseConfiguration dbConfig = getDatabaseConfiguration(getConfig().getConfigurationSection("stats.database"));
        IStatDataProvider db = getDataProvider(dbConfig);

        if (db == null) {
            getLogger().severe(" Error loading database, disabling plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }
        
        if(dbConfig.runUUIDUpdate){
            new ProfileUUIDUpdater(getLogger(), db, new HttpProfileRepository());
            getConfig().set("stats.database.uuidUpdate",false);
            saveConfig();
        }
        

        // start the player manager
        this.statManager = new EntityStatManager(this, db);


        getLogger().info("Loading id mapping");

        StatUtils.setManager(this.statManager);
        
        IdentifierService.setGenerator(new HomebrewIdentifierGenerator());
        
        getLogger().info("Registering events and collectors");

        // register event listeners
        try {
            StatBlockListener sbl = new StatBlockListener(this.statManager, this);
            StatPlayerListener spl = new StatPlayerListener(this.statManager, this);
            StatEntityListener sel = new StatEntityListener(this.statManager, this);
            StatVehicleListener svl = new StatVehicleListener(this.statManager, this);
            StatCraftListener scl = new StatCraftListener(this.statManager, this);
            getServer().getPluginManager().registerEvents(sbl, this);
            getServer().getPluginManager().registerEvents(spl, this);
            getServer().getPluginManager().registerEvents(sel, this);
            getServer().getPluginManager().registerEvents(svl, this);
            getServer().getPluginManager().registerEvents(scl, this);
        } catch (Exception e) {
            handleError(new BeardStatRuntimeException("Error registering events", e, false));
        }

        // start Database flusher.
        try {
            this.saveTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new DbFlusher(), 2400L, 2400L);
        } catch (Exception e) {
            handleError(new BeardStatRuntimeException("Error starting database flusher", e, false));
        }

        getLogger().info("Loading commands");
        try {
            getCommand("stats").setExecutor(new StatCommand(this.statManager, this));
            getCommand("played").setExecutor(new playedCommand(this.statManager, this));
            getCommand("statpage").setExecutor(new StatPageCommand(this.statManager, this));
            getCommand("laston").setExecutor(new LastOnCommand(this.statManager, this));
            //getCommand("beardstatdebug").setExecutor(this.statManager);
            getCommand("statadmin").setExecutor(new StatAdmin(this.statManager, this));
            getCommand("statexecscript").setExecutor(new StatScriptExecCommand(this.statManager, this,  (db instanceof JDBCStatDataProvider ? (JDBCStatDataProvider) db : null)));
        } catch (Exception e) {
            handleError(new BeardStatRuntimeException("Error registering commands", e, false));
        }

        getLogger().info("loading any players already online");// Fix people being dumb
        for (Player player : getServer().getOnlinePlayers()) {

            OnlineTimeManager.setRecord(player.getName(), player.getWorld().getName());
        }

        // Enabled metrics
        Metrics metrics;
        try {
            metrics = new Metrics(this);
            metrics.createGraph("Database Type").addPlotter(
                    new Plotter(getConfig().getString("stats.database.type").toLowerCase()) {
                @Override
                public int getValue() {
                    return 1;
                }
            });// record database type

            metrics.start();
        } catch (Exception e) {
            handleError(new BeardStatRuntimeException("Metrics threw an error during startup", null, true));
        }
        getLogger().info("BeardStat Loaded");

    }

    /**
     * Update config as needed.
     */
    private void updateConfig() {

        //Write out config files as needed.
        saveResource("config.yml", false);
        saveResource("worlds.yml", false);

        // update config if nessecary
        if (getConfig().getInt("stats.configversion", 0) < getConfig().getDefaults().getInt("stats.configversion")) {

            getLogger().info("Updating config to include newest configuration options");
            getConfig().set("stats.configversion", null);
            getConfig().getDefaults().set("stats.database.sql_db_version", null);
            getConfig().options().copyDefaults(true);

        }

        if (getConfig().contains("stats.blacklist")) {
            try {
                getLogger().info("Moving blacklist to worlds.yml");
                File worldsFile = new File(getDataFolder(), "worlds.yml");
                YamlConfiguration worldCfg = YamlConfiguration.loadConfiguration(worldsFile);
                for (String world : getConfig().getStringList("stats.blacklist")) {
                    worldCfg.set("worlds." + world + ".survival", false);
                    worldCfg.set("worlds." + world + ".creative", false);
                    worldCfg.set("worlds." + world + ".adventure", false);
                }

                worldCfg.save(worldsFile);
            } catch (IOException ex) {
                Logger.getLogger(BeardStat.class.getName()).log(Level.SEVERE, null, ex);
            }
            getConfig().set("stats.blacklist", null);
        }

        saveConfig();
    }

    /**
     * Override default command handler, to indicate we derped
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage("Command " + commandLabel + " not implemented!");
        return true;

    }

    @Override
    public boolean configValueIsSet(String key) {
        return getConfig().isSet(key);
    }

    @Override
    public void configValueSet(String key, Object val) {
        getConfig().set(key, val);
    }

    @Override
    public void loadEvent(EntityStatBlob esb) {
        Bukkit.getPluginManager().callEvent(new EntityStatBlobLoadEvent(esb));
    }

    @Override
    public boolean isPlayerOnline(String player) {
        return Bukkit.getOfflinePlayer(player).isOnline();
    }

    @Override
    public String getWorldForPlayer(String entityName) {
        return Bukkit.getPlayer(entityName).getWorld().getName();
    }

    /**
     * Flush cache of player stats to database at regular intervals
     *
     * @author James
     *
     */
    public class DbFlusher implements Runnable {

        @Override
        public void run() {
            getLogger().config("Flushing to database.");
            statManager.saveCache();
            statManager.flush();
            getLogger().config("flush completed");

        }
    }

    public static void sendNoPermissionError(CommandSender sender) {
        sendNoPermissionError(sender, LanguagePack.getMsg("error.permission"));
    }

    public static void sendNoPermissionError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    /**
     * Attempt to parse MySQL error codes to help users
     *
     * @param e
     */
    public void mysqlError(SQLException e, String script) {
        Logger logger = getLogger();
        logger.severe("=========================================");
        logger.severe("|             DATABASE ERROR            |");
        logger.severe("=========================================");
        logger.severe("");
        if (script != null) {
            logger.severe("Caused by script: " + script);
            logger.severe("");
        }
        logger.severe("Mysql error code: " + e.getErrorCode());

        switch (e.getErrorCode()) {
        case 1042:
            logger.severe("Cannot find hostname provided, check spelling of hostname in config file");
            break;
        case 1044:
        case 1045:
            logger.severe("Cannot connect to database, check user credentials, database exists and that user is able to log in from this machine");
            break;
        case 1049:
            logger.severe("Cannot locate database, check you spelt database name correctly and username has access rights from this machine.");
            break;

        default:
            logger.severe("Error code ["
                    + e.getErrorCode()
                    + "] not found (or not supplied!), either check the error code online, or post on the dev.bukkit.org/server-mods/beardstat page");
            logger.severe("Exception Detail:");
            logger.severe(e.getMessage());
            break;
        }

        // dump stack trace if in verbose mode
        if (getConfig().getBoolean("general.verbose", false)) {
            logger.severe("=========================================");
            logger.severe("            Begin error dump             ");
            logger.severe("=========================================");
            e.printStackTrace();
            logger.severe("=========================================");
            logger.severe("             End error dump              ");
            logger.severe("=========================================");
        }

    }

    /**
     * Load a data provider from config
     *
     * @param config
     * @return
     */
    private IStatDataProvider getDataProvider(DatabaseConfiguration config) {
        IStatDataProvider db = null;
        // MySQL provider
        if (config.databaseType.equalsIgnoreCase("mysql")) {
            try {
                db = new MysqlStatDataProvider(this,
                        config);
            } catch (BeardStatRuntimeException e) {
                handleError(e);
            } catch (SQLException e) {
                mysqlError(e, null);
                db = null;
            }
        }
        // SQLite provider
        if (config.databaseType.equalsIgnoreCase("sqlite")) {
            try {
                db = new SQLiteStatDataProvider(this, new File(getDataFolder(), "stats.db").toString(), config);
            } catch (BeardStatRuntimeException e) {
                handleError(e);
            } catch (SQLException e) {
                e.printStackTrace();
                db = null;
            }

        }

        // In memory provider
        if (config.databaseType.equalsIgnoreCase("memory")) {
            try {
                db = new SQLiteStatDataProvider(this, ":memory:", config);
            } catch (BeardStatRuntimeException e) {
                handleError(e);
            } catch (SQLException e) {
                e.printStackTrace();
                db = null;
            }
        }

        // File provider, kept for alert message, remove in 0.7
        if (config.databaseType.equalsIgnoreCase("file")) {
            getLogger().warning("FILE DRIVER NO LONGER SUPPORTED, PLEASE TRANSFER TO SQLITE/MYSQL IN PREVIOUS VERSION BEFORE LOADING");
        }

        // transfer provider, calls method again to load handlers for transfer
        if (config.databaseType.equalsIgnoreCase("transfer")) {
            throw new UnsupportedOperationException("NOT IMPLEMENTED YET");//TODO - FIX
            /*IStatDataProvider _old = getDataProvider(getDatabaseConfiguration(getConfig().getConfigurationSection("stats.transfer.old")));
             IStatDataProvider _new = getDataProvider(getDatabaseConfiguration(getConfig().getConfigurationSection("stats.transfer.new")));
             printCon("Initiating transfer of stats, this may take a while");
             new TransferDataProvider(this, _old, _new);
             db = _new;*/
        }
        return db;
    }

    /**
     * Handle an error, if it's a {@link BeardStatRuntimeException} it will try to kill BeardStat if the error is non-recoverable
     *
     * @param e
     */
    public void handleError(Exception e) {
        if (e instanceof BeardStatRuntimeException) {
            BeardStatRuntimeException be = (BeardStatRuntimeException) e;
            if (!be.isRecoverable()) {
                getLogger().severe("WARNING: BeardStat has encountered an error and cannot recover, disabling plugin.");
                getLogger().severe(be.getMessage());
                if (e != null) {
                    handleUnknownError(e);
                }
                Bukkit.getPluginManager().disablePlugin(this);
            } else {
                getLogger().severe("WARNING: BeardStat has encountered an error.");
                getLogger().severe(be.getMessage());
                if (e != null) {
                    handleUnknownError(e);
                }
            }
        } else {
            handleUnknownError(e);
        }
    }

    /**
     * Print out information related to unknown errors
     *
     * @param e
     */
    private void handleUnknownError(Exception e) {
        getLogger().severe("=========");
        getLogger().severe("BeardStat");
        getLogger().severe("=========");
        getLogger().severe("BeardStat encountered an error, please submit the following info + stack trace to the dev bukkit page (http://dev.bukkit.org/server-mods/BeardStat/");
        getLogger().severe("");
        getLogger().severe("BeardStat version: " + getDescription().getVersion());
        getLogger().severe("");
        e.printStackTrace();
        getLogger().severe("");
        getLogger().severe("=========");
    }

    public DatabaseConfiguration getDatabaseConfiguration(ConfigurationSection section) {
        DatabaseConfiguration dbc = new DatabaseConfiguration(getConfig().getDefaults().getInt("stats.database.sql_db_version"));//assign latest version here to config
        new YamlConfigInjector(section).inject(dbc);
        return dbc;
    }
}
