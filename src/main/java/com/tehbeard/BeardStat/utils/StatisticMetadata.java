package com.tehbeard.BeardStat.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tehbeard.BeardStat.commands.formatters.StatFormatter;

public class StatisticMetadata {

    private static Map<String, StatisticMetadata> meta       = new HashMap<String, StatisticMetadata>();

    private static Map<Formatting, StatFormatter> formatters = new HashMap<StatisticMetadata.Formatting, StatFormatter>();

    static {
        formatters.put(Formatting.time, new StatFormatter() {

            @Override
            public String format(int value) {
                long seconds = value;
                int weeks = (int) seconds / 604800;
                int days = (int) Math.ceil((seconds - (604800 * weeks)) / 86400);
                int hours = (int) Math.ceil((seconds - ((86400 * days) + (604800 * weeks))) / 3600);
                int minutes = (int) Math.ceil((seconds - ((604800 * weeks) + (86400 * days) + (3600 * hours))) / 60);

                return LanguagePack.getMsg("format.time", weeks, days, hours, minutes);
            }
        });
        formatters.put(Formatting.timestamp, new StatFormatter() {

            @Override
            public String format(int value) {
                return (new Date(value)).toString();
            }

        });
        formatters.put(Formatting.none, new StatFormatter() {

            @Override
            public String format(int value) {

                return "" + value;
            }
        });
    }

    public enum Formatting {
        none, time, timestamp
    }

    private int        id;
    private String     name;
    private String     localizedName;
    private Formatting format;
    private String     outputStr = "%s"; // TODO - Add support for

    public StatisticMetadata(int id, String name, String localizedName, Formatting format) {
        this.id = id;
        this.name = name;
        this.localizedName = localizedName;
        this.format = format;
        meta.put(name, this);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return this.localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Formatting getFormat() {
        return this.format;
    }

    public void setFormat(Formatting format) {
        this.format = format;
    }

    public String formatStat(int value) {
        return String.format(this.outputStr, formatters.get(this.format).format(value));
        // Wrap output of formatter with outputStr, to allow for things like x
        // metres
    }

    public static StatisticMetadata getMeta(String name) {
        return meta.get(name);
    }

    public static String formatStat(String name, int value) {
        return getMeta(name) == null ? "" + value : getMeta(name).formatStat(value);
    }

    public static String localizedName(String name) {
        String genMetaName = HumanReadbleOutputGenerator.getNameOf(name);
        StatisticMetadata nameMeta = getMeta(name);
        // Cascade, go with cached meta name first, then generated meta name,
        // then just return if generated meta failed.
        return nameMeta != null ? getMeta(name).getLocalizedName() : (genMetaName != null ? genMetaName : name);
    }
}
