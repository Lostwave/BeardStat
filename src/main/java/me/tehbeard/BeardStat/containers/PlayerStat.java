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
	public String getName();

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


	public String getCat() ;

	public void clearArchive();

	public boolean isArchive();
	
	public void archive();

	public PlayerStatBlob getOwner();

	public void setOwner(PlayerStatBlob playerStatBlob);
	
	
}
