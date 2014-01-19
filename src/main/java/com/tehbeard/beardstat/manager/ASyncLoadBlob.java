/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.manager;

import com.tehbeard.beardstat.NoRecordFoundException;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import net.dragonzone.promise.Deferred;

/**
 *
 * @author James
 */
public class ASyncLoadBlob implements Runnable{
    private final ProviderQuery query;
    private final IStatDataProvider provider;
    private final Deferred<EntityStatBlob> promise;

    public ASyncLoadBlob(ProviderQuery query, IStatDataProvider provider, Deferred<EntityStatBlob> promise){
        this.query = query;
        this.provider = provider;
        this.promise = promise;

        
    }
    
    @Override
    public void run() {
        EntityStatBlob blob = provider.pullEntityBlob(query);
        if(blob == null){
            promise.reject(new NoRecordFoundException());
        }
        else
        {
            promise.resolve(blob);
        }
    }
    
}
