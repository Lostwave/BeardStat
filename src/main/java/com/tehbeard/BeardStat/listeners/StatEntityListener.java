package com.tehbeard.BeardStat.listeners;

import java.util.List;

import net.dragonzone.promise.Promise;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.*;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.PlayerStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

public class StatEntityListener implements Listener{


    List<String> worlds;
    private PlayerStatManager playerStatManager;

    private final String[] DAMAGELBLS = {"damagedealt","damagetaken"};
    private final String[] KDLBLS = {"kills","deaths"};

    public StatEntityListener(List<String> worlds,PlayerStatManager playerStatManager){
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {

        if(event.isCancelled()==false && !worlds.contains(event.getEntity().getWorld().getName())){

            processEntityDamage(event, DAMAGELBLS,false);

        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        //ignore the blacklisted worlds
        if(worlds.contains(event.getEntity().getWorld().getName())){
            return;
        }

        EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
        if(lastCause != null){
            processEntityDamage(lastCause, KDLBLS,true);
        }

    }

    /**
     * ATTACKER NAME, ATTACKED NAME
     * @param event
     * @param category
     */
    private void processEntityDamage(EntityDamageEvent event,String[] category,boolean forceOne){
        //Initialise base stats
        Entity attacked = event.getEntity();
        DamageCause cause = event.getCause();
        int amount = forceOne ? 1 : event.getDamage();
        Entity attacker = null;
        Projectile projectile = null;
        //grab the attacker if one exists.
        if(event instanceof EntityDamageByEntityEvent){
            attacker = ((EntityDamageByEntityEvent) event).getDamager();
        }
        //Projectile -> projectile + attacker
        if(attacker instanceof Projectile){
            projectile = (Projectile)attacker;
            attacker = projectile.getShooter();
        }

        //dragon fixer
        if(attacker instanceof ComplexEntityPart){
            attacker = ((ComplexEntityPart)attacker).getParent();
        }
        if(attacked instanceof ComplexEntityPart){
            attacked = ((ComplexEntityPart)attacked).getParent();
        }

        //get the player
        Player player = attacked instanceof Player ? (Player)attacked : attacker instanceof Player ? (Player)attacker : null;
        Entity other = attacked instanceof Player ? attacker : attacker instanceof Player ? attacked : null;
        int idx = attacker instanceof Player ? 0 : 1;

        if(player == null){return;}//kill if no player involved
        
        if(player.getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}

        Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(player.getName());

        //Total damage
        promiseblob.onResolve(new DelegateIncrement(category[idx], "total",amount));


        //Damage cause
        if(cause != DamageCause.PROJECTILE){
            promiseblob.onResolve(new DelegateIncrement(category[idx], cause.toString().toLowerCase().replace("_",""),amount));
        }
        //Entity damage
        if(other !=null && !(other instanceof Player)){
            MetaDataCapture.saveMetaDataEntityStat(promiseblob, category[idx], other, amount);
        }
        //Projectile damage
        if(projectile!=null){
            promiseblob.onResolve(new DelegateIncrement(category[idx], projectile.getType().toString().toLowerCase().replace("_",""),amount));
        }

        //TODO: pvp Damage
        if(attacker instanceof Player  && attacked instanceof Player){
            Promise<PlayerStatBlob> attackerBlob = playerStatManager.getPlayerBlobASync(((Player)attacker).getName());
            Promise<PlayerStatBlob> attackedBlob = playerStatManager.getPlayerBlobASync(((Player)attacked).getName());

            attackerBlob.onResolve(new DelegateIncrement(category[0],"pvp",1));
            attackedBlob.onResolve(new DelegateIncrement(category[1],"pvp",1));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)

    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

        if(event.isCancelled()==false && event.getEntity() instanceof Player && !worlds.contains(event.getEntity().getWorld().getName())){
            int amount = event.getAmount();
            RegainReason reason = event.getRegainReason();
            
            if(((Player)event.getEntity()).getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
        		return;
        	}
            
            Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(((Player)event.getEntity()).getName());
            promiseblob.onResolve(new DelegateIncrement("stats","damagehealed",amount));
            if(reason != RegainReason.CUSTOM){
                promiseblob.onResolve(new DelegateIncrement("stats","heal" + reason.toString().replace("_", "").toLowerCase(),amount));	
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        if(event.isCancelled()==false && event.getOwner() instanceof Player && !worlds.contains(event.getEntity().getWorld().getName())){
        	if(((Player)event.getOwner()).getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
        		return;
        	}
            Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getOwner().getName());
            promiseblob.onResolve(new DelegateIncrement("stats","tame"+event.getEntity().getType().toString().toLowerCase().replace("_", ""),1));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event){
        if(event.isCancelled()==false && !worlds.contains(event.getPotion().getWorld().getName())){
            ThrownPotion potion = event.getPotion();

            for(Entity e :event.getAffectedEntities()){
                if(e instanceof Player){
                    Player p = (Player) e;
                    
                    if(p.getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
                		return;
                	}
                    Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(p.getName());
                    promiseblob.onResolve(new DelegateIncrement("potions","splashhit",1));
                    //added per potion details
                    for(PotionEffect potionEffect : potion.getEffects()){
                        String effect = potionEffect.getType().toString().toLowerCase().replaceAll("_", "");
                        promiseblob.onResolve(new DelegateIncrement("potions","splash" + effect,1));
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBowShoot(EntityShootBowEvent event){

        if(event.isCancelled()==false && !worlds.contains(event.getEntity().getWorld().getName())){
            if(event.getEntity() instanceof Player){
                Player p = (Player) event.getEntity();
                
                if(p.getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
            		return;
            	}

                Promise<PlayerStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(p.getName());
                //total shots fired
                promiseblob.onResolve(new DelegateIncrement("bow","shots",1));

                if(event.getBow().containsEnchantment(Enchantment.ARROW_FIRE)){
                    promiseblob.onResolve(new DelegateIncrement("bow","fireshots",1));
                }

                if(event.getBow().containsEnchantment(Enchantment.ARROW_INFINITE)){
                    promiseblob.onResolve(new DelegateIncrement("bow","infiniteshots",1));
                }

            }
        }
    }
}
