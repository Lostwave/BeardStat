/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import java.util.UUID;

/**
 *
 * @author James
 */
public class ProviderQuery {
    public final String name;
    public final String type;
    public final UUID uuid;
    public final boolean create;
    
    public ProviderQuery(String name,String type,UUID uuid,boolean create){
        this.name = name;
        this.type = type;
        this.uuid = uuid;
        this.create = create;
    }
    
    
}
