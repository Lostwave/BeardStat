package com.tehbeard.BeardStat.commands;

import org.bukkit.command.CommandExecutor;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.manager.EntityStatManager;

public abstract class BeardStatCommand implements CommandExecutor {

    protected final EntityStatManager playerStatManager;
    protected final BeardStat         plugin;

    public BeardStatCommand(EntityStatManager playerStatManager, BeardStat plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }
}
