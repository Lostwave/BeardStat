package com.tehbeard.BeardStat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.LanguagePack;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.containers.OnlineTimeManager;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.containers.StatVector;

/**
 * Shows a users playtime
 * 
 * @author James
 * 
 */
public class playedCommand implements CommandExecutor {

    private PlayerStatManager playerStatManager;

    public playedCommand(PlayerStatManager playerStatManager) {
        this.playerStatManager = playerStatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
        /*
         * ERRORS BLOCK
         * 
         * 
         * BeardStat.sendNoPermissionError(sender);
         */

        long seconds = 0;
        EntityStatBlob blob;

        // select sender if they are a Player
        OfflinePlayer selectedPlayer = (sender instanceof OfflinePlayer) ? (OfflinePlayer) sender : null;

        // Ok, try an exact player.dat match
        if ((args.length == 1) && BeardStat.hasPermission(sender, "command.played.other")) {
            selectedPlayer = Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() ? Bukkit.getOfflinePlayer(args[0])
                    : null;
        }

        // failed to ascertain a player
        if (selectedPlayer == null) {
            sender.sendMessage(ChatColor.RED + LanguagePack.getMsg("command.error.noconsole.noargs"));
            return true;
        }

        // TODO: async this
        blob = this.playerStatManager.getPlayerBlob(selectedPlayer.getName());
        if (blob == null) {
            sender.sendMessage(ChatColor.RED + LanguagePack.getMsg("command.error.noplayer", args[0]));
            return true;
        }
        StatVector vector = blob.getStats(BeardStat.DEFAULT_DOMAIN, "*", "stats", "playedfor");
        seconds = vector.getValue();

        seconds += OnlineTimeManager.getRecord(selectedPlayer.getName()).sessionTime();
        sender.sendMessage(getPlayedString(seconds) + " total");

        for (IStat stat : vector) {
            sender.sendMessage(getPlayedString(stat.getValue()) + " [" + stat.getWorld() + "]");
        }

        return true;
    }

    public static String getPlayedString(long seconds) {
        String output = LanguagePack.getMsg("command.played.zero");
        if (seconds > 0) {
            int weeks = (int) seconds / 604800;
            int days = (int) Math.ceil((seconds - (604800 * weeks)) / 86400);
            int hours = (int) Math.ceil((seconds - ((86400 * days) + (604800 * weeks))) / 3600);
            int minutes = (int) Math.ceil((seconds - ((604800 * weeks) + (86400 * days) + (3600 * hours))) / 60);

            output = LanguagePack.getMsg("command.played.output", weeks, days, hours, minutes);
        }

        return output;
    }
}
