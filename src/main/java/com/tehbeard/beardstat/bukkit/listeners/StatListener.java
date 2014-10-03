package com.tehbeard.beardstat.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.manager.EntityStatManager;

/**
 * Base listener for stats, adds helper methods
 * 
 * @author James
 * 
 */
public abstract class StatListener implements Listener {

    private final EntityStatManager playerStatManager;
    private final BukkitPlugin         plugin;

    /**
     * @param worlds
     * @param playerStatManager
     * @param plugin
     */
    public StatListener( EntityStatManager playerStatManager, BukkitPlugin plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }

    protected boolean shouldTrackPlayer(Player player, String trackType) {
        return BukkitPlugin.worldManager.shouldTrack(player, trackType);
    }
    
    protected BukkitPlugin getPlugin() {
        return this.plugin;
    }

    public EntityStatManager getPlayerStatManager() {
        return this.playerStatManager;
    }

}
