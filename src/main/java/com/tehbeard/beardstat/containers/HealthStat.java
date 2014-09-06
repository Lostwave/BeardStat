package com.tehbeard.beardstat.containers;

import org.bukkit.Bukkit;

import com.tehbeard.beardstat.BeardStat.Refs;

public class HealthStat implements IStat {

    public HealthStat(EntityStatBlob owner) {
        this.owner = owner;
    }

    private int            lastHealth = 20;
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

        return Refs.GLOBAL_WORLD;// Bukkit.getPlayer(getName()).getWorld().getName();
    }

    @Override
    public int getValue() {
        if (Bukkit.getPlayer(this.owner.getName()) != null) {
            this.lastHealth = (int) Math.floor(Bukkit.getPlayer(this.owner.getName()).getHealth());
        }
        return this.lastHealth;
    }

    @Override
    public String getStatistic() {
        return "health";
    }

    @Override
    public EntityStatBlob getOwner() {
        return this.owner;
    }

    @Override
    public String getDomain() {
        return Refs.DEFAULT_DOMAIN;
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
