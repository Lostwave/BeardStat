package com.tehbeard.beardstat.test;

import com.google.gson.annotations.Expose;
import com.tehbeard.beardstat.containers.documents.DocumentRegistry;
import com.tehbeard.beardstat.containers.documents.IStatDocument;
import com.tehbeard.beardstat.containers.documents.StatDocument;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James
*/
@StatDocument("memo")
public class MemoDocument implements IStatDocument{
    
    static{
        DocumentRegistry.registerDocument(MemoDocument.class);
    }
    
    @Expose
    public List<Memo> memos;
    
    public MemoDocument(){
        memos = new ArrayList<Memo>();
    }
    
    public static class Memo{
        @Expose
        private final String from;
        @Expose
        private final String msg;

        public Memo(String from, String msg){
            this.from = from;
            this.msg = msg;
            
        }

        public String getFrom() {
            return from;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "from: " + from + ", " + msg;
        }
        
        
    }
    
    public static void main(String[] args){
        MemoDocument doc = new MemoDocument();
        doc.memos.add(new Memo("Tulonsae","Good job on the document system."));
        doc.memos.add(new Memo("comet1","I still no vet."));
        doc.memos.add(new Memo("invoop","Enigma needs more glands."));
        System.out.println(DocumentRegistry.instance().toJson(doc));
    }
}
