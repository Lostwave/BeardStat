package me.tehbeard.BeardStat.containers;

import java.sql.Date;

public class TopPlayed {

	public TopPlayed(int rank, String name, String timeString, Date firstOn, Date lastOn){
		this.playername = name;
		this.rank = rank;
		this.time = timeString;
		this.firstOn = firstOn;
		this.lastOn = lastOn;
	}
	
	public String playername;
	public int rank;
	public String time;
	public Date firstOn;
	public Date lastOn;
}
