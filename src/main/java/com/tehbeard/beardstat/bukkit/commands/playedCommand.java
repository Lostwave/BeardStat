package com.tehbeard.beardstat.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.Refs;
import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.containers.IStat;
import com.tehbeard.beardstat.containers.StatVector;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.manager.OnlineTimeManager;
import com.tehbeard.beardstat.manager.OnlineTimeManager.ManagerRecord;
import com.tehbeard.beardstat.LanguagePack;

/**
 * /played - Show users playtime /played name - show player of name
 * 
 * @author James
 * 
 */
public class playedCommand extends BeardStatCommand {

    public playedCommand(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        super(playerStatManager, plugin);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
        try {

            int seconds = 0;
            EntityStatBlob blob;

            // If sender is a player, default to them
            OfflinePlayer selectedPlayer = (sender instanceof OfflinePlayer) ? (OfflinePlayer) sender : null;

            // We got an argument, use that player instead
            if ((args.length == 1) && sender.hasPermission(Refs.PERM_COMMAND_PLAYED_OTHER)) {
                selectedPlayer = Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() ? Bukkit.getOfflinePlayer(args[0])
                        : null;
            }

            // failed to get a player, send error and finish
            if (selectedPlayer == null) {
                sender.sendMessage(ChatColor.RED + LanguagePack.getMsg("command.error.noconsole.noargs"));
                return true;
            }

            // Grab player blob and format out stat
            blob = this.playerStatManager.getPlayer(selectedPlayer.getUniqueId(), false);
            if (blob == null) {
                sender.sendMessage(ChatColor.RED + LanguagePack.getMsg("command.error.noplayer", args[0]));
                return true;
            }
            StatVector vector = blob.getStats(Refs.DEFAULT_DOMAIN, "*", "stats", "playedfor");
            seconds = vector.getValue();
            
            //Only get record if player is online.
            ManagerRecord onlineTimeRecord = OnlineTimeManager.getRecord(selectedPlayer.getName());
            if(onlineTimeRecord != null){
                seconds += onlineTimeRecord.sessionTime();
            }
            
            sender.sendMessage(getPlayedString(seconds) + " total");

            for (IStat stat : vector) {
                sender.sendMessage(LanguagePack.getMsg("command.stat.stat", stat.getWorld(),
                        getPlayedString(stat.getValue())));
            }
        } catch (Exception e) {
            this.plugin.handleError(new BeardStatRuntimeException("An error occured running /played", e, true));
        }

        return true;
    }

    public String getPlayedString(int seconds) {

        if (seconds > 0) {
            return playerStatManager.formatStat("playedfor", seconds);
        }

        return LanguagePack.getMsg("command.played.zero");
    }
}
