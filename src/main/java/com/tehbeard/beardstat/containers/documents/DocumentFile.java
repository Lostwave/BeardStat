/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.containers.documents;

/**
 * Holds metadata for a document
 * @author James
 */
public class DocumentFile<T extends IStatDocument> {
	
    private boolean archive = false;
    private final String revision;
    private final String domain;
    private final String key;
    private final T document;
        
        /**
     *
     * @param revision
     * @param domain
     * @param key
     * @param document
     */
    public DocumentFile(String revision,String domain,String key,T document){
        this.revision = revision;
        this.domain = domain;
        this.key = key;
        this.document = document;

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

    public T getDocument() {
        return document;
    }
	
    
    
	public boolean shouldArchive(){return archive;}
	public void setArchiveFlag(){archive=true;}
	public void clearArchiveFlag(){archive=false;}

	
}