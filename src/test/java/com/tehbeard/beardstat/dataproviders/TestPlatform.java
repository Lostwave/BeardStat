/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class TestPlatform implements DbPlatform {

    public TestPlatform() {
    }

    public Logger getLogger() {
        return Logger.getLogger("stat");
    }

    public File getDataFolder() {
        return new File(".");
    }

    public void mysqlError(SQLException sqlException, String file) {
        throw new RuntimeException("error in file " + file,sqlException);
    }

    public InputStream getResource(String file) {
        return getClass().getClassLoader().getResourceAsStream(file);
    }

    public void saveConfig() {
    }

    public boolean configValueIsSet(String key) {
        return true;
    }

    public void configValueSet(String key, Object val) {
    }

    public void loadEvent(EntityStatBlob esb) {
        
    }

    @Override
    public boolean isPlayerOnline(String player) {
        return false;
    }

    @Override
    public String getWorldForPlayer(String entityName) {
        return "world";
    }

    @Override
    public void handleError(Exception e) {
        throw new RuntimeException(e);
    }
    
}
