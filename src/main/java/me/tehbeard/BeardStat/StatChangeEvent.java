package me.tehbeard.BeardStat;

import me.tehbeard.BeardStat.containers.PlayerStat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatChangeEvent extends Event implements Cancellable {

	
	
	private PlayerStat stat;
	private int to;

	private boolean cancelled = false;
	public boolean isCancelled() {return cancelled;}
	public void setCancelled(boolean cancel) {cancelled = cancel;}
	
	public StatChangeEvent(PlayerStat stat,int to){
		this.stat = stat;
		this.to = to;
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(stat.getOwner().getName());
	}
	
	
	public int getOldValue(){
		return stat.getValue();
	}
	
	public int getNewValue(){
		return to;
	}
	
	public void setNewValue(int newValue){
		this.to = newValue;
		
	}

	
	
	
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {return handlers;}
	public static HandlerList getHandlerList() {return handlers;}

}
