/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public interface DbPlatform {

    public Logger getLogger();

    public File getDataFolder();

    public void mysqlError(SQLException sqlException, String string);

    public InputStream getResource(String string);

    public void saveConfig();

    public boolean configValueIsSet(String key);

    public void configValueSet(String key, Object val);

    public void loadEvent(EntityStatBlob esb);
    
}
