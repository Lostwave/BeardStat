package com.tehbeard.beardstat.listeners.defer;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import com.tehbeard.beardstat.containers.EntityStatBlob;

/**
 * Delegate incrementing a stat to a later date
 * 
 * @author James
 * 
 */
public class DelegateIncrement implements Delegate<Void, Promise<EntityStatBlob>> {

    private String domain;
    private String world;
    private String category;
    private String name;
    private int    increment;

    public DelegateIncrement(String domain, String world, String category, String name, int increment) {
        super();
        this.domain = domain;
        this.world = world;
        this.category = category;
        this.name = name;
        this.increment = increment;
    }

    @Override
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        params.getValue().getStat(this.domain, this.world, this.category, this.name).incrementStat(this.increment);
        return null;
    }

}
