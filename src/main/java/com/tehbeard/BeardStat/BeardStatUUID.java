/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat;

import com.tehbeard.BeardStat.DataProviders.IStatDataProvider;
import com.tehbeard.BeardStat.DataProviders.MysqlStatDataProvider;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.utils.mojang.api.profiles.HttpProfileRepository;
import com.tehbeard.utils.mojang.api.profiles.Profile;
import com.tehbeard.utils.mojang.api.profiles.ProfileCriteria;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class BeardStatUUID {
    
    private static HttpProfileRepository repo = new HttpProfileRepository();
    public static final int MAX_PER_REQUEST = 64 * 2;
    
    public static Map<String, String> getUUIDS(String... players) {
        Map<String, String> mapping = new HashMap<String, String>();
        
        List<ProfileCriteria> criteria = new ArrayList<ProfileCriteria>(players.length);
        for (String player : players) {
            criteria.add(new ProfileCriteria(player, "minecraft"));
        }
        
        Profile[] results = repo.findProfilesByCriteria(criteria.toArray(new ProfileCriteria[0]));
        for (Profile profile : results) {
            
            mapping.put(
                    profile.getName(),
                    profile.getId());
        }
        
        
        return mapping;
    }

    /**
     * This is probably a bad idea.
     *
     * @param plugin
     */
    public static void hackTheGibson(BeardStat plugin) {
        IStatDataProvider provider = plugin.getStatManager().backendDatabase;
        plugin.printCon("Getting player names");
        List<String> names = provider.getStatBlobsHeld();
        plugin.printCon("" + names.size() +" entries found");
        long t = System.currentTimeMillis();
        for (int i = 0; i < names.size(); i += MAX_PER_REQUEST) {
            String[] toGet = new String[Math.min(MAX_PER_REQUEST, names.size())];
            for (int k = 0; k < toGet.length; k++) {
                toGet[k] = names.get(i + k);
            }
            Map<String, String> map = getUUIDS(toGet);
            System.out.println("found " + map.size() + "/" + MAX_PER_REQUEST + " entries");
            for (Entry<String, String> e : map.entrySet()) {
                //System.out.println(e.getKey() + " = " + e.getValue());
            }
        }
        plugin.printCon("UUIDs gotten: Took " + (System.currentTimeMillis() - t) + " milliseconds");
        
    }
    
    public static void main(String[] args){
        System.out.println(getUUIDS("Tehbeard").get("Tehbeard"));
    }
}
