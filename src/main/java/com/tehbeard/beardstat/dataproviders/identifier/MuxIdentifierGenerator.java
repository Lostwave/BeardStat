package com.tehbeard.beardstat.dataproviders.identifier;

import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;

/**
 * Muxes various {@link IIdentifierGenerator} instances, priority given to the first declared generator
 * @author James
 *
 */
public class MuxIdentifierGenerator implements IIdentifierGenerator {
    
    
    private IIdentifierGenerator[] generators;

    public MuxIdentifierGenerator(IIdentifierGenerator...generators){
        this.generators = generators;
    }

    @Override
    public String keyForId(int id) {
        for(IIdentifierGenerator gen : generators){
            String s = gen.keyForId(id);
            if(s!=null){
                return s;
            }
        }
        return null;
    }

    @Override
    public String keyForEntity(Entity entity) {
        for(IIdentifierGenerator gen : generators){
            String s = gen.keyForEntity(entity);
            if(s!=null){
                return s;
            }
        }
        return null;
    }

    @Override
    public String keyForId(int id, int meta) {
        for(IIdentifierGenerator gen : generators){
            String s = gen.keyForId(id, meta);
            if(s!=null){
                return s;
            }
        }
        return null;
    }

    @Override
    public String keyForPotionEffect(PotionEffect effect) {
        for(IIdentifierGenerator gen : generators){
            String s = gen.keyForPotionEffect(effect);
            if(s!=null){
                return s;
            }
        }
        return null;
    }

    @Override
    public String getHumanName(String key) {
        for(IIdentifierGenerator gen : generators){
            String s = gen.getHumanName(key);
            if(s!=null){
                return s;
            }
        }
        return key;
    }


}
