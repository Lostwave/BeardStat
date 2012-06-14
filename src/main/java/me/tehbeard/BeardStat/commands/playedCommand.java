package me.tehbeard.BeardStat.commands;



import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class playedCommand implements CommandExecutor {

    private PlayerStatManager playerStatManager;

    public playedCommand(PlayerStatManager playerStatManager) {
        this.playerStatManager = playerStatManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String cmdLabel,
            String[] args) {
        if(sender instanceof Player){
            if(!BeardStat.hasPermission((Player)sender, "command.played")){return true;}
            long seconds = playerStatManager.getPlayerBlob(((Player)sender).getName()).getStat("stats","playedfor").getValue() +
                    BeardStat.self().getSessionTime(((Player)sender).getName());
            int weeks   = (int) seconds / 604800;
            int days = (int)Math.ceil((seconds -604800*weeks) / 86400);
            int hours = (int)Math.ceil((seconds - (86400 * days + 604800*weeks)) / 3600);
            int minutes = (int)Math.ceil((seconds - (604800*weeks + 86400 * days + 3600 * hours)) / 60);




            sender.sendMessage("you have played: "+ 
                    weeks +"wks "+
                    days +"days "+
                    hours +"hours "+
                    minutes +"mins");



        }
        return true;



    }

}