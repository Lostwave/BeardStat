/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat.DataProviders.metadata;

/**
 * Class to hold metadata about a world
 * @author James
 */
public class WorldMeta {
    private final int dbId;
    private final String gameTag;
    private final String descName;
    
    
    
    public WorldMeta(int dbId, String gameTag, String descName){
        this.dbId = dbId;
        this.gameTag = gameTag;
        this.descName = descName;
        
    }

    public int getDbId() {
        return dbId;
    }

    public String getGameTag() {
        return gameTag;
    }

    public String getDescName() {
        return descName;
    }
    
    
}
