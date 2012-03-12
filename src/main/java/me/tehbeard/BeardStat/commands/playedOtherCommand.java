
package me.tehbeard.BeardStat.commands;

import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class playedOtherCommand implements CommandExecutor {


	private PlayerStatManager playerStatManager;

	public playedOtherCommand(PlayerStatManager playerStatManager) {
		this.playerStatManager = playerStatManager;
	}

	public boolean onCommand(CommandSender sender, Command command, String cmdLabel,
			String[] args) {
		Player pp = null ;
		if(args.length==1){
			pp = Bukkit.getPlayer(args[0]);
			if(!BeardStat.hasPermission(sender, "command.played.other")){return true;}
			if(pp==null){
				List<Player> ply = Bukkit.getServer().matchPlayer(args[0]);
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
			long seconds = 0;
			PlayerStatBlob blob;

			if(pp==null){
				blob = playerStatManager.findPlayerBlob(args[0]);
				sender.sendMessage(ChatColor.GOLD + args[0]);
			}
			else
			{
				blob = playerStatManager.findPlayerBlob(pp.getName());
				sender.sendMessage(ChatColor.GOLD + pp.getName());
				seconds += BeardStat.self().getSessionTime(((Player)sender).getName());

			}

			if(blob != null){
				seconds += (long)blob.getStat("stats","playedfor").getValue();

				
				int weeks   = (int) seconds / 604800;
				int days = (int)Math.ceil((seconds -604800*weeks) / 86400);
				int hours = (int)Math.ceil((seconds - (86400 * days + 604800*weeks)) / 3600);
				int minutes = (int)Math.ceil((seconds - (604800*weeks + 86400 * days + 3600 * hours)) / 60);


				
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "playtime: " + ChatColor.WHITE+ 
						weeks + ChatColor.LIGHT_PURPLE +  " wks " + ChatColor.WHITE +
						days + ChatColor.LIGHT_PURPLE + " days " + ChatColor.WHITE+
						hours + ChatColor.LIGHT_PURPLE + " hours " + ChatColor.WHITE+
						minutes + ChatColor.LIGHT_PURPLE + " mins ");


				return true;
			}
			else
			{
				sender.sendMessage(ChatColor.GOLD + "Could not find record for player " + args[0] + ".");
			}
		}
		return false;


	}
}