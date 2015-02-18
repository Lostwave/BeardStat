package com.tehbeard.beardstat.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.tehbeard.beardstat.bukkit.BukkitPlugin;
import com.tehbeard.beardstat.Refs;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.bukkit.utils.StatUtils;

public class StatCraftListener extends StatListener {

    public StatCraftListener(EntityStatManager playerStatManager, BukkitPlugin plugin) {
        super( playerStatManager, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraftItem(CraftItemEvent event) {
        if (event.isCancelled() || !shouldTrackPlayer((Player) event.getWhoClicked(),Refs.TRACK_ITEM_CRAFT)) {
            return;
        }

        int amount = event.getRecipe().getResult().getAmount();
        final Player p = (Player) event.getWhoClicked();
        if (event.isShiftClick()) {

            final Inventory inv = event.getWhoClicked().getInventory();
            final ItemStack is = event.getRecipe().getResult();
            final int preAmount = getItemCount(inv, is);

            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

                @Override
                public void run() {
                    int made = getItemCount(inv, is) - preAmount;
                    // String item =
                    // is.getType().toString().toLowerCase().replace("_","");
                    StatUtils.instance.modifyStatItem(p,"crafting",is,made);
                }

            });
        } else {

            /**
             * if MetaDataable, make the item string correct
             */
            StatUtils.instance.modifyStatItem(p,"crafting", event.getRecipe().getResult(), amount);
        }

    }

    private int getItemCount(Inventory inv, ItemStack item) {
        int i = 0;
        for (ItemStack is : inv.all(item.getType()).values()) {
            if (is.getDurability() == item.getDurability()) {
                i += is.getAmount();
            }
        }
        return i;
    }

}
