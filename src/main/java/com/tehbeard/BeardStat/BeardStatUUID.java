/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat;

import com.tehbeard.utils.mojang.api.profiles.HttpProfileRepository;
import com.tehbeard.utils.mojang.api.profiles.Profile;
import com.tehbeard.utils.mojang.api.profiles.ProfileCriteria;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.scoreboard.Criterias;

/**
 *
 * @author James
 */
public class BeardStatUUID {
    
    private static HttpProfileRepository repo = new HttpProfileRepository();
    
    public static Map<String,String> getUUIDS(String... players){
        Map<String,String> mapping = new HashMap<String, String>();
        
        List<ProfileCriteria> criteria = new ArrayList<ProfileCriteria>(players.length);
        for(String player : players){
            criteria.add(new ProfileCriteria(player, "minecraft"));
        }
        
        Profile[] results = repo.findProfilesByCriteria(criteria.toArray(new ProfileCriteria[0]));
        for(Profile profile : results){
            
            mapping.put(
                    profile.getName(),
                    profile.getId()
                    );
        }
        
        
        return mapping;
    }
    
    
    public static void main(String[] args){
        for(Entry<String,String> e : getUUIDS("tehbeard","killamcgriefstab","wokka1","tulonsae").entrySet()){
        System.out.println(e.getKey() + " = " + e.getValue());
        }
    }
    
}
