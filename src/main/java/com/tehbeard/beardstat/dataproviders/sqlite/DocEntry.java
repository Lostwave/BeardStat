/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders.sqlite;

import com.google.gson.annotations.Expose;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.containers.documents.IStatDocument;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James
 */
public class DocEntry {

    @Expose
    private Map<String, DocRev> revisions = new HashMap<String, DocRev>();
    @Expose
    private String currentRevision = null;

    public Map<String, DocRev> getRevisions() {
        return revisions;
    }

    public static class DocRev {

        @Expose
        public Timestamp dateAdded = new Timestamp(System.currentTimeMillis());
        @Expose
        public IStatDocument document;
        @Expose
        public String parentRev;

        public DocRev(String parentRev,IStatDocument document) {
            this.parentRev = parentRev;
            this.document = document;
        }
        
    }

    public String getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(String currentRevision) {
        this.currentRevision = currentRevision;
    }
    
    
}
