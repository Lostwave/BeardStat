package com.tehbeard.BeardStat.commands;

import org.bukkit.command.CommandExecutor;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

public abstract class BeardStatCommand implements CommandExecutor {

    protected final PlayerStatManager playerStatManager;
    protected final BeardStat         plugin;

    public BeardStatCommand(PlayerStatManager playerStatManager, BeardStat plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
    }
}
