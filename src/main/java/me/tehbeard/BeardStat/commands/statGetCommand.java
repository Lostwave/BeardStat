package me.tehbeard.BeardStat.commands;



import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class statGetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(sender instanceof Player){
			if(!BeardStat.hasPermission((Player)sender, "command.statget")){return true;}
		}
		if(args.length>1){
			PlayerStatBlob pbs = PlayerStatManager.findPlayerBlob(args[0]);
			if(pbs == null){
				sender.sendMessage("Player not found!");
				return true;
			}
			for(int i = 1;i<args.length;i++){
				String arg = args[i];
				String[] part = arg.split("::");
				if(part.length==2){
					if(pbs.hasStat(part[0],part[1])){
						sender.sendMessage(arg +": " + pbs.getStat(part[0],part[1]).getValue());
					}
				}
				else
				{
					sender.sendMessage(arg + " not found!");
				}


			}
			return true;
		}

		return false;

	}
}

