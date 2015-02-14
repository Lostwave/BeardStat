package com.tehbeard.beardstat.dataproviders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.stream.JsonWriter;
import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.documents.DocumentHistory;
import com.tehbeard.beardstat.containers.documents.DocumentRegistry;
import com.tehbeard.beardstat.containers.documents.StatDocument;
import com.tehbeard.beardstat.containers.documents.docfile.DocumentFile;
import com.tehbeard.beardstat.dataproviders.sqlite.DocEntry;
import com.tehbeard.beardstat.dataproviders.sqlite.DocEntry.DocRev;
import com.tehbeard.beardstat.dataproviders.sqlite.DocumentDatabase;
import com.tehbeard.utils.FileUtils;

public class SQLiteStatDataProvider extends JDBCStatDataProvider {

    private final String filename;
    private DocumentDatabase docDB;
    private File docDbFile;

    public SQLiteStatDataProvider(DbPlatform platform, String filename, DatabaseConfiguration config) throws SQLException, ClassNotFoundException {

        super(platform, "sqlite", "org.sqlite.JDBC", config);

        setConnectionUrl(String.format("jdbc:sqlite:%s", filename));
        setTag("PREFIX", "stats");
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
    public boolean generateBackup(String file) {
        if(filename.equals(":memory:")){return true;}
        try {
            FileUtils.copy(new File(filename), new File(platform.getDataFolder(), file));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public DocumentFile pullDocument(int entityId, String domain, String key) {
        DocEntry dbEntry = docDB.getStore(entityId).getDocumentData(domain, key);
        DocRev docRevision = dbEntry.getRevisions().get(dbEntry.getCurrentRevision());

        if (docRevision == null) {
            return null;
        }

        return new DocumentFile(dbEntry.getCurrentRevision(), docRevision.parentRev, domain, key, docRevision.document, docRevision.dateAdded);

    }

    @Override
    public DocumentFile pushDocument(int entityId, DocumentFile document) throws RevisionMismatchException, DocumentTooLargeException {
        try {
            byte[] doc = DocumentRegistry.instance().toJson(document.getDocument(), DocumentRegistry.getSerializeAs(document.getDocument().getClass())).getBytes();
            if (doc.length > MysqlStatDataProvider.MAX_DOC_SIZE) {
                throw new DocumentTooLargeException("Document exceeds max size.");//TODO - Change to a specific exception for this usecase
            }

            //2) Generate new revision tag.
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            String newRevision = byteArrayToHexString(digest.digest(doc));
            boolean isSingleton = document.getDocument().getClass().getAnnotation(StatDocument.class).singleInstance();

            String currentRevision = document.getRevision();

            DocEntry dbEntry = docDB.getStore(entityId).getDocumentData(document.getDomain(), document.getKey());

            if (currentRevision != null && !currentRevision.equals(dbEntry.getCurrentRevision())) {
                throw new RevisionMismatchException(pullDocument(entityId, document.getDomain(), document.getKey()));
            }

            //Add the new document, if singleton, null the parent revision.
            dbEntry.getRevisions().put(newRevision, new DocRev(isSingleton ? null : currentRevision, document.getDocument()));

            dbEntry.setCurrentRevision(newRevision);
            //delete old entry if singleton
            if (isSingleton) {
                dbEntry.getRevisions().remove(currentRevision);
            }
            DocumentFile d = pullDocument(entityId, document.getDomain(), document.getKey());
            d.setOwner(document.getOwner());
            return d;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public DocumentHistory getDocumentHistory(int entityId, String domain, String key) {
        DocEntry d = docDB.getStore(entityId).getDocumentData(domain, key);
        DocumentHistory history = new DocumentHistory(domain, key, d.getCurrentRevision());
        for (Entry<String, DocRev> e : d.getRevisions().entrySet()) {
            history.addEntry(e.getKey(), e.getValue().parentRev, e.getValue().dateAdded);
        }
        return history;
    }

    @Override
    public void deleteDocument(int entityId, String domain, String key) {
        docDB.getStore(entityId).deleteDocument(domain, key);
    }

    @Override
    public void deleteDocumentRevision(int entityId, String domain, String key, String revision) {
        docDB.getStore(entityId).getDocumentData(domain, key).getRevisions().remove(revision);
        if (docDB.getStore(entityId).getDocumentData(domain, key).getRevisions().isEmpty()) {
            deleteDocument(entityId, domain, key);
        }
    }

    @Override
    public String[] getDocumentKeysInDomain(int entityId, String domain) {
        return docDB.getStore(entityId).getDocsUnderDomain(domain);
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

    @Override
    public boolean restoreBackup(String file) {
        try {
            teardown();

            FileUtils.copy(new File(platform.getDataFolder(), file), new File(filename));
            initialise();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SQLiteStatDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            platform.getLogger().log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public synchronized boolean checkConnection() {
        return true;
    }

}
