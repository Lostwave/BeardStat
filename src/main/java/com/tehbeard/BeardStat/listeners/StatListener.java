package com.tehbeard.BeardStat.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Base listener for stats, adds helper methods
 * 
 * @author James
 * 
 */
public abstract class StatListener implements Listener {

    private final PlayerStatManager playerStatManager;
    private final BeardStat         plugin;

    /**
     * @param worlds
     * @param playerStatManager
     * @param plugin
     */
    public StatListener( PlayerStatManager playerStatManager, BeardStat plugin) {
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

    public PlayerStatManager getPlayerStatManager() {
        return this.playerStatManager;
    }

}
