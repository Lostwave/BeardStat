package com.tehbeard.beardstat.commands;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.dataproviders.JDBCStatDataProvider;
import com.tehbeard.beardstat.manager.EntityStatManager;

public class StatScriptExecCommand extends BeardStatCommand{

    private JDBCStatDataProvider provider;

    public StatScriptExecCommand(EntityStatManager playerStatManager, BeardStat plugin,JDBCStatDataProvider provider) {
        super(playerStatManager, plugin);
        this.provider = provider;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlbl, String[] args) {
        if(!sender.isOp()){return true;}
        if(args.length == 0){return false;}
        File sqlFile = new File(plugin.getDataFolder(),"sqlfix/" + args[0] + ".sql");
        if(sqlFile.exists()){
            if(provider != null){
                try {
                    provider.runExternalScript(sqlFile);
                    sender.sendMessage("Executed script.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("An error occured, check console");
                }
            }
            else
            {
                sender.sendMessage("Not using a SQL driver");
            }
        }
        else{
            sender.sendMessage("Could not find file" + sqlFile.toString());
        }
        
        return true;
    }


}
