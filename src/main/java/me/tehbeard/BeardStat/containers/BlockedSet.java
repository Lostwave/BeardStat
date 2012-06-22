package me.tehbeard.BeardStat.containers;

import java.util.HashSet;

public class BlockedSet<T> extends HashSet<T>{

    boolean blocked = false;
    
    public void setBlocked(boolean blocked){
        this.blocked = blocked;
    }
    
    public boolean add(T e) {
        if(blocked){throw new UnsupportedOperationException();}
        return super.add(e);
        
    }
    
    public boolean remove(Object o) {
        if(blocked){throw new UnsupportedOperationException();}
        return super.remove(o);
    }
}
