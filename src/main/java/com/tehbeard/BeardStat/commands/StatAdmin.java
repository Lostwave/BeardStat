package com.tehbeard.BeardStat.commands;

import me.tehbeard.utils.commands.ArgumentPack;
import me.tehbeard.vocalise.prompts.QuickConfirmationPrompt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Stat admin /statadmin -a tehbeard - reset all stats for person, clears all
 * stats for them. /statadmin -s tehbeard cat.stat1 cat.stat2 - Reset stat to 0
 * 
 * @author James
 * 
 */
public class StatAdmin implements CommandExecutor {

    private final PlayerStatManager manager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        ArgumentPack arguments = new ArgumentPack(new String[] { "a", "s" }, new String[0], args);

        if (arguments.size() == 1) {
            if (arguments.getFlag("a")) {
                final String name = arguments.get(0);

                new Conversation(BeardStat.self(), (Conversable) sender, new QuickConfirmationPrompt() {

                    @Override
                    public void called(boolean result) {
                        if (result) {
                            StatAdmin.this.manager.deletePlayer(name);
                        }

                    }
                }).begin();
            } else {
                sender.sendMessage("Operation not supported yet!");
            }
            return true;
        }

        return false;
    }

    /**
     * @param manager
     */
    public StatAdmin(PlayerStatManager manager) {
        super();
        this.manager = manager;
    }

}
