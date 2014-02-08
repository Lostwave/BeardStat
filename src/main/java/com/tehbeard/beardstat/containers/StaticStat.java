package com.tehbeard.beardstat.containers;

/**
 * Concrete implementation of a player stat. This is the default type for stats,
 * They do not change themselves, instead relying on other code to modify them.
 * Upon modification they raise the archive flag, making eligable to be saved.
 * 
 * @author James
 * 
 */

public class StaticStat implements IStat {

    EntityStatBlob  owner    = null;
    private String  domain;
    private String  world;
    private String  category = "stats";
    private String  statistic;
    private int     value;

    private boolean archive  = false;

    public StaticStat(String domain, String world, String cat, String statistic, int value) {
        if(statistic == null){throw new IllegalArgumentException("Passed null statistic");}
        this.domain = domain;
        this.world = world;
        this.statistic = statistic;
        this.value = value;
        this.category = cat;
    }

    /**
     * Get the stats value
     * 
     * @return
     */
    @Override
    public synchronized int getValue() {
        return this.value;
    }

    /**
     * Set the stats value
     * 
     * @param value
     *            value to set stat to
     */
    @Override
    public synchronized void setValue(int value) {
        changeValue(value);
    }

    /**
     * Get the stats name
     * 
     * @return name of tstat
     */
    @Override
    public String getStatistic() {
        return this.statistic;
    }

    /**
     * Increment the stat by i
     * 
     * @param i
     *            amount to increment stat by new value = old value + i
     */
    @Override
    public synchronized void incrementStat(int i) {
        // if(i < 0 ){throw new
        // IllegalArgumentException("Cannot increment by negative number!");}
        changeValue(this.value + i);
    }

    /**
     * decrement the stat by i
     * 
     * @param i
     *            amount to dencrement stat by new value = old value - i
     */
    @Override
    public synchronized void decrementStat(int i) {
        // if(i < 0 ){throw new
        // IllegalArgumentException("Cannot decrement by negative number!");}
        changeValue(this.value - i);
    }

    /**
     * @return name of category stat is in
     */
    @Override
    public String getCategory() {
        return this.category;
    }

    /**
     * Clear the archive flag
     */
    @Override
    public synchronized void clearArchive() {
        this.archive = false;
    }

    /**
     * Is archive flag set? if the flag is set, the stat will be stored in the
     * database, and the flag cleared on the next save.
     */
    @Override
    public synchronized boolean isArchive() {
        return this.archive;
    }

    /**
     * get the blob of stats this stat belongs to.
     */
    @Override
    public EntityStatBlob getOwner() {
        return this.owner;
    }

    private synchronized void changeValue(int to) {
        this.value = to;
        this.archive = true;
    }

    /**
     * Set owner of this stat
     */
    @Override
    public void setOwner(EntityStatBlob playerStatBlob) {
        this.owner = playerStatBlob;
    }

    @Override
    public String toString() {
        return this.category + "." + this.statistic + "=" + this.value;
    }

    @Override
    public synchronized void archive() {
        this.archive = true;

    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setWorld(String world) {
        this.world = world;
    }

    @Override
    public String getWorld() {
        return this.world;
    }

    @Override
    public IStat clone() {
        return new StaticStat(this.domain, this.world, this.category, this.statistic, this.value);
    }
}
