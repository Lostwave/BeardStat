/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.containers.documents.docfile;

import java.sql.Timestamp;

import com.tehbeard.beardstat.containers.documents.IStatDocument;
import com.tehbeard.beardstat.containers.documents.IStatDynamicDocument;

/**
 * Holds metadata for a document
 *
 * @author James
 */
public class DocumentFile {

    private boolean archive = false;
    private final String revision;
    private final String parentRevision;
    private final String domain;
    private final String key;
    private final IStatDocument document;
    private final Timestamp dateCreated;
    private boolean isInvalid = false;

    /**
     * Create a new DocumentFile with no parent/revision information
     * @param domain
     * @param key
     * @param document
     */
    public DocumentFile(String domain,String key,IStatDocument document){
        this(null,null,domain,key,document,null);
    }
    
    /**
     * Create DocumentFile with full revision information
     * @param revision
     * @param parentRevision
     * @param domain
     * @param key
     * @param document
     * @param dateCreated
     */
    public DocumentFile(String revision, String parentRevision, String domain, String key, IStatDocument document, Timestamp dateCreated) {
        this.revision = revision;
        this.parentRevision = parentRevision;
        this.domain = domain;
        this.key = key;
        this.document = document;
        this.dateCreated = dateCreated;

    }

    /**
     * Return this documents revision id, this uniquely identifies this iteration of the document from others. 
     * @return
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Domain key for this Document
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Unique id for this document
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the date this document was created / saved.
     * @return
     */
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    /**
     * Whether this document should be saved 
     * @return
     */
    public boolean shouldArchive() {
        return archive || (document instanceof IStatDynamicDocument);
    }

    /**
     * Once done with your document, call this method to flag it for storage.
     */
    public void setArchiveFlag() {
        archive = true;
    }

    /**
     * Opposite of setArchiveFlag()
     */
    public void clearArchiveFlag() {
        archive = false;
    }
    
    /**
     * Returns the id of the revision of this document this one was based off of.
     * @return
     */
    public String getParentRevision() {
        return parentRevision;
    }
    
    /**
     * Returns the Document
     * Note: This runs updateDocument() on {@link IStatDynamicDocument} documents.
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends IStatDocument> T getDocument() {
        if(document instanceof IStatDynamicDocument){
            ((IStatDynamicDocument) document).updateDocument();
        }
        return (T)document;
    }
    
    /**
     * If this returns true, the document has been saved since you got this object, and may have changed due to a merge.
     * You should try grab it again.
     * @return
     */
    public synchronized boolean isInvalid(){
        return isInvalid;
    }
    
    /**
     * Flags this DocumentFile as invalid, called by the save system once the document is saved.
     * @return
     */
    public synchronized void invalidateDocument(){
        isInvalid = true;
    }

}