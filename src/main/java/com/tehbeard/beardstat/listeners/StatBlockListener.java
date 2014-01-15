package com.tehbeard.beardstat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.utils.StatUtils;

public class StatBlockListener extends StatListener {

    public StatBlockListener(EntityStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        StatUtils.statPlayer(event.getPlayer(), "stats", "totalblockcreate", 1);
        StatUtils.statBlock(event.getPlayer(), "blockcreate", event.getBlock(), 1);
        

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        StatUtils.statPlayer(event.getPlayer(), "stats", "totalblockdestroy", 1);
        StatUtils.statBlock(event.getPlayer(), "blockdestroy", event.getBlock(), 1);
    }

}
