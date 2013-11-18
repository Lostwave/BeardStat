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
    private final int documentId;
    
    public DocumentFile(String rev, String domain,String key,IStatDocument document){
        this(rev, null, domain, key, document, null, 0);
    }

    public DocumentFile(String revision, String parentRevision, String domain, String key, IStatDocument document, Timestamp dateCreated,int documentId) {
        this.revision = revision;
        this.parentRevision = parentRevision;
        this.domain = domain;
        this.key = key;
        this.document = document;
        this.dateCreated = dateCreated;
        this.documentId = documentId;

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

    public int getDocumentId() {
        return documentId;
    }
    
    
}