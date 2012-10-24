package me.tehbeard.BeardStat;

import me.tehbeard.BeardStat.listeners.MetaDataCapture;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionType;

public class MaterialListOutput {

    public static void main(String[] args){

        System.out.println("<html><head><title>BeardStat Type list</title></head><body>");
        System.out.println("<h1>Block Types</h1>");
        System.out.println("<hr><ul>");
        for(Material m : Material.values()){
            if(m.isBlock()){
                System.out.println("<li>" + m.toString().replace("_", "").toLowerCase() + "</li>");
            }
        }
        System.out.println("</ul>");
        System.out.println("<h1>Dumping item data</h1>");
        System.out.println("===");
        for(Material m : Material.values()){
            if(!m.isBlock())
                System.out.println("<li>" + m.toString().replace("_", "").toLowerCase() + "</li>");
        }

        System.out.println("</ul>");
        System.out.println("<h1>Dumping entity data</h1>");
        System.out.println("<hr><ul>");
        for(EntityType m : EntityType.values()){
            System.out.println("<li>" + m.toString().replace("_", "").toLowerCase() + "</li>");
        }

        System.out.println("</ul>");
        System.out.println("<h1>Dumping potion data</h1>");
        System.out.println("<hr><ul>");
        for(PotionType m : PotionType.values()){
            System.out.println("<li>" + "splash" + m.toString().replace("_", "").toLowerCase() + "</li>");
        }

        System.out.println("</ul>");
        System.out.println("<h1>Dumping damage cause data</h1>");
        System.out.println("<hr><ul>");
        for(DamageCause m : DamageCause.values()){
            System.out.println("<li>" + m.toString().replace("_", "").toLowerCase() + "</li>");
        }

        System.out.println("</ul>");
        System.out.println("<h1>Dumping regen data</h1>");
        System.out.println("<hr><ul>");
        for(RegainReason m : RegainReason.values()){
            System.out.println("<li>" + m.toString().replace("_", "").toLowerCase() + "</li>");
        }


        System.out.println("</ul>");
        System.out.println("Dumping meta data");
        System.out.println("<hr><ul>");
        MetaDataCapture.dumpData();

        System.out.println("</ul>");
        System.out.println("</body></html>");
    }
}
