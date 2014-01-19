/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

/**
 *
 * @author James
 */
public class DocumentStore {
    
    @Expose
    Map<String,DocEntry> documents = new HashMap<String, DocEntry>();
    
    public DocEntry getDocumentData(String domainId,String key){
        String keyCode = domainId + ":" + key;
        if(!documents.containsKey(keyCode)){
            documents.put(keyCode, new DocEntry());
        }
        return documents.get(keyCode);
    }
    
    public String[] getDocsUnderDomain(String domain){
        List<String> s = new ArrayList<String>();
        for(String k : documents.keySet()){
            if(k.startsWith(domain+":")){
                s.add(k.replaceAll(domain+":", ""));
            }
        }
        return s.toArray(new String[0]);
    }
    
    public void deleteDocument(String domain,String key){
        documents.remove(domain + ":" + key);
    }
}
