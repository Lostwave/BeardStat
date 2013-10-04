/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat;

import me.tehbeard.utils.syringe.configInjector.InjectConfig;

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
}
