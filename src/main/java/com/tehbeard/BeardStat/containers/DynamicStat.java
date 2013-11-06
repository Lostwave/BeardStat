package com.tehbeard.beardstat.containers;

import com.tehbeard.utils.expressions.InFixExpression;

/**
 * Dynamic player stats generated from composites of other player stats. A
 * Dynamic player stat is only stored if expressly set to be stored. it is
 * computed at runtime using an expression bound to that stat.
 * 
 * @author James
 * 
 */
public class DynamicStat implements IStat {

    private String          domain;
    private String          world;
    private String          category;
    private String          statistic;
    private EntityStatBlob  owner;
    private InFixExpression expression;
    String                  expr;

    private boolean         archive = false;

    public DynamicStat(String domain, String world, String cat, String stat, String expr) {
        this(domain, world, cat, stat, expr, false);

    }

    public DynamicStat(String domain, String world, String cat, String stat, String expr, boolean archive) {
        this.domain = domain;
        this.world = world;
        this.category = cat;
        this.statistic = stat;
        this.expression = new InFixExpression(expr);
        this.archive = archive;
        this.expr = expr;

    }

    @Override
    public int getValue() {
        return this.expression.getValue(this.owner);
    }

    @Override
    public void setValue(int value) {
    }

    @Override
    public String getStatistic() {
        return this.statistic;
    }

    @Override
    public void incrementStat(int i) {
    }

    @Override
    public void decrementStat(int i) {
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public void clearArchive() {
    }

    @Override
    public boolean isArchive() {
        return this.archive;
    }

    @Override
    public EntityStatBlob getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(EntityStatBlob playerStatBlob) {
        this.owner = playerStatBlob;
    }

    @Override
    public void archive() {
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
        return new StaticStat(this.domain, this.world, this.category, this.statistic, getValue());

    }

    public IStat duplicateForPlayer(EntityStatBlob owner) {
        DynamicStat ns = new DynamicStat(this.domain, this.world, this.category, this.statistic, this.expr,
                this.archive);
        ns.setOwner(owner);
        return ns;
    }
}
