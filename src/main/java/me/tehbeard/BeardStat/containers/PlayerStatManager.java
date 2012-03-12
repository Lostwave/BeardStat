package me.tehbeard.BeardStat.containers;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import me.tehbeard.BeardStat.DataProviders.MysqlStatDataProvider;


import java.util.Map.Entry;

/**
 * Provides a cache between backend storage and the stats plugin
 * @author James
 *
 */
public class PlayerStatManager {

	private HashMap<String,PlayerStatBlob> cache = new HashMap<String,PlayerStatBlob>();
	private IStatDataProvider backendDatabase = null;

	public PlayerStatManager(IStatDataProvider database){
		backendDatabase = database;
	}
	/**
	 * clears the cache of all offline players, and optionally flushes the data to the backend storage.
	 * @param flush
	 */
	public void clearCache(List<String> onlinePlayers,boolean flush){
		if(backendDatabase == null){return;}
		Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();
		if(i==null){return;}
		while(i.hasNext()){
			Entry<String, PlayerStatBlob> entry = i.next();
			if(flush){
				String player = entry.getKey();
					long seconds = BeardStat.self().getSessionTime(player);

					BeardStat.printDebugCon("saving time: [Player : " + player +" ] time: " +Integer.parseInt(""+seconds));
				getPlayerBlob(player).getStat("stats","playedfor").incrementStat(Integer.parseInt(""+seconds));
					BeardStat.self().setLoginTime(player,(new Date()).getTime());
				
				backendDatabase.pushPlayerStatBlob(entry.getValue());
			}
			//remove offline players
			if(!onlinePlayers.contains(entry.getKey())){
				i.remove();
			}
			
		}
		backendDatabase.flush();

	}

	/**
	 * Force save of all cached stats to backend storage
	 */
	public void saveCache(){
		if(backendDatabase == null){return;}
		Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();

		while(i.hasNext()){
			Entry<String, PlayerStatBlob> entry = i.next();
			String player = entry.getKey();
			
				long seconds = BeardStat.self().getSessionTime(player);

				BeardStat.printDebugCon("saving time: [Player : " + player +" ] time: " +Integer.parseInt(""+seconds));
				getPlayerBlob(player).getStat("stats","playedfor").incrementStat(Integer.parseInt(""+seconds));
				BeardStat.self().setLoginTime(player,System.currentTimeMillis());
			
			backendDatabase.pushPlayerStatBlob(entry.getValue());
			
			
		}
		if(backendDatabase instanceof MysqlStatDataProvider){
			MysqlStatDataProvider sq = (MysqlStatDataProvider)backendDatabase;
			sq.flushNow();
		}
		else
		{
		backendDatabase.flush();
		}
	}



	/**
	 * Retrieve a players Stat Blob, or create one if it doesn't exist
	 * @param name
	 * @return
	 */
	public PlayerStatBlob getPlayerBlob(String name){
		if(backendDatabase == null){return null;}
		if(!cache.containsKey(name)){
			cache.put(name,backendDatabase.pullPlayerStatBlob(name));
		}
		return cache.get(name);
	}
	/**
	 * Finds a player's stat blob, but does not try to make it
	 * @param name player to find
	 * @return The player's stat blob or a null if not found
	 */
	public PlayerStatBlob findPlayerBlob(String name){
		if(backendDatabase == null){return null;}
		if(!cache.containsKey(name)){
			PlayerStatBlob pbs = backendDatabase.pullPlayerStatBlob(name,false);
			if(pbs==null){
				return null;
			}
			cache.put(name,pbs);
		}
		return cache.get(name);
	}
	public void flush(){
		
		backendDatabase.flush();
	}
}
