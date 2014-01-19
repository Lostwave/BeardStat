package com.tehbeard.beardstat.dataproviders.identifier;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;

import com.tehbeard.beardstat.utils.BukkitHumanNameGenerator;

/**
 * Uses the old metadata id system, To be dropped in 0.7.2 in favour of minecraft string ids
 * @author James
 *
 */
@SuppressWarnings("deprecation")
public class HomebrewIdentifierGenerator implements IIdentifierGenerator{

    
    @Override
    public String keyForId(int id, int meta) {
        Material m = Material.getMaterial(id);
        EntryInfo mdi = mats.get(m);
        if(mdi!=null){
            return m.toString().toLowerCase().replace("_", "") + "_" + mdi.getMetdataValue(meta);
        }
        return null;
    }

    @Override
    public String keyForId(int id) {
        return Material.getMaterial(id).toString().toLowerCase().replace("_", "");
    }

    @Override
    public String keyForEntity(Entity entity) {
        return entity.getType().toString().toLowerCase().replaceAll("_", "");
    }

    @Override
    public String keyForPotionEffect(PotionEffect effect){
        String effectName = effect.getType().getName().toLowerCase().replaceAll("_", "");
        String level = "" + effect.getAmplifier();
        return effectName + "_" + level;
    }
    
    public static final Map<Material, EntryInfo> mats = new HashMap<Material, EntryInfo>();

    static {
        mats.put(Material.QUARTZ_BLOCK, new EntryInfo(15, 0, 15) {
            @Override
            public int getMetdataValue(int value) {
                return (value > 2) ? 2 : value;
            }
        });
    }

    public static void readData(InputStream is) {
        Scanner s = new Scanner(is);

        while (s.hasNextLine()) {
            String line = s.nextLine();
            if(line.startsWith("#")){continue;}
            String[] entry = line.split(",");
            if(entry.length < 4){System.out.println("Invalid entry found [" + line + "]");continue;}
            try {
                int blockId = Integer.parseInt(entry[0].replaceAll("[^0-9]", ""));
                int metaMask = Integer.parseInt(entry[1].replaceAll("0(X|x)", "").replaceAll("[^0-9A-Fa-f]", ""), 16);
                int min =  Integer.parseInt(entry[2].replaceAll("[^0-9A-Fa-f]", ""));
                int max = Integer.parseInt(entry[3].replaceAll("[^0-9A-Fa-f]", ""));
                
                Material mat = Material.getMaterial(blockId);
                if(mat == null){continue;}
                EntryInfo ei = new EntryInfo(metaMask, min, max);
                mats.put(mat, ei);
            } catch (Exception e) {
                System.out.println("Failed to load metadata for id: " + entry[0] + ", skipping (version mismatch?)");
            }
        }

        s.close();
    }
    


    public static class EntryInfo {
        public int mask;
        public int min;
        public int max;

        public EntryInfo(int mask, int min, int max) {
            super();
            this.mask = mask;
            this.min = min;
            this.max = max;
        }

        public boolean valid(int value) {
            return ((value >= this.min) && (value <= this.max));
        }

        public int getMetdataValue(int value) {
            return value & this.mask;
        }

        @Override
        public String toString() {
            return "EntryInfo [mask=" + this.mask + ", min=" + this.min + ", max=" + this.max + "]";
        }

    }



    @Override
    public String getHumanName(String key) {
        return BukkitHumanNameGenerator.map(key);
    }

}