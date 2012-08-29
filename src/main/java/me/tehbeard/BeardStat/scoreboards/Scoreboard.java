package me.tehbeard.BeardStat.scoreboards;

import org.bukkit.configuration.Configuration;

/**
 * Represents an a record of a scoreboard
 * @author James
 *
 */
public class Scoreboard {

    private final String tag;
    private final String name;
    private final int entryLimit;
    private final int maxEntriesPerPlayer;
    private final boolean desc;
    private ScoreboardManager manager;
    /**
     * @param tag
     * @param name
     * @param entryLimit
     * @param maxEntriesPerPlayer
     * @param desc
     */
    public Scoreboard(String tag, String name, int entryLimit,
            int maxEntriesPerPlayer, boolean desc) {
        this.tag = tag;
        this.name = name;
        this.entryLimit = entryLimit;
        this.maxEntriesPerPlayer = maxEntriesPerPlayer;
        this.desc = desc;
    }
    
    public Scoreboard(Configuration config){
        tag = config.getString("tag");
        name = config.getString("name");
        entryLimit=config.getInt("entryLimit");
        maxEntriesPerPlayer=config.getInt("maxEntriesPerPlayer");
        desc = config.getBoolean("desc");
    }

    protected void setManager(ScoreboardManager manager) {
        this.manager = manager;
    }
    public String getTag() {
        return tag;
    }
    public String getName() {
        return name;
    }
    public int getEntryLimit() {
        return entryLimit;
    }
    public int getMaxEntriesPerPlayer() {
        return maxEntriesPerPlayer;
    }
    public boolean isDesc() {
        return desc;
    }
    
    
}
