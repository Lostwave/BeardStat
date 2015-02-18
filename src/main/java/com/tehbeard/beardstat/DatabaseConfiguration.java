package com.tehbeard.beardstat;

import com.tehbeard.beardstat.cfg.InjectConfig;

/**
 *
 * @author James
 */
public class DatabaseConfiguration {

    @InjectConfig("type")
    public String databaseType;
    @InjectConfig("sql_db_version")
    public int version;
    @InjectConfig("host")
    public String host;
    @InjectConfig("username")
    public String username;
    @InjectConfig("password")
    public String password;
    @InjectConfig("database")
    public String database;
    @InjectConfig("prefix")
    public String tablePrefix;
    @InjectConfig("port")
    public int port;
    @InjectConfig("backups")
    public boolean backups;
    @InjectConfig("uuidUpdate")
    public boolean runUUIDUpdate;
    
    public int latestVersion;
    
    public DatabaseConfiguration(int latestVersion){
        this.latestVersion = latestVersion;
    }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" + "databaseType=" + databaseType + ", version=" + version + ", host=" + host + ", username=" + username + ", database=" + database + ", tablePrefix=" + tablePrefix + ", port=" + port + ", backups=" + backups + ", latestVersion=" + latestVersion + '}';
    }
    
    
}
