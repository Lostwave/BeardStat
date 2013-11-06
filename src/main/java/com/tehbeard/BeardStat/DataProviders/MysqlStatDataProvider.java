package com.tehbeard.beardstat.DataProviders;

import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;

public class MysqlStatDataProvider extends JDBCStatDataProvider {

    public MysqlStatDataProvider(BeardStat plugin, String host, int port, String database, String tablePrefix,
            String username, String password) throws SQLException {

        super(plugin, "sql", "com.mysql.jdbc.Driver");
        this.tblPrefix = tablePrefix;

        this.connectionUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        this.connectionProperties.put("user", username);
        this.connectionProperties.put("password", password);
        this.connectionProperties.put("autoReconnect", "true");

        initialise();
    }

}
