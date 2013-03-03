package com.tehbeard.BeardStat.listeners;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.dragonzone.promise.Promise;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

import com.tehbeard.BeardStat.containers.EntityStatBlob;

/**
 * translates a material to the metadata to capture.
 * 
 * Current materials
 * Wood
 * log
 * sapling
 * dye
 * coal
 * slab
 * wool
 * stone brick
 * @author james
 *
 */
public class MetaDataCapture {

    //private static Material[] mats = {Material.WOOD,Material.LOG,Material.SAPLING,Material.INK_SACK,Material.COAL,Material.STEP,Material.WOOL,Material.SMOOTH_BRICK};

    public static final Map<Material,EntryInfo> mats = new HashMap<Material, EntryInfo>();

    
    public static void readData(InputStream is){
    	Scanner s = new Scanner(is);
    	
    	while(s.hasNext()){
    		String line = s.nextLine();
    		String[] entry = line.split(",");
    		
    		EntryInfo ei = new EntryInfo();
    		Material mat = Material.getMaterial(Integer.parseInt(entry[0].replaceAll("[^0-9]","")));
    		ei.mask      = Integer.parseInt(entry[1].replaceAll("[^0-9]",""));
    		ei.min       = Integer.parseInt(entry[2].replaceAll("[^0-9]",""));
    		ei.max       = Integer.parseInt(entry[3].replaceAll("[^0-9]",""));
    		mats.put(mat, ei);
    	}
    	
    	s.close();
    }


    public static void saveMetaDataMaterialStat(Promise<EntityStatBlob> blob,String domain,String world,String category,Material material,int dataValue,int value){
        String matName = material.toString().toLowerCase().replace("_","");
        
        blob.onResolve(new DelegateIncrement(domain,world,category, matName,value));
        if(mats.containsKey(material)){
            String tag = "_" + (dataValue & mats.get(material).mask);
            blob.onResolve(new DelegateIncrement(domain,world,category, matName + tag,value));
        }
        if(material.isRecord()){
            blob.onResolve(new DelegateIncrement(domain,world,category, "records",value));

        }
    }

    public static void saveMetaDataEntityStat(Promise<EntityStatBlob> blob,String domain,String world,String category,Entity entity,int value){
        String entityName = entity.getType().toString().toLowerCase().replace("_","");
        blob.onResolve(new DelegateIncrement(domain,world,category, entityName,value));

        if(entity instanceof Skeleton){
            blob.onResolve(new DelegateIncrement(domain,world,category, ((Skeleton)entity).getSkeletonType().toString().toLowerCase() + "_" + entityName,value));
        }

        if(entity instanceof Zombie){
            if(((Zombie)entity).isVillager()){
                blob.onResolve(new DelegateIncrement(domain,world,category, "villager_zombie",value));
            }
        }
    }



    public static boolean hasMetaData(Material mat){
        return mats.containsKey(mat);


    }
    
    

    public static class EntryInfo{
    	public int mask;
    	public int min;
    	public int max;
    }
}
