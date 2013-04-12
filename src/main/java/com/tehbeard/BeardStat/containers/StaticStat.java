package com.tehbeard.BeardStat.containers;

/**
 * Concrete implementation of a player stat.
 * This is the default type for stats, they are saved to the database
 * @author James
 *
 */

public class StaticStat implements IStat{

	EntityStatBlob owner = null;
	private String domain;
	private String world;
	private String category="stats";
	private String statistic;
	private int value;
	

	private boolean archive = false;
	




	public StaticStat(String domain, String world, String cat,
			String statistic, int value) {
		super();
		this.domain = domain;
		this.world = world;
		this.statistic = statistic;
		this.value = value;
		this.category = cat;
	}

	/**
	 * Get the stats value
	 * @return
	 */
	public synchronized int getValue(){
		return value;
	}

	/**
	 * Set the stats value
	 * @param value value to set stat to
	 */
	public synchronized void setValue(int value){
		changeValue(value);
	}

	/**
	 * Get the stats name
	 * @return name of tstat
	 */
	public String getStatistic(){
		return statistic;
	}

	/**
	 * Increment the stat by i 
	 * @param i amount to increment stat by
	 * new value = old value + i
	 */
	public synchronized void incrementStat(int i){
		//if(i < 0 ){throw new IllegalArgumentException("Cannot increment by negative number!");}
		changeValue(value + i);
	}

	/**
	 * decrement the stat by i
	 * @param i amount to dencrement stat by
     * new value = old value - i
	 */
	public synchronized void decrementStat(int i){
		//if(i < 0 ){throw new IllegalArgumentException("Cannot decrement by negative number!");}
		changeValue(value - i);
	}


	/**
	 * @return name of category stat is in
	 */
	public String getCategory() {	
		return category;
	}

	/**
	 * Clear the archive flag
	 */
	public synchronized void clearArchive() {
		this.archive = false;
	}

	/**
	 * Is archive flag set?
	 * if the flag is set, the stat will be stored in the database, and the flag cleared on the next save.
	 */
	public synchronized boolean isArchive() {
		return archive;
	}

	/**
	 * get the blob of stats this stat belongs to.
	 */
	public EntityStatBlob getOwner(){
		return owner;
	}
	
	private synchronized void changeValue(int to){
			value = to;
			archive = true;
	}



	/**
	 * Set owner of this stat
	 */
	public void setOwner(EntityStatBlob playerStatBlob) {
		owner = playerStatBlob;
	}
	
	

	public String toString(){
	    return category + "." + statistic + "=" + value;
	}



    public synchronized void archive() {
        archive=true;
        
    }



	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}



	@Override
	public String getDomain() {
		return domain;
	}



	@Override
	public void setWorld(String world) {
		this.world = world;
	}



	@Override
	public String getWorld() {
		return world;
	}
	
	public IStat clone(){
		return new StaticStat(domain, world, category, statistic, value);
	}
}
