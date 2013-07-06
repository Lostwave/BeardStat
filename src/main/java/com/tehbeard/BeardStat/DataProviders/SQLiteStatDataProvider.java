package com.tehbeard.BeardStat.DataProviders;

import java.sql.SQLException;

import com.tehbeard.BeardStat.BeardStat;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {

    public SQLiteStatDataProvider(BeardStat plugin, String filename) throws SQLException {

        super(plugin, "sqlite", "org.sqlite.JDBC");

        this.connectionUrl = String.format("jdbc:sqlite:%s", filename);
        this.tblPrefix = "stats";
        initialise();

    }

}
