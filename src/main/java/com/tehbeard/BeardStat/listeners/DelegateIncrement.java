package com.tehbeard.BeardStat.listeners;

import com.tehbeard.BeardStat.containers.EntityStatBlob;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

public class DelegateIncrement implements Delegate<Void, Promise<EntityStatBlob>> {

	private String domain;
	private String world;
    private String category;
    private String name;
    private int increment;
    
    




    public DelegateIncrement(String domain, String world, String category,
			String name, int increment) {
		super();
		this.domain = domain;
		this.world = world;
		this.category = category;
		this.name = name;
		this.increment = increment;
	}






	public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
        params.getValue().getStat(domain,world,category, name).incrementStat(increment);
        return null;
    }

}
