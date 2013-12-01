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
            String uuid = params.getValue().getString();
            if(uuid != null){
                cache.put(uuid.toString(),params);
            }
        }
        return null;
    }
}
