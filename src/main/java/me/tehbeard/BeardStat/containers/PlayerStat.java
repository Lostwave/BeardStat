package me.tehbeard.BeardStat.containers;

/**
 * Represents a players stat
 * @author James
 *
 */

public class PlayerStat{


	private String name;
	private int value;
	private String cat="stats";

	private boolean archive;
	
	public PlayerStat(String cat,String name,int value){
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
		this.value = value;
		archive=true;
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
		value += i;
		archive=true;
	}

	/**
	 * decrement the stat by i
	 * @param i
	 */
	public void decrementStat(int i){
		value -= i;
		archive=true;
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


}
