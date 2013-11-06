package com.tehbeard.beardstat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tehbeard.beardstat.containers.StaticStat;

public class TestStaticPlayerStat {

    StaticStat stat;

    /*
     * Given a StaticStat object With a value set to 0 and the domain domain and
     * the world world and the category category and the stat statistic
     */
    @Before
    public void setup() {
        this.stat = new StaticStat("domain", "world", "category", "statistic", 0);
    }

    /*
     * When I increment the stat by 1 Then the value should be 1 And the archive
     * flag should be set And When I increment the stat by 5 again Then the
     * value should be 6
     */
    @Test
    public void test_increment() {
        assertEquals("stat is zero", 0, this.stat.getValue());
        this.stat.incrementStat(1);
        assertEquals("stat is one", 1, this.stat.getValue());
        assertTrue("Archive flag set", this.stat.isArchive());
        this.stat.incrementStat(5);
        assertEquals("stat is six", 6, this.stat.getValue());
    }

    /*
     * When I decrement the stat by 1 Then the value should be -1 And the
     * archive flag should be set And When I increment the stat by 4 again Then
     * the value should be -5
     */
    @Test
    public void test_decrement() {
        assertEquals("stat is zero", 0, this.stat.getValue());
        this.stat.decrementStat(1);
        assertEquals("stat is minus one", -1, this.stat.getValue());
        assertTrue("Archive flag set", this.stat.isArchive());
        this.stat.decrementStat(4);
        assertEquals("stat is minus six", -5, this.stat.getValue());
    }

    /*
     * When I set the value of the stat to 500 Then the value of the stat should
     * be 500 and the archive flag should be set
     */
    @Test
    public void test_set() {
        assertEquals("stat is zero", 0, this.stat.getValue());
        this.stat.setValue(500);
        assertEquals("stat is five hundred", 500, this.stat.getValue());
        assertTrue("Archive flag set", this.stat.isArchive());
    }

    /*
     * Then the domain should be domain and the world should be world and the
     * category should be category and the stat should be statistic
     */
    @Test
    public void test_fields() {
        assertEquals("Domain", "domain", this.stat.getDomain());
        assertEquals("World", "world", this.stat.getWorld());
        assertEquals("Category", "category", this.stat.getCategory());
        assertEquals("Statistic", "statistic", this.stat.getStatistic());
    }
}
