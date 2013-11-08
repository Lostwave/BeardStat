/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;



/**
 *
 * @author James
 */
public class ProviderQueryResult {
    public final int dbid;
    public final String name;
    public final String type;
    public final String uuid;

    public ProviderQueryResult(int dbid,String name,String type,String uuid){
        this.dbid = dbid;
        this.name = name;
        this.type = type;
        this.uuid = uuid;
        
    }
}
