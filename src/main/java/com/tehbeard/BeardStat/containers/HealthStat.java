package com.tehbeard.BeardStat.containers;

import org.bukkit.Bukkit;

import com.tehbeard.BeardStat.BeardStat;

public class HealthStat implements IStat {
    
    public HealthStat(EntityStatBlob owner){
        this.owner = owner;
    }

    private int lastHealth = 20;
    private EntityStatBlob owner;

    @Override
    public void setWorld(String world) {
    }

    @Override
    public void setValue(int value) {

    }

    @Override
    public void setOwner(EntityStatBlob playerStatBlob) {

    }

    @Override
    public void setDomain(String domain) {

    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public void incrementStat(int i) {
    }

    @Override
    public String getWorld() {

        return BeardStat.GLOBAL_WORLD;// Bukkit.getPlayer(getName()).getWorld().getName();
    }

    @Override
    public int getValue() {
        if (Bukkit.getPlayer(owner.getName()) != null) {
            this.lastHealth = (int) Math.floor(Bukkit.getPlayer(owner.getName()).getHealth());
        }
        return this.lastHealth;
    }

    @Override
    public String getStatistic() {
        return "health";
    }

    @Override
    public EntityStatBlob getOwner() {
        return owner;
    }

    @Override
    public String getDomain() {
        return BeardStat.DEFAULT_DOMAIN;
    }

    @Override
    public String getCategory() {
        return "status";
    }

    @Override
    public void decrementStat(int i) {
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
}
