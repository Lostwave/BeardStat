/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders.sqlite;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James
 */
public class DocumentStore {
    
    @Expose
    Map<String,DocEntry> documents = new HashMap<String, DocEntry>();
    
    public DocEntry getDocumentData(int domainId,String key){
        String keyCode = "" + domainId + ":" + key;
        if(!documents.containsKey(keyCode)){
            documents.put(keyCode, new DocEntry());
        }
        return documents.get(keyCode);
    }
}
