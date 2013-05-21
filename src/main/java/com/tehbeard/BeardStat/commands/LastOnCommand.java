package com.tehbeard.BeardStat.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Implements last on feature, figures out when a user was last online
 * @author James
 *
 */
public class LastOnCommand implements CommandExecutor {

    private static final String PLAYEDCAT       = "stats";
    private static final String FIRSTPLAYEDSTAT = "firstlogin";
    private static final String LASTPLAYEDSTAT  = "lastlogin";
    private PlayerStatManager   playerStatManager;

    public LastOnCommand(PlayerStatManager statmanager) {
        this.playerStatManager = statmanager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {

        if (!BeardStat.hasPermission(sender, "command.laston")) {
            BeardStat.sendNoPermissionError(sender);
            return true;
        }

        String name = "";
        EntityStatBlob blob = null;
        OfflinePlayer player = null;
        if (args.length == 1) {
            player = Bukkit.getOfflinePlayer(args[0]);
            name = args[0];

            blob = this.playerStatManager.findPlayerBlob(args[0]);
        } else if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED
                        + "You cannot run this command from the console with no arguments, you must specify a player name.  Use: firston <player>");
                return true;
            }

            player = Bukkit.getOfflinePlayer(sender.getName());
            if (player != null) {
                name = player.getName();
                blob = this.playerStatManager.findPlayerBlob(name);
            }
        }

        sender.sendMessage(GetLastOnString(name, blob, player));
        return true;
    }

    public static String[] GetLastOnString(String name, EntityStatBlob blob, OfflinePlayer player) {
        ArrayList<String> output = new ArrayList<String>();

        long bFirst = 0;
        long bLast = 0;
        long sFirst = 0;
        long sLast = 0;

        if (player != null) {
            bFirst = player.getFirstPlayed();
            bLast = player.getLastPlayed();
        }

        if (blob != null) {
            sFirst = (blob.getStat(BeardStat.DEFAULT_DOMAIN, BeardStat.GLOBAL_WORLD, PLAYEDCAT, FIRSTPLAYEDSTAT)
                    .getValue());
            // multiply by 1000 to convert to milliseconds
            sFirst *= 1000;

            sLast = (blob.getStat(BeardStat.DEFAULT_DOMAIN, BeardStat.GLOBAL_WORLD, PLAYEDCAT, LASTPLAYEDSTAT)
                    .getValue());
            // multiply by 1000 to convert to milliseconds
            sLast *= 1000;
        }

        // this value from bukkit should be correct moving forward, but for
        // players who joined before (I think) mid Dec. 2011 will show that
        // date, not their actual first login
        if (bFirst > 0) {
            output.add(ChatColor.DARK_RED + "Bukkit thinks " + name + " was " + ChatColor.WHITE + "first"
                    + ChatColor.DARK_RED + " on " + ChatColor.GOLD + (new SimpleDateFormat()).format(bFirst));
        }

        // including this because bukkit hasn't stored this value for long
        // enough
        if ((sFirst > 0) && (Math.abs(bFirst - sFirst) > 86400000)) {
            output.add(ChatColor.DARK_RED + "I heard that " + name + " was " + ChatColor.WHITE + "first"
                    + ChatColor.DARK_RED + " on " + ChatColor.GOLD + (new SimpleDateFormat()).format(sFirst));
        }

        if (bLast > 0) {
            output.add(ChatColor.DARK_RED + "Bukkit thinks " + name + " was " + ChatColor.WHITE + "last"
                    + ChatColor.DARK_RED + " on " + ChatColor.GOLD + (new SimpleDateFormat()).format(bLast));
        }

        // only showing this if the values differ by a day.
        if ((sLast > 0) && (Math.abs(bLast - sLast) > 86400000)) {
            output.add(ChatColor.DARK_RED + "I heard that " + name + " was " + ChatColor.WHITE + "last"
                    + ChatColor.DARK_RED + " on " + ChatColor.GOLD + (new SimpleDateFormat()).format(sLast));
        }

        if (output.isEmpty()) {
            output.add(ChatColor.GOLD + "Could not find record for player " + name + ".");
        }

        return output.toArray(new String[output.size()]);
    }
}