package me.tehbeard.BeardStat.StatCollectors;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.event.Event;

/**
 * Manages StatCollectors for BeardStat.
 * @author James
 *
 */
public class StatCollectorManager {
	
	private static HashMap<Event.Type,HashSet<IStatCollector>> collectors = new HashMap<Event.Type,HashSet<IStatCollector>>();

	/**
	 * Add a stat collector to an event.
	 * @param type
	 * @param collector
	 */
	public static void registerStatCollector(Event.Type type,IStatCollector collector){
		if(!collectors.containsKey(type)){
			collectors.put(type, new HashSet<IStatCollector>());
		}
		collectors.get(type).add(collector);
	}
	
	/**
	 * Return the collectors for an event.
	 * @param type
	 * @return
	 */
	public static HashSet<IStatCollector> getCollectors(Event.Type type){
		return collectors.get(type);
	}
	
	/**
	 * Wipe the StatCollectors
	 */
	public static void clearCollectors(){
		collectors.clear();
	}

}
