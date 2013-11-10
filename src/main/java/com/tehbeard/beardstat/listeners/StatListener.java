package com.tehbeard.beardstat.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.manager.EntityStatManager;

/**
 * Base listener for stats, adds helper methods
 * 
 * @author James
 * 
 */
public abstract class StatListener implements Listener {

    private final EntityStatManager playerStatManager;
    private final BeardStat         plugin;

    /**
     * @param worlds
     * @param playerStatManager
     * @param plugin
     */
    public StatListener( EntityStatManager playerStatManager, BeardStat plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }

    protected boolean shouldTrackPlayer(Player player) {
        return BeardStat.worldManager.shouldTrack(player);
    }
    
    protected boolean isBlacklistedWorld(World world){
        return BeardStat.worldManager.isBlackListed(world);
    }

    protected BeardStat getPlugin() {
        return this.plugin;
    }

    public EntityStatManager getPlayerStatManager() {
        return this.playerStatManager;
    }

}
