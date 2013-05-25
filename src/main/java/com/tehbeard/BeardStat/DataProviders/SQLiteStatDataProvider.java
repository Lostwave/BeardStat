package com.tehbeard.BeardStat.DataProviders;

import java.sql.SQLException;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {

    public SQLiteStatDataProvider(String filename) throws SQLException {

        super("sqlite", "org.sqlite.JDBC");

        this.connectionUrl = String.format("jdbc:sqlite:%s", filename);
        this.tblPrefix = "stats";
        initialise();

    }

}
