/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.BeforeClass;

import com.tehbeard.beardstat.DatabaseConfiguration;


/**
 * TODO - FIX THIS.
 * @author James
 */

public class TestSQLiteDataProvider extends IStatDataProviderTest  {
     
    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        DatabaseConfiguration config = new DatabaseConfiguration(7);
        config.version = config.latestVersion;
        config.backups = false;
        
        instance = new SQLiteStatDataProvider(new TestPlatform(), ":memory:", config);
        String preloadStmt = ((SQLiteStatDataProvider)instance).readSQL("sqlite","preload",config.tablePrefix);
        for(String s : preloadStmt.split("\\;")){
            try{
           ((SQLiteStatDataProvider)instance).conn.createStatement().execute(s);
            }catch(SQLException e ){
                System.out.println(s);
                e.printStackTrace();
                
                throw e;
            }
        }
        ((JDBCStatDataProvider)instance).cacheComponents();
    }

}
