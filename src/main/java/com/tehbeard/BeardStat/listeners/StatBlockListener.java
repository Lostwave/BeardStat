package com.tehbeard.BeardStat.listeners;

import java.util.List;

import net.dragonzone.promise.Promise;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.listeners.defer.DelegateIncrement;
import com.tehbeard.BeardStat.utils.MetaDataCapture;

public class StatBlockListener implements Listener {

    List<String>              worlds;

    private PlayerStatManager playerStatManager;

    public StatBlockListener(List<String> worlds, PlayerStatManager playerStatManager) {
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if ((event.getPlayer().getGameMode() == GameMode.CREATIVE)
                && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode", false)) {
            return;
        }
        if ((event.isCancelled() == false) && !this.worlds.contains(event.getPlayer().getWorld().getName())) {
            Promise<EntityStatBlob> promiseblob = this.playerStatManager
                    .getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld()
                    .getName(), "stats", "totalblockcreate", 1));
            MetaDataCapture.saveMetaDataMaterialStat(promiseblob, BeardStat.DEFAULT_DOMAIN, event.getPlayer()
                    .getWorld().getName(), "blockcreate", event.getBlock().getType(), event.getBlock().getData(), 1);

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getGameMode() == GameMode.CREATIVE)
                && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode", false)) {
            return;
        }
        if ((event.isCancelled() == false) && !this.worlds.contains(event.getPlayer().getWorld().getName())) {
            Promise<EntityStatBlob> promiseblob = this.playerStatManager
                    .getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld()
                    .getName(), "stats", "totalblockdestroy", 1));
            MetaDataCapture.saveMetaDataMaterialStat(promiseblob, BeardStat.DEFAULT_DOMAIN, event.getPlayer()
                    .getWorld().getName(), "blockdestroy", event.getBlock().getType(), event.getBlock().getData(), 1);
        }
    }

}
