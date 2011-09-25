package me.tehbeard.BeardStat.commands;



import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class statOtherCommand implements CommandExecutor {

	
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(sender instanceof Player){
			if(!BeardStat.hasPermission((Player)sender, "command.stat.other")){return true;}
			if(args.length > 0)
			{

				Player pp = null ;
				if(args.length>1){
					pp = BeardStat.self.getServer().getPlayer(args[0]);

					if(pp==null){
						List<Player> ply = BeardStat.self.getServer().matchPlayer(args[0]);
						if(ply.size()>1){
							for(Player p:ply){
								if(p.getName().equals(args[0])){
									pp = p;
									break;
								}
							}
						} else if(ply.size()==1){
							pp = ply.get(0);
						}
					}

					for(int i = 1;i<args.length;i++){
						String arg = args[i];
						//TODO: FIX THIS
						/*if(PlayerStatManager.getPlayerBlob(pp.getName()).hasStat(arg)){
							sender.sendMessage(arg +": " + PlayerStatManager.getPlayerBlob(pp.getName()).getStat(arg).getValue());
						}
						else
						{
							sender.sendMessage(arg + " not found!");
						}*/


					}
				}
				return true;
			}


		}
		return false;

	}
}

