package com.tehbeard.BeardStat.DataProviders;

import java.sql.SQLException;

import com.tehbeard.BeardStat.BeardStat;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {

	public SQLiteStatDataProvider(String filename) throws SQLException {
		
		super("com.mysql.jdbc.Driver");
		
		connectionUrl = String.format("jdbc:sqlite:%s",filename);
		
		initialise();
		
		saveEntityData = conn.prepareStatement(BeardStat.self().readSQL("sql/load/saveStat.sqlite.sql", tblConfig));
	}

}
