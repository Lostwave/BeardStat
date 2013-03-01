package com.tehbeard.BeardStat.containers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a collection of IStats, usually the result of a regex get operation
 * Mutator methods related to value affect ALL objects inside this.
 * Other mutator methods fail silently
 * @author James
 *
 */
public class StatVector implements IStat,Iterable<IStat> {

	private List<IStat> stats = new ArrayList<IStat>();

	private String domain;
	private String world;
	private String category;
	private String statistic;
	

	public StatVector(String domain, String world, String category,
			String statistic) {
		this.domain = domain;
		this.world = world;
		this.category = category;
		this.statistic = statistic;
	}

	public void add(IStat stat){
		stats.add(stat);
	}

	@Override
	public int getValue() {
		int ss = 0;
		for(IStat s : stats){
			ss+= s.getValue();
		}
		
		return ss;
	}

	@Override
	public void setValue(int value) {
		
		for(IStat s : stats){
			s.setValue(value);
		}
		
		
	}

	@Override
	public String getStatistic() {
		return statistic;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public void clearArchive() {

	}

	@Override
	public void archive() {

	}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public void setOwner(EntityStatBlob playerStatBlob) {

	}

	@Override
	public EntityStatBlob getOwner() {
		return null;
	}

	@Override
	public void setDomain(String domain) {

	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public void setWorld(String world) {

	}

	@Override
	public String getWorld() {
		return world;
	}

	@Override
	public void incrementStat(int i) {
		for(IStat s : stats){
			s.incrementStat(i);
		}

	}

	@Override
	public void decrementStat(int i) {
		for(IStat s : stats){
			s.decrementStat(i);
		}

	}

	@Override
	public IStat clone(){
		return null;
	}

	@Override
	public Iterator<IStat> iterator() {
		return stats.iterator();
	}


}
