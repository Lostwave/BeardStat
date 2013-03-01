package com.tehbeard.BeardStat.containers;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tehbeard.BeardStat.BeardStat;

import me.tehbeard.utils.expressions.VariableProvider;

/**
 * Represents a collection of player statistics
 * @author James
 *
 */
public class EntityStatBlob implements VariableProvider{

	private static Map<String,String> dynamics = new HashMap<String, String>();

	private static Map<String,String> dynamicsSaved = new HashMap<String, String>();

	public static void addDynamicStat(String stat,String expr){
		dynamics.put(stat,expr);
	}

	public static void addDynamicSavedStat(String stat,String expr){
		dynamicsSaved.put(stat,expr);
	}

	private void addDynamics(){
		for(Entry<String, String> entry  : dynamics.entrySet()){

			BeardStat.printDebugCon("Making temporary stat: " + (entry.getKey().split("\\.")[0] + " " +  entry.getKey().split("\\.")[1] + " = " + entry.getValue()));
			addStat(new DynamicStat(entry.getKey().split("\\.")[0], entry.getKey().split("\\.")[1],entry.getValue()));
		}

		//dynamics that will be saved to database
		for(Entry<String, String> entry  : dynamicsSaved.entrySet()){

			BeardStat.printDebugCon("Making custom stat: " + (entry.getKey().split("\\.")[0] + " " +  entry.getKey().split("\\.")[1] + " = " + entry.getValue()));
			addStat(new DynamicStat(entry.getKey().split("\\.")[0], entry.getKey().split("\\.")[1],entry.getValue(),true));
		}
	}

	private HashSet<IStat> stats;

	private int entityId;
	private String name;
	private String type;

	public String getName() {
		return name;
	}

	public int getEntityID() {
		return entityId;
	}


	/**
	 * 
	 * @param name Players name
	 * @param ID playerID in database
	 */
	public EntityStatBlob(String name,int entityId,String type){
		this.name = name;
		this.entityId=entityId;
		this.type = type;
		stats = new HashSet<IStat>();

		addDynamics();
	}

	/**
	 * add stat
	 * @param stat
	 */
	public void addStat(IStat stat){
		stats.add(stat);
		stat.setOwner(this);
	}

	/**
	 * Get a players stat, creates new object if not found.
	 * @param name
	 * @return
	 */
	public IStat getStat(String domain,String world,String cat,String name){
		for(IStat ps: stats){
			if(     ps.getDomain().equals(domain) &&
					ps.getWorld().equals(domain) && 
					ps.getCategory().equals(cat) &&
					ps.getStatistic().equals(name)){
				return ps;
			}
		}
		IStat psn = new StaticStat(domain,world,cat,name,0);
		psn.setValue(0);
		addStat(psn);
		return psn;
	}
	/**
	 * Return all the stats!
	 * @return
	 */
	public Set<IStat> getStats(){
		return  new HashSet<IStat>(stats);
	}

	public boolean hasStat(String domain,String world,String cat,String name){
		for(IStat ps: stats){
			if(     ps.getDomain().equals(domain) &&
					ps.getWorld().equals(domain) && 
					ps.getCategory().equals(cat) &&
					ps.getStatistic().equals(name)){
				return true;
			}
		}
		return false;
	}

	public int resolveVariable(String var) {
		String[] parts = var.split("\\.");
		return getStat(
				parts[0],
				parts[1],
				parts[2],
				parts[3]
				).getValue();
	}

	public String getType() {
		return type;
	}


	public EntityStatBlob cloneForArchive(){
		EntityStatBlob blob = new EntityStatBlob(name, entityId, type);
		for(IStat stat : stats){
			if(stat.isArchive()){
				IStat is = stat.clone();
				if(is!=null){
					blob.stats.add(is);
					stat.clearArchive();
				}
			}
		}

		return blob;
	}

}