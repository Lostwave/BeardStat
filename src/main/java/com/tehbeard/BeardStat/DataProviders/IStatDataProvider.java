package com.tehbeard.BeardStat.DataProviders;

import java.util.List;

import net.dragonzone.promise.Promise;

import com.tehbeard.BeardStat.containers.EntityStatBlob;

/**
 * Provides push/pull service for getting and saving stats to a backend storage
 * system.
 * 
 * @author James
 * 
 */
public interface IStatDataProvider {

    /**
     * Pull the players stats from the database.
     * 
     * @param player
     *            Player to pull stats for. Creates a new object if non exists
     * @return a PlayerStatBlob containing all stats for a player
     */
    public Promise<EntityStatBlob> pullStatBlob(String player, String type);

    /**
     * Pull the players stats from the database.
     * 
     * @param player
     *            Player to pull stats for. Creates a new object if non exists
     * @param create
     *            wether to create the player object
     * @return a PlayerStatBlob containing all stats for a player
     */
    public Promise<EntityStatBlob> pullStatBlob(String player, String type, boolean create);

    /**
     * Push all stats for this player to the database. This may happen
     * immediately or at some point in the future
     * 
     * @param player
     *            StatBlob to push to the database
     */
    public void pushStatBlob(EntityStatBlob player);

    /**
     * Forces the DataProvider to flush data to the backend, in the case of a
     * second level cache or queue.
     */
    public void flush();

    /**
     * Forces the DataProvider to flush data to the backend, in the case of a
     * second level cache. Execution must block the caller until completion
     */
    public void flushSync();

    /**
     * Deletes all stats for a player as stored in the database
     * 
     * @param player
     *            Player to delete stats of
     */
    public void deleteStatBlob(String player);

    /**
     * Has a stat blob for a player
     * 
     * @param player
     *            player name
     * @return blob exists
     */
    public boolean hasStatBlob(String player);

    /**
     * List of players held by provider
     * 
     * @return
     */
    public List<String> getStatBlobsHeld();
}