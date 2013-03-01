package com.tehbeard.BeardStat.listeners;

import java.util.List;

import net.dragonzone.promise.Promise;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;


public class StatVehicleListener implements Listener {

    
    private PlayerStatManager playerStatManager;
    private List<String> worlds;
    
    public StatVehicleListener(List<String> worlds,PlayerStatManager playerStatManager){
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;
    }
    
    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
    public void onVehicleMove(VehicleMoveEvent event){
        if(
                (event.getTo().getBlockX() != event.getFrom().getBlockX() || 
                event.getTo().getBlockY() != event.getFrom().getBlockY() || 
                event.getTo().getBlockZ() != event.getFrom().getBlockZ() )&& 
                !worlds.contains(event.getVehicle().getWorld().getName())){

            
            Location from,to;
            Player player = (event.getVehicle().getPassenger() instanceof Player ? (Player)event.getVehicle().getPassenger() : null);
            if(player==null){return;}
            
            if(player.getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
        		return;
        	}
            
            from = event.getFrom();
            to = event.getTo();
            BeardStat.printDebugCon("Vehicle move fired!");
            if(from.getWorld().equals(to.getWorld())){
                if(from.distance(to) < 10){
                    Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(player.getName());
                    promiseblob.onResolve(new DelegateIncrement(
                            "vehicle",
                            event.getVehicle().getType().toString().toLowerCase(),
                            (int)Math.ceil(from.distance(to))));
                    
                    
                }
            }
        }

    }
}
