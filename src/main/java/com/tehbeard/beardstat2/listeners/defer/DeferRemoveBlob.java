/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.listeners.defer;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import java.util.HashMap;
import java.util.Map;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

/**
 *
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
        if (params.getValue() == null) {
            cache.remove(cacheKey);
        }
        return null;
    }
}
