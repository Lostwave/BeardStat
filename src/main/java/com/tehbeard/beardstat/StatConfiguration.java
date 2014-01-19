/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat;

import com.tehbeard.utils.syringe.configInjector.InjectConfig;

/**
 *
 * @author James
 */
public class StatConfiguration {
    
    @InjectConfig("general.debug")
    public boolean debugMode;
    
    @InjectConfig("general.verbose")
    public boolean verboseMode;
    
    @InjectConfig("stats.configversion")
    public int configVersion;
    
    @InjectConfig("stats.database.type")
    public String dbType;
    
    @InjectConfig("general.loglevel")
    public String logLevel = "INFO";

    @Override
    public String toString() {
        return "StatConfiguration{" + "debugMode=" + debugMode + ", verboseMode=" + verboseMode + ", configVersion=" + configVersion + ", dbType=" + dbType + '}';
    }
    
    
}
