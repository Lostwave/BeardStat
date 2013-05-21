package com.tehbeard.BeardStat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.bukkit.ChatColor;

public class LanguagePack {

    private static Properties db;

    public static void load(InputStream in) throws IOException {
        db = new Properties();
        db.load(in);
    }

    public static void overlay(InputStream in) throws IOException {
        Properties p = new Properties();
        p.load(in);
        for (Entry<Object, Object> kv : p.entrySet()) {
            db.setProperty(kv.getKey().toString(), kv.getValue().toString());
        }
    }

    private static String _getMsg(String code) {
        return db.getProperty(code);
    }

    public static String getMsg(String code) {
        return colorFormat(_getMsg(code));
    }

    public static String getMsg(String code, Object... args) {
        return colorFormat(String.format(_getMsg(code), args));
    }

    private static String colorFormat(String s) {

        return s.replaceAll("\\[([0-9A-FK-OR])\\]", ChatColor.COLOR_CHAR + "$1");
        // return s.replaceAll("[A]", ChatColor.COLOR_CHAR + "$0");

    }

    public static void main(String[] main) {
        String s = String.format("[A]hello%1s", " foobar");
        System.out.println(colorFormat(s));
    }

}
