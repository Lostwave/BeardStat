package com.tehbeard.beardstat.dataproviders.identifier;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Provides methods to get string ids for various items
 * @author James
 *
 */
@SuppressWarnings("deprecation")
public class IdentifierService {
    
    private static IIdentifierGenerator generator;
    
    public static void setGenerator(IIdentifierGenerator generator){
        IdentifierService.generator = generator;
    }
    
    public static String getIdForMaterial(Material material){
        return generator.keyForId(material.getId());
    }
            
    public static String getIdForMaterial(Material material, int meta){
        return generator.keyForId(material.getId(), meta);
    }
    
    public static String getIdForItemStack(ItemStack itemStack){
        return generator.keyForId(itemStack.getType().getId());
    }
    
    public static String getIdForItemStackWithMeta(ItemStack itemStack){
        return generator.keyForId(itemStack.getType().getId(), itemStack.getDurability());
    }
    
    public static String getIdForEntity(Entity entity){
        return generator.keyForEntity(entity);
    }

    public static String getIdForPotionEffect(PotionEffect effect) {
        return generator.keyForPotionEffect(effect);
    }
    
    public static String getHumanName(String key){
        return generator.getHumanName(key);
    }

}
