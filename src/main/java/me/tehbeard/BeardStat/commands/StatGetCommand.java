package me.tehbeard.BeardStat.commands;



import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class StatGetCommand implements CommandExecutor {


	private PlayerStatManager playerStatManager;

	public StatGetCommand(PlayerStatManager playerStatManager) {
		this.playerStatManager = playerStatManager;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(sender instanceof Player){
			if(!BeardStat.hasPermission((Player)sender, "command.stat.get")){
    			BeardStat.sendNoPermissionError(sender);
    			return true;
    		}
		}

		if(args.length > 1){
			PlayerStatBlob psb = playerStatManager.findPlayerBlob(args[0]);
			if(psb==null){
				sender.sendMessage("player not found");
				return true;
			}


			if(args[1].equals("-c")){
				if(args.length==3){

					sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting stats in category");
					HashSet<String> stats = new HashSet<String>();
					for( PlayerStat ps :psb.getStats()){
						if(ps.getCat().equals(args[2])){
							stats.add(ps.getName());
						}
					}
					String msg = "";

					Iterator<String> it = stats.iterator();
					while(it.hasNext()){
						for(int i=0;i<10;i++){
							if(it.hasNext()){
								if(i>0){msg+=", ";}
								msg+=it.next();
							}
							else
							{

								sender.sendMessage(msg);
								msg="";
								break;
							}
						}
						if(!msg.equals("")){
							sender.sendMessage(msg);
							msg="";}
					}
					return true;

				}else{
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting categories");
					HashSet<String> cats = new HashSet<String>();
					for( PlayerStat ps :psb.getStats()){
						if(!cats.contains(ps.getCat())){
							cats.add(ps.getCat());
						}
					}
					String msg = "";

					Iterator<String> it = cats.iterator();
					while(it.hasNext()){
						for(int i=0;i<10;i++){
							if(it.hasNext()){
								if(i>0){msg+=", ";}
								msg+=it.next();
							}
							else
							{
								sender.sendMessage(msg);
								msg="";
								break;
							}
						}
						sender.sendMessage(msg);
						msg="";
					}


				}
				return true;
			}

			String arg;
			for(int i=1;i<args.length;i++){
				arg = args[i];
				String[] part = arg.split("\\.");


				if(part.length==2){
					BeardStat.printDebugCon("sending stat to player"); 

					if(psb.hasStat(part[0],part[1])){
						sender.sendMessage(arg +": " + psb.getStat(part[0],part[1]).getValue());
					}
					else
					{
						sender.sendMessage("not found");
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

