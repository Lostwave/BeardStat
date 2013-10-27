/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat.listeners.defer;

import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.manager.EntityStatManager;
import java.util.Map;
import java.util.UUID;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

/**
 *
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
            UUID uuid = params.getValue().getUUID();
            if(uuid != null){
                cache.put(EntityStatManager.getCacheKey(params.getValue().getName(), params.getValue().getType()),params);
            }
        }
        return null;
    }
}
