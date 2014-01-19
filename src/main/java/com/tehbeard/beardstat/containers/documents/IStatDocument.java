package com.tehbeard.beardstat.containers.documents;

import com.tehbeard.beardstat.containers.documents.docfile.DocumentFile;

/**
 *
 * @author James
 */
public interface IStatDocument {
    
    public IStatDocument mergeDocument(DocumentFile file);
}
