package com.tehbeard.beardstat.dataproviders;

import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;
import java.io.File;
import org.bukkit.util.FileUtil;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {
    private final String filename;

    public SQLiteStatDataProvider(BeardStat plugin, String filename,boolean backups) throws SQLException {

        super(plugin, "sqlite", "org.sqlite.JDBC",backups);

        this.connectionUrl = String.format("jdbc:sqlite:%s", filename);
        this.tblPrefix = "stats";
        initialise();
        this.filename = filename;

    }

    @Override
    public void generateBackup(File file) {
        FileUtil.copy(new File(filename), file);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
