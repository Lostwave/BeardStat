package com.tehbeard.beardstat.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.dragonzone.promise.Promise;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.listeners.defer.DelegateIncrement;

/**
 * translates a material/entity to the metadata to capture.
 * WARNING: THIS DISAPPEARS IN 0.7.2 release when we swap to minecraft string ids
 * @author james
 * 
 */
public class MetaDataCapture {
    
    public static final Map<Material, EntryInfo> mats = new HashMap<Material, EntryInfo>();

    static {
        mats.put(Material.QUARTZ_BLOCK, new EntryInfo(15, 0, 15) {
            @Override
            public int getMetdataValue(int value) {
                return (value > 2) ? 2 : value;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static void readData(InputStream is) {
        Scanner s = new Scanner(is);

        while (s.hasNext()) {
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
    
    public static void saveMetadataPotionStat(Promise<EntityStatBlob> blob, String domain, String world,
    String category, PotionEffect effect, int value) {
        String effectName = effect.getType().getName().toLowerCase().replaceAll("_", "");
        String level = "" + effect.getAmplifier();
        String statName = effectName + "_" + level;
        
        blob.onResolve(new DelegateIncrement(domain, world, category, statName , value));
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
}
