package com.tehbeard.BeardStat.commands.formatters;

/**
 * Uses String.Format to format a stats
 * @author James
 *
 */
public class StaticStatFormatter implements StatFormatter {

    String format = "%s";

    public StaticStatFormatter(String format) {
        this.format = format;
    }

    @Override
    public String format(int value) {
        return String.format(this.format, value);
    }

}