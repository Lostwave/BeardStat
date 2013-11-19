package com.tehbeard.beardstat.dataproviders;

import com.google.gson.stream.JsonWriter;
import java.sql.SQLException;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.containers.documents.DocumentRegistry;
import com.tehbeard.beardstat.dataproviders.sqlite.DocEntry;
import com.tehbeard.beardstat.dataproviders.sqlite.DocEntry.DocRev;
import com.tehbeard.beardstat.dataproviders.sqlite.DocumentDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.util.FileUtil;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {

    private final String filename;
    private DocumentDatabase docDB;
    private File docDbFile;

    public SQLiteStatDataProvider(DbPlatform platform, String filename, DatabaseConfiguration config) throws SQLException {

        super(platform, "sqlite", "org.sqlite.JDBC", config);

        this.connectionUrl = String.format("jdbc:sqlite:%s", filename);
        config.tablePrefix = "stats";
        initialise();

        try {
            docDbFile = new File(platform.getDataFolder(), "documents.json.gz");
            if (!docDbFile.exists()) {
                docDB = new DocumentDatabase();
            } else {
                docDB = DocumentRegistry.instance().fromJson(new InputStreamReader(new GZIPInputStream(new FileInputStream(docDbFile))), DocumentDatabase.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BeardStatRuntimeException("Error generating documents database", e, false);
        }
        this.filename = filename;

    }

    @Override
    public void generateBackup(File file) {
        FileUtil.copy(new File(filename), file);
    }

    @Override
    public DocumentFile pullDocument(int entityId, String domain, String key) {
        DocEntry dbEntry = docDB.getStore(entityId).getDocumentData(getDomain(domain).getDbId(), key);
        DocRev docRevision = dbEntry.getRevisions().get(dbEntry.getCurrentRevision());

        return new DocumentFile(dbEntry.getCurrentRevision(), docRevision.parentRev, domain, key, docRevision.document, docRevision.dateAdded);

    }

    @Override
    public DocumentFile pushDocument(int entityId, DocumentFile document) throws RevisionMismatchException {
        try {
            byte[] doc = DocumentRegistry.instance().toJson(document.getDocument(), DocumentRegistry.getSerializeAs(document.getDocument().getClass())).getBytes();
            //2) Generate new revision tag.
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            String newRevision = byteArrayToHexString(digest.digest(doc));

            String currentRevision = document.getRevision();

            DocEntry dbEntry = docDB.getStore(entityId).getDocumentData(getDomain(document.getDomain()).getDbId(), document.getKey());

            if (!currentRevision.equals(dbEntry.getCurrentRevision())) {
                throw new RevisionMismatchException(pullDocument(entityId, document.getDomain(), document.getKey()));
            }

            //Add the revision
            dbEntry.getRevisions().put(newRevision, new DocRev(currentRevision, document.getDocument()));
            dbEntry.setCurrentRevision(newRevision);

            return pullDocument(entityId, document.getDomain(), document.getKey());

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void deleteDocument(int entityId, String domain, String key, String revision) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getDocumentKeysInDomain(int entityId, String domain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() {
        super.flush();
        try {
            DocumentRegistry.instance().toJson(docDB, DocumentDatabase.class, new JsonWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(docDbFile)))));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
