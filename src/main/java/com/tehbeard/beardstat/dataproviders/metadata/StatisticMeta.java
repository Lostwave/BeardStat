package com.tehbeard.beardstat.dataproviders.metadata;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tehbeard.beardstat.commands.formatters.StatFormatter;
import com.tehbeard.beardstat.LanguagePack;

public class StatisticMeta {

    private static Map<Formatting, StatFormatter> formatters = new HashMap<StatisticMeta.Formatting, StatFormatter>();

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
                return (new Date(value*1000L)).toString();
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
    private String     gameTag;
    private String     localizedName;
    private Formatting format;
    private String     outputStr = "%s";

    public StatisticMeta(int id, String gameTag, String localizedName, Formatting format) {
        this.id = id;
        this.gameTag = gameTag;
        this.localizedName = localizedName;
        this.format = format;
    }

    public int getDbId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.gameTag;
    }

    public void setName(String name) {
        this.gameTag = name;
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

}
