package com.tehbeard.BeardStat.containers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import com.tehbeard.BeardStat.containers.OnlineTimeManager.ManagerRecord;

/**
 * Provides a cache between backend storage and the stats plugin/listeners
 * 
 * @author James
 * 
 */
public class PlayerStatManager implements CommandExecutor {

    private HashMap<String, Promise<EntityStatBlob>> cache           = new HashMap<String, Promise<EntityStatBlob>>();
    private IStatDataProvider                        backendDatabase = null;

    public PlayerStatManager(IStatDataProvider database) {
        this.backendDatabase = database;
    }

    /**
     * Force save of all cached stats to backend storage
     */
    public void saveCache() {

        if (this.backendDatabase == null) {
            return;
        }

        Iterator<Entry<String, Promise<EntityStatBlob>>> i = this.cache.entrySet().iterator();

        // iterate over cache and save
        while (i.hasNext()) {
            Entry<String, Promise<EntityStatBlob>> entry = i.next();
            String player = entry.getKey();

            // check if rejected promise, remove from cache silently
            if (entry.getValue().isRejected()) {
                BeardStat.printCon("Promise[" + player + "] was rejected (error?), removing from cache.");// alert
                // debug
                // dump
                i.remove();// clear it out
                continue;// Skip now
            }

            // skip if not resolved
            if (!entry.getValue().isResolved()) {
                continue;
            }

            // record time for player
            ManagerRecord timeRecord = OnlineTimeManager.getRecord(player);

            if (entry.getValue().getValue() != null) {
                if (timeRecord != null) {
                    BeardStat.printDebugCon("saving time: [Player : " + player + " , world: " + timeRecord.world
                            + ", time: " + timeRecord.sessionTime() + "]");
                    if (timeRecord.world != null) {
                        entry.getValue().getValue()
                                .getStat(BeardStat.DEFAULT_DOMAIN, timeRecord.world, "stats", "playedfor")
                                .incrementStat(timeRecord.sessionTime());
                    }
                }

                this.backendDatabase.pushPlayerStatBlob(entry.getValue().getValue());

                if (isPlayerOnline(player)) {
                    OnlineTimeManager.setRecord(player,Bukkit.getPlayer(player).getWorld().getName());
                } else {
                    OnlineTimeManager.wipeRecord(player);
                    i.remove();
                }
            } else {
                // Nulled player data
                BeardStat.printCon("Promise[" + player + "] had a null value! Removed from cache.");
                i.remove();
            }

        }

    }

    private boolean isPlayerOnline(String player) {
        return Bukkit.getOfflinePlayer(player).isOnline();
    }

    /**
     * Asyncronously retrieves a players Stat Blob, or create one if it doesn't
     * exist
     * 
     * @param name
     * @return a promise object that will later contain the stat blob or return
     *         an error
     */
    public Promise<EntityStatBlob> getPlayerBlobASync(String name) {
        if (this.backendDatabase == null) {
            return null;
        }
        if (!this.cache.containsKey(name)) {
            this.cache.put(name, this.backendDatabase.pullPlayerStatBlob(name));
        }
        return this.cache.get(name);
    }

    /**
     * Returns a stat blob immediately, halting the calling thread until it is
     * returned.
     * 
     * @param name
     * @return
     */
    public EntityStatBlob getPlayerBlob(String name) {
        return getPlayerBlobASync(name).getValue();
    }

    /**
     * Finds a player's stat blob, but does not try to make it
     * 
     * @param name
     *            player to find
     * @return The player's stat blob or a null if not found
     */
    public Promise<EntityStatBlob> findPlayerBlobASync(final String name) {
        if (this.backendDatabase == null) {
            return null;
        }
        if (!this.cache.containsKey(name)) {
            Promise<EntityStatBlob> pbs = this.backendDatabase.pullPlayerStatBlob(name, false);
            pbs.onResolve(new Delegate<Void, Promise<EntityStatBlob>>() {

                // add promise to cache on resolve
                @Override
                public <P extends Promise<EntityStatBlob>> Void invoke(P params) {
                    PlayerStatManager.this.cache.put(name, params);
                    return null;
                }
            });
            return pbs;
        }
        return this.cache.get(name);
    }

    /**
     * returns a player blob without creating it.
     * 
     * @param name
     * @return
     */
    public EntityStatBlob findPlayerBlob(String name) {
        return findPlayerBlobASync(name).getValue();
    }

    public void flush() {

        this.backendDatabase.flush();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Iterator<Entry<String, Promise<EntityStatBlob>>> i = this.cache.entrySet().iterator();
        sender.sendMessage("Players in Stat cache");
        while (i.hasNext()) {
            Entry<String, Promise<EntityStatBlob>> entry = i.next();
            String player = entry.getKey();
            sender.sendMessage(ChatColor.GOLD + player);
        }

        sender.sendMessage("Players in login cache");

        for (String player : OnlineTimeManager.getPlayers()) {
            sender.sendMessage(ChatColor.GOLD + player);
        }
        return true;
    }

    public boolean deletePlayer(String player) {
        if (!this.backendDatabase.hasStatBlob(player)) {
            return false;
        }
        this.cache.remove(player);
        this.backendDatabase.deletePlayerStatBlob(player);
        return true;
    }

}
