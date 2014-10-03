package com.tehbeard.beardstat.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.Refs;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.bukkit.utils.StatUtils;

public class StatBlockListener extends StatListener {

    public StatBlockListener(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        super(playerStatManager, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer(), Refs.TRACK_BLOCK_PLACE)) {
            return;
        }
        StatUtils.instance.modifyStatPlayer(event.getPlayer(), "stats", "totalblockcreate", 1);
        StatUtils.instance.modifyStatBlock(event.getPlayer(), "blockcreate", event.getBlock(), 1);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer(), Refs.TRACK_BLOCK_BREAK)) {
            return;
        }

        StatUtils.instance.modifyStatPlayer(event.getPlayer(), "stats", "totalblockdestroy", 1);
        StatUtils.instance.modifyStatBlock(event.getPlayer(), "blockdestroy", event.getBlock(), 1);
    }

}
