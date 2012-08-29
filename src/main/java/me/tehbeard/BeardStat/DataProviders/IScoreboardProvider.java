package me.tehbeard.BeardStat.DataProviders;

import java.util.List;

import me.tehbeard.BeardStat.scoreboards.Scoreboard;
import me.tehbeard.BeardStat.scoreboards.ScoreboardEntry;

public interface IScoreboardProvider {
    
    public List<Scoreboard> getScoreboards();
    
    public void registerScoreboard(Scoreboard scoreboard);
    
    public void addScore(Scoreboard scoreboard,ScoreboardEntry entry);
}
