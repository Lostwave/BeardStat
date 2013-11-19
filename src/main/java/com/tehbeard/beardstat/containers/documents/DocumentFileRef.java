package com.tehbeard.beardstat.containers.documents;

/**
 * Internal reference class to allow updating of EntityStatBlob's document store from save thread. NOT API SAFE
 *
 * @author James
 */
public class DocumentFileRef {

    private DocumentFile ref;
    
    private boolean invalid = false;

    public DocumentFileRef(DocumentFile file) {
        this.ref = file;
    }

    public synchronized DocumentFile getRef() {
        return ref;
    }

    public synchronized void setRef(DocumentFile ref) {
        this.ref = ref;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void invalidateRef() {
        this.invalid = true;
    }
    
    
}
