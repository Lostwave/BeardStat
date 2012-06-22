package me.tehbeard.BeardStat.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.commands.interactive.FindPlayerPrompt;
import me.tehbeard.BeardStat.commands.interactive.SelectCategoryPrompt;
import me.tehbeard.BeardStat.commands.interactive.SelectStatisticPrompt;
import me.tehbeard.BeardStat.commands.interactive.SetSelfPrompt;
import me.tehbeard.BeardStat.commands.interactive.ShowStatisticPrompt;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.utils.commands.ArgumentPack;
import me.tehbeard.vocalise.parser.PromptBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {

    private PlayerStatManager playerStatManager;


    private PromptBuilder builder;

    @SuppressWarnings("unchecked")
    public StatCommand(PlayerStatManager playerStatManager) {
        this.playerStatManager = playerStatManager;
        builder = new PromptBuilder(BeardStat.self());
        builder.AddPrompts(
                SelectCategoryPrompt.class,
                SelectStatisticPrompt.class,
                ShowStatisticPrompt.class,
                SetSelfPrompt.class,
                FindPlayerPrompt.class);

        builder.load(BeardStat.self().getResource("interactive.yml"));
    }


    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {


        if (!BeardStat.hasPermission(sender, "command.stat")) {
            BeardStat.sendNoPermissionError(sender);
            return true;
        }

        
        ArgumentPack arguments = new ArgumentPack(new String[] {"i","h"}, new String[] {"p","c","s"}, args);
        String player = arguments.getOption("p");

        if(player == null && sender instanceof Player){
            player = ((Player)sender).getName();
        }

        if(player == null){
            sendHelpMessage(sender);
            return true;
        }
        if(args.length == 0 ){
            Bukkit.dispatchCommand(sender, "statpage default");
            return true;
        }
        
        if(arguments.getFlag("i")){
            builder.makeConversation((Conversable) sender);
            return true;
        }

        //TODO: FINISH UP
        


        return true;
    }

    public static void sendHelpMessage(CommandSender sender) {
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
        if (BeardStat.hasPermission(sender, "command.topplayer")) {
            sender.sendMessage(ChatColor.GREEN + "/topplayer : shows top players on the server");
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
                seconds += BeardStat.self().getStatManager().getSessionTime(((Player) sender).getName());
            }
            sender.sendMessage(playedCommand.GetPlayedString(seconds));

            Bukkit.dispatchCommand(sender, "statpage " + blob.getName() + " default");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Player not found.");
        }
    }
}