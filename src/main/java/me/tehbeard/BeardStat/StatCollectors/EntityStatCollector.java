package me.tehbeard.BeardStat.StatCollectors;

import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;

public class EntityStatCollector implements IStatCollector {

	public void onEntityDamage(Entity entity, DamageCause cause, int damage,Entity attacker, Block block) {

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

	public void onEntityDeath(Entity entity, List<ItemStack> drops) {
		BeardStat.printDebugCon("entity death collector Fired");//Type.ENTITY_DEATH
		// TODO Auto-generated method stub
		EntityDamageEvent event = entity.getLastDamageCause();
		DamageCause cause = null;
		if(event!=null){
		cause = event.getCause();
		}

		Entity attacker = null;
		Block block = null;

		if(event instanceof EntityDamageByEntityEvent){
			attacker = ((EntityDamageByEntityEvent)event).getDamager();
			BeardStat.printDebugCon("attack ID'd Fired");//Type.ENTITY_DEATH
		}
		if(event instanceof EntityDamageByBlockEvent){
			BeardStat.printDebugCon("block ID'd Fired");//Type.ENTITY_DEATH
			block = ((EntityDamageByBlockEvent)event).getDamager();
		}
		if(event instanceof EntityDamageByProjectileEvent){
			attacker  = ((EntityDamageByProjectileEvent)event).getDamager();
		}
		//set attacker and entity total k/d accordingly
		if(entity instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths","total").incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths",cause.toString().toLowerCase().replace("_","")).incrementStat(1);
		}
		if(attacker instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills","total").incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills",cause.toString().toLowerCase().replace("_","")).incrementStat(1);

		}

		//PVP
		if(entity instanceof Player && attacker instanceof Player){
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths","player").incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)attacker).getName()).getStat("kills","Player").incrementStat(1);
		}
		//global damage count

//TODO: FIX THIS
		/*PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths",attacker.getClass().getSimpleName().toLowerCase().replace("craft", "")).incrementStat(1);
			PlayerStatManager.getPlayerBlob(((Player)entity).getName()).getStat("deaths",block.getType().toString().toLowerCase().replace("_", "")).incrementStat(1);
		*/


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

	public void onEntityHeal(Player entity, RegainReason regainReason,
			int amount) {
		PlayerStatManager.getPlayerBlob(entity.getName()).getStat("stats","damagehealed").incrementStat(amount);

	}

	public void onEntityTame(Entity entity, AnimalTamer owner) {
		// TODO Auto-generated method stub
		PlayerStatManager.getPlayerBlob(((Player)owner).getName()).getStat("stats","tame"+entity.getClass().getSimpleName().replace("craft", "")).incrementStat(1);
	}


}
