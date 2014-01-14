package com.tehbeard.beardstat.containers;

import com.tehbeard.beardstat.containers.documents.docfile.DocumentFileRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Record for IStatDataProviders containing the changed stats
 * NOT API SAFE
 * @author James
 */
public class StatBlobRecord {
    
    public final int entityId;
    
    public StatBlobRecord(int entityId){
        this.entityId = entityId;
        
    }
    
    public final List<IStat> stats = new ArrayList<IStat>();
    public final List<DocumentFileRef> files = new ArrayList<DocumentFileRef>();
    
}
