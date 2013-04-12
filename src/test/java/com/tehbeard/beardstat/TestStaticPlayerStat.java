package com.tehbeard.beardstat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tehbeard.BeardStat.containers.StaticStat;

public class TestStaticPlayerStat {

	StaticStat stat;

	/*
	 * Given a StaticStat object
	 * With a value set to 0
	 * and the domain domain
	 * and the world world
	 * and the category category
	 * and the stat statistic
	 */
	@Before
	public void setup(){
		stat = new StaticStat("domain", "world", "category", "statistic", 0);
	}

	/*
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

	/*
	 * When I decrement the stat by 1
	 * Then the value should be -1
	 * And the archive flag should be set
	 * And When I increment the stat by 4 again
	 * Then the value should be -5
	 */
	@Test
	public void test_decrement(){
		assertEquals("stat is zero",0, stat.getValue());
		stat.decrementStat(1);
		assertEquals("stat is minus one", -1,stat.getValue());
		assertTrue("Archive flag set",stat.isArchive());
		stat.decrementStat(4);
		assertEquals("stat is minus six", -5, stat.getValue());
	}

	/*
	 * When I set the value of the stat to 500
	 * Then the value of the stat should be 500
	 * and the archive flag should be set
	 */
	@Test
	public void test_set(){
		assertEquals("stat is zero",0, stat.getValue());
		stat.setValue(500);
		assertEquals("stat is five hundred", 500,stat.getValue());
		assertTrue("Archive flag set",stat.isArchive());
	}

	/*
	 * Then the domain should be domain
	 * and the world should be world
	 * and the category should be category
	 * and the stat should be statistic

	 */
	@Test 
	public void test_fields(){ 
		assertEquals("Domain","domain", stat.getDomain());
		assertEquals("World","world", stat.getWorld());
		assertEquals("Category","category", stat.getCategory());
		assertEquals("Statistic","statistic", stat.getStatistic());
	}
}
