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
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

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
        promise.onReject(new Delegate<Void, Promise<EntityStatBlob>>() {
            
            @Override
            public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
                params.getError().printStackTrace();
                return null;
            }
        });

        
    }
    
    @Override
    public void run() {
        EntityStatBlob blob = provider.pullEntityBlob(query);
        if(blob == null){
            promise.reject(new NoRecordFoundException(query.name,query.type,query.getUUIDString()));
        }
        else
        {
            promise.resolve(blob);
        }
    }
    
}
