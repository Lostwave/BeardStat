/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.listeners.defer;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.manager.EntityStatManager;
import java.util.Map;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

/**
 * Consumes the result of loading a entityStatBlob, adding it to EntityStatManagers cache for name:type searches
 * @author James
 */
public class DeferAddNameType implements Delegate<Void, Promise<EntityStatBlob>> {
    private final Map<String, Promise<EntityStatBlob>> cache;


    /**
     *
     * @param name
     * @param type
     * @param cache
     */
    public DeferAddNameType(Map<String, Promise<EntityStatBlob>> cache){
        this.cache = cache;
    }
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        if (params.getValue() == null) {
            String uuid = params.getValue().getString();
            if(uuid != null){
                cache.put(EntityStatManager.getCacheKey(params.getValue().getName(), params.getValue().getType()),params);
            }
        }
        return null;
    }
}
