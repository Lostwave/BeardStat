package me.tehbeard.BeardStat.listeners;

import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.block.Block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class StatEntityListener implements Listener{

	
	List<String> worlds;
	

	public StatEntityListener(List<String> worlds){
		this.worlds = worlds;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {

		if(event.isCancelled()==false && !worlds.contains(event.getEntity().getWorld().getName())){

			Entity attacker = null;
			Block block = null;
			if(event instanceof EntityDamageByEntityEvent){
				attacker = ((EntityDamageByEntityEvent)event).getDamager();
			}
			if(event instanceof EntityDamageByBlockEvent){
				block  = ((EntityDamageByBlockEvent)event).getDamager();
			}
			Entity entity = event.getEntity();
			int damage = event.getDamage();
			DamageCause cause = event.getCause();
				//if the player gets attacked
				if(entity instanceof Player){
					//global damage count
					PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("damagetaken","total").incrementStat(damage);
					PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("damagetaken", cause.toString().toLowerCase().replace("_","")).incrementStat(damage);
					//pvp damage
					if(attacker instanceof Player){
						PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("damagetaken","player").incrementStat(damage);
						PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("damagedealt","player").incrementStat(damage);
						//mob damage
					} else if(attacker!=null){				
						PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("damagetaken",attacker.getClass().getSimpleName().toLowerCase().replace("craft", "")).incrementStat(damage);
					}


				}else{
					if(attacker instanceof Player){
						//global damage dealt
						PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("damagedealt","total").incrementStat(damage);
						PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("damagedealt",cause.toString().toLowerCase().replace("_","")).incrementStat(damage);
						PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("damagedealt",entity.getClass().getSimpleName().toLowerCase().replace("craft","")).incrementStat(damage);
					}				
				}

		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {

		EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
		DamageCause cause = null;
		if(lastCause!=null){
		cause = lastCause.getCause();
		}

		Entity attacker = null;
		Block block = null;

		if(lastCause instanceof EntityDamageByEntityEvent){
			attacker = ((EntityDamageByEntityEvent)lastCause).getDamager();
			BeardStat.printDebugCon("attack ID'd Fired");//Type.ENTITY_DEATH
		}
		if(lastCause instanceof EntityDamageByBlockEvent){
			BeardStat.printDebugCon("block ID'd Fired");//Type.ENTITY_DEATH
			block = ((EntityDamageByBlockEvent)lastCause).getDamager();
		}
		Entity entity = event.getEntity();
		
		//set attacker and entity total k/d accordingly
		if(entity instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths","total").incrementStat(1);
			if(cause!=null){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths",cause.toString().toLowerCase().replace("_","")).incrementStat(1);
			}
		}
		if(attacker instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills","total").incrementStat(1);
			if(cause!=null){
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills",cause.toString().toLowerCase().replace("_","")).incrementStat(1);
			}

		}

		//PVP
		if(entity instanceof Player && attacker instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths","player").incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills","Player").incrementStat(1);
		}
		//global damage count

		//PLAYER KILLS ENTITY

		if((entity instanceof Player)==false && attacker instanceof Player){
			//global damage dealt
			//PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kill_by_"+ cause.toString().toLowerCase()).incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills", entity.getClass().getSimpleName().replace("Craft", "")).incrementStat(1);
		}				
		//ENTITY KILLS PLAYER
		if((entity instanceof Player) && !(attacker instanceof Player) && attacker !=null){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths",attacker.getClass().getSimpleName().replace("Craft", "")).incrementStat(1);
			BeardStat.printDebugCon("Death by entity for player logged");
			BeardStat.printDebugCon("" + attacker.getClass().getSimpleName().replace("Craft", ""));


		}


		 
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		
		if(event.isCancelled()==false && event.getEntity() instanceof Player && !worlds.contains(event.getEntity().getWorld().getName())){
			int amount = event.getAmount();
			RegainReason reason = event.getRegainReason();
			PlayerStatManager.getPlayerBlob(((Player)event.getEntity()).getName()).getStat("stats","damagehealed").incrementStat(amount);
			if(reason != RegainReason.CUSTOM){
				PlayerStatManager.getPlayerBlob(((Player)event.getEntity()).getName()).getStat("stats","heal" + reason.toString().replace("_", "").toLowerCase()).incrementStat(amount);	
			}
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityTame(EntityTameEvent event) {
		if(event.isCancelled()==false && event.getOwner() instanceof Player && !worlds.contains(event.getEntity().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(((Player)event.getOwner()).getName()).getStat("stats","tame"+event.getEntity().getClass().getSimpleName().replace("craft", "")).incrementStat(1);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPotionSplash(PotionSplashEvent event){
		ThrownPotion potion = event.getPotion();
		System.out.println("Potion class: " + potion.getClass().toString());
		
		for(Entity e :event.getAffectedEntities()){
			if(e instanceof Player){
				Player p = (Player) e;
			PlayerStatManager.getPlayerBlob(p.getName()).getStat("potions","splashhit").incrementStat(1);
			}
		}
	}
}
