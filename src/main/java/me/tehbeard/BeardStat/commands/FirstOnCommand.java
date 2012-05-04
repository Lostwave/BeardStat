package me.tehbeard.BeardStat.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.BeardStat.BeardStat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FirstOnCommand implements CommandExecutor {

    private static final String FIRSTPLAYEDCAT = "stats";
    private static final String FIRSTPLAYEDSTAT = "firstlogin";
    private PlayerStatManager playerStatManager;

    public FirstOnCommand(PlayerStatManager statmanager){
        playerStatManager = statmanager;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {

        if(!BeardStat.hasPermission(sender, "command.laston")){
            BeardStat.sendNoPermissionError(sender);
            return true;
        }
        
        String name = "";
        long bukkitDate = 0;
        PlayerStatBlob blob = null;
        
        if(args.length==1){
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            name = args[0];
            if(player!=null){
                // shouldn't it use our stat?
                bukkitDate = player.getFirstPlayed();
            }

            blob = playerStatManager.getPlayerBlob(args[0]);
        }
        else if(args.length == 0){
            if(! (sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "You cannot run this command from the console with no arguments, you must specify a player name.  Use: firston <player>");
                return true;
            }
            
            Player player = (Player)sender; 
            if(player!=null){
                name = player.getName();
                bukkitDate = player.getFirstPlayed();
                blob = playerStatManager.getPlayerBlob(name);
            }
        }
        
        sender.sendMessage(GetFirstOnString(name, blob, bukkitDate));
        return true;
    }
    
    public static String[] GetFirstOnString(String name, PlayerStatBlob blob, long date){
        ArrayList<String> output = new ArrayList<String>();
        
        if(date > 0)
        { output.add(ChatColor.DARK_RED + "According to Bukkit " + name +" was first on " + ChatColor.GOLD + (new SimpleDateFormat()).format(date)); }
        
        if(blob != null)
        {
            long statsdate = (blob.getStat(FIRSTPLAYEDCAT, FIRSTPLAYEDSTAT).getValue());

            // multiply by 1000 to convert to milliseconds
            statsdate *= 1000;
            if(statsdate > 0)
            { output.add(ChatColor.DARK_RED + "According to Stats " + name + " was first on " + ChatColor.GOLD + (new SimpleDateFormat()).format(statsdate)); } 
        }
        
        if(output.isEmpty())
        { output.add(ChatColor.GOLD + "Could not find record for player " + name + "."); }
        
        return output.toArray(new String[output.size()]);
    }
}