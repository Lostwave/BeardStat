package me.tehbeard.BeardStat.containers;

import java.util.HashMap;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Provides a cache between backend storage and the stats plugin
 * @author James
 *
 */
public class PlayerStatManager implements CommandExecutor {

    private HashMap<String,PlayerStatBlob> cache = new HashMap<String,PlayerStatBlob>();
    private IStatDataProvider backendDatabase = null;


    public PlayerStatManager(IStatDataProvider database){
        backendDatabase = database;
    }


    /**
     * Force save of all cached stats to backend storage
     */
    public void saveCache(){
        if(backendDatabase == null){return;}
        Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();

        while(i.hasNext()){
            Entry<String, PlayerStatBlob> entry = i.next();
            String player = entry.getKey();

            int seconds = getSessionTime(player);

            BeardStat.printDebugCon("saving time: [Player : " + player +" ] time: " +seconds);
            entry.getValue().getStat("stats","playedfor").incrementStat(seconds);

            backendDatabase.pushPlayerStatBlob(getPlayerBlob(player));

            if(isPlayerOnline(player)){
                setLoginTime(player,System.currentTimeMillis());
            }
            else
            {
                wipeLoginTime(player);
                i.remove();
            }



        }


    }



    private boolean isPlayerOnline(String player) {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getName().equals(player)){
                return true;
            }
        }
        return false;
    }


    /**
     * Retrieve a players Stat Blob, or create one if it doesn't exist
     * @param name
     * @return
     */
    public PlayerStatBlob getPlayerBlob(String name){
        if(backendDatabase == null){return null;}
        if(!cache.containsKey(name)){
            cache.put(name,backendDatabase.pullPlayerStatBlob(name));
        }
        return cache.get(name);
    }
    /**
     * Finds a player's stat blob, but does not try to make it
     * @param name player to find
     * @return The player's stat blob or a null if not found
     */
    public PlayerStatBlob findPlayerBlob(String name){
        if(backendDatabase == null){return null;}
        if(!cache.containsKey(name)){
            PlayerStatBlob pbs = backendDatabase.pullPlayerStatBlob(name,false);
            if(pbs==null){
                return null;
            }
            cache.put(name,pbs);
        }
        return cache.get(name);
    }
    public void flush(){

        backendDatabase.flush();
    }



    private HashMap<String,Long> loginTimes = new HashMap<String, Long>();

    /**
     * Returns length of current session in memory
     * @param player
     * @return
     */
    public int getSessionTime(String player){
        if(loginTimes.containsKey(player)){
            return Integer.parseInt("" + ((System.currentTimeMillis()  - loginTimes.get(player))/1000L));

        }
        return 0;
    }

    public Long getLoginTime(String player){
        if(!loginTimes.containsKey(player)){
            setLoginTime(player,System.currentTimeMillis());
        }
        return loginTimes.get(player);

    }

    public void setLoginTime(String player,long time){
        loginTimes.put(player,time);

    }

    public void wipeLoginTime(String player){
        loginTimes.remove(player);
    }


    public boolean onCommand(CommandSender sender, Command cmd, String lbl,
            String[] args) {
        Iterator<Entry<String, PlayerStatBlob>> i = cache.entrySet().iterator();
        sender.sendMessage("Players in Stat cache");
        while(i.hasNext()){
            Entry<String, PlayerStatBlob> entry = i.next();
            String player = entry.getKey();
            sender.sendMessage(ChatColor.GOLD + player);
        }

        Iterator<String> ii = loginTimes.keySet().iterator();
        sender.sendMessage("Players in login cache");
        while(ii.hasNext()){
            String player = ii.next();
            sender.sendMessage(ChatColor.GOLD + player);
        }
        return true;
    }

}
