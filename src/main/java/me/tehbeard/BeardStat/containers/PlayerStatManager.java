package me.tehbeard.BeardStat.containers;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
/**
 * Provides a cache between backend storage and the stats plugin
 * @author James
 *
 */
public class PlayerStatManager {

	private static HashMap<String,PlayerStatBlob> cache = new HashMap<String,PlayerStatBlob>();
	private static IStatDataProvider backendDatabase = null;

	public static void setDatabase(IStatDataProvider database){
		PlayerStatManager.backendDatabase = database;
	}
	/**
	 * clears the cache of all offline players, and optionally flushes the data to the backend storage.
	 * @param flush
	 */
	public static void clearCache(boolean flush){
		if(backendDatabase == null){return;}
		Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();
		if(i==null){return;}
		while(i.hasNext()){
			Entry<String, PlayerStatBlob> entry = i.next();
			if(flush){
				String player = entry.getKey();
				if(BeardStat.loginTimes.containsKey(player)){
					long seconds = (((new Date()).getTime() - BeardStat.loginTimes.get(player))/1000L);
					BeardStat.printDebugCon("saving time: [Player : " + player +" ] time: " +Integer.parseInt(""+seconds));
					PlayerStatManager.getPlayerBlob(player).getStat("stats","playedfor").incrementStat(Integer.parseInt(""+seconds));
					BeardStat.loginTimes.put(player,(new Date()).getTime());
				}
				backendDatabase.pushPlayerStatBlob(entry.getValue());
			}
			//remove offline players
			if(BeardStat.self.getServer().getPlayer(entry.getKey())==null){
				i.remove();
			}
			
		}
		backendDatabase.flush();

	}

	/**
	 * Force save of all cached stats to backend storage
	 */
	public static void saveCache(){
		if(backendDatabase == null){return;}
		Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();

		while(i.hasNext()){
			Entry<String, PlayerStatBlob> entry = i.next();
			String player = entry.getKey();
			if(BeardStat.loginTimes.containsKey(player)){
				long seconds = (((new Date()).getTime() - BeardStat.loginTimes.get(player))/1000L);
				BeardStat.printDebugCon("saving time: [Player : " + player +" ] time: " +Integer.parseInt(""+seconds));
				PlayerStatManager.getPlayerBlob(player).getStat("stats","playedfor").incrementStat(Integer.parseInt(""+seconds));
				BeardStat.loginTimes.put(player,(new Date()).getTime());
			}
			
			BeardStat.loginTimes.put(player,0L);
			backendDatabase.pushPlayerStatBlob(entry.getValue());
			
			
		}
		backendDatabase.flush();
	}



	/**
	 * Retrieve a players Stat Blob, or create one if it doesn't exist
	 * @param name
	 * @return
	 */
	public static PlayerStatBlob getPlayerBlob(String name){
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
	public static PlayerStatBlob findPlayerBlob(String name){
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
	public static void flush(){
		backendDatabase.flush();
	}
}
