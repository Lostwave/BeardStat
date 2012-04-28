package me.tehbeard.BeardStat.DataProviders;

import java.util.List;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.TopPlayed;


/**
 * Provides push/pull service for stats to a backend storage facility
 * @author James
 *
 */
public abstract class IStatDataProvider {

	/**
	 * creates a new instance of the data provider
	 * @return 
	 */
	public static IStatDataProvider newInstance(){
		return null;
	}


	/**
	 * Pull the players stats from the database.
	 * @param player Player to pull stats for. Creates a new object if non exists
	 * @return a PlayerStatBlob containing all stats for a player
	 */
	public abstract PlayerStatBlob pullPlayerStatBlob(String player);


	/**
	 * Pull the players stats from the database.
	 * @param player Player to pull stats for. Creates a new object if non exists
	 * @param create wether to create the player object
	 * @return a PlayerStatBlob containing all stats for a player
	 */
	public abstract PlayerStatBlob pullPlayerStatBlob(String player,boolean create);
	/**
	 * Push all stats for this player to the database
	 * @param player StatBlob to push to the database
	 */
	public abstract void pushPlayerStatBlob(PlayerStatBlob player);
	
	/**
	 * Forces the DataProvider to flush data to the backend, in the case of a second level cache.
	 */
	public abstract void flush();
	

	/**
	 * Gets the top 20 players from the database
	 */
	public abstract List<TopPlayed> pullTopPlayed();

}