package com.tehbeard.BeardStat.listeners;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;



public class StatCraftListener implements Listener {

    List<String> worlds;
    private PlayerStatManager playerStatManager;


    public StatCraftListener(List<String> worlds,
            PlayerStatManager playerStatManager) {
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onCraftItem(CraftItemEvent event){
    	if(event.getWhoClicked().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(!worlds.contains(event.getWhoClicked().getWorld().getName())){
            int amount = event.getRecipe().getResult().getAmount();
            final Player p = (Player)event.getWhoClicked();
            if(event.isShiftClick()){

                final Inventory inv = event.getWhoClicked().getInventory();
                final ItemStack is = event.getRecipe().getResult();
                final int preAmount = getItemCount(inv,is);

                Bukkit.getScheduler().runTaskAsynchronously(BeardStat.self(), new Runnable(){

                    public void run() {
                        int made = getItemCount(inv,is) - preAmount;
                        //String item = is.getType().toString().toLowerCase().replace("_","");
                        MetaDataCapture.saveMetaDataMaterialStat(playerStatManager.getPlayerBlobASync(p.getName()),
                        		BeardStat.DEFAULT_DOMAIN,
                        		p.getWorld().getName(),
                                "crafting", 
                                is.getType(), 
                                is.getDurability(), 
                                made);
                    }

                });
            }
            else
            {


                /**
                 * if MetaDataable, make the item string correct
                 */
                MetaDataCapture.saveMetaDataMaterialStat(playerStatManager.getPlayerBlobASync(p.getName()), 
                		BeardStat.DEFAULT_DOMAIN,
                		p.getWorld().getName(),
                        "crafting", 
                        event.getRecipe().getResult().getType(), 
                        event.getRecipe().getResult().getDurability(), 
                        amount);

            }
        }
    }


    private int getItemCount(Inventory inv,ItemStack item){
        int i = 0;
        for(ItemStack is : inv.all(item.getType()).values()){
            if(is.getDurability() == item.getDurability()){
                i += is.getAmount();
            }
        }
        return i;
    }

}
