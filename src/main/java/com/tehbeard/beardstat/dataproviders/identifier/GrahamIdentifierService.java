package com.tehbeard.beardstat.dataproviders.identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 * Generates Ids based off the data from http://minecraft-ids.grahamedgecombe.com
 * @author James
 *
 */
public class GrahamIdentifierService implements IIdentifierGenerator {
    
    private static Map<Integer,GrahamItem> items;

    @Override
    public String keyForId(int id, int meta) {
        return null;
    }

    @Override
    public String keyForId(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String keyForEntity(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String keyForPotionEffect(PotionEffect effect) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHumanName(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void readData(InputStream is){
        Gson gson = new Gson();
        List<GrahamItem> itemList = gson.fromJson(new InputStreamReader(is), new TypeToken<List<GrahamItem>>() {
        }.getType());
        
        for(GrahamItem i : itemList){
            items.put(i.type, i);
        }
    }
    
    public class GrahamItem {
        public int type;
        public int meta;
        public String name;
        public String text_type;
    }
}
