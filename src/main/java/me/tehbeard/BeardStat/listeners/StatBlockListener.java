package me.tehbeard.BeardStat.listeners;

import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import net.dragonzone.promise.Promise;

import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


public class StatBlockListener implements Listener{

    List<String> worlds;

    private PlayerStatManager playerStatManager;

    public StatBlockListener(List<String> worlds,	PlayerStatManager playerStatManager){
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;

    }


    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement("stats","totalblockcreate",1));
            MetaDataCapture.saveMetaDataMaterialStat(promiseblob, 
                    "blockcreate", 
                    event.getBlock().getType(), 
                    event.getBlock().getData(), 
                    1);
            
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement("stats","totalblockdestroy",1));
            MetaDataCapture.saveMetaDataMaterialStat(promiseblob, 
                    "blockdestroy", 
                    event.getBlock().getType(), 
                    event.getBlock().getData(), 
                    1);
        }
    }



}
