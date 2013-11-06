/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.DataProviders.metadata;

/**
 * Class to hold metadata about a world
 * @author James
 */
public class CategoryMeta {
    private final int dbId;
    private final String gameTag;
    private final String statWrapper;
    
    
    public CategoryMeta(int dbId, String gameTag){
        this(dbId, gameTag,"%s");
    }
    
    public CategoryMeta(int dbId, String gameTag, String statwrapper){
        this.dbId = dbId;
        this.gameTag = gameTag;
        this.statWrapper = statwrapper;
        
    }

    public int getDbId() {
        return dbId;
    }

    public String getGameTag() {
        return gameTag;
    }

    public String wrapStatistic(String contents) {
        return String.format(statWrapper, contents);
    }
    
    
}
