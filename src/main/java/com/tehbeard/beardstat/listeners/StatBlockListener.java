package com.tehbeard.beardstat.listeners;

import net.dragonzone.promise.Promise;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.listeners.defer.DelegateIncrement;
import com.tehbeard.beardstat.utils.MetaDataCapture;

public class StatBlockListener extends StatListener {

    public StatBlockListener(EntityStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                event.getPlayer().getName());
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld().getName(),
                "stats", "totalblockcreate", 1));
        MetaDataCapture.saveMetaDataMaterialStat(promiseblob, BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld()
                .getName(), "blockcreate", event.getBlock().getType(), event.getBlock().getData(), 1);

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                event.getPlayer().getName());
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld().getName(),
                "stats", "totalblockdestroy", 1));
        MetaDataCapture.saveMetaDataMaterialStat(promiseblob, BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld()
                .getName(), "blockdestroy", event.getBlock().getType(), event.getBlock().getData(), 1);
    }

}
