package com.tehbeard.beardstat.dataproviders;

import com.google.gson.stream.JsonReader;
import java.sql.SQLException;

import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.documents.DocumentHistory;
import com.tehbeard.beardstat.containers.documents.StatDocument;
import com.tehbeard.beardstat.containers.documents.DocumentRegistry;
import com.tehbeard.beardstat.containers.documents.IStatDocument;
import com.tehbeard.beardstat.containers.documents.docfile.DocumentFile;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
        this.tblPrefix = config.tablePrefix;
        initialise();
    }

    @Override
    protected void prepareStatements() {
        super.prepareStatements();
        //Meta
        stmtMetaInsert = getStatementFromScript(SQL_DOC_META_INSERT, Statement.RETURN_GENERATED_KEYS);
        stmtMetaUpdate = getStatementFromScript(SQL_DOC_META_UPDATE);
        stmtMetaDelete = getStatementFromScript(SQL_DOC_META_DELETE);
        stmtMetaSelect = getStatementFromScript(SQL_DOC_META_SELECT);
        stmtMetaPoll = getStatementFromScript(SQL_DOC_META_POLL);
        //Store
        stmtDocInsert = getStatementFromScript(SQL_DOC_STORE_INSERT, Statement.RETURN_GENERATED_KEYS);
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
    public DocumentFile pullDocument(int entityId, String domain, String key) {
        DocumentFile file = null;

        try {
            boolean acStatus = conn.getAutoCommit();
            //Look for existing document
            ResultSet rs = getDocumentResultSet(entityId, domain, key);

            //if existing document is found, load it.
            if (rs.next()) {
                int docId = getDocumentId(rs);
                String curRev = getCurrentRev(rs);
                rs.close();

                //Get the latest revision
                stmtDocSelect.setInt(1, docId);
                stmtDocSelect.setString(2, curRev);
                rs = stmtDocSelect.executeQuery();
                //If found, grab it.
                if (rs.next()) {
                    //`parentRev`, `added`,`document`
                    String parentRev = rs.getString("parentRev");
                    Timestamp added = rs.getTimestamp("added");
                    Blob document = rs.getBlob("document");
                    JsonReader jsr = new JsonReader(new InputStreamReader(document.getBinaryStream()));


                    platform.getLogger().fine(new String(document.getBytes(1, (int) document.length())));
                    //Load the document
                    IStatDocument fromJson = DocumentRegistry.instance().fromJson(jsr, IStatDocument.class);

                    file = new DocumentFile(curRev, parentRev, domain, key, fromJson, added);

                }
                rs.close();
            } else {
                platform.getLogger().fine("No document entry found.");
                rs.close();
            }
            conn.setAutoCommit(acStatus);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return file;
    }

    public final static int MAX_DOC_SIZE = 16 * 1024 * 1024;//16mb limit

    @Override
    public DocumentFile pushDocument(int entityId, DocumentFile document) throws RevisionMismatchException, DocumentTooLargeException {


        DocumentFile returnDoc = null;

        try {
            boolean isSingleton = document.getDocument().getClass().getAnnotation(StatDocument.class).singleInstance();

            //1) lock meta document record, get headrev revision tag
            ResultSet rs = getDocumentResultSet(entityId, document.getDomain(), document.getKey());

            //If we found the document
            if (rs.next()) {
                int docId = getDocumentId(rs);
                String headRev = getCurrentRev(rs);
                rs.close();


                IStatDocument doc = document.getDocument();
                if (!headRev.equalsIgnoreCase(document.getRevision())) {
                    //TODO - Check this actually works.
                    DocumentFile dbDoc = pullDocument(entityId, document.getDomain(), document.getKey());
                    doc = doc.mergeDocument(dbDoc);

                }


                //2) Generate JSON

                byte[] docData = DocumentRegistry.instance().toJson(document.getDocument(),DocumentRegistry.getSerializeAs(document.getDocument().getClass())).getBytes();

                if (docData.length > MAX_DOC_SIZE) {
                    throw new DocumentTooLargeException("Document exceeds max size.");//TODO - Change to a specific exception for this usecase
                }

                //3) Generate new revision tag.
                MessageDigest digest = MessageDigest.getInstance("SHA1");
                String newRevision = byteArrayToHexString(digest.digest(docData));

                stmtMetaUpdate.setString(1, newRevision);
                stmtMetaUpdate.setInt(2, docId);

                int updResult = stmtMetaUpdate.executeUpdate();
                if (updResult != 1) {
                    throw new SQLException("Update to doc meta table affected " + updResult + " rows instead of 1.");
                }

                //`documentId`, `revision`, `parentRev`, `added`, `document`
                stmtDocInsert.setInt(1, docId);
                stmtDocInsert.setString(2, newRevision);
                stmtDocInsert.setString(3,isSingleton ? null : headRev);//Do not write out the parent revision
                Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                stmtDocInsert.setTimestamp(4, tStamp);

                stmtDocInsert.setBlob(5, new ByteArrayInputStream(docData));


                stmtDocInsert.executeUpdate();
                rs = stmtDocInsert.getGeneratedKeys();
                rs.next();
                rs.close();
                returnDoc = new DocumentFile(newRevision, headRev, document.getDomain(), document.getKey(), document.getDocument(), tStamp);
                if(isSingleton && document.getRevision() != null){
                    deleteDocumentRevision(entityId, document.getDomain(), document.getKey(), document.getRevision());
                }
            } else {
                rs.close();

                byte[] doc = DocumentRegistry.instance().toJson(document.getDocument(),DocumentRegistry.getSerializeAs(document.getDocument().getClass())).getBytes();

                if (doc.length > MAX_DOC_SIZE) {
                    throw new RuntimeException("Document exceeds max size.");
                }

                //3) Generate new revision tag.
                MessageDigest digest = MessageDigest.getInstance("SHA1");
                String newRevision = byteArrayToHexString(digest.digest(doc));

                //We are inserting a record
                //`entityId`, `domainId`, `key`, `curRevision`
                stmtMetaInsert.setInt(1, entityId);
                stmtMetaInsert.setInt(2, getDomain(document.getDomain(), true).getDbId());
                stmtMetaInsert.setString(3, document.getKey());
                stmtMetaInsert.setString(4, newRevision);
                stmtMetaInsert.executeUpdate();
                rs = stmtMetaInsert.getGeneratedKeys();
                rs.next();
                int docId = rs.getInt(1);
                rs.close();

                //`documentId`, `revision`, `parentRev`, `added`, `document`
                stmtDocInsert.setInt(1, docId);
                stmtDocInsert.setString(2, newRevision);
                stmtDocInsert.setNull(3, java.sql.Types.CHAR);
                Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                stmtDocInsert.setTimestamp(4, tStamp);

                stmtDocInsert.setBlob(5, new ByteArrayInputStream(doc));
                stmtDocInsert.execute();

                returnDoc = new DocumentFile(newRevision, null, document.getDomain(), document.getKey(), document.getDocument(), tStamp);
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                platform.mysqlError(e, "push doc");
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        returnDoc.setOwner(document.getOwner());
        return returnDoc;
    }

    @Override
    public String[] getDocumentKeysInDomain(int entityId, String domain) {
        try {
            stmtMetaPoll.setInt(1,entityId);
            stmtMetaPoll.setInt(2, getDomain(domain, true).getDbId());
            ResultSet rs = stmtMetaPoll.executeQuery();

            List<String> keys = new ArrayList<String>();
            while(rs.next()){
                keys.add(rs.getString("key"));
            }
            rs.close();
            return keys.toArray(new String[0]);
        } catch (SQLException ex) {
            Logger.getLogger(MysqlStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public DocumentHistory getDocumentHistory(int entityId, String domain, String key) {
        ResultSet rs;
        try {
            rs = getDocumentResultSet(entityId, domain, key);

            if (rs.next()) {
                int docId = getDocumentId(rs);
                String headRev = getCurrentRev(rs);
                rs.close();
                stmtDocPoll.setInt(1, docId);
                rs = stmtDocPoll.executeQuery();
                DocumentHistory history = new DocumentHistory(domain, key, headRev);


                while(rs.next()){
                    history.addEntry(
                            rs.getString("revision"), 
                            rs.getString("parentRev"), 
                            rs.getTimestamp("added"));
                }
                rs.close();
                return history;

            }
        } catch (SQLException e) {
            platform.mysqlError(e, SQL_DOC_STORE_POLL);
        }
        return null;


    }

    @Override
    public void deleteDocumentRevision(int entityId, String domain, String key, String revision) {
        if(revision == null){ throw new IllegalArgumentException("Cannot have null revision");}
        try{
            ResultSet rs = getDocumentResultSet(entityId, domain, key);
            int docId = -1;
            if (rs.next()) {
                docId = getDocumentId(rs);
                rs.close();
            }
            else
            {
                return;
            }


            //If the head rev is targeted, we want to set the head back to it's parent.
            DocumentHistory history = getDocumentHistory(entityId, domain, key);
            boolean isHead = history.getHeadRevision().equals(revision);
            String newHeadRev = history.getEntry(revision).getParentRev();

            stmtDocDelete.setInt(1, docId);
            stmtDocDelete.setString(2, revision);
            stmtDocDelete.execute();

            //Update head revision to point to the parent of the revision we just deleted,
            //if that revision was the head one.
            if(isHead){
                stmtMetaUpdate.setString(1,newHeadRev);
                stmtMetaUpdate.setInt(2,docId);
                stmtMetaUpdate.executeUpdate();
            }


            //If we have zero entries, delete the meta
            if(getDocumentHistory(entityId, domain, key).getEntries().size() == 0){
                stmtMetaDelete.setInt(1, docId);
                stmtMetaDelete.execute();
            }
        }catch(SQLException e){
            platform.mysqlError(e, "Delete document");
        }
    }

    /**
     * Get the ResultSet for document, or null on not found
     * @param entityId
     * @param domain
     * @param key
     * @return
     * @throws SQLException 
     */
    private ResultSet getDocumentResultSet(int entityId, String domain,String key) throws SQLException{
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        conn.setAutoCommit(false);
        int domainId = getDomain(domain, true).getDbId();//Get domain int it
        stmtMetaSelect.setInt(1, entityId);
        stmtMetaSelect.setInt(2, domainId);
        stmtMetaSelect.setString(3, key);
        platform.getLogger().log(Level.FINE, "eid: {0}, domainId: {1}, key: {2}", new Object[]{entityId, domainId, key});
        return stmtMetaSelect.executeQuery();

    }

    private String getCurrentRev(ResultSet rs) throws SQLException{
        return rs.getString("curRevision");
    }
    private int getDocumentId(ResultSet rs) throws SQLException{
        return rs.getInt("documentId");
    }

    @Override
    public void deleteDocument(int entityId, String domain, String key) {
        try{
            ResultSet rs = getDocumentResultSet(entityId, domain, key);

            if (rs.next()) {
                int docId = getDocumentId(rs);
                rs.close();
                //use purge, delete all previous revisions
                stmtDocPurge.setInt(1, docId);
                stmtDocPurge.execute();
                //If we have zero entries, delete the meta
                if(getDocumentHistory(entityId, domain, key).getEntries().size() == 0){
                    stmtMetaDelete.setInt(1, docId);
                    stmtMetaDelete.execute();
                }
            }
        }catch(SQLException e){
            platform.mysqlError(e, "deleteDocument");
        }
    }

}
