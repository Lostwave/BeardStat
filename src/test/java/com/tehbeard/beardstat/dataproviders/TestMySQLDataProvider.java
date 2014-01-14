/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.DatabaseConfiguration;
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

public class TestMySQLDataProvider extends IStatDataProviderTest  {
    
    public TestMySQLDataProvider(){
        

    }
    
    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        InputStream is = TestMySQLDataProvider.class.getClassLoader().getResourceAsStream("mysql.properties");
        if(is == null){
            System.out.println("WARNING: MYSQL TEST NOT CONFIGURED, TEST SKIPPED.");
        }
        Assume.assumeTrue("MySQL test properties configured.", is != null);
        DatabaseConfiguration config = new DatabaseConfiguration(7);
        Properties properties = new Properties();
        properties.load(is);
        new JavaPropertiesInjector(properties).inject(config);
        config.version = config.latestVersion;
        System.out.println(config.toString());
        //System.out.println(config);
        instance = new MysqlStatDataProvider(new TestPlatform(), config);
        
        System.out.println("deleting tables.");
        String preloadStmt = ((MysqlStatDataProvider)instance).readSQL("sql","cleanup","stats");
        for(String s : preloadStmt.split("\\;")){
            ((MysqlStatDataProvider)instance).conn.createStatement().execute(s);
        }
        instance = new MysqlStatDataProvider(new TestPlatform(), config);
        
        System.out.println("Loaded driver.");
        preloadStmt = ((MysqlStatDataProvider)instance).readSQL("sql","preload",config.tablePrefix);
        for(String s : preloadStmt.split("\\;")){
            try{
           ((MysqlStatDataProvider)instance).conn.createStatement().execute(s);
            }catch(SQLException e ){
                e.printStackTrace();
                
                throw e;
            }
        }
    }
    


    
}
