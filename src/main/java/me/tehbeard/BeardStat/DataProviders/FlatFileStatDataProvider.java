package me.tehbeard.BeardStat.DataProviders;

import java.io.File;


import org.bukkit.configuration.file.YamlConfiguration;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;


/**
 * YAML data storage provider for stats
 * @author James
 *
 */
public class FlatFileStatDataProvider extends IStatDataProvider {

	YamlConfiguration database;

	

	public FlatFileStatDataProvider(File file) {
		database = YamlConfiguration.loadConfiguration(file);
		BeardStat.printCon("Creating FlatFile DataProvider");
		

	}


	public PlayerStatBlob pullPlayerStatBlob(String player) {
		return pullPlayerStatBlob(player,true);
	}

	public PlayerStatBlob pullPlayerStatBlob(String player,boolean create) {
		// TODO Auto-generated method stub
		/*BeardStat.printCon("Loading stats for player " + player);

		try{
			ConfigurationNode pl = database.get("stats.players." + player);

			PlayerStatBlob blob = new PlayerStatBlob(player,0);
			if(pl!=null){
				for(String key : pl.getKeys()){
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
		}*/
		return null;
	}


	
	public void pushPlayerStatBlob(PlayerStatBlob player) {
		// TODO Auto-generated method stub
		/*HashMap<String,Integer> nodes = new HashMap<String, Integer>();

		for(PlayerStat stat : player.getStats()){

			nodes.put(stat.getCat() + "-" + stat.getName(),stat.getValue());

		}
		database.setProperty("stats.players." + player.getName(), nodes);
		//TODO: Flush to I/O every X Seconds?
		database.save();*/
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		//database.save();
	}

}
