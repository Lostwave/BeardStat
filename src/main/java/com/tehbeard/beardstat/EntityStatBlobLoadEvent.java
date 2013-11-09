/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author James
 */
public class EntityStatBlobLoadEvent extends Event{
    
    private static final HandlerList handlers = new HandlerList();
    private final EntityStatBlob blob;
    
    public EntityStatBlobLoadEvent(EntityStatBlob blob){
        this.blob = blob;
        
    }

    public EntityStatBlob getBlob() {
        return blob;
    }
    
    
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
