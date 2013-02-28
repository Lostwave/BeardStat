package com.tehbeard.BeardStat.commands.formatters;

public class StaticStatFormatter implements StatFormatter{

    String format = "%s";
    public StaticStatFormatter(String format){
        this.format = format;
    }
    public String format(int value) {
        return String.format(format, value);
    }
    
}