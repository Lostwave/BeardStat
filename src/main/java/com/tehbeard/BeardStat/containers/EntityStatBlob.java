package com.tehbeard.BeardStat.containers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.tehbeard.utils.expressions.VariableProvider;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.BeardStatRuntimeException;

/**
 * Represents a collection of statistics bound to an entity Currently only used
 * for Players.
 * 
 * @author James
 * 
 */
public class EntityStatBlob implements VariableProvider {

    private static Set<DynamicStat> dynamicStats = new HashSet<DynamicStat>();

    public static void addDynamic(String statName, String expr, boolean archive) {

        Stack<String> stack = new Stack<String>();
        for (String s : statName.split("\\:\\:")) {
            stack.add(s);
        }

        String stat = !stack.isEmpty() ? stack.pop() : null;
        String cat = !stack.isEmpty() ? stack.pop() : null;
        String world = !stack.isEmpty() ? stack.pop() : BeardStat.GLOBAL_WORLD;
        String domain = !stack.isEmpty() ? stack.pop() : BeardStat.DEFAULT_DOMAIN;
        if ((stat == null) || (cat == null)) {
            throw new BeardStatRuntimeException("Invalid stat name provided [" + statName + "]",
                    new IllegalArgumentException(), false);
        }

        dynamicStats.add(new DynamicStat(domain, world, cat, stat, expr, archive));
    }

    private void addDynamics() {
        if (this.type.equals(BeardStat.PLAYER_TYPE)) {
            for (DynamicStat ds : dynamicStats) {

                addStat(ds.duplicateForPlayer(this));
            }

            // Add health status
            addStat(new HealthStat(this));
        }
    }

    private Map<String, IStat> stats = new ConcurrentHashMap<String, IStat>();

    private int                entityId;
    private String             name;
    private String             type;

    public String getName() {
        return this.name;
    }

    public int getEntityID() {
        return this.entityId;
    }

    /**
     * 
     * @param name
     *            Players name
     * @param ID
     *            playerID in database
     */
    public EntityStatBlob(String name, int entityId, String type) {
        this.name = name;
        this.entityId = entityId;
        this.type = type;
        addDynamics();
    }

    /**
     * add stat
     * 
     * @param stat
     */
    public void addStat(IStat stat) {
        this.stats.put(
                stat.getDomain() + "::" + stat.getWorld() + "::" + stat.getCategory() + "::" + stat.getStatistic(),
                stat);
        stat.setOwner(this);
    }

    /**
     * Get a players stat, creates new object if not found.
     * 
     * @param statistic
     * @return
     */
    public IStat getStat(String domain, String world, String category, String statistic) {
        IStat psn = this.stats.get(domain + "::" + world + "::" + category + "::" + statistic);
        if (psn != null) {
            return psn;
        }
        psn = new StaticStat(domain, world, category, statistic, 0);
        addStat(psn);
        return psn;
    }

    /**
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines
     * multiple stats into one easy to access object {@link StatVector} supports
     * the use of regex, with the shortcut "*" to denote all possible values
     * (substituted for ".*" in regex engine) Defaults to readonly mode, any
     * mutators called on this {@link StatVector} will throw
     * {@link IllegalStateException} if readOnly is true
     * 
     * @param domain
     * @param world
     * @param category
     * @param statistic
     * @return
     */
    public StatVector getStats(String domain, String world, String category, String statistic) {
        return getStats(domain, world, category, statistic, true);
    }

    /**
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines
     * multiple stats into one easy to access object {@link StatVector} supports
     * the use of regex, with the shortcut "*" to denote all possible values
     * (substituted for ".*" in regex engine) Defaults to readonly mode, any
     * mutators called on this {@link StatVector} will throw
     * {@link IllegalStateException} if readOnly is true
     * 
     * @param domain
     * @param world
     * @param category
     * @param statistic
     * @param readOnly
     * @return
     */
    public StatVector getStats(String domain, String world, String category, String statistic, boolean readOnly) {
        String pattern = starToRegex(domain);
        pattern += "\\::" + starToRegex(world);
        pattern += "\\::" + starToRegex(category);
        pattern += "\\::" + starToRegex(statistic);

        return getStats(domain, world, category, statistic, pattern, readOnly);
    }

    /**
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines
     * multiple stats into one easy to access object {@link StatVector} supports
     * the use of regex, with the shortcut "*" to denote all possible values
     * (substituted for ".*" in regex engine) Defaults to readonly mode, any
     * mutators called on this {@link StatVector} will throw
     * {@link IllegalStateException} if readOnly is true
     * 
     * This method differs from other getStats() as it provides direct control
     * of the final regex expression used, domain,world etc are used to populate
     * the respective fields of the returned StatVector
     * 
     * @param domain
     * @param world
     * @param category
     * @param statistic
     * @param regex
     * @param readOnly
     * @return
     */
    public StatVector getStats(String domain, String world, String category, String statistic, String regex,
            boolean readOnly) {
        StatVector vector = new StatVector(domain, world, category, statistic, readOnly);
        for (Entry<String, IStat> e : this.stats.entrySet()) {
            if (Pattern.matches(regex, e.getKey())) {
                vector.add(e.getValue());
            }
        }
        return vector;
    }

    private String starToRegex(String s) {
        if (s.equals("*")) {
            return "[a-zA-Z0-9_]*";
        }
        return s;
    }

    /**
     * Return all the stats!
     * 
     * @return
     */
    public Collection<IStat> getStats() {
        return this.stats.values();
    }

    public boolean hasStat(String domain, String world, String category, String statistic) {
        return this.stats.containsKey(domain + "::" + world + "::" + category + "::" + statistic);

    }

    @Override
    public int resolveVariable(String var) {
        String[] parts = var.split("\\::");
        String domain = BeardStat.DEFAULT_DOMAIN;
        String world = "*";
        String cat = "";
        String stat = "";
        if (parts.length == 4) {
            domain = parts[0];
            world = parts[1];
            cat = parts[2];
            stat = parts[3];
        }
        if (parts.length == 2) {
            cat = parts[0];
            stat = parts[1];
        } else {
            throw new IllegalStateException("Attempt to parse invalid varriable " + var);
        }
        return getStats(domain, world, cat, stat).getValue();
    }

    public String getType() {
        return this.type;
    }

    public EntityStatBlob cloneForArchive() {
        EntityStatBlob blob = new EntityStatBlob(this.name, this.entityId, this.type);
        blob.stats.clear();
        for (IStat stat : this.stats.values()) {
            if (stat.isArchive()) {
                IStat is = stat.clone();
                if (is != null) {
                    blob.addStat(is);
                    stat.clearArchive();
                }
            }
        }
        return blob;
    }

    @Override
    public int[] resolveReference(String array) {
        throw new UnsupportedOperationException("Array support not yet available."); //To change body of generated methods, choose Tools | Templates.
    }

}