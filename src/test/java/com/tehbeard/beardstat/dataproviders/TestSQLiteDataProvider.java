/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.DatabaseConfiguration;
import static com.tehbeard.beardstat.dataproviders.IStatDataProviderTest.instance;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.Assume;
import org.junit.BeforeClass;

/**
 *
 * @author James
 */

public class TestSQLiteDataProvider extends IStatDataProviderTest  {
     
    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        DatabaseConfiguration config = new DatabaseConfiguration(7);
        config.version = config.latestVersion;
        config.backups = false;
        
        //System.out.println(config);
        instance = new SQLiteStatDataProvider(new TestPlatform(), ":memory:", config);
       
        String preloadStmt = ((SQLiteStatDataProvider)instance).readSQL("sqlite","preload",config.tablePrefix);
        for(String s : preloadStmt.split("\\;")){
            try{
           ((SQLiteStatDataProvider)instance).conn.createStatement().execute(s);
            }catch(SQLException e ){
                e.printStackTrace();
                
                throw e;
            }
        }
    }

}
