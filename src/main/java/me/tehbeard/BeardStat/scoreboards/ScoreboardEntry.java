package me.tehbeard.BeardStat.scoreboards;

import org.bukkit.configuration.Configuration;

/**
 * Entry in a scoreboard
 * @author James
 *
 */
public class ScoreboardEntry {

    public int getRank() {
        return rank;
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public Configuration getCustomData() {
        return custom;
    }

    private int id;
    private int rank;
    private String player;
    private int score;
    private Configuration custom;

    public ScoreboardEntry(String player, int score) {
        this(0,player,score,null);
    }
    
    public ScoreboardEntry(int id,String player, int score, Configuration custom) {
        this.id = id;
        this.player = player;
        this.score = score;
        this.custom = custom;
    }
    
    protected void setId(int id){
        this.id = id;
    }

    
}
