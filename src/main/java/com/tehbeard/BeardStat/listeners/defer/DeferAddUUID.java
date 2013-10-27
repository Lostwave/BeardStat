/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat.listeners.defer;

import com.tehbeard.BeardStat.containers.EntityStatBlob;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

/**
 *
 * @author James
 */
public class DeferAddUUID implements Delegate<Void, Promise<EntityStatBlob>> {
    private final Map<String, Promise<EntityStatBlob>> cache;

    public DeferAddUUID(Map<String, Promise<EntityStatBlob>> cache){
        this.cache = cache;
        
    }
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        if (params.getValue() == null) {
            UUID uuid = params.getValue().getUUID();
            if(uuid != null){
                cache.put(uuid.toString(),params);
            }
        }
        return null;
    }
}
