/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.DatabaseConfiguration;
import junit.framework.Test;

/**
 *
 * @author James
 */

public class TestSQLiteDataProvider extends IStatDataProviderTest{
    public TestSQLiteDataProvider(){
        DatabaseConfiguration config = new DatabaseConfiguration(7);
    }
}
