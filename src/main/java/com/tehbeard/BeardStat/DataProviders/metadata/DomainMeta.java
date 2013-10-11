/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat.DataProviders.metadata;

/**
 * Class to hold metadata about a world
 *
 * @author James
 */
public class DomainMeta {

    private final int dbId;
    private final String gameTag;

    public DomainMeta(int dbId, String gameTag) {
        this.dbId = dbId;
        this.gameTag = gameTag;
    }

    public int getDbId() {
        return dbId;
    }

    public String getGameTag() {
        return gameTag;
    }
}
