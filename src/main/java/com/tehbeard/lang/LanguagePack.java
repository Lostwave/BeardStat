package com.tehbeard.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class LanguagePack {
	
	private static Properties db;
	
	public static void load(InputStream in) throws IOException{
		db = new Properties();
		db.load(in);
	}
	
	public static String getMsg(String code){
		return db.getProperty(code);
	}
	
	public static String getMsg(String code,Object... args){
		return colorFormat(String.format(getMsg(code),args));
	}
	
	
	private static String colorFormat(String s){
		
		return s.replaceAll("\\[([0-9A-FK-OR])\\]", ChatColor.COLOR_CHAR + "$1");
		//return s.replaceAll("[A]", ChatColor.COLOR_CHAR + "$0");
		
	}
	
	public static void main(String[] main){
		System.out.println(colorFormat("[A]hello"));
	}

}
