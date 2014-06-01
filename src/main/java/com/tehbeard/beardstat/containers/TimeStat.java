package com.tehbeard.beardstat.containers;

import com.tehbeard.beardstat.BeardStat;

/**
 * Represents a timeable stat, adds a few handy features.
 * @author James
 *
 */
public class TimeStat implements IStat {

    public TimeStat(EntityStatBlob owner) {
        this.owner = owner;
    }

    /**
     * @param owner
     * @param domain
     * @param world
     * @param statistic
     * @param category
     */
    public TimeStat(EntityStatBlob owner, String domain, String world, String statistic, String category) {
        this.owner = owner;
        this.domain = domain;
        this.world = world;
        this.statistic = statistic;
        this.category = category;
    }

    private int value = 0;
    private EntityStatBlob owner;
    
    private String domain;
    private String world;
    private String statistic;
    private String category;

    @Override
    public void setWorld(String world) {
        this.world = world;
    }

    @Override
    public void setValue(int value) {
        this.value =value;  
    }

    @Override
    public void setOwner(EntityStatBlob playerStatBlob) {
        this.owner = owner;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public void incrementStat(int i) {
        value += i;
    }

    @Override
    public String getWorld() {

        return BeardStat.GLOBAL_WORLD;// Bukkit.getPlayer(getName()).getWorld().getName();
    }

    @Override
    public int getValue() {
        return value + getTime();
    }

    @Override
    public String getStatistic() {
        return statistic;
    }

    @Override
    public EntityStatBlob getOwner() {
        return this.owner;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void decrementStat(int i) {
        value -= i;
    }

    @Override
    public void clearArchive() {
    }

    @Override
    public void archive() {
    }

    @Override
    public IStat clone() {
        return new StaticStat(getDomain(), getWorld(), getCategory(), getStatistic(), getValue());
    }
    
    
    private long timerStarted = 0;
    
    
    /**
     * Start a timer running
     */
    public void startTimer(){
        timerStarted = System.currentTimeMillis();
    }
    
    /**
     * Get current time elapsed. This is added to the current value.
     * @return
     */
    public int getTime(){
        return (timerStarted > 0) ? (int) ((System.currentTimeMillis() - timerStarted) / 1000L) : 0;
    }
    
    /**
     * Stop the timer and reset it's value.
     */
    public void resetTimer(){
        timerStarted = 0;
    }
    
    /**
     * Add the timer value to static value and reset. 
     */
    public void markAndResetTimer(){
        value += getTime();
        resetTimer();
    }
}
