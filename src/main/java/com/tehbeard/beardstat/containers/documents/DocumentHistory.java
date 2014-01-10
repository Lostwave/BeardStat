package com.tehbeard.beardstat.containers.documents;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of metadata on a document's various revisions.
 * @author James
 *
 */
public class DocumentHistory {
    
    private final List<DocumentHistoryEntry> entries = new ArrayList<DocumentHistoryEntry>();
    private final String domain;
    private final String key;
    private final String latestRevision;

    /**
     * @param domain
     * @param key
     */
    public DocumentHistory(String domain, String key,String latestRevision) {
        this.domain = domain;
        this.key = key;
        this.latestRevision = latestRevision;
    }
    

    public String getDomain() {
        return domain;
    }


    public String getKey() {
        return key;
    }
    
    public List<DocumentHistoryEntry> getEntries(){
        return entries;
    }
    
    public DocumentHistoryEntry getLatestEntry(){
        return getEntry(latestRevision);
    }
    
    public DocumentHistoryEntry getEntry(String revisionId){
        for(DocumentHistoryEntry e : entries){
            if(e.revision.equals(revisionId)){
                return e;
            }
        }
        return null;
    }

    
    public void addEntry(String revision, String parentRev, Timestamp added, int storeId){
        entries.add(new DocumentHistoryEntry(revision, parentRev, added, storeId));
    }

    public class DocumentHistoryEntry {
        
        private final String revision;
        private final String parentRev;
        private final Timestamp added;
        private final int storeId;
        
        public String getRevision() {
            return revision;
        }
        public String getParentRev() {
            return parentRev;
        }
        public Timestamp getAdded() {
            return added;
        }
        /**
         * @param revision
         * @param parentRev
         * @param added
         * @param storeId
         */
        public DocumentHistoryEntry(String revision, String parentRev, Timestamp added, int storeId) {
            this.revision = revision;
            this.parentRev = parentRev;
            this.added = added;
            this.storeId = storeId;
        }
        
        /**
         * Returns the parent entry this one was written from.
         * @return
         */
        public DocumentHistoryEntry getParent(){
            return getEntry(parentRev);
        }
        
        
        
    }
    
    

}
