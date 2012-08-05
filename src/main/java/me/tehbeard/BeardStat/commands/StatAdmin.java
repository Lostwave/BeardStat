package me.tehbeard.BeardStat.commands;

import me.tehbeard.utils.commands.ArgumentPack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Stat admin
 * /statadmin -a tehbeard - reset all stats for person, clears all stats for them.
 * /statadmin -s tehbeard cat.stat1 cat.stat2 - Reset stat to 0
 * @author James
 *
 */
public class StatAdmin implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String lbl,
            String[] args) {
        ArgumentPack arguments = new ArgumentPack(new String[] {"a","s"}, new String[0], args);
        return false;
    }

}
