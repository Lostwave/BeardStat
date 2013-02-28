package com.tehbeard.BeardStat.listeners;

import com.tehbeard.BeardStat.containers.PlayerStatBlob;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

public class DelegateDecrement implements Delegate<Void, Promise<PlayerStatBlob>> {

    private String category;
    private String name;
    private int decrement;
    
    
    
    /**
     * @param category
     * @param name
     * @param decrement
     */
    public DelegateDecrement(String category, String name, int decrement) {
        this.category = category;
        this.name = name;
        this.decrement = decrement;
    }



    public <P extends Promise<PlayerStatBlob>> Void invoke(P params) {
        params.getValue().getStat(category, name).decrementStat(decrement);
        return null;
    }

}
