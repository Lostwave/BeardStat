package com.tehbeard.beardstat.dataproviders;

import com.google.gson.stream.JsonReader;
import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.StatConfiguration;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.containers.documents.DocumentRegistry;
import com.tehbeard.beardstat.containers.documents.IStatDocument;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class MysqlStatDataProvider extends JDBCStatDataProvider {

    //Document meta scripts
    public static final String SQL_DOC_META_INSERT = "sql/doc/meta/metaInsert";
    public static final String SQL_DOC_META_LOCK = "sql/doc/meta/metaLock";
    public static final String SQL_DOC_META_UPDATE = "sql/doc/meta/metaUpdate";
    public static final String SQL_DOC_META_DELETE = "sql/doc/meta/metaDelete";
    public static final String SQL_DOC_META_SELECT = "sql/doc/meta/metaSelect";
    public static final String SQL_DOC_META_POLL = "sql/doc/meta/metaPoll";
    //Document store scripts
    public static final String SQL_DOC_STORE_INSERT = "sql/doc/store/storeInsert";
    public static final String SQL_DOC_STORE_SELECT = "sql/doc/store/storeSelect";
    public static final String SQL_DOC_STORE_POLL = "sql/doc/store/storePoll";
    public static final String SQL_DOC_STORE_DELETE = "sql/doc/store/storeDelete";
    public static final String SQL_DOC_STORE_PURGE = "sql/doc/store/storePurge";
    //meta
    private PreparedStatement stmtMetaInsert;
    private PreparedStatement stmtMetaLock;
    private PreparedStatement stmtMetaUpdate;
    private PreparedStatement stmtMetaDelete;
    private PreparedStatement stmtMetaSelect;
    private PreparedStatement stmtMetaPoll;
    //docs
    private PreparedStatement stmtDocInsert;
    private PreparedStatement stmtDocSelect;
    private PreparedStatement stmtDocPoll;
    private PreparedStatement stmtDocDelete;
    private PreparedStatement stmtDocPurge;

    
    public MysqlStatDataProvider(DbPlatform platform, DatabaseConfiguration config) throws SQLException {

        super(platform, "sql", "com.mysql.jdbc.Driver", config);
        this.connectionUrl = String.format("jdbc:mysql://%s:%s/%s", config.host, config.port, config.database);
        this.connectionProperties.put("user", config.username);
        this.connectionProperties.put("password", config.password);
        this.connectionProperties.put("autoReconnect", "true");

        initialise();
    }

    @Override
    protected void prepareStatements() {
        super.prepareStatements();
        //Meta
        stmtMetaInsert = getStatementFromScript(SQL_DOC_META_INSERT);
        stmtMetaUpdate = getStatementFromScript(SQL_DOC_META_UPDATE);
        stmtMetaDelete = getStatementFromScript(SQL_DOC_META_DELETE);
        stmtMetaSelect = getStatementFromScript(SQL_DOC_META_SELECT);
        stmtMetaPoll = getStatementFromScript(SQL_DOC_META_POLL);
        //Store
        stmtDocInsert = getStatementFromScript(SQL_DOC_STORE_INSERT);
        stmtDocSelect = getStatementFromScript(SQL_DOC_STORE_SELECT);
        stmtDocPoll = getStatementFromScript(SQL_DOC_STORE_POLL);
        stmtDocDelete = getStatementFromScript(SQL_DOC_STORE_DELETE);
        stmtDocPurge = getStatementFromScript(SQL_DOC_STORE_PURGE);

    }

    @Override
    public void generateBackup(File file) {
        platform.getLogger().log(Level.INFO, "Creating backup of database at {0}", file.toString());
        try {
            FileOutputStream fw = new FileOutputStream(file);
            GZIPOutputStream gos = new GZIPOutputStream(fw) {
                {
                    def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };
            dumpToBuffer(new BufferedWriter(new OutputStreamWriter(gos)));
        } catch (IOException ex) {
            Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private ResultSet query(String sql) throws SQLException {
        return conn.prepareStatement(sql).executeQuery();
    }

    private void dumpToBuffer(BufferedWriter buff) {
        try {

            String version = "-- If restoring to this backup, set stats.database.sql_db_version to : " + config.latestVersion;
            StringBuilder sb = new StringBuilder();
            ResultSet rs = query("SHOW FULL TABLES WHERE Table_type != 'VIEW'");
            while (rs.next()) {
                String tbl = rs.getString(1);
                if (!tbl.startsWith(tblPrefix)) {
                    continue;
                }
                sb.append(version).append("\n");
                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- Table structure for `").append(tbl)
                        .append("`\n-- ----------------------------\n");
                sb.append("DROP TABLE IF EXISTS `").append(tbl).append("`;\n");
                ResultSet rs2 = query("SHOW CREATE TABLE `" + tbl + "`");
                rs2.next();
                String crt = rs2.getString(2) + ";";
                sb.append(crt).append("\n");
                sb.append("\n");
                sb.append("-- ----------------------------\n").append("-- Records for `").append(tbl).append("`\n-- ----------------------------\n");

                ResultSet rss = query("SELECT * FROM " + tbl);
                while (rss.next()) {
                    int colCount = rss.getMetaData().getColumnCount();
                    if (colCount > 0) {
                        sb.append("INSERT INTO ").append(tbl).append(" VALUES(");

                        for (int i = 0; i < colCount; i++) {
                            if (i > 0) {
                                sb.append(",");
                            }
                            String s = "";
                            try {
                                s += "'";
                                s += rss.getObject(i + 1).toString();
                                s += "'";
                            } catch (Exception e) {
                                s = "NULL";
                            }
                            sb.append(s);
                        }
                        sb.append(");\n");
                        buff.append(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            }

            ResultSet rs2 = query("SHOW FULL TABLES WHERE Table_type = 'VIEW'");
            while (rs2.next()) {
                String tbl = rs2.getString(1);

                sb.append("\n");
                sb.append("-- ----------------------------\n")
                        .append("-- View structure for `").append(tbl)
                        .append("`\n-- ----------------------------\n");
                sb.append("DROP VIEW IF EXISTS `").append(tbl).append("`;\n");
                ResultSet rs3 = query("SHOW CREATE VIEW `" + tbl + "`");
                rs3.next();
                String crt = rs3.getString(2) + ";";
                sb.append(crt).append("\n");
            }

            buff.flush();
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DocumentFile pullDocument(ProviderQuery query, String domain, String key) {
        ProviderQueryResult result = getSingleEntity(query);
        DocumentFile file = null;

        if (result == null) {
            throw new IllegalArgumentException("No entity found.");
        }

        int entityId = result.dbid;
        int domainId = getDomain(domain).getDbId();

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stmtMetaSelect.setInt(1, entityId);
            stmtMetaSelect.setInt(2, domainId);
            stmtMetaSelect.setString(3, key);
            
            platform.getLogger().log(Level.FINE, "eid: {0}, domainId: {1}, key: {2}", new Object[]{entityId, domainId, key});
            ResultSet rs = stmtMetaSelect.executeQuery();

            if (rs.next()) {
                int docId = rs.getInt("documentId");
                String curRev = rs.getString("curRevision");
                rs.close();

                //Get document
                stmtDocSelect.setInt(1, docId);
                stmtDocSelect.setString(2, curRev);
                rs = stmtDocSelect.executeQuery();

                if (rs.next()) {
                    //`parentRev`, `added`,`document`
                    String parentRev = rs.getString("parentRev");
                    Timestamp added = rs.getTimestamp("added");
                    Blob document = rs.getBlob("document");
                    JsonReader jsr = new JsonReader(new InputStreamReader(document.getBinaryStream()));
                    
                    
                    platform.getLogger().fine(new String(document.getBytes(1,(int)document.length())));
                    
                    IStatDocument fromJson = DocumentRegistry.instance().fromJson(jsr, IStatDocument.class);
                    
                    file = new DocumentFile(curRev, parentRev, domain, key, fromJson, added);
                    
                }
                rs.close();
            } else {
                platform.getLogger().info("No entry found");
                rs.close();
            }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return file;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getDocumentKeysInDomain(ProviderQuery query, String domain) {
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
}
