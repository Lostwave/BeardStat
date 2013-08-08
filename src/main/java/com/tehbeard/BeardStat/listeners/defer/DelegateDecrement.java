package com.tehbeard.BeardStat.listeners.defer;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import com.tehbeard.BeardStat.containers.EntityStatBlob;

/**
 * Delegate the decrementing of a stat to occur at at a later date
 * 
 * @author James
 * 
 */
public class DelegateDecrement implements Delegate<Void, Promise<EntityStatBlob>> {

    private String domain;
    private String world;
    private String category;
    private String name;
    private int    decrement;

    public DelegateDecrement(String domain, String world, String category, String name, int decrement) {
        super();
        this.domain = domain;
        this.world = world;
        this.category = category;
        this.name = name;
        this.decrement = decrement;
    }

    @Override
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        params.getValue().getStat(this.domain, this.world, this.category, this.name).decrementStat(this.decrement);
        return null;
    }

}
