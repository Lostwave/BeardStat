package me.tehbeard.BeardStat.commands;

import java.util.Date;


import me.tehbeard.BeardStat.BeardStat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LastOnCommand implements CommandExecutor {



    public boolean onCommand(CommandSender sender, Command command, String cmdLabel,
            String[] args) {
        Player pp = null ;
        if(args.length==1){
            if(!BeardStat.hasPermission(sender, "command.laston")){return true;}
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if(player!=null){
                Date d = new Date(player.getLastPlayed());
                sender.sendMessage(ChatColor.GOLD + d.toString());
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "Could not find record for player " + args[0] + ".");

        }

        return false;


    }
}