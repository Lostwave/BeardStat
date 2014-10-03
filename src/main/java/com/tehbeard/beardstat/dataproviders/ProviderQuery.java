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
    
    public static final ProviderQuery ALL_PLAYERS = new ProviderQuery(null, IStatDataProvider.PLAYER_TYPE, null, false);
    
    public final String name;
    public final String type;
    private final UUID uuid;
    
    public final boolean create;
    
    public boolean likeName = false;
    
    public ProviderQuery(UUID player, boolean create){
        this(null, IStatDataProvider.PLAYER_TYPE, player, create);
    }
    
    public ProviderQuery(String name,String type,UUID uuid,boolean create){
        if(type == null){throw new IllegalArgumentException("Type must not be null.");}
        this.name = name;
        this.type = type;
        this.uuid = uuid != null ? uuid : name != null ? UUID.nameUUIDFromBytes(name.getBytes()) : null;
        this.create = create;
    }
    
    public ProviderQuery partialNameMatch(){
        likeName = true;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 31 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 31 * hash + (this.create ? 1 : 0);
        hash = 31 * hash + (this.likeName ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProviderQuery other = (ProviderQuery) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid)) {
            return false;
        }
        if (this.create != other.create) {
            return false;
        }
        if (this.likeName != other.likeName) {
            return false;
        }
        return true;
    }

    @Override
    public ProviderQuery clone(){
        ProviderQuery p = new ProviderQuery(name, type, uuid, create);
        p.likeName = likeName;
        return p;
    }

    @Override
    public String toString() {
        return "ProviderQuery [name=" + name + ", type=" + type + ", uuid=" + uuid + ", create=" + create
                + ", likeName=" + likeName + "]";
    }
    
    public String getUUIDString(){
        return uuid == null ? null : uuid.toString().replaceAll("-", "");
    }
    
    public UUID getUUID(){
        return uuid;
    }
    
}
