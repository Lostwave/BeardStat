package com.tehbeard.BeardStat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.tehbeard.BeardStat.Metrics.Plotter;
import com.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import com.tehbeard.BeardStat.DataProviders.MysqlStatDataProvider;
import com.tehbeard.BeardStat.DataProviders.SQLiteStatDataProvider;
import com.tehbeard.BeardStat.DataProviders.TransferDataProvider;
import com.tehbeard.BeardStat.commands.LastOnCommand;
import com.tehbeard.BeardStat.commands.StatAdmin;
import com.tehbeard.BeardStat.commands.StatCommand;
import com.tehbeard.BeardStat.commands.StatPageCommand;
import com.tehbeard.BeardStat.commands.playedCommand;
import com.tehbeard.BeardStat.commands.formatters.FormatFactory;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.OnlineTimeManager;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.listeners.MetaDataCapture;
import com.tehbeard.BeardStat.listeners.StatBlockListener;
import com.tehbeard.BeardStat.listeners.StatCraftListener;
import com.tehbeard.BeardStat.listeners.StatEntityListener;
import com.tehbeard.BeardStat.listeners.StatPlayerListener;
import com.tehbeard.BeardStat.listeners.StatVehicleListener;

/**
 * BeardStat Statistic's tracking for the gentleman server
 * 
 * @author James
 * 
 */
public class BeardStat extends JavaPlugin {

    // Default values for domain and world
    public static final String  DEFAULT_DOMAIN = "default";
    public static final String  GLOBAL_WORLD   = "__global__";
    // Stat permission prefix
    private static final String PERM_PREFIX    = "stat";

    private static BeardStat    self;
    private int                 saveTaskId;
    private PlayerStatManager   playerStatManager;

    /**
     * Return the instance of this plugin
     * 
     * @return
     */
    public static BeardStat self() {
        return self;
    }

    /**
     * Returns the stat manager for use by other plugins
     * 
     * @return
     */
    public PlayerStatManager getStatManager() {
        return this.playerStatManager;
    }

    /**
     * Check for permission
     * 
     * @param player
     *            player to check
     * @param node
     *            permission node to check
     * @return
     */
    public static boolean hasPermission(Permissible player, String node) {

        return player.hasPermission(PERM_PREFIX + "." + node);

    }

    /**
     * Print to console
     * 
     * @param line
     */
    public static void printCon(String line) {
        self.getLogger().info(line);
    }

    /**
     * Print to console if debug mode is active
     * 
     * @param line
     */
    public static void printDebugCon(String line) {

        if ((self != null) && self.getConfig().getBoolean("general.debug", false)) {
            printCon("[DEBUG] " + line);
        }
    }

    @Override
    public void onDisable() {
        /*
         * Shut down auto flusher, force cache to be saved, then kill static
         * reference to this plugin
         */
        printCon("Stopping auto flusher");
        getServer().getScheduler().cancelTask(this.saveTaskId);
        if (this.playerStatManager != null) {
            printCon("Flushing cache to database");
            this.playerStatManager.saveCache();
            this.playerStatManager.flush();
            printCon("Cache flushed to database");
        }
        self = null;
    }

    @Override
    public void onEnable() {

        self = this;
        printCon("Starting BeardStat");

        // Read in the metadata file from jar and from data folder
        MetaDataCapture.readData(getResource("metadata.txt"));
        try {
            MetaDataCapture.readData(new FileInputStream(new File(getDataFolder(), "metadata.txt")));
        } catch (FileNotFoundException e) {
            BeardStat.printCon("No External metadata file detected");
        }

        // load language file from jar and from data folder
        try {
            printCon("Loading default language pack");
            LanguagePack.load(getResource("messages.lang"));
            File extLangPack = new File(getDataFolder(), "messages.lang");
            if (extLangPack.exists()) {
                printCon("External language pack detected! Loading...");
                LanguagePack.overlay(new FileInputStream(extLangPack));
            }
        } catch (IOException e1) {
            printCon("Failed to load language pack");
            e1.printStackTrace();
        }

        // run config updater
        updateConfig();
        saveConfig();
        reloadConfig();

        // setup our data provider, fail out if it's not found
        printCon("Connecting to database");
        printCon("Using " + getConfig().getString("stats.database.type") + " Adpater");
        if (getConfig().getString("stats.database.type") == null) {
            printCon("INVALID ADAPTER SELECTED");
            getPluginLoader().disablePlugin(this);
            return;
        }

        IStatDataProvider db = getProvider(getConfig().getConfigurationSection("stats.database"));

        if (db == null) {
            printCon(" Error loading database, disabling plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        // start the player manager
        this.playerStatManager = new PlayerStatManager(db);

        printCon("initializing composite stats");
        // Load the dynamic stats from file
        //loadDynamicStatConfiguration();
        // load custom stat formats from file
        //loadCustomFormats();

        printCon("Registering events and collectors");

        // register event listeners
        // get blacklist, then start and register each type of listener
        List<String> worldList = getConfig().getStringList("stats.blacklist");
        StatBlockListener sbl = new StatBlockListener(worldList, this.playerStatManager);
        StatPlayerListener spl = new StatPlayerListener(worldList, this.playerStatManager);
        StatEntityListener sel = new StatEntityListener(worldList, this.playerStatManager);
        StatVehicleListener svl = new StatVehicleListener(worldList, this.playerStatManager);
        StatCraftListener scl = new StatCraftListener(worldList, this.playerStatManager);
        getServer().getPluginManager().registerEvents(sbl, this);
        getServer().getPluginManager().registerEvents(spl, this);
        getServer().getPluginManager().registerEvents(sel, this);
        getServer().getPluginManager().registerEvents(svl, this);
        getServer().getPluginManager().registerEvents(scl, this);

        // start Database flusher.

        this.saveTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new DbFlusher(), 2400L, 2400L);

        printCon("Loading commands");
        getCommand("stats").setExecutor(new StatCommand(this.playerStatManager));
        getCommand("played").setExecutor(new playedCommand(this.playerStatManager));
        getCommand("statpage").setExecutor(new StatPageCommand(this));
        getCommand("laston").setExecutor(new LastOnCommand(this.playerStatManager));
        getCommand("beardstatdebug").setExecutor(this.playerStatManager);
        getCommand("statadmin").setExecutor(new StatAdmin(this.playerStatManager));

        printCon("loading any players already online");// Fix people being dumb
                                                       // and /reload-ing
        for (Player player : getServer().getOnlinePlayers()) {

            OnlineTimeManager.setRecord(player);
        }

        // Enabled metrics
        Metrics metrics;
        try {
            metrics = new Metrics(this);

            metrics.addCustomData(new Plotter(getConfig().getString("stats.database.type").toLowerCase()) {

                @Override
                public int getValue() {
                    return 1;
                }

            });// record database type

            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printCon("BeardStat Loaded");
    }

    /**
     * Update config as needed.
     */
    private void updateConfig() {

        // convert old world lists over to blacklist (introduced. 0.4.7 - Honey)
        if (getConfig().contains("stats.worlds")) {
            printCon("Moving blacklist to new location");
            getConfig().set("stats.blacklist", getConfig().getStringList("stats.worlds"));
            getConfig().set("stats.worlds", null);
        }

        // Standard defaults updater
        if (!new File(getDataFolder(), "config.yml").exists()) {
            printCon("Writing default config file to disk.");
            getConfig().set("stats.configversion", null);
            getConfig().options().copyDefaults(true);
        }
        // update config if nessecary
        if (getConfig().getInt("stats.configversion", 0) < Integer.parseInt("${project.config.version}")) {

            printCon("Updating config to include newest configuration options");
            getConfig().set("stats.configversion", null);
            getConfig().options().copyDefaults(true);

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

    /**
     * Flush cache of player stats to database at regular intervals
     * 
     * @author James
     * 
     */
    public class DbFlusher implements Runnable {

        @Override
        public void run() {
            if (getConfig().getBoolean("general.verbose", false)) {
                BeardStat.printCon("Flushing to database.");
            }

            BeardStat.this.playerStatManager.saveCache();
            BeardStat.this.playerStatManager.flush();
            if (getConfig().getBoolean("general.verbose", false)) {
                BeardStat.printCon("flush completed");
            }
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
    public static void mysqlError(SQLException e) {
        self.getLogger().severe("=========================================");
        self.getLogger().severe("|             DATABASE ERROR            |");
        self.getLogger().severe("=========================================");
        self.getLogger().severe("An error occured while trying to connect to the BeardStat database");
        self.getLogger().severe("Mysql error code: " + e.getErrorCode());

        switch (e.getErrorCode()) {
        case 1042:
            self.getLogger().severe("Cannot find hostname provided, check spelling of hostname in config file");
            break;
        case 1044:
        case 1045:
            self.getLogger()
                    .severe("Cannot connect to database, check user credentials, database exists and that user is able to log in from this machine");
            break;
        case 1049:
            self.getLogger()
                    .severe("Cannot locate database, check you spelt database name correctly and username has access rights from this machine.");
            break;

        default:
            self.getLogger()
                    .severe("Error code ["
                            + e.getErrorCode()
                            + "] not found (or not supplied!), either check the error code online, or post on the dev.bukkit.org/server-mods/beardstat page");
            self.getLogger().severe("Exception Detail:");
            self.getLogger().severe(e.getMessage());
            break;
        }

        // dump stack trace if in verbose mode
        if (self.getConfig().getBoolean("general.verbose", false)) {
            self.getLogger().severe("=========================================");
            self.getLogger().severe("            Begin error dump             ");
            self.getLogger().severe("=========================================");
            e.printStackTrace();
            self.getLogger().severe("=========================================");
            self.getLogger().severe("             End error dump              ");
            self.getLogger().severe("=========================================");
        }

    }

    /**
     * Load custom stats from config custom stats use a formula to manipulate
     * other stats.
     */
    private void loadDynamicStatConfiguration() {

        for (String cstat : getConfig().getStringList("customstats")) {

            String[] i = cstat.split("\\=");
            EntityStatBlob.addDynamicStat(i[0].trim(), i[1].trim());

        }

        for (String cstat : getConfig().getStringList("savedcustomstats")) {

            String[] i = cstat.split("\\=");
            EntityStatBlob.addDynamicSavedStat(i[0].trim(), i[1].trim());

        }

    }

    /**
     * Load custom formats from config formats based on String.Format
     */
    private void loadCustomFormats() {
        for (String format : getConfig().getStringList("customformats")) {
            String stat = format.split(":")[0];
            String formating = format.replace(stat + ":", "");
            FormatFactory.addStringFormat(stat.split("\\.")[0], stat.split("\\.")[1], formating);
        }
    }

    /**
     * Load a data provider from config
     * 
     * @param config
     * @return
     */
    private IStatDataProvider getProvider(ConfigurationSection config) {
        IStatDataProvider db = null;
        // MySQL provider
        if (config.getString("type").equalsIgnoreCase("mysql")) {
            try {
                db = new MysqlStatDataProvider(config.getString("host"), config.getInt("port", 3306),
                        config.getString("database"), config.getString("prefix"), config.getString("username"),
                        config.getString("password"));
            } catch (SQLException e) {
                mysqlError(e);
                db = null;
            }
        }
        // SQLite provider
        if (config.getString("type").equalsIgnoreCase("sqlite")) {
            try {
                db = new SQLiteStatDataProvider(new File(getDataFolder(), "stats.db").toString());
            } catch (SQLException e) {
                e.printStackTrace();
                db = null;
            }

        }

        // In memory provider
        if (config.getString("type").equalsIgnoreCase("memory")) {
            try {
                db = new SQLiteStatDataProvider(":memory:");
            } catch (SQLException e) {
                e.printStackTrace();
                db = null;
            }
        }

        // File provider, kept for alert message, remove in 0.7
        if (config.getString("type").equalsIgnoreCase("file")) {
            BeardStat
                    .printCon("FILE DRIVER NO LONGER SUPPORTED, PLEASE TRANSFER TO SQLITE/MYSQL IN PREVIOUS VERSION BEFORE LOADING");
        }

        // transfer provider, calls method again to load handlers for transfer
        if (config.getString("type").equalsIgnoreCase("transfer")) {
            IStatDataProvider _old = getProvider(getConfig().getConfigurationSection("stats.transfer.old"));
            IStatDataProvider _new = getProvider(getConfig().getConfigurationSection("stats.transfer.new"));
            BeardStat.printCon("Initiating transfer of stats, this may take a while");
            new TransferDataProvider(_old, _new);
            db = _new;
        }
        return db;
    }

    /**
     * Utility method to load SQL commands from files in JAR
     * 
     * @param type
     *            extension of file to load, if not found will try load sql type
     *            (which is the type for MySQL syntax)
     * @param filename
     *            file to load, minus extension
     * @param prefix
     *            table prefix, replaces ${PREFIX} in loaded files
     * @return SQL commands loaded from file.
     */
    public String readSQL(String type, String filename, String prefix) {
        BeardStat.printDebugCon("Loading SQL: " + filename);
        InputStream is = getResource(filename + "." + type);
        if (is == null) {
            is = getResource(filename + ".sql");
        }
        if (is == null) {
            throw new IllegalArgumentException("No SQL file found with name " + filename);
        }
        String sql = new Scanner(is).useDelimiter("\\Z").next().replaceAll("\\Z", "").replaceAll("\\n|\\r", "");

        return sql.replaceAll("\\$\\{PREFIX\\}", prefix);

    }

}
