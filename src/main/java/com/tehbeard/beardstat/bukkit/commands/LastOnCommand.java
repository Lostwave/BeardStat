package com.tehbeard.beardstat.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.Refs;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.LanguagePack;

/**
 * Implements last on feature, figures out when a user was last online
 * 
 * @author James
 * 
 */
public class LastOnCommand extends BeardStatCommand {

    public LastOnCommand(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        super(playerStatManager, plugin);
    }

    private static final String PLAYEDCAT       = "stats";
    private static final String FIRSTPLAYEDSTAT = "firstlogin";
    private static final String LASTPLAYEDSTAT  = "lastlogin";

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {

        EntityStatBlob blob = null;
        OfflinePlayer player = sender instanceof Player ? (OfflinePlayer) sender : null;
        if (args.length == 1) {
            player = Bukkit.getOfflinePlayer(args[0]);
        }
        blob = this.playerStatManager.getPlayer(null, player.getUniqueId(), false);
        
        if(blob==null){sender.sendMessage(ChatColor.RED + LanguagePack.getMsg("command.error.noplayer", args[0]));return true;}
        
        if (args.length == 1) {
            sender.sendMessage(ChatColor.YELLOW + args[0]);
        }
        sender.sendMessage(ChatColor.YELLOW + "First on: " + this.playerStatManager.formatStat(FIRSTPLAYEDSTAT, blob.getStat(Refs.DEFAULT_DOMAIN, Refs.GLOBAL_WORLD, PLAYEDCAT, FIRSTPLAYEDSTAT).getValue()));
        sender.sendMessage(ChatColor.YELLOW + "Last on: " + this.playerStatManager.formatStat(LASTPLAYEDSTAT, blob.getStat(Refs.DEFAULT_DOMAIN, Refs.GLOBAL_WORLD, PLAYEDCAT, LASTPLAYEDSTAT).getValue()));
        
        return true;
    }

        

    
}