package com.tehbeard.BeardStat.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sandstone;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Step;
import org.bukkit.material.TexturedMaterial;
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
        readers.put(Coal.class, "getType");
        readers.put(FlowerPot.class, "getContents");
        //readers.put(TexturedMaterial.class, "getMaterial");

    }

    public static Map<String,String> generateHumanNames(){
        Map<String,String> out = new TreeMap<String, String>();

        for(Material material : Material.values()){
            EntryInfo info     = MetaDataCapture.hasMetaData(material) ? MetaDataCapture.mats.get(material) : null;
            
            String bsid_nometa = material.toString().toLowerCase().replace("_", "");
           
            out.put(bsid_nometa, material.toString().toLowerCase().replace("_", " "));

            if(info!=null){
                Set<Integer> tags = new HashSet<Integer>();

                for(int i =0;i<16;i++){
                    tags.add(info.getMetdataValue(i));
                }

                for(int i : tags){
                    String bsid = bsid_nometa + "_" + i;
                    String humanName = getDataBasedName(material,(byte)(i&0xF)).toLowerCase().replace("_", " ");
                    humanName = humanName.replace("generic", "oak");
                    out.put(bsid, humanName + " " + material.toString().toLowerCase().replace("_", " "));
                }
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


    public static void main(String[] args) throws FileNotFoundException{
        MetaDataCapture.readData(new FileInputStream("c:/users/james/workspace/BeardStat/src/main/resources/metadata.txt"));

        for(Entry<String, String> entry : generateHumanNames().entrySet()){
            System.out.println(entry.getKey() + " ==> " + entry.getValue());
        }

        /*for(Material mat : MetaDataCapture.mats.keySet()){
            System.out.println(mat);
            System.out.println();



            for(int i = 0;i<16;i++){
                System.out.print("" + i + " : ");
                System.out.println(getDataBasedName(mat, (byte)i).toLowerCase() + " " + mat.toString().toLowerCase().replace("_", " "));
            }
            System.out.println();
        }*/
    }
}
