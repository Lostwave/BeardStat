package com.tehbeard.beardstat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tehbeard.BeardStat.containers.StaticStat;

public class TestStaticPlayerStat {

    StaticStat stat;

    @Before
    public void setup(){
        stat = new StaticStat("domain", "world", "category", "statistic", 0);
    }
    
    /*
     * Given a StaticStat object
     * With a value set to default (0)
     * When I increment the stat by 1
     * Then the value should be 1
     * And the archive flag should be set
     * And When I increment the stat by 5 again
     * Then the value should be 6
     */
    @Test
    public void test_increment(){
        assertEquals("stat is zero",0, stat.getValue());
        stat.incrementStat(1);
        assertEquals("stat is one", 1,stat.getValue());
        assertTrue("Archive flag set",stat.isArchive());
        stat.incrementStat(5);
        assertEquals("stat is six", 6, stat.getValue());
    }
    
    @Test
    public void test_decrement(){
        assertEquals("stat is zero",0, stat.getValue());
        stat.decrementStat(1);
        assertEquals("stat is minus one", -1,stat.getValue());
        assertTrue("Archive flag set",stat.isArchive());
        stat.decrementStat(4);
        assertEquals("stat is minus six", -5, stat.getValue());
    }
    
    @Test
    public void test_set(){
        assertEquals("stat is zero",0, stat.getValue());
        stat.setValue(500);
        assertEquals("stat is five hundred", 500,stat.getValue());
        assertTrue("Archive flag set",stat.isArchive());
    }
    
    @Test 
    public void test_fields(){ 
        assertEquals("Domain","domain", stat.getDomain());
        assertEquals("World","world", stat.getWorld());
        assertEquals("Category","category", stat.getCategory());
        assertEquals("Statistic","statistic", stat.getStatistic());
    }
}
