package com.tehbeard.beardstat.dataproviders;

import java.io.File;
import java.util.UUID;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.containers.documents.DocumentHistory;
import com.tehbeard.beardstat.containers.documents.docfile.DocumentFile;
import com.tehbeard.beardstat.dataproviders.metadata.CategoryMeta;
import com.tehbeard.beardstat.dataproviders.metadata.DomainMeta;
import com.tehbeard.beardstat.dataproviders.metadata.StatisticMeta;
import com.tehbeard.beardstat.dataproviders.metadata.WorldMeta;

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
    public EntityStatBlob pullEntityBlob(ProviderQuery query);

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

    /**
     * Returns the DomainMeta object for the given domain
     * @param gameTag
     * @return
     */
    public DomainMeta getDomain(String gameTag);
    /**
     * Returns the WorldMeta object for the given world
     * @param gameTag
     * @return
     */
    public WorldMeta getWorld(String gameTag);
    /**
     * Returns the CategoryMeta object for the given category
     * @param gameTag
     * @return
     */
    public CategoryMeta getCategory(String gameTag);
    /**
     * Returns the StatisticMeta object for the given statistic
     * @param gameTag
     * @return
     */
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
    public DocumentFile pullDocument(int entityId, String domain, String key);

    /**
     * Pushes a document into storage
     * @param query
     * @param document
     * @return new DocumentFile with the revision of the stored document
     * @throws #RevisionMismatchException if revision key passed does not match current (latest) one.
     */
    public DocumentFile pushDocument(int entityId, DocumentFile document) throws RevisionMismatchException;
    
    /**
     * Deletes a document revision
     * @param query entity to delete from
     * @param domain domain key
     * @param key unique id for document
     * @param revision specific revision to delete.
     */
    public void deleteDocumentRevision(int entityId, String domain, String key, String revision);
    
    /**
     * Deletes a document, all revisions
     * @param entityId
     * @param domain
     * @param key
     */
    public void deleteDocument(int entityId, String domain,String key);

    /**
     * Returns a list of document keys under a specific domain for a entity.
     * @param query
     * @param domain
     * @return 
     */
    public String[] getDocumentKeysInDomain(int entityId, String domain);
    
    /**
     * Returns the available history for a document.
     * @param entityId entity the document belongs to
     * @param domain - domain document is under
     * @param key - key document is under
     * @return
     */
    public DocumentHistory getDocumentHistory(int entityId, String domain, String key);


    /**
     * Thrown when the head revision of a document in the database is not the same as the revision we checked out.
     * This allows for Multi server (i.e Bungeecord) environments, and provides a mechanism to get the changed head revision and 
     * take appropriate action (merge, overwrite, ignore)
     * @author James
     *
     */
    public class RevisionMismatchException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 467302713446315251L;
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
    
    public void setUUID(String player, UUID uuid);

}