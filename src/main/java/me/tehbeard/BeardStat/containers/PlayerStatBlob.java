package me.tehbeard.BeardStat.containers;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a collection of player statistics
 * @author James
 *
 */
public class PlayerStatBlob{

	private HashSet<PlayerStat> stats;
	private String name;
	public String getName() {
		return name;
	}

	public int getPlayerID() {
		return playerID;
	}
	private int playerID;
	
	/**
	 * 
	 * @param name Players name
	 * @param ID playerID in database
	 */
	public PlayerStatBlob(String name,int ID){
		this.name = name;
		playerID=ID;
		 stats = new HashSet<PlayerStat>();
	}
	
	/**
	 * add stat
	 * @param stat
	 */
	public void addStat(PlayerStat stat){
		stats.add(stat);
	}
	
	/**
	 * Get a players stat, creates new object if not found.
	 * @param name
	 * @return
	 */
	public PlayerStat getStat(String cat,String name){
		for(PlayerStat ps: stats){
			if(ps.getName().equals(name) && ps.getCat().equals(cat)){
				return ps;
			}
		}
		PlayerStat psn = new PlayerStat(cat,name,0);
		addStat(psn);
		return psn;
	}
	/**
	 * Return all the stats!
	 * @return
	 */
	public Collection<PlayerStat> getStats(){
		return stats;
	}
	
	public boolean hasStat(String cat,String stat){
		for(PlayerStat ps: stats){
			if(ps.getName().equals(stat) && ps.getCat().equals(cat)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Expose create tag
	 */

}