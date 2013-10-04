package com.tehbeard.BeardStat.listeners;

import java.util.List;

import net.dragonzone.promise.Promise;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.listeners.defer.DelegateIncrement;
import com.tehbeard.BeardStat.utils.MetaDataCapture;

public class StatBlockListener extends StatListener {

    public StatBlockListener(PlayerStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getPlayerBlobASync(
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

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getPlayerBlobASync(
                event.getPlayer().getName());
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld().getName(),
                "stats", "totalblockdestroy", 1));
        MetaDataCapture.saveMetaDataMaterialStat(promiseblob, BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld()
                .getName(), "blockdestroy", event.getBlock().getType(), event.getBlock().getData(), 1);
    }

}
