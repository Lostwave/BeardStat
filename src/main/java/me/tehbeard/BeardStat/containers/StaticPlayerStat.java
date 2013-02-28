package me.tehbeard.BeardStat.containers;

/**
 * Concrete implmentation of a player stat.
 * This is the default type for stats, they are saved to the database
 * @author James
 *
 */

public class StaticPlayerStat implements PlayerStat{

	PlayerStatBlob owner = null;
	private String domain;
	private String world;
	private String statistic;
	private int value;
	private String cat="stats";

	private boolean archive = false;
	
	/**
	 * Constructs a new stat
	 * @param cat Category
	 * @param name name of stat
	 * @param value inital value
	 */
	public StaticPlayerStat(String cat,String name,int value){
		this.statistic=name;
		this.value=value;
		this.cat=cat;
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
	public String getCat() {	
		return cat;
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
	public PlayerStatBlob getOwner(){
		return owner;
	}
	
	private synchronized void changeValue(int to){
			value = to;
			archive = true;
	}



	/**
	 * Set owner of this stat
	 */
	public void setOwner(PlayerStatBlob playerStatBlob) {
		owner = playerStatBlob;
	}
	
	

	public String toString(){
	    return cat + "." + statistic + "=" + value;
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
		// TODO Auto-generated method stub
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
}
