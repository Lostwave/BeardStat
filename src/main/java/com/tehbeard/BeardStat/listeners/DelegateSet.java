package com.tehbeard.BeardStat.listeners;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import com.tehbeard.BeardStat.containers.EntityStatBlob;

/**
 * Delegate setting a stat's value to a later date
 * 
 * @author James
 * 
 */
public class DelegateSet implements Delegate<Void, Promise<EntityStatBlob>> {

    private String domain;
    private String world;
    private String category;
    private String name;
    private int    value;

    public DelegateSet(String domain, String world, String category, String name, int value) {
        super();
        this.domain = domain;
        this.world = world;
        this.category = category;
        this.name = name;
        this.value = value;
    }

    @Override
    public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        params.getValue().getStat(this.domain, this.world, this.category, this.name).setValue(this.value);
        return null;
    }

}
