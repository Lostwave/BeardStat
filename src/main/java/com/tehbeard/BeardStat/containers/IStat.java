package com.tehbeard.BeardStat.containers;

/**
 * Represents a stat
 * 
 * @author James
 * 
 */

public interface IStat {

    /**
     * Get the stats value
     * 
     * @return
     */
    public int getValue();

    /**
     * Set the stats value
     * 
     * @param value
     */
    public void setValue(int value);

    /**
     * Get the stats name
     * 
     * @return
     */
    public String getStatistic();

    public String getCategory();

    public void clearArchive();

    public void archive();

    public boolean isArchive();

    public void setOwner(EntityStatBlob playerStatBlob);

    public EntityStatBlob getOwner();

    public void setDomain(String domain);

    public String getDomain();

    public void setWorld(String world);

    public String getWorld();

    /**
     * Increment the stat by i
     * 
     * @param i
     */
    public void incrementStat(int i);

    /**
     * decrement the stat by i
     * 
     * @param i
     */
    public void decrementStat(int i);

    /**
     * Clone self
     * 
     * @return
     */
    public IStat clone();
}
