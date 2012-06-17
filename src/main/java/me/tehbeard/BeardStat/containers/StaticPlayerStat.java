package me.tehbeard.BeardStat.containers;

import org.bukkit.Bukkit;

import me.tehbeard.BeardStat.StatChangeEvent;

/**
 * Represents a players stat
 * @author James
 *
 */

public class StaticPlayerStat implements PlayerStat{

	PlayerStatBlob owner = null;
	private String name;
	private int value;
	private String cat="stats";

	private boolean archive;
	
	public StaticPlayerStat(String cat,String name,int value){
		this.name=name;
		this.value=value;
		this.cat=cat;
	}



	/**
	 * Get the stats value
	 * @return
	 */
	public int getValue(){
		return value;
	}

	/**
	 * Set the stats value
	 * @param value
	 */
	public void setValue(int value){
		changeValue(value);
	}

	/**
	 * Get the stats name
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * Increment the stat by i 
	 * @param i
	 */
	public void incrementStat(int i){
		if(i < 0 ){throw new IllegalArgumentException("Cannot increment by negative number!");}
		changeValue(value + i);
	}

	/**
	 * decrement the stat by i
	 * @param i
	 */
	public void decrementStat(int i){
		if(i < 0 ){throw new IllegalArgumentException("Cannot decrement by negative number!");}
		changeValue(value - i);
	}


	public String getCat() {	
		return cat;
	}

	public void clearArchive() {
		this.archive = false;
	}

	public boolean isArchive() {
		return archive;
	}

	public PlayerStatBlob getOwner(){
		return owner;
	}
	
	private void changeValue(int to){
		StatChangeEvent event = new StatChangeEvent(this, to);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			value = event.getNewValue();
			archive = true;
		}
	}



	public void setOwner(PlayerStatBlob playerStatBlob) {
		owner = playerStatBlob;
	}
	
	

}
