/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders.sqlite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James
 */
public class DocumentDatabase {
    
    @Expose
    @SerializedName("e")
    private Map<Integer,DocumentStore> entries = new HashMap<Integer, DocumentStore>();
    
    public DocumentStore getStore(int entityId){
        if(!entries.containsKey(entityId)){
            entries.put(entityId,new DocumentStore());
        }
        return entries.get(entityId);
    }
    
}
