package me.tehbeard.BeardStat.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

    private enum CommandType {
        help, defaultstats, interactive, category, specific, unknown
    }

    private enum SenderType {
        console, player, unknown
    }

    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
        String player;
        CommandType cmdtype = CommandType.unknown;
        SenderType usertype = SenderType.unknown;
        if (!BeardStat.hasPermission(sender, "command.stat")) {
            BeardStat.sendNoPermissionError(sender);
            return true;
        }

        // set the sender type
        if (sender instanceof ConsoleCommandSender || sender instanceof ConsoleCommandSender) {
            usertype = SenderType.console;
        }
        else if (sender instanceof Player) {
            usertype = SenderType.player;
        }

        if (args.length > 0) {
            // set the command type
            if (args[0].equalsIgnoreCase("-i")) {
                cmdtype = CommandType.interactive;
            }
            else if (args[0].equalsIgnoreCase("-h")) {
                cmdtype = CommandType.help;
            }
            else if (args[0].equalsIgnoreCase("-c")) {
                cmdtype = CommandType.category;
            }
            else if (args[0].equalsIgnoreCase("-s")) {
                cmdtype = CommandType.specific;
            }
        }
        else { // no args
            if (usertype == SenderType.player) {
                cmdtype = CommandType.defaultstats;
            }
            else {
                sender.sendMessage(ChatColor.RED + "You cannot run this command from the console with no arguments, you must specify a player name.  Use: stats <player>");
                return false;
            }
        }

        switch (cmdtype) {
        case help:
            SendHelpMessage(sender);
            return true;
        case interactive:
            if (usertype == SenderType.player) {
                ((Player) sender).beginConversation(cFactory.withFirstPrompt(SelectCategoryPrompt.getInstance()).buildConversation((Player) sender));
            }
            else {
                sender.sendMessage(ChatColor.RED + "You cannot run this command from the console.");
                return false;
            }
            break;
        case category:
            player = "";
            String cat = "";
            switch (usertype) {
            case player:
                if (args.length == 2) {
                    player = ((Player) sender).getName();
                    cat = args[1];
                }
                else if(args.length == 1){
                    player = ((Player) sender).getName();
                }                
                break;
            case console:
                if (args.length == 3) {
                    player = args[1];
                    cat = args[2];
                }
                else if (args.length == 2) {
                    player = args[1];
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You cannot run this command from the console with no arguments, you must specify a player name.  Use: stats -c <player>");
                    return false;
                }

                Collection<PlayerStat> stats = playerStatManager.getPlayerBlob(player).getStats();
                SendCategoryMessage(sender, stats, cat);

                break;
            }
            break;
        case specific:
            if (args.length > 1 && args[1].indexOf(".") < 0) {
                player = args[1];
            }
            else if (usertype == SenderType.player) {
                player = ((Player) sender).getName();
            }
            else {
                sender.sendMessage(ChatColor.RED + "You cannot run this command from the console with no arguments, you must specify a player name.  Use: stats -s <player> <stat> [<stat>]");
                return false;
            }

            for (String arg : args) {
                String[] part = arg.split("\\.");

                for (String p : part) {
                    BeardStat.printDebugCon(p);
                }

                if (part.length == 2) {
                    BeardStat.printDebugCon("sending stat to player");

                    if (playerStatManager.getPlayerBlob(player).hasStat(part[0], part[1])) {
                        sender.sendMessage(ChatColor.GOLD + player + " " + ChatColor.LIGHT_PURPLE + arg + ": " + ChatColor.WHITE + playerStatManager.getPlayerBlob(player).getStat(part[0], part[1]).getValue());
                    }
                    else {
                        sender.sendMessage("Stat not found");
                    }
                }
            }
            break;
        default:
            player = "";
            switch (usertype) {
            case console:
                player = args[0]; // we already make sure there is at least one
                break;
            case player:
                if (args.length == 0) {
                    player = ((Player) sender).getName();
                }
                else if (BeardStat.hasPermission(sender, "command.stat.other")) {
                    player = args[0];
                }
                else {
                    BeardStat.sendNoPermissionError(sender);
                    return false;
                }
                break;
            }

            PlayerStatBlob blob = null;
            if (player.length() > 0) {
                blob = playerStatManager.findPlayerBlob(player);

                SendPlayerStats(sender, blob);
            }
            else {
                sender.sendMessage(ChatColor.RED + "Player not found.");
            }
            break;
        }

        if (args.length == 1 && args[0].indexOf('.') == -1) {}
        else {}
        sender.sendMessage(ChatColor.GREEN + "Use /stats -h to display the help page!");
        return true;
    }

    public static void SendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Stats Help page");
        sender.sendMessage(ChatColor.GREEN + "/stats [user]: Default display of your [or users] stats");
        sender.sendMessage(ChatColor.GREEN + "/stats -h : This page");
        sender.sendMessage(ChatColor.GREEN + "/stats -i : Interactive stats menu");
        sender.sendMessage(ChatColor.GREEN + "/stats -s [user] <category1> [<category2>] : value of those stats for you or [user]");
        sender.sendMessage(ChatColor.GREEN + "/stats -c : list categories you have stats for");
        sender.sendMessage(ChatColor.GREEN + "/stats -c blockcreate : List possible stats you have for that category");
        sender.sendMessage(ChatColor.GREEN + "/statpage : list available stat pages");
        sender.sendMessage(ChatColor.GREEN + "/statpage [user] page : show a specific stat page");
        if (BeardStat.hasPermission(sender, "command.laston")) {
            sender.sendMessage(ChatColor.GREEN + "/laston [user] : show when you [or user] was last on");
        }
        if (BeardStat.hasPermission(sender, "command.laston")) {
            sender.sendMessage(ChatColor.GREEN + "/firston [user] : show when you [or user] was first on");
        }
        if (BeardStat.hasPermission(sender, "command.played")) {
            sender.sendMessage(ChatColor.GREEN + "/played [user] : shows how long you [or user] have played");
        }
        if (BeardStat.hasPermission(sender, "command.topplayed")) {
            sender.sendMessage(ChatColor.GREEN + "/topplayed : shows top players on the server");
        }
    }

    public static void SendCategoryMessage(CommandSender sender, Collection<PlayerStat> playerstats, String cat) {
        if (cat.length() == 0) {
            SendCategoryList(sender, playerstats);
            return;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting stats in category");
        HashSet<String> stats = new HashSet<String>();
        for (PlayerStat ps : playerstats) {
            if (ps.getCat().equals(cat)) {
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
                }
                else {

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
    }

    public static void SendCategoryList(CommandSender sender, Collection<PlayerStat> playerstats) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "getting categories");
        HashSet<String> cats = new HashSet<String>();
        for (PlayerStat ps : playerstats) {
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
    }

    public static void SendPlayerStats(CommandSender sender, PlayerStatBlob blob) {
        if (blob != null && blob.getStat("stats", "playedfor").getValue() != 0) {
            sender.sendMessage(ChatColor.GOLD + "-= " + blob.getName() + "'s Stats =-");

            long seconds = blob.getStat("stats", "playedfor").getValue();
            if (sender instanceof Player) {
                seconds += BeardStat.self().getSessionTime(((Player) sender).getName());
            }
            sender.sendMessage(playedCommand.GetPlayedString(seconds));

            Bukkit.dispatchCommand(sender, "statpage " + blob.getName() + " default");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Player not found.");
        }
    }
}