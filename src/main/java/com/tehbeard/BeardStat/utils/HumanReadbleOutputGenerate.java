package com.tehbeard.BeardStat.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.material.Dye;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sandstone;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.WoodenStep;
import org.bukkit.material.Wool;

import com.tehbeard.BeardStat.utils.MetaDataCapture.EntryInfo;

public class HumanReadbleOutputGenerate {

    private static Map<Class<? extends MaterialData>, String> readers = new HashMap<Class<? extends MaterialData>, String>();

    static {

        readers.put(Dye.class, "getColor");
        readers.put(Wool.class, "getColor");
        readers.put(Leaves.class, "getSpecies");
        readers.put(Tree.class, "getSpecies");
        readers.put(WoodenStep.class, "getSpecies");
        readers.put(LongGrass.class, "getSpecies");
        readers.put(Step.class, "getMaterial");
        readers.put(SmoothBrick.class, "getMaterial");
        readers.put(Sandstone.class, "getType");
        readers.put(FlowerPot.class, "getContents");

    }
    
    public static Map<String,String> generateHumanNames(){
        Map<String,String> out = new TreeMap<String, String>();
        for(Entry<Material, EntryInfo> e :MetaDataCapture.mats.entrySet()){
            Material material  = e.getKey();
            EntryInfo info     = e.getValue();
            
            Set<Integer> tags = new HashSet<Integer>();
            for(int i =0;i<16;i++){
                tags.add(info.getMetdataValue(i));
            }
            
            for(int i : tags){
                String bsid = material.toString().toLowerCase().replace("_", "") + "_" + i;
                String humanName = getDataBasedName(material,(byte)(i&0xF));
                out.put(bsid, humanName);
            }
            
        }
        return out;
    }

    public static String getDataBasedName(Material m, byte data) {
        MaterialData md = m.getNewData(data);
        try {
            Method method = md.getClass().getMethod(readers.get(md.getClass()));

            if (method == null) {
                return "";
            }

            // System.out.println(m + "" + data);
            Object o = method.invoke(md);
            if (o instanceof MaterialData) {
                return ((MaterialData) o).getItemType().toString().toLowerCase();
            } else {
                return o.toString();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return "";
        }
        return null;
    }

}
