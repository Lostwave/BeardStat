package me.tehbeard.BeardStat.listeners;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

public class DelegateSet implements Delegate<Void, Promise<PlayerStatBlob>> {

    private String category;
    private String name;
    private int value;
    
    
    
    /**
     * @param category
     * @param name
     * @param value
     */
    public DelegateSet(String category, String name, int value) {
        this.category = category;
        this.name = name;
        this.value = value;
    }



    public <P extends Promise<PlayerStatBlob>> Void invoke(P params) {
        params.getValue().getStat(category, name).setValue(value);
        return null;
    }

}
