/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.DatabaseConfiguration;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author James
 */

public class TestSQLiteDataProvider extends IStatDataProviderTest  {
    
    public TestSQLiteDataProvider() throws IOException, SQLException{
        DatabaseConfiguration config = new DatabaseConfiguration(7);
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("mysql.properties"));
        new JavaPropertiesInjector(properties).inject(config);
        config.version = config.latestVersion;
        
        
        //instance = new MysqlStatDataProvider(new TestPlatform(), config);
        try{
        instance = new SQLiteStatDataProvider(new TestPlatform(), ":memory:", config);
        }catch(BeardStatRuntimeException e){
            e.getCause().printStackTrace();
        }
        
    }

}
