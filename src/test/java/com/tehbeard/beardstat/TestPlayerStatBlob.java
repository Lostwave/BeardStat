package com.tehbeard.beardstat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.containers.StaticStat;

public class TestPlayerStatBlob {

    
    private EntityStatBlob blob;
    
    /**
     * Given an EntityStatBlob
     * with Name name
     * and id 101
     * and type player
     */
    @Before
    public void setup(){
        blob = new EntityStatBlob("name", 101, "player");
    }
    
    /*
     * When I add a StaticStat to the EntityStatBlob
     * Then the blob size() should be 1
     * And When I getState with the same fields as the StaticStat
     * Then the IStat returned should be the same object put in (StaticStat) 
     */
    @Test
    public void testAddStat(){
        
        StaticStat stat = new StaticStat("domain","world", "cat", "stat", 555);
        
        blob.addStat(stat);
        assertEquals("Stat in blobs collection", 1,blob.getStats().size());
        IStat st = blob.getStat("domain", "world","cat", "stat");
        assertEquals("Same stat returned",stat,st);
    }
    
    /*
     * When I getStat()
	 * with the domain domain
	 * and the world world
	 * and the category category
	 * and the stat statistic
	 * Then I should get a new Stat object back
	 * with the domain domain
	 * and the world world
	 * and the category category
	 * and the stat statistic
	 * and the value 0
	 */
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
