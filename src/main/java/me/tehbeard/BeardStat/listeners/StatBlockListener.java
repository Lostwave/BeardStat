package me.tehbeard.BeardStat.listeners;

import java.util.List;

import me.tehbeard.BeardStat.StatCollectors.IStatCollector;
import me.tehbeard.BeardStat.StatCollectors.BlockStatCollector;
import me.tehbeard.BeardStat.StatCollectors.StatCollectorManager;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


public class StatBlockListener extends BlockListener{

	List<String> worlds;
	

	public StatBlockListener(List<String> worlds){
		this.worlds = worlds;
	}
	
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
				((BlockStatCollector)sc).onBlockPlace(event.getPlayer(),event.getBlock(),event.getBlockReplacedState());
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
				((BlockStatCollector)sc).onBlockBreak(event.getPlayer(),event.getBlock());
			}
		}
	}
	
	
}
