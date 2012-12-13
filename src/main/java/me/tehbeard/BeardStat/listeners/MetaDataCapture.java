package me.tehbeard.BeardStat.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import me.tehbeard.BeardStat.containers.PlayerStatBlob;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
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

    private static Map<Material,Integer> mats = new HashMap<Material, Integer>();

    static{
        //nature
        mats.put(Material.WOOD            ,0x3);
        mats.put(Material.LOG             ,0x3);
        mats.put(Material.LEAVES          ,0x3);
        mats.put(Material.SAPLING         ,0x3);
        mats.put(Material.DEAD_BUSH       ,0x3);

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


    public static void saveMetaDataMaterialStat(PlayerStatBlob blob,String category,Material material,int dataValue,int value){
        String matName = material.toString().toLowerCase().replace("_","");
        blob.getStat(category, matName).incrementStat(value);
        if(mats.containsKey(material)){
            String tag = "_" + (dataValue & mats.get(material));
            blob.getStat(category, matName + tag).incrementStat(value);
        }
        if(material.isRecord()){
            blob.getStat(category, "records").incrementStat(value);
            
        }
    }
    
    public static void saveMetaDataEntityStat(PlayerStatBlob blob,String category,Entity entity,int dataValue,int value){
        String entityName = entity.getType().toString().toLowerCase().replace("_","");
        blob.getStat(category, entityName).incrementStat(value);
        
        if(entity instanceof Skeleton){
            if(((Skeleton)entity).getSkeletonType() == SkeletonType.WITHER){
                blob.getStat(category, "wither_skeleton").incrementStat(value);
            }
        }
        
        if(entity instanceof Zombie){
            if(((Zombie)entity).isVillager()){
                blob.getStat(category, "villager_zombie").incrementStat(value);
            }
        }
    }
    


    public static void dumpData(){
        HashSet<String> lines = new HashSet<String>();
        for(Entry<Material, Integer> entry  : mats.entrySet()){
            Material m = entry.getKey();
            int k = entry.getValue();
            for(int i = 0;i<16;i++){
                String s = m.toString().toLowerCase().replace("_","") + "_" + (i & k);
                if(!lines.contains(s)){
                lines.add(s);
                System.out.println("<ul>" + s + "</ul>");
                }
            }
        }
        
    }
    public static boolean hasMetaData(Material mat){
        return mats.containsKey(mat);
        

    }
    

}
