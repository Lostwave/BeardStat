package com.tehbeard.utils.mojang.api.profiles;

import java.util.UUID;

public class Profile {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Generates a UUID from the returned id
     * @author Tehbeard
     * @return 
     */
    public UUID getUUID(){
        return UUID.nameUUIDFromBytes(getId().getBytes());
    }
}
