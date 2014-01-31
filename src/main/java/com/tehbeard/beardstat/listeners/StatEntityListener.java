package com.tehbeard.beardstat.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.utils.StatUtils;

public class StatEntityListener extends StatListener {

    public StatEntityListener( EntityStatManager playerStatManager, BeardStat plugin) {
        super(playerStatManager, plugin);
    }

    private final String[] DAMAGELBLS = { "damagedealt", "damagetaken" };
    private final String[] KDLBLS     = { "kills", "deaths" };

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.isCancelled() || isBlacklistedWorld(event.getEntity().getWorld())) {
            return;
        }

        processEntityDamage(event, this.DAMAGELBLS, false);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        if (isBlacklistedWorld(event.getEntity().getWorld())) {
            return;
        }

        EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
        if (lastCause != null) {
            processEntityDamage(lastCause, this.KDLBLS, true);
        }

    }

    /**
     * ATTACKER NAME, ATTACKED NAME
     * 
     * @param event
     * @param category
     */
    private void processEntityDamage(EntityDamageEvent event, String[] category, boolean forceOne) {
        // Initialise base stats
        Entity attacked = event.getEntity();
        DamageCause cause = event.getCause();
        int amount = forceOne ? 1 : (int) Math.floor(event.getDamage());
        Entity attacker = null;
        Projectile projectile = null;

        boolean dispenserFired = false;
        // grab the attacker if one exists.
        if (event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent) event).getDamager();
        }
        // Projectile -> projectile + attacker
        if (attacker instanceof Projectile) {
            projectile = (Projectile) attacker;
            ProjectileSource projectileSource = projectile.getShooter();
            if(projectileSource instanceof Entity){
                attacker = (Entity)projectileSource;
            }
            if(projectileSource instanceof BlockProjectileSource){
                dispenserFired = true;
            }
        }

        // dragon fixer
        if (attacker instanceof ComplexEntityPart) {
            attacker = ((ComplexEntityPart) attacker).getParent();
        }
        if (attacked instanceof ComplexEntityPart) {
            attacked = ((ComplexEntityPart) attacked).getParent();
        }

        // get the player
        Player player = attacked instanceof Player ? (Player) attacked : attacker instanceof Player ? (Player) attacker
                : null;
        Entity other = attacked instanceof Player ? attacker : attacker instanceof Player ? attacked : null;
        int idx = attacker instanceof Player ? 0 : 1;

        if (player == null) {
            return;
        }// kill if no player involved

        if (event.isCancelled() || !shouldTrackPlayer(player)) {
            return;
        }

        // Total damage
        StatUtils.instance.modifyStatPlayer(player, category[idx], "total", amount);


        // Damage cause if not from 
        if (cause != DamageCause.PROJECTILE) {
            StatUtils.instance.modifyStatPlayer(player, category[idx], cause.toString().toLowerCase().replace("_", ""), amount);
        }
        // Entity damage
        if ((other != null) && !(other instanceof Player)) {
            StatUtils.instance.modifyStatEntity(player, category[idx], other, amount);
        }
        // Projectile damage
        if (projectile != null) {
            StatUtils.instance.modifyStatEntity(player, category[idx], projectile, amount);
        }
        // Dispenser
        if(dispenserFired){
            StatUtils.instance.modifyStatPlayer(player, category[idx], "dispenser", amount);
        }

        //PvP
        if ((attacker instanceof Player) && (attacked instanceof Player)) {
            StatUtils.instance.modifyStatPlayer((Player)attacker, category[0], "pvp", 1);
            StatUtils.instance.modifyStatPlayer((Player)attacked, category[1], "pvp", 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

        if ((event.isCancelled() == false) && (event.getEntity() instanceof Player)
                && !isBlacklistedWorld(event.getEntity().getWorld())) {

            int amount = (int) Math.floor(event.getAmount());
            RegainReason reason = event.getRegainReason();
            Player player = (Player) event.getEntity();

            if (!shouldTrackPlayer(player)) {
                return;
            }

            StatUtils.instance.modifyStatPlayer(player, "stats", "damagehealed", amount);
            if (reason != RegainReason.CUSTOM) {
                StatUtils.instance.modifyStatPlayer(player, "stats", "heal" + reason.toString().replace("_", "").toLowerCase(), amount);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        if ((event.isCancelled() == false) && (event.getOwner() instanceof Player)
                && !isBlacklistedWorld(event.getEntity().getWorld())) {
            if (event.isCancelled() || !shouldTrackPlayer((Player) event.getOwner())) {
                return;
            }

            StatUtils.instance.modifyStatEntity((Player)event.getOwner(), "tame", event.getEntity(), 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {

        if (event.isCancelled() || isBlacklistedWorld(event.getPotion().getWorld())) {
            return;
        }

        ThrownPotion potion = event.getPotion();

        for (Entity e : event.getAffectedEntities()) {
            if (e instanceof Player) {
                Player p = (Player) e;

                if (!shouldTrackPlayer(p)) {
                    continue;
                }

                StatUtils.instance.modifyStatPlayer(p, "potions", "splashhit", 1);
                // added per potion details
                for (PotionEffect potionEffect : potion.getEffects()) {
                    StatUtils.instance.modifyStatPotion(p, "potions",potionEffect, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBowShoot(EntityShootBowEvent event) {

        if ((event.isCancelled() || isBlacklistedWorld(event.getEntity().getWorld()))) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (!shouldTrackPlayer(player)) {
                return;
            }

            StatUtils.instance.modifyStatPlayer(player, "bow", "shots", 1);

            if (event.getBow().containsEnchantment(Enchantment.ARROW_FIRE)) {
                StatUtils.instance.modifyStatPlayer(player, "bow", "fireshots", 1);
            }

            if (event.getBow().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                StatUtils.instance.modifyStatPlayer(player, "bow", "infiniteshots", 1);
            }

        }

    }
}
