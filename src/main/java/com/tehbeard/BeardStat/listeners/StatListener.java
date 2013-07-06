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

    protected final List<String>      worlds;
    protected final PlayerStatManager playerStatManager;
    protected final BeardStat         plugin;

    /**
     * @param worlds
     * @param playerStatManager
     * @param plugin
     */
    public StatListener(List<String> worlds, PlayerStatManager playerStatManager, BeardStat plugin) {
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }

    protected boolean shouldTrackPlayer(Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            return true;
        }
        return this.plugin.getConfig().getBoolean("stats.trackcreativemode", false);
    }

    protected boolean isBlacklistedWorld(World w) {
        return this.worlds.contains(w.getName());
    }

    protected boolean shouldTrack(Player p, World w) {
        return shouldTrackPlayer(p) && !isBlacklistedWorld(w);
    }

}
