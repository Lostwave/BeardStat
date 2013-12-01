package com.tehbeard.beardstat.listeners;

import net.dragonzone.promise.Promise;

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

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.listeners.defer.DelegateIncrement;
import com.tehbeard.beardstat.utils.MetaDataCapture;

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
        String world = attacked.getWorld().getName();
        DamageCause cause = event.getCause();
        int amount = forceOne ? 1 : (int) Math.floor(event.getDamage());
        Entity attacker = null;
        Projectile projectile = null;
        // grab the attacker if one exists.
        if (event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent) event).getDamager();
        }
        // Projectile -> projectile + attacker
        if (attacker instanceof Projectile) {
            projectile = (Projectile) attacker;
            attacker = projectile.getShooter();
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

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(player.getName());

        // Total damage
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, category[idx], "total", amount));

        // Damage cause
        if (cause != DamageCause.PROJECTILE) {
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, category[idx], cause
                    .toString().toLowerCase().replace("_", ""), amount));
        }
        // Entity damage
        if ((other != null) && !(other instanceof Player)) {
            MetaDataCapture.saveMetaDataEntityStat(promiseblob, BeardStat.DEFAULT_DOMAIN, world, category[idx], other,
                    amount);
        }
        // Projectile damage
        if (projectile != null) {
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, category[idx], projectile
                    .getType().toString().toLowerCase().replace("_", ""), amount));
        }

        if ((attacker instanceof Player) && (attacked instanceof Player)) {
            Promise<EntityStatBlob> attackerBlob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                    ((Player) attacker).getName());
            Promise<EntityStatBlob> attackedBlob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                    ((Player) attacked).getName());

            attackerBlob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, category[0], "pvp", 1));
            attackedBlob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, category[1], "pvp", 1));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

        if ((event.isCancelled() == false) && (event.getEntity() instanceof Player)
                && !isBlacklistedWorld(event.getEntity().getWorld())) {

            String world = event.getEntity().getWorld().getName();
            int amount = (int) Math.floor(event.getAmount());
            RegainReason reason = event.getRegainReason();
            Player player = (Player) event.getEntity();

            if (!shouldTrackPlayer(player)) {
                return;
            }

            Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(player.getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "stats", "damagehealed",
                    amount));
            if (reason != RegainReason.CUSTOM) {
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "stats", "heal"
                        + reason.toString().replace("_", "").toLowerCase(), amount));
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

            String world = event.getEntity().getWorld().getName();
            Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                    event.getOwner().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "stats", "tame"
                    + event.getEntity().getType().toString().toLowerCase().replace("_", ""), 1));
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

                String world = event.getEntity().getWorld().getName();

                Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(p.getName());
                promiseblob
                        .onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "potions", "splashhit", 1));
                // added per potion details
                for (PotionEffect potionEffect : potion.getEffects()) {
                    MetaDataCapture.saveMetadataPotionStat(promiseblob, BeardStat.DEFAULT_DOMAIN, world, "potions", potionEffect, 1);
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
            Player p = (Player) event.getEntity();

            if (!shouldTrackPlayer(p)) {
                return;
            }

            String world = event.getEntity().getWorld().getName();

            Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(p.getName());
            // total shots fired
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "bow", "shots", 1));

            if (event.getBow().containsEnchantment(Enchantment.ARROW_FIRE)) {
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "bow", "fireshots", 1));
            }

            if (event.getBow().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                promiseblob
                        .onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN, world, "bow", "infiniteshots", 1));
            }

        }

    }
}
