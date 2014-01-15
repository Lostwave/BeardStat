package com.tehbeard.beardstat.dataproviders.identifier;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.tehbeard.beardstat.utils.MetaDataCapture;
import com.tehbeard.beardstat.utils.MetaDataCapture.EntryInfo;

/**
 * Uses the old metadata id system, To be dropped in 0.7.2 in favour of minecraft string ids
 * @author James
 *
 */
@SuppressWarnings("deprecation")
public class HomebrewIdentifierGenerator implements IIdentifierGenerator{

    
    @Override
    public String keyForId(int id, int meta) {
        Material m = Material.getMaterial(id);
        EntryInfo mdi = MetaDataCapture.mats.get(m);
        if(mdi!=null){
            return m.toString().toLowerCase().replace("_", "") + "_" + mdi.getMetdataValue(meta);
        }
        return null;
    }

    @Override
    public String keyForId(int id) {
        return Material.getMaterial(id).toString().toLowerCase().replace("_", "");
    }

    @Override
    public String keyForEntity(Entity entity) {
        return entity.getType().toString().toLowerCase().replaceAll("_", "");
    }

}