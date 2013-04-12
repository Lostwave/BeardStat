package com.tehbeard.beardstat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.containers.StaticStat;

public class TestPlayerStatBlob {

    
    private EntityStatBlob blob;
    
    @Before
    public void setup(){
        blob = new EntityStatBlob("name", 101, "player");
    }
    
    @Test
    public void testAddStat(){
        
        StaticStat stat = new StaticStat("domain","world", "cat", "stat", 555);
        
        blob.addStat(stat);
        assertEquals("Stat in blobs collection", 1,blob.getStats().size());
        IStat st = blob.getStat("domain", "world","cat", "stat");
        assertEquals("Same stat returned",stat,st);
    }
    
    @Test
    public void testGetStatCreate(){
        
        IStat stat = blob.getStat("domain","world", "cat", "stat");
        
        assertEquals("Domain","domain", stat.getDomain());
        assertEquals("World","world", stat.getWorld());
        assertEquals("Category","cat", stat.getCategory());
        assertEquals("Statistic","stat", stat.getStatistic());
        assertEquals("Value",0, stat.getValue());
    }
}
