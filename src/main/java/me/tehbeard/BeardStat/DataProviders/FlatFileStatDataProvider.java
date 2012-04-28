package me.tehbeard.BeardStat.DataProviders;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;
import me.tehbeard.BeardStat.containers.TopPlayed;


/**
 * YAML data storage provider for stats
 * @author James
 *
 */
public class FlatFileStatDataProvider extends IStatDataProvider {

	YamlConfiguration database;

	
	File file;
	public FlatFileStatDataProvider(File file) {
		database = YamlConfiguration.loadConfiguration(file);
		this.file = file;
		BeardStat.printCon("Creating FlatFile DataProvider");
		

	}


	public PlayerStatBlob pullPlayerStatBlob(String player) {
		return pullPlayerStatBlob(player,true);
	}

	public PlayerStatBlob pullPlayerStatBlob(String player,boolean create) {
		BeardStat.printDebugCon("Loading stats for player " + player);

		try{
			ConfigurationSection pl = database.getConfigurationSection("stats.players." + player);

			PlayerStatBlob blob = new PlayerStatBlob(player,0);
			if(pl!=null){
				for(String key : pl.getKeys(false)){
					BeardStat.printDebugCon("loading stat " +key);
					BeardStat.printDebugCon("parts " + key.split("\\-")[0] + " : "+
							key.split("\\-")[1]);
					blob.addStat(new PlayerStat(key.split("\\-")[0]
							,key.split("\\-")[1], pl.getInt(key, 0)));
				}
				return blob;
			}
			else if(pl==null && create)
			{
				return blob;
			}
			BeardStat.printDebugCon("FAILED TO LOAD KEY FROM DATABASE!" + player);
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<TopPlayed> pullTopPlayed(){
		// not implemented for flat file.
		return null;
	}

	
	public void pushPlayerStatBlob(PlayerStatBlob player) {
		HashMap<String,Integer> nodes = new HashMap<String, Integer>();

		for(PlayerStat stat : player.getStats()){

			nodes.put(stat.getCat() + "-" + stat.getName(),stat.getValue());

		}
		database.set("stats.players." + player.getName(), nodes);
		try {
			database.save(file);
		} catch (IOException e) {
		    BeardStat.printCon("IO error occured when trying to save player data");
		    e.printStackTrace();
		}
	}

	@Override
	public void flush() {
		//database.save();
	}

}
