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

    public static final Map<Material,entryInfo> mats = new HashMap<Material, entryInfo>();

    static{
        //nature
        mats.put(Material.WOOD            ,0x3);
        mats.put(Material.LOG             ,0x3);
        mats.put(Material.LEAVES          ,0x3);
        mats.put(Material.SAPLING         ,0x3);
        mats.put(Material.LONG_GRASS       ,0x3);
        mats.put(Material.FLOWER_POT, 0xF);
        
        //ART
        mats.put(Material.INK_SACK        ,0xF);
        mats.put(Material.WOOL            ,0xF);

        //INDUSTRY
        mats.put(Material.COAL            ,0x1);

        //CONSTRUCTION
        mats.put(Material.STEP            ,0x7);
        mats.put(Material.DOUBLE_STEP     ,0x7);
        mats.put(Material.WOOD_STEP       ,0x3);
        mats.put(Material.WOOD_DOUBLE_STEP,0x3);

        //STRONGBADS STRONGHOLD
        mats.put(Material.SMOOTH_BRICK    ,0x3);
        mats.put(Material.MONSTER_EGGS    ,0x3);

        //EYPGT
        mats.put(Material.SANDSTONE       ,0x3);
    }

    /*public static void addData(int typeid, int mask){
        Material m = Material.getMaterial(typeid);
        if(m!=null){
            mats.put(m, mask);
        }
    }*/
    
    public static void readData(InputStream is){
    	Scanner s = new Scanner(is);
    	
    	while(s.hasNext()){
    		String line = s.nextLine();
    		String[] entry = line.split(",");
    		
    		entryInfo ei = new entryInfo();
    		Material mat = Material.getMaterial(Integer.parseInt(entry[0].replaceAll("[^0-9]","")));
    		ei.mask      = Integer.parseInt(entry[1].replaceAll("[^0-9]",""));
    		ei.min       = Integer.parseInt(entry[2].replaceAll("[^0-9]",""));
    		ei.max       = Integer.parseInt(entry[3].replaceAll("[^0-9]",""));
    		
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
    
    

    public class entryInfo{
    	public int mask;
    	public int min;
    	public int max;
    }
}
