/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.manager;

import com.tehbeard.beardstat.DatabaseConfiguration;
import com.tehbeard.beardstat.dataproviders.SQLiteStatDataProvider;
import com.tehbeard.beardstat.dataproviders.TestPlatform;
import com.tehbeard.utils.uuid.MojangWebAPI;
import java.sql.SQLException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author James
 */
public class EntityStatManagerTest {
    
    EntityStatManager manager = null;
    
    public EntityStatManagerTest() {
    }
    
    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        DatabaseConfiguration config = new DatabaseConfiguration(7);
        config.version = config.latestVersion;
        config.backups = false;
        
        SQLiteStatDataProvider instance = new SQLiteStatDataProvider(new TestPlatform(), ":memory:", config);
                
        String preloadStmt = instance.readSQLFile("sqlite","preload");
        for(String s : preloadStmt.split("\\;")){
            try{
           instance.getConnection().createStatement().execute(s);
            }catch(SQLException e ){
                System.out.println(s);
                e.printStackTrace();
                
                throw e;
            }
        }
        instance.cacheComponents();
        
        manager = new EntityStatManager(new TestPlatform(), instance);
    }

    /**
     * Test of getPlayer method, of class EntityStatManager.
     */
    @Test
    public void testGetPlayer_UUID_String() {
        assertNotNull(manager.getPlayer(MojangWebAPI.expandUUID("09d770ac7bfe48a2bf6877cbd21c51a1"), "Tehbeard").getValue());
    }

    /**
     * Test of getPlayerAsync method, of class EntityStatManager.
     */
    @Test
    public void testGetPlayerAsync() {
        assertNotNull(manager.getPlayerAsync(MojangWebAPI.expandUUID("09d770ac7bfe48a2bf6877cbd21c51a1"), "Tehbeard", true).getValue());
    }

    /**
     * Test of getPlayer method, of class EntityStatManager.
     */
    @Test
    public void testGetPlayer_3args() {
    }

    /**
     * Test of get method, of class EntityStatManager.
     */
    @Test
    public void testGet() {
    }

    /**
     * Test of queryDatabase method, of class EntityStatManager.
     */
    @Test
    public void testQueryDatabase() {
    }

    /**
     * Test of saveCache method, of class EntityStatManager.
     */
    @Test
    public void testSaveCache() {
    }

    /**
     * Test of getLocalizedStatisticName method, of class EntityStatManager.
     */
    @Test
    public void testGetLocalizedStatisticName() {
    }

    /**
     * Test of formatStat method, of class EntityStatManager.
     */
    @Test
    public void testFormatStat() {
    }

    /**
     * Test of flush method, of class EntityStatManager.
     */
    @Test
    public void testFlush() {
    }
    
}
