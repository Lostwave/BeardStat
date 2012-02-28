package me.tehbeard.BeardStat;

import org.bukkit.Material;

public class MaterialListOutput {

    public static void main(String[] args){
        for(Material m : Material.values()){
            System.out.println(m.toString().replace("_", "").toLowerCase());
        }
    }
}
