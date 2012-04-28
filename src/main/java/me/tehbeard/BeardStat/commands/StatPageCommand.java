package me.tehbeard.BeardStat.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class StatPageCommand implements CommandExecutor {


	private Map<String,List<String>> pages;
	private PlayerStatManager playerStatManager;
	public StatPageCommand(BeardStat beardStatPlugin){
		playerStatManager = beardStatPlugin.getStatManager();
		pages = new HashMap<String, List<String>>();

		ConfigurationSection pageConfig = beardStatPlugin.getConfig().getConfigurationSection("stats.pages");
		if(pageConfig !=null){
			Set<String> pageNames = pageConfig.getKeys(false);
			for(String pageName: pageNames){
				List<String> page = pageConfig.getStringList(pageName);


				pages.put(pageName, page);
			}
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		String playername = "";
		String pagename = "";
		if(sender instanceof ConsoleCommandSender){
			if(args.length == 2){
				playername = args[0];
				pagename = args[1];
			}
		}
		else if(sender instanceof Player){
			if(args.length == 2){ 
				playername = args[0];
				pagename = args[1];
			}
			else if(args.length == 1)
			{
				playername = ((Player)sender).getName();
				pagename = args[0];
			}
		}
		
			
		if(playername.length() > 0 && pagename.length() > 0){
			if(pages.containsKey(pagename)){
				for(String entry:pages.get(pagename)){
					String[] p = entry.split("\\:");
					if(p.length==2){

						if(p[1].split("\\.").length==2){
							String cat,stat;
							cat = p[1].split("\\.")[0];
							stat = p[1].split("\\.")[1];
							sender.sendMessage(ChatColor.LIGHT_PURPLE + p[0] + ": " + ChatColor.WHITE + playerStatManager.getPlayerBlob(playername).getStat(cat,stat).getValue());
						}
					}
				}
			}
		}
		else
		{
			for(String page:pages.keySet()){
				if(!page.equals("default")){
					sender.sendMessage(page);
				}
			}
		}
		return true;
	}

}
