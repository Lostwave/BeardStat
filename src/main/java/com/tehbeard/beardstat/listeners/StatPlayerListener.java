package com.tehbeard.beardstat.listeners;

import java.util.Collection;
import java.util.Date;

import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.listeners.defer.DelegateSet;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.manager.OnlineTimeManager;
import com.tehbeard.beardstat.manager.OnlineTimeManager.ManagerRecord;
import com.tehbeard.beardstat.utils.StatUtils;

/**
 * Calls the stat manager to trigger events
 * 
 * @author James
 * 
 */
public class StatPlayerListener extends StatListener {

    public StatPlayerListener(EntityStatManager playerStatManager, BeardStat plugin) {
        super( playerStatManager, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            StatUtils.statPlayer(event.getPlayer(), "stats", "armswing", 1);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        StatUtils.statPlayer(event.getPlayer(), "stats", "login",1);
        StatUtils.statPlayer(event.getPlayer(), "stats", "lastlogin",
                (int) (System.currentTimeMillis() / 1000L));
        
        //Special case for first join
        getPlayerStatManager().getOrCreatePlayerStatBlob(event.getPlayer().getName())
        .onResolve(new Delegate<Void, Promise<EntityStatBlob>>() {

            @Override
            public <P extends Promise<EntityStatBlob>> Void invoke(P params) {

                if (!params.getValue().hasStat(BeardStat.DEFAULT_DOMAIN, BeardStat.GLOBAL_WORLD, "stats", "firstlogin")) {
                    params.getValue().getStat(BeardStat.DEFAULT_DOMAIN, BeardStat.GLOBAL_WORLD, "stats", "firstlogin")
                    .setValue((int) (event.getPlayer().getFirstPlayed() / 1000L));

                }

                return null;
            }
        });

        OnlineTimeManager.setRecord(event.getPlayer().getName(), event.getPlayer().getWorld().getName());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if ((event.isCancelled() == false) && !isBlacklistedWorld(event.getPlayer().getWorld())) {
            int len = event.getMessage().length();

            StatUtils.statPlayer(event.getPlayer(), "stats", "chatletters", len);
            StatUtils.statPlayer(event.getPlayer(), "stats", "chat", 1);

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        
        StatUtils.statItem(event.getPlayer(), "itemdrop", event.getItemDrop().getItemStack(), event.getItemDrop().getItemStack().getAmount());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        
        //TODO : FIX FISHING. NEED 1.7 API FOR THIS :(
        StatUtils.statPlayer(event.getPlayer(), "stats", "fishcaught", 1);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled() == false) {
            StatUtils.statPlayer(event.getPlayer(), "stats", "kicks", 1);
            StatUtils.statPlayer(event.getPlayer(), "stats", "lastlogout", (int) ((new Date()).getTime() / 1000L));
            addTimeOnlineAndWipe(event.getPlayer().getName());
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        StatUtils.statPlayer(event.getPlayer(), "stats", "lastlogout",
                (int) ((new Date()).getTime() / 1000L));
        addTimeOnlineAndWipe(event.getPlayer().getName());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        if (((event.getTo().getBlockX() != event.getFrom().getBlockX())
                || (event.getTo().getBlockY() != event.getFrom().getBlockY()) || (event.getTo().getBlockZ() != event
                .getFrom().getBlockZ()))) {

            Location from;
            Location to;

            from = event.getFrom();
            to = event.getTo();

            if (from.getWorld().equals(to.getWorld())) {
                final double distance = from.distance(to);
                if (distance < 8) {
                    StatUtils.statPlayer(event.getPlayer(), "stats", "move", (int) Math.ceil(distance));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        StatUtils.statItem(event.getPlayer(), "itempickup", event.getItem().getItemStack(), event.getItem().getItemStack().getAmount());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if ((event.isCancelled() == false) && !isBlacklistedWorld(event.getPlayer().getWorld())) {
            StatUtils.statPlayer(event.getPlayer(), "stats", "portal", 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if ((event.isCancelled() == false) && !isBlacklistedWorld(event.getPlayer().getWorld())) {
            final TeleportCause teleportCause = event.getCause();

            if (teleportCause == TeleportCause.ENDER_PEARL) {
                StatUtils.statPlayer(event.getPlayer(), "itemuse", "enderpearl", 1);
            }
            StatUtils.statPlayer(event.getPlayer(), "stats", "teleport", 1);

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        StatUtils.statPlayer(event.getPlayer(), "stats", "fill" + event.getBucket().toString().toLowerCase().replace("_", ""), 1);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        StatUtils.statPlayer(event.getPlayer(), "stats", "empty" + event.getBucket().toString().toLowerCase().replace("_", ""), 1);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        Material material = event.getPlayer().getItemInHand().getType();
        Entity rightClicked = event.getRightClicked();


        if ((material == Material.BUCKET) && (rightClicked instanceof Cow)) {
            StatUtils.statPlayer(event.getPlayer(), "interact", "milkcow", 1);
            return;
        }

        if ((material == Material.BOWL) && (rightClicked instanceof MushroomCow)) {
            StatUtils.statPlayer(event.getPlayer(), "interact", "milkmushroomcow", 1);
            return;
        }

        if ((material == Material.INK_SACK) && (rightClicked instanceof Sheep)) {
            StatUtils.statPlayer(event.getPlayer(), "dye", "total", 1);
            StatUtils.statItem(event.getPlayer(), "dye", event.getPlayer().getItemInHand(), 1);
            return;
        }

        if ((material == Material.INK_SACK) && (rightClicked instanceof Wolf)) {

            // Check it's our wolf
            Wolf wolf = (Wolf) rightClicked;
            if (!wolf.isTamed()) {
                return;
            }
            if (wolf.getOwner() != event.getPlayer()) {
                return;
            }

            StatUtils.statPlayer(event.getPlayer(), "wolfdye", "total", 1);

            /**
             * if MetaDataable, make the item string correct
             */
            
            StatUtils.statItem(event.getPlayer(), "wolfdye", event.getPlayer().getItemInHand(), 1);

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void shearEvent(PlayerShearEntityEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }


        if (event.getEntity() instanceof Sheep) {
            StatUtils.statPlayer(event.getPlayer(), "sheared", "sheep", 1);
        }

        if (event.getEntity() instanceof MushroomCow) {
            StatUtils.statPlayer(event.getPlayer(), "sheared", "mushroomcow", 1);
        }

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        Action action = event.getAction();
        ItemStack item = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        Result result = event.useItemInHand();

        if ((item != null) && (action != null) && (clickedBlock != null)) {

            if (result.equals(Result.DENY) == false) {
                if ((item.getType() == Material.FLINT_AND_STEEL) || (item.getType() == Material.FIREBALL)
                        || (item.getType() == Material.SIGN)) {
                    StatUtils.statItem(event.getPlayer(), "itemuse", item, 1);
                }
            }
            if ((clickedBlock.getType() == Material.CAKE_BLOCK)
                    || ((clickedBlock.getType() == Material.TNT) && (item.getType() == Material.FLINT_AND_STEEL))) {
                StatUtils.statBlock(event.getPlayer(), "itemuse", clickedBlock, 1);
            }
            if (clickedBlock.getType().equals(Material.CHEST)) {
                StatUtils.statPlayer(event.getPlayer(), "stats", "openchest", 1);
            }
            if (clickedBlock.getType().equals(Material.FLOWER_POT) && (action == Action.RIGHT_CLICK_BLOCK)
                    && (clickedBlock.getData() == 0)) {
                Material[] m = { Material.RED_ROSE, Material.YELLOW_FLOWER, Material.SAPLING, Material.RED_MUSHROOM,
                        Material.BROWN_MUSHROOM, Material.CACTUS, Material.DEAD_BUSH };
                for (Material mm : m) {

                    if (mm.equals(item.getType())) {
                        StatUtils.statItem(event.getPlayer(),"plant",item,1);
                    }
                }

            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExp(PlayerExpChangeEvent event) {
        if (!shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        
        StatUtils.statPlayer(event.getPlayer(),"exp", "lifetimexp", event.getAmount());
        
        getPlayerStatManager().getOrCreatePlayerStatBlob(event.getPlayer().getName()).onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld().getName(), "exp",
                "currentexp", event.getPlayer().getTotalExperience() + event.getAmount()));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExpLevel(PlayerLevelChangeEvent event) {
        if (!shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        Promise<EntityStatBlob> promiseblob = this.getPlayerStatManager().getOrCreatePlayerStatBlob(
                event.getPlayer().getName());
        promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN, event.getPlayer().getWorld().getName(), "exp",
                "currentlvl", event.getNewLevel()));
        int change = event.getNewLevel() - event.getOldLevel();
        if (change > 0) {
            StatUtils.statPlayer(event.getPlayer(), "exp", "lifetimelvl", change);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();

        if (event.isCancelled() || !shouldTrackPlayer(event.getEnchanter())) {
            return;
        }

        if ((event.isCancelled() == false) && !isBlacklistedWorld(player.getWorld())) {
            StatUtils.statPlayer(player, "enchant", "total", 1);
            StatUtils.statPlayer(player, "enchant", "totallvlspent", event.getExpLevelCost());
        }
    }

    private void addTimeOnlineAndWipe(String player) {

        ManagerRecord timeRecord = OnlineTimeManager.getRecord(player);
        if (timeRecord == null) {
            return;
        }
        if (timeRecord.world == null) {
            return;
        }
        StatUtils.increment(player, BeardStat.DEFAULT_DOMAIN, timeRecord.world, "stats", "playedfor",
                timeRecord.sessionTime());
        OnlineTimeManager.wipeRecord(player);

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void worldJump(PlayerChangedWorldEvent event) {
        addTimeOnlineAndWipe(event.getPlayer().getName());
        OnlineTimeManager.setRecord(event.getPlayer().getName(), event.getPlayer().getWorld().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNom(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }

        if (event.getItem().getType().isEdible()) {
            StatUtils.statItem(player,"consume", event.getItem(), 1);
            return;
        }
        if (event.getItem().getType() == Material.POTION) {

            // process meta potions
            PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
            if (meta != null) {
                for (PotionEffect effect : meta.getCustomEffects()) {
                    StatUtils.statPotion(player, "consume", effect, 1);
                }
                return;
            }

            // processs base effect if nessecary
            @SuppressWarnings("deprecation")
            Collection<PotionEffect> potion = Potion.getBrewer().getEffectsFromDamage(
                    event.getItem().getDurability());

            for (PotionEffect effect : potion) {
                StatUtils.statPotion(player, "consume", effect, 1);
            }
        }


    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeash(PlayerLeashEntityEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer(event.getPlayer())) {
            return;
        }
        Player player = event.getPlayer();
        StatUtils.statEntity(player, "leash", event.getEntity(), 1);
    }
}