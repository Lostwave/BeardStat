/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.manager;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dragonzone.promise.Promise;

/**
 *
 * @author James
 */
public class CacheDatabase {

    private Map<ProviderQuery, Promise<EntityStatBlob>> cache = new HashMap<ProviderQuery, Promise<EntityStatBlob>>();
    private Map<String, Promise<EntityStatBlob>> uuidCache = new HashMap<String, Promise<EntityStatBlob>>();

    public synchronized boolean hasEntry(ProviderQuery query) {
        return cache.containsKey(makeQryKey(query));
    }

    public synchronized void addToCache(ProviderQuery query, Promise<EntityStatBlob> promise) {
        cache.put(makeQryKey(query), promise);
        if (query.uuid != null) {
            uuidCache.put(query.uuid, promise);
        }
    }

    public synchronized Promise<EntityStatBlob> getCache(ProviderQuery query) {
        if (query.uuid == null) {  
            return cache.get(makeQryKey(query));
        } else {
            return uuidCache.get(query.uuid);
        }
    }

    public synchronized void remove(ProviderQuery query) {
        cache.remove(makeQryKey(query));
        if (query.uuid != null) {
            uuidCache.remove(query.uuid);
        }
    }

    private synchronized ProviderQuery makeQryKey(ProviderQuery qry) {
        return new ProviderQuery(qry.name, qry.type, null, false);
    }
    
    public synchronized List<EntityStatBlob> getLoadedBlobs(){
        List<EntityStatBlob> blobs = new ArrayList<EntityStatBlob>();
        for( Promise<EntityStatBlob> promise : cache.values()){
            if(promise.isResolved()){
                blobs.add(promise.getValue());
            }
        }
        return blobs;
    }
}
