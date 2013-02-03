package me.tehbeard.BeardStat.listeners;

import java.util.HashMap;
import java.util.Map;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import net.dragonzone.promise.Promise;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

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

    public static final Map<Material,Integer> mats = new HashMap<Material, Integer>();

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

    public static void addData(int typeid, int mask){
        Material m = Material.getMaterial(typeid);
        if(m!=null){
            mats.put(m, mask);
        }
    }


    public static void saveMetaDataMaterialStat(Promise<PlayerStatBlob> blob,String category,Material material,int dataValue,int value){
        String matName = material.toString().toLowerCase().replace("_","");
        
        blob.onResolve(new DelegateIncrement(category, matName,value));
        if(mats.containsKey(material)){
            String tag = "_" + (dataValue & mats.get(material));
            blob.onResolve(new DelegateIncrement(category, matName + tag,value));
        }
        if(material.isRecord()){
            blob.onResolve(new DelegateIncrement(category, "records",value));

        }
    }

    public static void saveMetaDataEntityStat(Promise<PlayerStatBlob> blob,String category,Entity entity,int value){
        String entityName = entity.getType().toString().toLowerCase().replace("_","");
        blob.onResolve(new DelegateIncrement(category, entityName,value));

        if(entity instanceof Skeleton){
            blob.onResolve(new DelegateIncrement(category, ((Skeleton)entity).getSkeletonType().toString().toLowerCase() + "_" + entityName,value));
        }

        if(entity instanceof Zombie){
            if(((Zombie)entity).isVillager()){
                blob.onResolve(new DelegateIncrement(category, "villager_zombie",value));
            }
        }
    }



    public static boolean hasMetaData(Material mat){
        return mats.containsKey(mat);


    }


}
