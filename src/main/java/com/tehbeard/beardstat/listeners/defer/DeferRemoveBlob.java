/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.listeners.defer;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import java.util.Map;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

/**
 * Consumes the result of loading a entityStatBlob, removing it from the cache it was preemptively added to if an error occurs during loading.
 * @author James
 */
public class DeferRemoveBlob implements Delegate<Void, Promise<EntityStatBlob>> {
    private final String cacheKey;
    private final Map<String, Promise<EntityStatBlob>> cache;

    public DeferRemoveBlob(String cacheKey,Map<String, Promise<EntityStatBlob>> cache){
        this.cacheKey = cacheKey;
        this.cache = cache;
        
    }
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
            cache.remove(cacheKey);
        return null;
    }
}
