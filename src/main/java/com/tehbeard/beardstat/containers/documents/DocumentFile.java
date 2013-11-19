/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.containers.documents;

import java.sql.Timestamp;

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
    
        public DocumentFile(String domain,String key,IStatDocument document){
            this(null,null,domain,key,document,null);
        }

    
    public DocumentFile(String rev, String domain,String key,IStatDocument document){
        this(rev, null, domain, key, document, null);
    }

    public DocumentFile(String revision, String parentRevision, String domain, String key, IStatDocument document, Timestamp dateCreated) {
        this.revision = revision;
        this.parentRevision = parentRevision;
        this.domain = domain;
        this.key = key;
        this.document = document;
        this.dateCreated = dateCreated;

    }

    public String getRevision() {
        return revision;
    }

    public String getDomain() {
        return domain;
    }

    public String getKey() {
        return key;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public boolean shouldArchive() {
        return archive;
    }

    public void setArchiveFlag() {
        archive = true;
    }

    public void clearArchiveFlag() {
        archive = false;
    }

    public String getParentRevision() {
        return parentRevision;
    }

    public <T extends IStatDocument> T getDocument() {
        return (T)document;
    }
    
    public synchronized boolean isInvalid(){
        return isInvalid;
    }
    
    public synchronized void invalidateDocument(){
        isInvalid = true;
    }
    
}