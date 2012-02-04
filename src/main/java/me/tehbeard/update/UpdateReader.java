package me.tehbeard.update;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class UpdateReader {

	private final Logger log = Logger.getLogger("Minecraft");
	private String url = "http://tehbeard.github.com/update.yml";
	private Plugin plugin;
	private VersionChecker checker;
	public UpdateReader(String url,Plugin plugin,VersionChecker checker){
		this.url = url;
		this.plugin = plugin;
		this.checker = checker;
	}

	public boolean checkUpdate(){
		try {
			//load config
			YamlConfiguration config = YamlConfiguration.loadConfiguration((new URL(url)).openStream());
			log.info("Parsing update file");
			
			String updateSiteName = config.getString("name","[]");
			log.info("Reading updates from " + updateSiteName);
			
			ConfigurationSection updatePage = config.getConfigurationSection("plugin." + plugin.getDescription().getName());
			if(updatePage == null){
				log.info("Update file does not have information on our plugin");
				return false;
			}
			String newVersion = updatePage.getString("version", plugin.getDescription().getVersion());
			checker.checkVersion(plugin.getDescription().getVersion(), newVersion);
		} catch (MalformedURLException e) {
			log.severe("Update URL specified could not be understood");
		} catch (IOException e) {
			log.severe("An I/O error occured while trying to read the update");
		}
		return false;
	}
	
	public abstract class VersionChecker{
		/**
		 * compares two version strings
		 * @param ourVersion version string for the plugin
		 * @param otherVersion version string read from update site
		 * @return integer determing value of comparison
		 *  -2 : plugin is ahead of update (major version)
		 *  -1 : plugin is ahead of update (minor version)
		 *   0 : plugin and site are equal
		 *   1 : plugin is behind update (minor version)
		 *   2 : plugin is behind update (major version)
		 */
		public abstract int checkVersion(String ourVersion,String otherVersion);
	}
}
