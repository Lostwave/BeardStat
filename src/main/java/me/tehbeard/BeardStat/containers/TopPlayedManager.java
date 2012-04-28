package me.tehbeard.BeardStat.containers;

import java.util.List;

import me.tehbeard.BeardStat.DataProviders.IStatDataProvider;

public class TopPlayedManager {
	private IStatDataProvider backendDatabase = null;

	public TopPlayedManager(IStatDataProvider backendDatabase){
		this.backendDatabase = backendDatabase;
	}
	
	/**
	 * Retrieve a players Stat Blob, or create one if it doesn't exist
	 * @param name
	 * @return
	 */
	public List<TopPlayed> getTopPlayed(){
		if(backendDatabase == null){return null;}
		
		List<TopPlayed> output = backendDatabase.pullTopPlayed();
		
		return output;
	}
	
}
