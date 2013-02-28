package me.tehbeard.BeardStat.containers;


/**
 * Represents a players stat
 * @author James
 *
 */

public interface PlayerStat{

	
		/**
	 * Get the stats value
	 * @return
	 */
	public int getValue();

	/**
	 * Set the stats value
	 * @param value
	 */
	public void setValue(int value);

	/**
	 * Get the stats name
	 * @return
	 */
	public String getStatistic();

	
	public String getCat() ;

	public void clearArchive();

	public void archive();
	
	public boolean isArchive();
		
	public void setOwner(PlayerStatBlob playerStatBlob);

	public PlayerStatBlob getOwner();

	
	public void setDomain(String domain);
	public String getDomain();
	
	public void setWorld(String world);
	public String getWorld();

	/**
	 * Increment the stat by i 
	 * @param i
	 */
	public void incrementStat(int i);

	/**
	 * decrement the stat by i
	 * @param i
	 */
	public void decrementStat(int i);
	
}
