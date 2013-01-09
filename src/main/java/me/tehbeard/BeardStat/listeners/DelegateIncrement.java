package me.tehbeard.BeardStat.listeners;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

public class DelegateIncrement implements Delegate<Void, Promise<PlayerStatBlob>> {

    private String category;
    private String name;
    private int increment;
    
    
    
    /**
     * @param category
     * @param name
     * @param increment
     */
    public DelegateIncrement(String category, String name, int increment) {
        this.category = category;
        this.name = name;
        this.increment = increment;
    }



    public <P extends Promise<PlayerStatBlob>> Void invoke(P params) {
        params.getValue().getStat(category, name).incrementStat(increment);
        return null;
    }

}
