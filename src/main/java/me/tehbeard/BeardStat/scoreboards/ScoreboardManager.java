package me.tehbeard.BeardStat.scoreboards;

import java.util.HashMap;
import java.util.Map;

import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;

public class ScoreboardManager {

    
    Map<String,Scoreboard> scoreboards;
    IStatDataProvider provider;
    
    public ScoreboardManager(IStatDataProvider provider){
        this.provider = provider;
        scoreboards = new HashMap<String, Scoreboard>();
    }
    
    public void addScoreboard(Scoreboard scoreboard){
        
    }
}
