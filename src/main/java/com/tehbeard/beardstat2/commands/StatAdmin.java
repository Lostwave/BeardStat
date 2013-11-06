package com.tehbeard.beardstat.commands;

import com.tehbeard.utils.commands.ArgumentPack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.manager.EntityStatManager;

/**
 * Stat admin /statadmin -a tehbeard - reset all stats for person, clears all
 * stats for them. /statadmin -s tehbeard cat.stat1 cat.stat2 - Reset stat to 0
 * 
 * @author James
 * 
 */
public class StatAdmin extends BeardStatCommand {

    public StatAdmin(EntityStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        ArgumentPack arguments = new ArgumentPack(new String[] { "a", "s" }, new String[0], args);

        if (arguments.size() == 1) {
            if (arguments.getFlag("a")) {
                // final String name = arguments.get(0);

                /*
                 * new Conversation(BeardStat.self(), (Conversable) sender, new
                 * QuickConfirmationPrompt() {
                 * 
                 * @Override public void called(boolean result) { if (result) {
                 * StatAdmin.this.manager.deletePlayer(name); }
                 * 
                 * } }).begin();
                 */
                sender.sendMessage("Operation not supported yet!");
            } else {
                sender.sendMessage("Operation not supported yet!");
            }
            return true;
        }

        return false;
    }

}
