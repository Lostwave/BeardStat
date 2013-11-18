package com.tehbeard.beardstat.dataproviders;

import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import java.io.File;
import org.bukkit.util.FileUtil;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {
    private final String filename;

    public SQLiteStatDataProvider(BeardStat plugin, String filename, DatabaseConfiguration config) throws SQLException {

        super(plugin, "sqlite", "org.sqlite.JDBC",config);

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

    @Override
    public DocumentFile pullDocument(ProviderQuery query, String domain, String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DocumentFile pushDocument(ProviderQuery query, DocumentFile document) throws RevisionMismatchException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteDocument(ProviderQuery query, String domain, String key, String revision) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getDocumentKeysInDomain(ProviderQuery query, String domain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
