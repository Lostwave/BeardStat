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

public class LastOnCommand implements CommandExecutor {



    public boolean onCommand(CommandSender sender, Command command, String cmdLabel,
            String[] args) {

    	if(!BeardStat.hasPermission(sender, "command.laston")){
			BeardStat.sendNoPermissionError(sender);
			return true;
		}
    	
    	if(args.length==1){
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if(player!=null){
            	// shouldn't it use our stat?
                Date d = new Date(player.getLastPlayed());
                sender.sendMessage(ChatColor.DARK_RED + args[0] +" was last on "+ ChatColor.GOLD + (new SimpleDateFormat()).format(d));
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "Could not find record for player " + args[0] + ".");

        }
        if(args.length == 0){
            if(BeardStat.self().getConfig().getBoolean("stats.lastonall",false)){
            	int count = 0;
                for(OfflinePlayer p:Bukkit.getOfflinePlayers()){
                	// limit to 100 to keep it from going forever.
                	if(count > 100) return true;
                	
                	// shouldn't it use our stat?
                    Date d = new Date(p.getLastPlayed());
                    sender.sendMessage(ChatColor.DARK_RED + p.getName() +" was last on "+ ChatColor.GOLD + (new SimpleDateFormat()).format(d));
                    count++;
                }
            }
        }

        return false;


    }
}