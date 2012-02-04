package me.tehbeard.BeardStat.commands;

import java.util.HashMap;
import java.util.List;
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
	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if(args.length==1 && sender instanceof Player){
			if(pages.containsKey(args[0])){
				for(String entry:pages.get(args[0])){
					String[] p = entry.split("\\:");
					if(p.length==2){

						if(p[1].split("\\.").length==2){
							String cat,stat;
							cat = p[1].split("\\.")[0];
							stat = p[1].split("\\.")[1];
							sender.sendMessage(ChatColor.LIGHT_PURPLE + p[0] + ": " + ChatColor.WHITE + playerStatManager.getPlayerBlob(((Player)sender).getName()).getStat(cat,stat).getValue());
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
