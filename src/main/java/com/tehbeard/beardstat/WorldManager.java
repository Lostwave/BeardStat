/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.tehbeard.beardstat.cfg.InjectConfig;
import com.tehbeard.beardstat.cfg.YamlConfigInjector;

/**
 *
 * @author James
 */
public class WorldManager {


    private WorldData defaultWorld = new WorldData(true,false,false);
    Map<String,WorldData> worlds = new HashMap<String, WorldData>();

    public WorldManager() {
    }

    private static class WorldData {

        @InjectConfig("survival")
        private boolean survival;//Track survival mode
        @InjectConfig("creative")
        private boolean creative;
        @InjectConfig("adventure")
        private boolean adventure;
        @InjectConfig("blacklist")
        private List<String> blacklist = new ArrayList<String>();
        @InjectConfig("whitelist")
        private List<String> whitelist = new ArrayList<String>();

        public WorldData() {
            survival  = true;
            creative  = false;
            adventure = false;
        }

        private WorldData(boolean s, boolean c, boolean a) {
            survival  = s;
            creative  = c;
            adventure = a;
        }

        public boolean shouldTrack(Player player, String trackType){
            GameMode gm = player.getGameMode();
            return 
                    (
                            (gm == GameMode.SURVIVAL  && survival) ||
                            (gm == GameMode.CREATIVE  && creative) ||
                            (gm == GameMode.ADVENTURE && adventure)
                    ) && trackType(trackType);
            //Track if mode allowed and not blocking a certain track type
        }
        
        public boolean trackType(String type){
            if(blacklist.size() > 0 && blacklist.contains(type)){
                    return false;
            }
            
            if(whitelist.size() > 0 && !whitelist.contains(type)){
                return false;
            }
            
            return true;    
        }
    }


    public WorldManager(ConfigurationSection section){

        if(section!=null){
            Set<String> keys = section.getKeys(false);

            for(String key : keys){
                WorldData d = new WorldData();
                new YamlConfigInjector(section.getConfigurationSection(key)).inject(d);
                worlds.put(key,d);
                if(key.equalsIgnoreCase("default")){
                    defaultWorld = d;
                }
            }
        }
    }

    public boolean shouldTrack(Player player, String trackType){
        if(worlds.containsKey(player.getWorld().getName())){
            return worlds.get(player.getWorld().getName()).shouldTrack(player, trackType);
        }
        return defaultWorld.shouldTrack(player, trackType);

    }

    public void addWorld(String name,boolean s,boolean c,boolean a){
        worlds.put(name,new WorldData(s,c,a));
    }
}
