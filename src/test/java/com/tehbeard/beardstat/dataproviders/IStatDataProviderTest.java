/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.dataproviders.metadata.CategoryMeta;
import com.tehbeard.beardstat.dataproviders.metadata.DomainMeta;
import com.tehbeard.beardstat.dataproviders.metadata.StatisticMeta;
import com.tehbeard.beardstat.dataproviders.metadata.WorldMeta;
import java.io.File;
import net.dragonzone.promise.Promise;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James
 */
public abstract class IStatDataProviderTest {
    
    protected IStatDataProvider instance;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    

    /**
     * Test of pullEntityBlob method, of class IStatDataProvider.
     */
    @Test
    public void testPullEntityBlob() {
        System.out.println("pullEntityBlob");
        ProviderQuery query = new ProviderQuery("tehbeard", IStatDataProvider.PLAYER_TYPE, null, false);
        Promise<EntityStatBlob> result = instance.pullEntityBlob(query);
        EntityStatBlob blob = result.getValue();
        assertEquals(blob.getName(), "Tehbeard");
        
    }

    /**
     * Test of pullEntityBlobDirect method, of class IStatDataProvider.
     */
    @Test
    public void testPullEntityBlobDirect() {
        
        System.out.println("pullEntityBlobDirect");
        ProviderQuery query = new ProviderQuery("tehbeard", IStatDataProvider.PLAYER_TYPE, null, false);
        EntityStatBlob result = instance.pullEntityBlobDirect(query);
        assertEquals(result.getName(), "Tehbeard");

    }

    /**
     * Test of pushEntityBlob method, of class IStatDataProvider.
     */
    @Test
    public void testPushEntityBlob() {
        System.out.println("pushEntityBlob");
        ProviderQuery query = new ProviderQuery("tehbeard", IStatDataProvider.PLAYER_TYPE, null, false);
        EntityStatBlob blob = instance.pullEntityBlobDirect(query);
        blob.getStat("world", "stats", "playedfor").setValue(500);
        instance.pushEntityBlob(blob);
        instance.flushSync();
        
        assertEquals("value was written", 500,instance.pullEntityBlobDirect(query).getStat("world", "stats", "playedfor").getValue());
        
        
    }

    /**
     * Test of hasEntityBlob method, of class IStatDataProvider.
     */
    @Test
    public void testHasEntityBlob() {
        System.out.println("hasEntityBlob");
        ProviderQuery query = new ProviderQuery("MrRogers", IStatDataProvider.PLAYER_TYPE, null, false);;
        boolean expResult = false;
        boolean result = instance.hasEntityBlob(query);
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteEntityBlob method, of class IStatDataProvider.
     */
    @Test
    public void testDeleteEntityBlob() {
        System.out.println("deleteEntityBlob");
        System.out.println("NOT IMPLEMENTED.");
    }

    /**
     * Test of queryDatabase method, of class IStatDataProvider.
     */
    @Test
    public void testQueryDatabase() {
        System.out.println("queryDatabase");
        ProviderQuery query = new ProviderQuery(null, IStatDataProvider.PLAYER_TYPE, null, false);;
        ProviderQueryResult[] result = instance.queryDatabase(query);
        assertEquals("5 entries returned",5, result.length);
    }

    /**
     * Test of flushSync method, of class IStatDataProvider.
     */
    @Test
    public void testFlushSync() {
        System.out.println("flushSync");
        instance.flushSync();
    }


    /**
     * Test of getDomain method, of class IStatDataProvider.
     */
    @Test
    public void testGetDomain() {
        System.out.println("getDomain");
        String gameTag = "default";
        DomainMeta result = instance.getDomain(gameTag);
        assertEquals(1, result.getDbId());
    }

    /**
     * Test of getWorld method, of class IStatDataProvider.
     */
    @Test
    public void testGetWorld() {
        System.out.println("getWorld");
        String gameTag = "world";
        WorldMeta result = instance.getWorld(gameTag);
        assertEquals("world", result.getGameTag());
    }

    /**
     * Test of getCategory method, of class IStatDataProvider.
     */
    @Test
    public void testGetCategory() {
        System.out.println("getCategory");
        String gameTag = "stats";
        CategoryMeta result = instance.getCategory(gameTag);
        assertEquals(gameTag, result.getGameTag());
    }

    /**
     * Test of getStatistic method, of class IStatDataProvider.
     */
    @Test
    public void testGetStatistic() {
        System.out.println("getStatistic");
        String gameTag = "playedfor";
        StatisticMeta result = instance.getStatistic(gameTag);
        assertEquals(gameTag, result.getName());
        assertEquals(StatisticMeta.Formatting.time, result.getFormat());
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateBackup method, of class IStatDataProvider.
     */
    @Test
    public void testGenerateBackup() {
        System.out.println("generateBackup");
        File file = new File("TestBackup");
        instance.generateBackup(file);
        file.delete();
    }

    /**
     * Test of pullDocument method, of class IStatDataProvider.
     */
    @Test
    public void testPullDocument() {
        
        System.out.println("pullDocument");
        fail("The test case is a prototype.");
        
    }

    /**
     * Test of pushDocument method, of class IStatDataProvider.
     */
    @Test
    public void testPushDocument() throws Exception {
        
        System.out.println("pushDocument");
        fail("The test case is a prototype.");
        
    }

    /**
     * Test of deleteDocument method, of class IStatDataProvider.
     */
    @Test
    public void testDeleteDocument() {
        
        System.out.println("deleteDocument");
        System.out.println("NOT IMPLEMENETED.");
        
    }

    /**
     * Test of getDocumentKeysInDomain method, of class IStatDataProvider.
     */
    @Test
    public void testGetDocumentKeysInDomain() {
        System.out.println("getDocumentKeysInDomain");
        fail("The test case is a prototype.");
    }

}