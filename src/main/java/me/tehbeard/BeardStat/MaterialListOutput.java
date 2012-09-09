package me.tehbeard.BeardStat;

import me.tehbeard.BeardStat.listeners.MetaDataCapture;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionType;

public class MaterialListOutput {

	public static void main(String[] args){

		System.out.println("Dumping block data");
		System.out.println("===");
		for(Material m : Material.values()){
			if(m.isBlock()){
				System.out.println(m.toString().replace("_", "").toLowerCase());
			}
		}
		System.out.println();
		System.out.println("Dumping item data");
		System.out.println("===");
		for(Material m : Material.values()){
			if(!m.isBlock())
			System.out.println(m.toString().replace("_", "").toLowerCase());
		}

		System.out.println();
		System.out.println("Dumping entity data");
		System.out.println("===");
		for(EntityType m : EntityType.values()){
			System.out.println(m.toString().replace("_", "").toLowerCase());
		}
		
		System.out.println();
		System.out.println("Dumping potion data");
		System.out.println("===");
		for(PotionType m : PotionType.values()){
			System.out.println("splash" + m.toString().replace("_", "").toLowerCase());
		}
		
		System.out.println();
		System.out.println("Dumping damage cause data");
		System.out.println("===");
		for(DamageCause m : DamageCause.values()){
			System.out.println(m.toString().replace("_", "").toLowerCase());
		}
		
		System.out.println();
		System.out.println("Dumping regen data");
		System.out.println("===");
		for(RegainReason m : RegainReason.values()){
			System.out.println(m.toString().replace("_", "").toLowerCase());
		}

		
		System.out.println();
        System.out.println("Dumping meta data");
        System.out.println("===");
		MetaDataCapture.dumpData();

	}
}
