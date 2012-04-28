package me.tehbeard.BeardStat.commands;

import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.commands.interactive.SelectCategoryPrompt;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {

	private PlayerStatManager playerStatManager;

	private ConversationFactory cFactory = new ConversationFactory(BeardStat.self());

	public StatCommand(PlayerStatManager playerStatManager) {
		this.playerStatManager = playerStatManager;

	}

	public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
		PlayerStatBlob blob = null;
		if (!BeardStat.hasPermission(sender, "command.stat")) {
			BeardStat.sendNoPermissionError(sender);
			return true;
		}
		if (args.length > 0) {
			if (args[0].equals("-i")) {

				((Player) sender).beginConversation(cFactory.withFirstPrompt(SelectCategoryPrompt.getInstance()).buildConversation((Player) sender));
			} else if (args[0].equals("-h")) {
				sender.sendMessage(ChatColor.GREEN + "Stats Help page");
				sender.sendMessage(ChatColor.GREEN + "/stats [User]: Default display of your [or users] stats");
				sender.sendMessage(ChatColor.GREEN + "/stats -h : This page");
				sender.sendMessage(ChatColor.GREEN + "/stats -i : Interactive stats menu");
				sender.sendMessage(ChatColor.GREEN + "/stats kills.total deaths.total : value of those stats");
				sender.sendMessage(ChatColor.GREEN + "/stats -c : list categories you have stats for");
				sender.sendMessage(ChatColor.GREEN + "/stats -c blockcreate : List stats you have for that category");
				sender.sendMessage(ChatColor.GREEN + "/statpage : list available stat pages");
				sender.sendMessage(ChatColor.GREEN + "/statpage [User] page : show a specific stat page");
				if (BeardStat.hasPermission((Player) sender, "command.laston")) {
					sender.sendMessage(ChatColor.GREEN + "/laston [user] : show when you [or user] was last on");
				}
				if (BeardStat.hasPermission((Player) sender, "command.laston")) {
					sender.sendMessage(ChatColor.GREEN + "/firston [user] : show when you [or user] was first on");
				}
				if (BeardStat.hasPermission((Player) sender, "command.played")) {
					sender.sendMessage(ChatColor.GREEN + "/played [user] : shows how long you [or user] have played");
				}
				if (BeardStat.hasPermission((Player) sender, "command.topplayed")) {
					sender.sendMessage(ChatColor.GREEN + "/topplayed : shows top players on the server");
				}
				return true;
			} else if (args[0].equals("-c")) {
				if (args.length == 2) {

					sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting stats in category");
					HashSet<String> stats = new HashSet<String>();
					for (PlayerStat ps : playerStatManager.getPlayerBlob(((Player) sender).getName()).getStats()) {
						if (ps.getCat().equals(args[1])) {
							stats.add(ps.getName());
						}
					}
					String msg = "";

					Iterator<String> it = stats.iterator();
					while (it.hasNext()) {
						for (int i = 0; i < 10; i++) {
							if (it.hasNext()) {
								if (i > 0) {
									msg += ", ";
								}
								msg += it.next();
							} else {

								sender.sendMessage(msg);
								msg = "";
								break;
							}
						}
						if (!msg.equals("")) {
							sender.sendMessage(msg);
							msg = "";
						}
					}
					return true;

				} else {
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting categories");
					HashSet<String> cats = new HashSet<String>();
					for (PlayerStat ps : playerStatManager.getPlayerBlob(((Player) sender).getName()).getStats()) {
						if (!cats.contains(ps.getCat())) {
							cats.add(ps.getCat());
						}
					}
					String msg = "";

					Iterator<String> it = cats.iterator();
					while (it.hasNext()) {
						for (int i = 0; i < 10; i++) {
							if (it.hasNext()) {
								if (i > 0) {
									msg += ", ";
								}
								msg += it.next();
							}
						}
						sender.sendMessage(msg);
						msg = "";
					}
					return true;

				}
			} else if (args.length == 1 && args[0].indexOf('.') == -1) {
				if(sender instanceof ConsoleCommandSender || BeardStat.hasPermission((Player) sender, "command.stat.other")){
					blob = playerStatManager.findPlayerBlob(args[0]);
					if (blob != null && blob.getStat("stats", "playedfor").getValue() != 0) {
						sender.sendMessage(ChatColor.GOLD + "-= " + args[0] + "'s Stats =-");
					}
				}
				else{
	      			BeardStat.sendNoPermissionError(sender);
				}
			} else {

				for (String arg : args) {
					String[] part = arg.split("\\.");
					for (String p : part) {
						BeardStat.printDebugCon(p);
					}

					if (part.length == 2) {
						BeardStat.printDebugCon("sending stat to player");

						if (playerStatManager.getPlayerBlob(((Player) sender).getName()).hasStat(part[0], part[1])) {
							sender.sendMessage(arg + ": " + playerStatManager.getPlayerBlob(((Player) sender).getName()).getStat(part[0], part[1]).getValue());
						} else {
							sender.sendMessage("not found");
						}
					} else {
						sender.sendMessage(arg + " not found!");
					}

				}
			}
		} else {
			if(sender instanceof Player) {
				sender.sendMessage(ChatColor.GOLD + "-= your Stats =-");
				blob = playerStatManager.getPlayerBlob(((Player) sender).getName());
			}
			else{
           	 sender.sendMessage(ChatColor.RED + "You cannot run this command from the console with no arguments, you must specify a player name.");				
			}
		}
		if (blob != null && blob.getStat("stats", "playedfor").getValue() != 0) {
			long seconds = blob.getStat("stats", "playedfor").getValue();
			if(sender instanceof Player){
				seconds += BeardStat.self().getSessionTime(((Player) sender).getName());
			}
			sender.sendMessage(playedCommand.GetPlayedString(seconds));

			Bukkit.dispatchCommand(sender, "statpage " + blob.getName() + " default");
			return true;
		}

		sender.sendMessage(ChatColor.GREEN + "Use /stats -h to display the help page!");
		return true;
	}
}