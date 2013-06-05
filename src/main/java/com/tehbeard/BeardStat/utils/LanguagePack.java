package com.tehbeard.BeardStat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.bukkit.ChatColor;

/**
 * Loads language files to allow better localization
 * 
 * @author James
 * 
 */
public class LanguagePack {

    private static Properties db;

    /**
     * Load in a language file, clears all loaded definitions
     * 
     * @param in
     * @throws IOException
     */
    public static void load(InputStream in) throws IOException {
        db = new Properties();
        db.load(in);
    }

    /**
     * loads in a language file, overwrites any conflicting loaded definitions
     * 
     * @param in
     * @throws IOException
     */
    public static void overlay(InputStream in) throws IOException {
        Properties p = new Properties();
        p.load(in);
        for (Entry<Object, Object> kv : p.entrySet()) {
            db.setProperty(kv.getKey().toString(), kv.getValue().toString());
        }
    }

    /**
     * Get raw message from table
     * 
     * @param code
     * @return
     */
    private static String _getMsg(String code) {
        return db.getProperty(code);
    }

    /**
     * Returns a localised message with colour formatting
     * 
     * @param code
     * @return
     */
    public static String getMsg(String code) {
        return colorFormat(_getMsg(code));
    }

    /**
     * Returns a localised message, with colour coding and parameters passed
     * into it
     * 
     * @param code
     * @param args
     * @return
     */
    public static String getMsg(String code, Object... args) {
        return colorFormat(String.format(_getMsg(code), args));
    }

    /**
     * Colorize a message
     * 
     * @param s
     * @return
     */
    private static String colorFormat(String s) {

        return s.replaceAll("\\[([0-9A-FK-OR])\\]", ChatColor.COLOR_CHAR + "$1");
    }

}
