package com.tehbeard.beardstat.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.BeardStat.Refs;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.utils.StatUtils;

public class StatVehicleListener extends StatListener {

    public StatVehicleListener(EntityStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (((event.getTo().getBlockX() != event.getFrom().getBlockX())
                || (event.getTo().getBlockY() != event.getFrom().getBlockY()) || (event.getTo().getBlockZ() != event
                .getFrom().getBlockZ()))) {

            Location from, to;
            Player player = (event.getVehicle().getPassenger() instanceof Player ? (Player) event.getVehicle()
                    .getPassenger() : null);
            if (player == null) {
                return;
            }

            if (!shouldTrackPlayer(player, Refs.TRACK_PLAYER_MOVE)) {
                return;
            }

            from = event.getFrom();
            to = event.getTo();
            if (from.getWorld().equals(to.getWorld())) {
                if (from.distance(to) < 10) {
                    StatUtils.instance.modifyStatEntity(player, "vehicle", event.getVehicle(), (int) Math.ceil(from.distance(to)));
                }
            }
        }

    }
}
