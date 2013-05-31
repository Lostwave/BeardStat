package com.tehbeard.BeardStat.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.BeardStatRuntimeException;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Display a statpage
 * 
 * @author James
 * 
 */
public class StatPageCommand implements CommandExecutor {

    private Map<String, List<String>> pages;
    private PlayerStatManager         playerStatManager;

    public StatPageCommand(BeardStat beardStatPlugin) {
        this.playerStatManager = beardStatPlugin.getStatManager();
        this.pages = new HashMap<String, List<String>>();

        ConfigurationSection pageConfig = beardStatPlugin.getConfig().getConfigurationSection("stats.pages");
        if (pageConfig != null) {
            Set<String> pageNames = pageConfig.getKeys(false);
            for (String pageName : pageNames) {
                List<String> page = pageConfig.getStringList(pageName);

                this.pages.put(pageName, page);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        try {
            String playername = "";
            String pagename = "";
            if (sender instanceof ConsoleCommandSender) {
                if (args.length == 2) {
                    playername = args[0];
                    pagename = args[1];
                }
            } else if (sender instanceof Player) {
                if (args.length == 2) {
                    playername = args[0];
                    pagename = args[1];
                } else if (args.length == 1) {
                    playername = ((Player) sender).getName();
                    pagename = args[0];
                }
            }

            if ((playername.length() > 0) && (pagename.length() > 0)) {
                if (this.pages.containsKey(pagename)) {
                    for (String entry : this.pages.get(pagename)) {
                        String[] p = entry.split("\\:");
                        if (p.length == 2) {

                            if (p[1].split("\\.").length == 2) {
                                String cat, stat;
                                cat = p[1].split("\\.")[0];
                                stat = p[1].split("\\.")[1];
                                sender.sendMessage(ChatColor.LIGHT_PURPLE
                                        + p[0]
                                        + ": "
                                        + ChatColor.WHITE
                                        + this.playerStatManager.getPlayerBlob(playername)
                                                .getStats(BeardStat.DEFAULT_DOMAIN, "*", cat, stat).getValue());
                            }
                        }
                    }
                }
            } else {
                for (String page : this.pages.keySet()) {
                    if (!page.equals("default")) {
                        sender.sendMessage(page);
                    }
                }
            }
        } catch (Exception e) {
            BeardStat.handleError(new BeardStatRuntimeException("/statpage threw an error", e, true));
        }
        return true;
    }

}
