package com.tehbeard.beardstat.containers;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.tehbeard.utils.expressions.VariableProvider;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import java.util.HashMap;

/**
 * Represents a collection of statistics bound to an entity Currently only used for Players.
 *
 * @author James
 *
 */
public class EntityStatBlob implements VariableProvider {

    private Map<String, IStat> stats = new ConcurrentHashMap<String, IStat>();
    private int entityId;
    private String name;
    private String type;
    private String uuid;
    
    private IStatDataProvider provider;
    
    private Map<String,DocumentFile> files = new HashMap<String, DocumentFile>();

    /**
     * The name of the entity this EntityStatBlob is associated with.
     * @return 
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the internal database id of the entity, not for public use.
     * @return 
     */
    public int getEntityID() {
        return this.entityId;
    }

    /**
     * 
     * @param name name of the entity
     * @param entityId internal database id
     * @param type
     * @param uuid 
     */
    public EntityStatBlob(String name, int entityId, String type, String uuid,IStatDataProvider provider) {
        this.name = name;
        this.entityId = entityId;
        this.type = type;
        this.uuid = uuid;
        this.provider = provider;
    }

    /**
     * Adds a IStat object to this EntityStatBlob
     * @param stat 
     */
    public void addStat(IStat stat) {
        this.stats.put(
                stat.getDomain() + "::" + stat.getWorld() + "::" + stat.getCategory() + "::" + stat.getStatistic(),
                stat);
        stat.setOwner(this);
    }
    
    /**
     * Returns a stat object from the default (BeardStat) domain, see other getStat() for details.
     * @param world
     * @param category
     * @param statistic
     * @return 
     */
    public IStat getStat(String world, String category, String statistic) {
        return getStat(BeardStat.DEFAULT_DOMAIN,world,category,statistic);
    }

    /**
     * Returns a stat object for the supplied coordinates
     * @param domain domain of the stats,
     * @param world world name stat is under
     * @param category category stat is under
     * @param statistic name of statistic
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
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines multiple stats into one easy to access object {@link StatVector} supports the use of regex, with the shortcut "*"
     * to denote all possible values (substituted for ".*" in regex engine) Defaults to readonly mode, any mutators called on this {@link StatVector} will throw {@link IllegalStateException}
     * if readOnly is true
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
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines multiple stats into one easy to access object {@link StatVector} supports the use of regex, with the shortcut "*"
     * to denote all possible values (substituted for ".*" in regex engine) Defaults to readonly mode, any mutators called on this {@link StatVector} will throw {@link IllegalStateException}
     * if readOnly is true
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
     * Query this blob for a {@link StatVector}, a {@link StatVector} combines multiple stats into one easy to access object {@link StatVector} supports the use of regex, with the shortcut "*"
     * to denote all possible values (substituted for ".*" in regex engine) Defaults to readonly mode, any mutators called on this {@link StatVector} will throw {@link IllegalStateException}
     * if readOnly is true
     *
     * This method differs from other getStats() as it provides direct control of the final regex expression used, domain,world etc are used to populate the respective fields of the returned
     * StatVector
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

    /**
     * Checks if a stat under these coordinates has been recorded.
     * @param domain
     * @param world
     * @param category
     * @param statistic
     * @return 
     */
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

    /**
     * Return the entity type of this EntityStatBlob
     * @return 
     */
    public String getType() {
        return this.type;
    }

    public EntityStatBlob cloneForArchive() {
        EntityStatBlob blob = new EntityStatBlob(this.name, this.entityId, this.type, uuid,provider);
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

    public String getUUID() {
        return uuid;
    }
    
    public DocumentFile getDocument(String domain,String key){
        String code = domain + "::" + key;
        if(!files.containsKey(code)){
            files.put(code,provider.pullDocument(entityId, domain, key));
        }
        return files.get(code);
    }
    
    public Collection<DocumentFile> getLoadedFiles(){
        return files.values();
    }
}