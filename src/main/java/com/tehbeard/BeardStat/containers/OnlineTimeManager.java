package com.tehbeard.BeardStat.containers;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;

/**
 * Manages online time tracking for a player
 * 
 * @author James
 * 
 */
public class OnlineTimeManager {

    public static class ManagerRecord {
        public ManagerRecord(String world, long time) {
            super();
            this.world = world;
            this.time = time;
        }

        public String world;
        private long  time;

        public int sessionTime() {
            return Integer.parseInt("" + ((System.currentTimeMillis() - this.time) / 1000L));
        }
    }

    private static HashMap<String, ManagerRecord> records = new HashMap<String, ManagerRecord>();

    public static ManagerRecord getRecord(String player) {
        return records.get(player);

    }

    public static void setRecord(Player player) {
        setRecord(player.getName(), player.getWorld().getName(), System.currentTimeMillis());
    }

    public static void setRecord(String player, String world, long time) {
        records.put(player, new ManagerRecord(world, time));

    }

    public static void wipeRecord(String player) {
        records.remove(player);
    }

    public static Collection<String> getPlayers() {
        return records.keySet();
    }
}
