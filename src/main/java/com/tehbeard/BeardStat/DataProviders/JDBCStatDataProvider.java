package com.tehbeard.BeardStat.DataProviders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.MatchResult;

import net.dragonzone.promise.Deferred;
import net.dragonzone.promise.Promise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.DataProviders.metadata.CategoryMeta;
import com.tehbeard.BeardStat.DataProviders.metadata.DomainMeta;
import com.tehbeard.BeardStat.NoRecordFoundException;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.DataProviders.metadata.StatisticMeta;
import com.tehbeard.BeardStat.DataProviders.metadata.StatisticMeta.Formatting;
import com.tehbeard.BeardStat.DataProviders.metadata.WorldMeta;
import com.tehbeard.BeardStat.utils.HumanNameGenerator;
import com.tehbeard.utils.misc.CallbackMatcher;
import com.tehbeard.utils.misc.CallbackMatcher.Callback;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * base class for JDBC based data providers Allows easy development of data
 * providers that make use of JDBC
 *
 * @author James
 *
 */
public abstract class JDBCStatDataProvider implements IStatDataProvider {

    // Database connection
    protected Connection conn;
    // Load components
    protected PreparedStatement loadDomainsList;
    protected PreparedStatement loadWorldsList;
    protected PreparedStatement loadCategoriesList;
    protected PreparedStatement loadStatisticsList;
    // save components
    protected PreparedStatement saveDomain;
    protected PreparedStatement saveWorld;
    protected PreparedStatement saveCategory;
    protected PreparedStatement saveStatistic;
    // Load data from db
    protected PreparedStatement loadEntity;
    protected PreparedStatement loadEntityData;
    // save to db
    protected PreparedStatement saveEntity;
    protected PreparedStatement saveEntityData;
    // Maintenance
    protected PreparedStatement keepAlive;
    protected PreparedStatement listEntities;
    protected PreparedStatement deleteEntity;
    protected PreparedStatement createTable;
    private HashMap<String, EntityStatBlob> writeCache = new HashMap<String, EntityStatBlob>();
    // default connection related configuration
    protected String connectionUrl = "";
    protected Properties connectionProperties = new Properties();
    protected String tblPrefix = "stats";
    private String type = "sql";
    // ID Cache
    private Map<String, DomainMeta> domainMetaMap = new HashMap<String, DomainMeta>();
    private HashMap<String, WorldMeta> worldMetaMap = new HashMap<String, WorldMeta>();
    private HashMap<String, CategoryMeta> categoryMetaMap = new HashMap<String, CategoryMeta>();
    private HashMap<String, StatisticMeta> statisticMetaMap = new HashMap<String, StatisticMeta>();
    // Write queue
    private ExecutorService loadQueue = Executors.newSingleThreadExecutor();
    protected BeardStat plugin;

    public JDBCStatDataProvider(BeardStat plugin, String type, String driverClass) {
        this.type = type;
        this.plugin = plugin;
        try {
            Class.forName(driverClass);// load driver
        } catch (ClassNotFoundException e) {
            plugin.printCon("JDBC " + driverClass + "Library not found!");
        }
    }

    protected void initialise() throws SQLException {
        createConnection();

        checkForMigration();

        checkAndMakeTable();
        prepareStatements();

        executeScript("sql/maintenence/metadata/category");
        executeScript("sql/maintenence/metadata/statistic");
        

        cacheComponents();
    }

    /**
     * checks config in data folder against default (current versions config) If
     * version conflicts it will attempt to run migration scripts sequentially
     * to upgrade
     *
     * @throws SQLException
     */
    private void checkForMigration() throws SQLException {
        int latestVersion = this.plugin.getConfig().getDefaults().getInt("stats.database.sql_db_version");

        if (!this.plugin.getConfig().isSet("stats.database.sql_db_version")) {
            this.plugin.getConfig().set("stats.database.sql_db_version", 1);
            this.plugin.saveConfig();
        }
        int installedVersion = this.plugin.getConfig().getInt("stats.database.sql_db_version", 1);

        if (installedVersion > latestVersion) {
            throw new RuntimeException("database version > this one, You appear to be running an out of date plugin!");
        }

        if (installedVersion < latestVersion) {
            // Swap to transaction based mode,
            // Execute each migration script in sequence,
            // commit if successful,
            // rollback and error out if not
            // Should support partial recovery of migration effort, saves
            // current version if successful commit

            this.plugin.printCon("Updating database to latest version");
            this.plugin.printCon("Your database: " + installedVersion + " latest: " + latestVersion);
            for (int i = 0; i < 3; i++) {
                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.RED + "WARNING: DATABASE MIGRATION WILL TAKE A LONG TIME ON LARGE DATABASES.");
            }
            this.conn.setAutoCommit(false);

            int migrateToVersion = 0;
            try {

                for (migrateToVersion = installedVersion + 1; migrateToVersion <= latestVersion; migrateToVersion++) {

                    Map<String, String> k = new HashMap<String, String>();
                    k.put("OLD_TBL", this.plugin.getConfig().getString("stats.database.table", ""));

                    executeScript("sql/maintenence/migration/migrate." + migrateToVersion, k);

                    this.conn.commit();
                    this.plugin.getConfig().set("stats.database.sql_db_version", migrateToVersion);
                    this.plugin.saveConfig();

                }

            } catch (SQLException e) {
                this.plugin.printCon("An error occured while migrating the database, initiating rollback to version "
                        + (migrateToVersion - 1));
                this.plugin.printCon("Begining database error dump");
                // plugin.mysqlError(e);
                this.conn.rollback();
                throw e;

            }

            this.plugin.printCon("Migration successful");
            this.conn.setAutoCommit(true);

        }
    }

    /**
     * Connection to the database.
     *
     * @throws SQLException
     */
    private void createConnection() {

        this.plugin.printCon("Connecting....");

        try {
            this.conn = DriverManager.getConnection(this.connectionUrl, this.connectionProperties);

            // conn.setAutoCommit(false);
        } catch (SQLException e) {
            this.plugin.mysqlError(e);
            this.conn = null;
        }

    }

    /**
     *
     * @return
     */
    private synchronized boolean checkConnection() {
        this.plugin.printDebugCon("Checking connection");
        try {
            if ((this.conn == null) || !this.conn.isValid(0)) {
                this.plugin.printDebugCon("Something is derp, rebooting connection.");
                createConnection();
                if (this.conn != null) {
                    this.plugin.printDebugCon("Rebuilding statements");
                    prepareStatements();
                } else {
                    this.plugin.printDebugCon("Reboot failed!");
                }

            }
        } catch (SQLException e) {
            this.conn = null;
            return false;
        } catch (AbstractMethodError e) {
        }

        return this.conn != null;
    }

    protected void checkAndMakeTable() {
        this.plugin.printCon("Constructing table as needed.");

        try {
            executeScript("sql/maintenence/create.tables");

        } catch (SQLException e) {
            this.plugin.mysqlError(e);
        }
    }

    /**
     * Load statements from jar
     */
    protected void prepareStatements() {
        try {
            this.plugin.printDebugCon("Preparing statements");

            this.loadEntity = getStatementFromScript("sql/load/getEntity");
            this.loadEntityData = getStatementFromScript("sql/load/getEntityData");

            // Load components
            this.loadDomainsList = getStatementFromScript("sql/load/components/getDomains");
            this.loadWorldsList = getStatementFromScript("sql/load/components/getWorlds");
            this.loadCategoriesList = getStatementFromScript("sql/load/components/getCategories");
            this.loadStatisticsList = getStatementFromScript("sql/load/components/getStatistics");

            // save components
            this.saveDomain = getStatementFromScript("sql/save/components/saveDomain", Statement.RETURN_GENERATED_KEYS);
            this.saveWorld = getStatementFromScript("sql/save/components/saveWorld", Statement.RETURN_GENERATED_KEYS);
            this.saveCategory = getStatementFromScript("sql/save/components/saveCategory",
                    Statement.RETURN_GENERATED_KEYS);
            this.saveStatistic = getStatementFromScript("sql/save/components/saveStatistic",
                    Statement.RETURN_GENERATED_KEYS);

            // save to db
            this.saveEntity = getStatementFromScript("sql/save/saveEntity", Statement.RETURN_GENERATED_KEYS);
            this.saveEntityData = getStatementFromScript("sql/save/saveStat");

            // Maintenance
            this.keepAlive = getStatementFromScript("sql/maintenence/keepAlive");
            this.listEntities = getStatementFromScript("sql/maintenence/listEntities");
            // deleteEntity =
            // conn.prepareStatement(plugin.readSQL(type,"sql/maintenence/deletePlayerFully",
            // tblPrefix));

            this.plugin.printDebugCon("Set player stat statement created");
            this.plugin.printCon("Initaised MySQL Data Provider.");
        } catch (SQLException e) {
            this.plugin.mysqlError(e);
        }
    }

    private void cacheComponents() {
        try {
            //Domains
            ResultSet rs = loadDomainsList.executeQuery();
            while (rs.next()) {
                DomainMeta dm = new DomainMeta(
                        rs.getInt("domainId"),
                        rs.getString("domain"));
                domainMetaMap.put(rs.getString("domain"), dm);
            }
            rs.close();

            //Worlds
            rs = loadWorldsList.executeQuery();
            while (rs.next()) {
                WorldMeta wm = new WorldMeta(
                        rs.getInt("worldId"),
                        rs.getString("world"),
                        rs.getString("name"));
                worldMetaMap.put(rs.getString("world"), wm);
            }
            rs.close();

            //Worlds
            rs = loadCategoriesList.executeQuery();
            while (rs.next()) {
                CategoryMeta cm = new CategoryMeta(
                        rs.getInt("categoryId"),
                        rs.getString("category"),
                        rs.getString("statwrapper"));
                categoryMetaMap.put(rs.getString("category"), cm);
            }
            rs.close();

            //Worlds
            rs = loadStatisticsList.executeQuery();
            while (rs.next()) {
                StatisticMeta sm = new StatisticMeta(
                        rs.getInt("statisticId"),
                        rs.getString("statistic"),
                        rs.getString("name"),
                        Formatting.valueOf(rs.getString("formatting")));
                statisticMetaMap.put(rs.getString("statistic"), sm);
            }
            rs.close();

        } catch (SQLException e) {
            this.plugin.mysqlError(e);
        }
    }

    @Override
    public Promise<EntityStatBlob> pullStatBlob(String player, String type) {
        return pullStatBlob(player, type, true);
    }

    @Override
    public Promise<EntityStatBlob> pullStatBlob(final String player, final String type, final boolean create) {

        final Deferred<EntityStatBlob> promise = new Deferred<EntityStatBlob>();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!checkConnection()) {
                        JDBCStatDataProvider.this.plugin.printCon("Database connection error!");
                        promise.reject(new SQLException("Error connecting to database"));
                        return;
                    }
                    long t1 = (new Date()).getTime();

                    // Ok, try to get entity from database
                    JDBCStatDataProvider.this.loadEntity.setString(1, player);
                    JDBCStatDataProvider.this.loadEntity.setString(2, type);

                    ResultSet rs = JDBCStatDataProvider.this.loadEntity.executeQuery();
                    EntityStatBlob pb = null;

                    if (!rs.next()) {
                        if (!create) {
                            promise.reject(new NoRecordFoundException());// Fail
                            // out
                            // here
                            // instead.
                            return;
                        }

                        // No player found! Let's create an entry for them!
                        rs.close();
                        rs = null;
                        JDBCStatDataProvider.this.saveEntity.setString(1, player);
                        JDBCStatDataProvider.this.saveEntity.setString(2, type);
                        JDBCStatDataProvider.this.saveEntity.executeUpdate();
                        rs = JDBCStatDataProvider.this.saveEntity.getGeneratedKeys();
                        rs.next();// load player id

                    }

                    // make the player object, close out result set.
                    pb = new EntityStatBlob(player, rs.getInt(1), "player");
                    rs.close();
                    rs = null;

                    // load all stats data
                    JDBCStatDataProvider.this.loadEntityData.setInt(1, pb.getEntityID());
                    JDBCStatDataProvider.this.loadEntityData.setInt(1, pb.getEntityID());
                    JDBCStatDataProvider.this.plugin.printDebugCon("executing "
                            + JDBCStatDataProvider.this.loadEntityData);
                    rs = JDBCStatDataProvider.this.loadEntityData.executeQuery();

                    while (rs.next()) {
                        // `domain`,`world`,`category`,`statistic`,`value`
                        IStat ps = pb.getStat(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                        ps.setValue(rs.getInt(5));
                        ps.clearArchive();
                    }
                    rs.close();

                    JDBCStatDataProvider.this.plugin.printDebugCon("time taken to retrieve: "
                            + ((new Date()).getTime() - t1) + " Milliseconds");

                    promise.resolve(pb);
                    return;
                } catch (SQLException e) {
                    JDBCStatDataProvider.this.plugin.mysqlError(e);
                    promise.reject(e);
                }

            }
        };

        this.loadQueue.execute(run);

        return promise;

    }

    @Override
    public void pushStatBlob(EntityStatBlob player) {

        synchronized (this.writeCache) {

            EntityStatBlob copy = player.cloneForArchive();

            if (!this.writeCache.containsKey(player.getName())) {
                this.writeCache.put(player.getName(), copy);
            }
        }

    }
    private Runnable flush = new Runnable() {
        @Override
        public void run() {
            synchronized (JDBCStatDataProvider.this.writeCache) {
                try {
                    JDBCStatDataProvider.this.keepAlive.execute();
                } catch (SQLException e1) {
                }

                if (!checkConnection()) {
                    Bukkit.getConsoleSender()
                            .sendMessage(
                            ChatColor.RED
                            + "Could not restablish connection, will try again later, WARNING: CACHE WILL GROW WHILE THIS HAPPENS");
                } else {
                    JDBCStatDataProvider.this.plugin.printDebugCon("Saving to database");
                    for (Entry<String, EntityStatBlob> entry : JDBCStatDataProvider.this.writeCache
                            .entrySet()) {
                        try {
                            EntityStatBlob pb = entry.getValue();

                            JDBCStatDataProvider.this.saveEntityData.clearBatch();
                            for (IStat stat : pb.getStats()) {
                                JDBCStatDataProvider.this.saveEntityData.setInt(1,
                                        pb.getEntityID());
                                JDBCStatDataProvider.this.saveEntityData.setInt(2, getDomain(stat.getDomain()).getDbId());
                                JDBCStatDataProvider.this.saveEntityData.setInt(2, getWorld(stat.getWorld()).getDbId());
                                JDBCStatDataProvider.this.saveEntityData.setInt(2, getCategory(stat.getCategory()).getDbId());
                                JDBCStatDataProvider.this.saveEntityData.setInt(2, getStatistic(stat.getStatistic()).getDbId());


                                JDBCStatDataProvider.this.saveEntityData.setInt(6,
                                        stat.getValue());

                                JDBCStatDataProvider.this.saveEntityData.addBatch();
                            }
                            JDBCStatDataProvider.this.saveEntityData.executeBatch();

                        } catch (SQLException e) {
                            JDBCStatDataProvider.this.plugin.mysqlError(e);
                            checkConnection();
                        }
                    }
                    JDBCStatDataProvider.this.plugin.printDebugCon("Clearing write cache");
                    JDBCStatDataProvider.this.writeCache.clear();
                }
            }

        }
    };

    @Override
    public void flushSync() {
        this.plugin.printCon("Flushing in main thread! Game will lag!");
        this.flush.run();
        this.plugin.printCon("Flushed!");
    }

    @Override
    public void flush() {

        new Thread(this.flush).start();
    }

    @Override
    public void deleteStatBlob(String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasStatBlob(String player) {
        try {
            this.loadEntity.clearParameters();
            this.loadEntity.setString(1, player);
            this.loadEntity.setString(2, "player");

            ResultSet rs = this.loadEntity.executeQuery();
            boolean found = rs.next();
            rs.close();
            return found;

        } catch (SQLException e) {
            checkConnection();
        }
        return false;
    }

    @Override
    public List<String> getStatBlobsHeld() {
        List<String> list = new ArrayList<String>();
        try {
            this.listEntities.setString(1, "player");

            ResultSet rs = this.listEntities.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();

        } catch (SQLException e) {
            checkConnection();
        }
        return list;
    }

    /**
     * Execute a script
     *
     * @param scriptName name of script (sql/load/loadEntity)
     * @param keys (list of non-standard keys ${KEY_NAME} to replace)
     *
     * Scripts support # for status comments and #!/script/path/here to execute
     * subscripts
     * @throws SQLException
     */
    public void executeScript(String scriptName) throws SQLException {
        executeScript(scriptName, new HashMap<String, String>());
    }

    public void executeScript(String scriptName, final Map<String, String> keys) throws SQLException {
        CallbackMatcher matcher = new CallbackMatcher("\\$\\{([A-Za-z0-9_]*)\\}");

        String[] sqlStatements = this.plugin.readSQL(this.type, scriptName, this.tblPrefix).split("\\;");
        for (String s : sqlStatements) {
            String statement = matcher.replaceMatches(s, new Callback() {
                @Override
                public String foundMatch(MatchResult result) {
                    if (keys.containsKey(result.group(1))) {
                        return keys.get(result.group(1));
                    }
                    return "";
                }
            });

            if (statement.startsWith("#!")) {
                String subScript = statement.substring(2);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Executing : " + subScript);
                executeScript(subScript, keys);
                continue;
            } else if (statement.startsWith("#")) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Status : " + statement.substring(1));
            } else {
                this.conn.prepareStatement(statement).execute();
            }
        }

    }

    public PreparedStatement getStatementFromScript(String scriptName, int flags) throws SQLException {
        return this.conn.prepareStatement(this.plugin.readSQL(this.type, scriptName, this.tblPrefix), flags);
    }

    public PreparedStatement getStatementFromScript(String scriptName) throws SQLException {
        return this.conn.prepareStatement(this.plugin.readSQL(this.type, scriptName, this.tblPrefix));
    }

    @Override
    public DomainMeta getDomain(String gameTag) {
        String qGameTag = sanitizeTag(gameTag);
        if (!domainMetaMap.containsKey(qGameTag)) {
            try {

                saveDomain.setString(1, qGameTag);
                saveDomain.execute();
                ResultSet rs = saveDomain.getGeneratedKeys();
                rs.next();
                domainMetaMap.put(gameTag, new DomainMeta(rs.getInt(1), gameTag));
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return domainMetaMap.get(qGameTag);
    }

    @Override
    public WorldMeta getWorld(String gameTag) {

        if (!worldMetaMap.containsKey(gameTag)) {
            try {
                saveWorld.setString(1, gameTag);
                saveWorld.setString(2, gameTag.replaceAll("_", " "));
                saveWorld.execute();
                ResultSet rs = saveWorld.getGeneratedKeys();
                rs.next();
                worldMetaMap.put(gameTag, new WorldMeta(rs.getInt(1), gameTag, gameTag.replaceAll("_", " ")));
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return worldMetaMap.get(gameTag);
    }

    @Override
    public CategoryMeta getCategory(String gameTag) {
        if (!categoryMetaMap.containsKey(gameTag)) {
            try {
                saveCategory.setString(1, gameTag);
                saveCategory.setString(2, gameTag.replaceAll("_", " "));
                saveCategory.execute();
                ResultSet rs = saveCategory.getGeneratedKeys();
                rs.next();
                categoryMetaMap.put(gameTag, new CategoryMeta(rs.getInt(1), gameTag, gameTag.replaceAll("_", " ")));
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return categoryMetaMap.get(gameTag);
    }

    @Override
    public StatisticMeta getStatistic(String gameTag) {
        if (!statisticMetaMap.containsKey(gameTag)) {
            try {
                saveStatistic.setString(1, gameTag);
                saveStatistic.setString(2, HumanNameGenerator.getNameOf(gameTag));
                saveStatistic.execute();
                ResultSet rs = saveStatistic.getGeneratedKeys();
                rs.next();
                statisticMetaMap.put(gameTag, new StatisticMeta(rs.getInt(1), gameTag, gameTag.replaceAll("_", " "), Formatting.none));
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return statisticMetaMap.get(gameTag);
    }

    private String sanitizeTag(String gameTag) {
        String truncatedName = gameTag.toLowerCase();
        if (truncatedName.length() > 64) {
            truncatedName = truncatedName.substring(0, 64);
        }
        return truncatedName;
    }
}