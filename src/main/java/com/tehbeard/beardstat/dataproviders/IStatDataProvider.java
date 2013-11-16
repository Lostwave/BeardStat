package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.dataproviders.metadata.*;

import net.dragonzone.promise.Promise;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import java.io.File;

/**
 * Provides push/pull service for getting and saving stats to a backend storage system.
 *
 * @author James
 *
 */
public interface IStatDataProvider {

    public static final String PLAYER_TYPE = "player";
    public static final String GROUP_TYPE = "group";
    public static final String FACTION_TYPE = "faction";
    public static final String ALLIANCE_TYPE = "alliance";
    public static final String WORLD_TYPE = "world";
    public static final String PLUGIN_TYPE = "plugin";

    /**
     * Pulls a entity out of the database
     *
     * @param query
     * @return
     */
    public Promise<EntityStatBlob> pullEntityBlob(ProviderQuery query);

    /**
     * Pulls a entity out of the database directly, may block.
     *
     * @param query
     * @return
     */
    public EntityStatBlob pullEntityBlobDirect(ProviderQuery query);

    /**
     * Pushes the entity into the database, this may not happen if the entity is queued and something stops the queue from being processed.
     *
     * @param blob
     */
    public void pushEntityBlob(EntityStatBlob blob);

    /**
     * Checks if the database contains a blob matching this one.
     *
     * @param query
     * @return
     */
    public boolean hasEntityBlob(ProviderQuery query);

    /**
     * Deletes a blob matching this one.
     *
     * @param blob
     * @return
     */
    public boolean deleteEntityBlob(EntityStatBlob blob);

    /**
     * Queries database for entities
     *
     * @param query
     * @return
     */
    public ProviderQueryResult[] queryDatabase(ProviderQuery query);

    /**
     * Flushes immediately to the database
     */
    public void flushSync();

    /**
     * Flush any cached data to the backend now, can do so in a seperate thread.
     */
    public void flush();

    public DomainMeta getDomain(String gameTag);

    public WorldMeta getWorld(String gameTag);

    public CategoryMeta getCategory(String gameTag);

    public StatisticMeta getStatistic(String gameTag);

    /**
     * backup the database, for MySQL this could be a schema dump. SQLite makes a copy of the db file.
     *
     * @param file
     */
    public void generateBackup(File file);

    /**
     * Pulls a document from the database
     *
     * @param domain domain to store document under
     * @param key id to store document under
     *
     * Documents exist under entity -> domain -> id, this composite key uniquely identifies a document
     */
    public DocumentFile pullDocument(ProviderQuery query, String domain, String key);

    /**
     * Pushes a document into storage
     * @param query
     * @param document
     * @return new DocumentFile with the revision of the stored document
     * @throws com.tehbeard.beardstat.dataproviders.IStatDataProvider.RevisionMismatchException if revision key passed does not match current (latest) one.
     */
    public DocumentFile pushDocument(ProviderQuery query, DocumentFile document) throws RevisionMismatchException;
    
    /**
     * Deletes a document
     * @param query entity to delete from
     * @param domain domain key
     * @param key unique id for document
     * @param revision specific revision to delete. If passed null deletes all revisions
     */
    public void deleteDocument(ProviderQuery query, String domain, String key, String revision);

    /**
     * Returns a list of document keys under a specific domain for a entity.
     * @param query
     * @param domain
     * @return 
     */
    public String[] getDocumentKeysInDomain(ProviderQuery query, String domain);


    public class RevisionMismatchException extends Exception {
        private final DocumentFile newFile;

        public DocumentFile getNewFile() {
            return newFile;
        }

        
        /**
         *
         * @param newFile
         */
        public RevisionMismatchException(DocumentFile newFile) {
            this.newFile = newFile;
        }
    }
}