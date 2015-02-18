/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tehbeard.beardstat.bukkit.commands;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.utils.intake.CommandException;
import com.tehbeard.utils.intake.context.CommandLocals;
import com.tehbeard.utils.intake.dispatcher.Dispatcher;
import com.tehbeard.utils.intake.dispatcher.SimpleDispatcher;
import com.tehbeard.utils.intake.util.auth.AuthorizationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 *
 * @author James
 */
public class Commands implements TabExecutor {

    Dispatcher dispatcher;
    private final EntityStatManager playerStatManager;
    private final BukkitPlugin         plugin;

    
    public Commands(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        this.playerStatManager = playerStatManager;
        this.plugin = plugin;
        this.dispatcher = new SimpleDispatcher();
        
    }
    
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String cmdlbl, String[] args) {
        CommandLocals locals = new CommandLocals();
            locals.put(CommandSender.class, cs);
            locals.put(EntityStatManager.class,playerStatManager);
            locals.put(BukkitPlugin.class, plugin);
        try {
            return dispatcher.getSuggestions(cmdlbl + " " + StringUtils.join(args, " "), locals);
        } catch (CommandException ex) {
            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<String>();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String cmdlbl, String[] args) {
        boolean result = false;
        try {
            CommandLocals locals = new CommandLocals();
            locals.put(CommandSender.class, cs);
            locals.put(EntityStatManager.class,playerStatManager);
            locals.put(BukkitPlugin.class, plugin);
            result = dispatcher.call(cmdlbl + " " + StringUtils.join(args, " "), locals, new String[0]);
        } catch (CommandException ex) {
            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorizationException ex) {
            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
}
