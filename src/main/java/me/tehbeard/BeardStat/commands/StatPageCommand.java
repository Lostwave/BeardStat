package me.tehbeard.BeardStat.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class StatPageCommand implements CommandExecutor {


	private Map<String,Map<String,String>> pages;
	private PlayerStatManager playerStatManager;
	public StatPageCommand(BeardStat beardStatPlugin){
		playerStatManager = beardStatPlugin.getStatManager();
		pages = new HashMap<String, Map<String,String>>();
		
		ConfigurationSection pageConfig = beardStatPlugin.getConfig().getConfigurationSection("stats.pages");
		if(pageConfig !=null){
			Set<String> pageNames = pageConfig.getKeys(false);
			for(String pageName: pageNames){
				ConfigurationSection page = pageConfig.getConfigurationSection(pageName);
				Set<String> stats = page.getKeys(false);
				Map<String,String> statList = new HashMap<String, String>();
				for(String stat : stats){
					statList.put(stat, page.getString(stat));
				}
				pages.put(pageName, statList);
			}
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if(sender instanceof Player == false){return true;}
		if(args.length==1){
			if(pages.containsKey(args[0])){
				for(Entry<String,String> entry:pages.get(args[0]).entrySet()){
					
					if(entry.getValue().split("\\.").length==2){
						String cat,stat;
						cat = entry.getValue().split("\\.")[0];
						stat = entry.getValue().split("\\.")[1];
						sender.sendMessage(ChatColor.LIGHT_PURPLE + entry.getKey() + ": " + ChatColor.WHITE + playerStatManager.getPlayerBlob(((Player)sender).getName()).getStat(cat,stat).getValue());
					}
				}
			}
		}
		else
		{
			for(String page:pages.keySet()){
				sender.sendMessage(page);
			}
		}
		return true;
	}

}
