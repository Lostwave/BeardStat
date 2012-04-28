package me.tehbeard.BeardStat.commands;

import java.text.SimpleDateFormat;
import java.util.Date;


import me.tehbeard.BeardStat.BeardStat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FirstOnCommand implements CommandExecutor {



    public boolean onCommand(CommandSender sender, Command command, String cmdLabel,
            String[] args) {

    	if(!BeardStat.hasPermission(sender, "command.laston")){
			BeardStat.sendNoPermissionError(sender);
        	return true;
        }
    	
    	Date d = null;
    	if(args.length==1){
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if(player!=null){
            	// shouldn't it use our stat?
                d = new Date(player.getFirstPlayed());
            }

        }
    	else if(args.length == 0){
        	Player player = (Player)sender; 
            if(player!=null){
            	// shouldn't it use our stat?
            	d = new Date(player.getFirstPlayed());
            }
        }
        
        if(d != null){
            sender.sendMessage(ChatColor.DARK_RED + args[0] +" was first on "+ ChatColor.GOLD + (new SimpleDateFormat()).format(d));
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "Could not find record for player " + args[0] + ".");
        return false;
    }
}