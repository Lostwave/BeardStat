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
