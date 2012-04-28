package me.tehbeard.BeardStat.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.TopPlayed;
import me.tehbeard.BeardStat.containers.TopPlayedManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopPlayedCommand implements CommandExecutor {

	private TopPlayedManager topPlayedManager;
	private Date dataAge;
	private List<TopPlayed> topPlayed = null;
	
	public TopPlayedCommand(TopPlayedManager topPlayedManager) {
		this.topPlayedManager = topPlayedManager;
	}
	

    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {

    	if(!BeardStat.hasPermission(sender, "command.topplayed")){
			BeardStat.sendNoPermissionError(sender);
        	return true;
        }
    	
    	Date now = new Date();
    	
    	// if our data is more than an hour old
    	if(dataAge == null || (dataAge.getTime() - now.getTime()) > 3600000){
    	
    		topPlayed = topPlayedManager.getTopPlayed();
    	
    		dataAge = new Date();
    	}
    	
    	if(topPlayed == null)
    	{ sender.sendMessage(ChatColor.RED + "Unable to retrieve the top players, not supported on flatfile data source.");}
    	
  
    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    	sender.sendMessage("/--------------------------------------------------------\\");
    	sender.sendMessage("| " + ChatColor.RED + "Top " + padLeft(Integer.toString(BeardStat.self().TopPlayerCount()), 3) + " players of all time:                                        |");
    	sender.sendMessage("| " + ChatColor.GOLD + "Rank " + ChatColor.WHITE + 
    			"| " + ChatColor.DARK_RED + "Player           " + ChatColor.WHITE + 
    			"| " + ChatColor.DARK_GRAY + "Time            " + ChatColor.WHITE + 
    			"| " + ChatColor.GRAY + "FirstLogin " + ChatColor.WHITE + "|");
    	sender.sendMessage("|------|------------------|-----------------|------------|");
		
    	for(TopPlayed item : topPlayed){
    		Player player = Bukkit.getPlayer(item.playername);
    		String coloredplayer = item.playername; //Bukkit.getOfflinePlayer(item.playername).getPlayer().getPlayerListName();
    		if(player != null){
    		  coloredplayer = player.getDisplayName();	
    		}
 			sender.sendMessage("| " + ChatColor.GOLD + padLeft(Integer.toString(item.rank), 4) + ChatColor.WHITE + 
	    			" | " + padRight(coloredplayer, 16) + ChatColor.WHITE + 
	    			" | " + ChatColor.DARK_GRAY + item.time + ChatColor.WHITE + 
	    			" | " + ChatColor.GRAY + f.format(item.firstOn) + ChatColor.WHITE + " |");
		}
    	sender.sendMessage("\\--------------------------------------------------------/");
		
		return true;
    }
    
    private static String padRight(String s, int n) { 
        return String.format("%1$-" + n + "s", s);   
    }
    
    private static String padLeft(String s, int n) { 
        return String.format("%1$#" + n + "s", s);   
    } 


}