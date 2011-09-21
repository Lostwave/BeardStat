package me.tehbeard.BeardStat.listeners;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.StatCollectors.*;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;

public class StatEntityListener extends EntityListener{


	@Override
	public void onEntityDamage(EntityDamageEvent event) {

		if(event.isCancelled()==false){

			Entity attacker = null;
			Block block = null;
			if(event instanceof EntityDamageByEntityEvent){
				attacker = ((EntityDamageByEntityEvent)event).getDamager();
			}
			if(event instanceof EntityDamageByBlockEvent){
				block  = ((EntityDamageByBlockEvent)event).getDamager();
			}
			if(event instanceof EntityDamageByProjectileEvent){
				attacker  = ((EntityDamageByProjectileEvent)event).getDamager();
			}

			for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
				((EntityStatCollector)sc).onEntityDamage(event.getEntity(),event.getCause(),event.getDamage(),attacker,block);
			}
		}
	}


	@Override
	public void onEntityDeath(EntityDeathEvent event) {

		BeardStat.printDebugCon("entity killed: "+event.getEntity().getClass().getSimpleName());
		for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
			((EntityStatCollector)sc).onEntityDeath(event.getEntity(),event.getDrops());

		}

	}

	@Override
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.isCancelled()==false && event.getEntity() instanceof Player){
			for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
				((EntityStatCollector)sc).onEntityHeal((Player)event.getEntity(),event.getRegainReason(),event.getAmount());
			}
		}
	}

	@Override
	public void onEntityTame(EntityTameEvent event) {
		if(event.isCancelled()==false && event.getOwner() instanceof Player){
			for( IStatCollector sc : StatCollectorManager.getCollectors(event.getType())){
				((EntityStatCollector)sc).onEntityTame(event.getEntity(),event.getOwner());
			}
		}
	}
}
