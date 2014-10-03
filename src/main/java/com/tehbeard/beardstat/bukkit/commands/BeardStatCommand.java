package com.tehbeard.beardstat.bukkit.commands;

import org.bukkit.command.CommandExecutor;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.manager.EntityStatManager;

public abstract class BeardStatCommand implements CommandExecutor {

    protected final EntityStatManager playerStatManager;
    protected final BukkitPlugin         plugin;

    public BeardStatCommand(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }
}
