package me.tehbeard.BeardStat.listeners;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

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
        if(!worlds.contains(event.getWhoClicked().getWorld().getName())){
            String item = event.getRecipe().getResult().getType().toString().toLowerCase().replace("_","");
            int amount = event.getRecipe().getResult().getAmount();
            final Player p = (Player)event.getWhoClicked();
            if(event.isShiftClick()){
                
                final Inventory inv = event.getWhoClicked().getInventory();
                final ItemStack is = event.getRecipe().getResult();
                final int preAmount = getItemCount(inv,is);
               
                Bukkit.getScheduler().scheduleAsyncDelayedTask(BeardStat.self(), new Runnable(){

                    public void run() {
                        int made = getItemCount(inv,is) - preAmount;
                        String item = 
                                is.getType().toString().toLowerCase().replace("_","");
                        if(MetaDataCapture.hasMetaData(is.getType())){
                            item += "_" + is.getDurability();

                        }

                        playerStatManager.getPlayerBlob(p.getName()).getStat("crafting", item).incrementStat(made);
                    }

                });
            }
            else
            {
                

                /**
                 * if MetaDataable, make the item string correct
                 */
                if(MetaDataCapture.hasMetaData(event.getRecipe().getResult().getType())){
                    item = 
                            event.getRecipe().getResult().getType().toString().toLowerCase().replace("_","") + 
                            "_" + event.getRecipe().getResult().getDurability();

                }

                playerStatManager.getPlayerBlob(p.getName()).getStat("crafting", item).incrementStat(amount);
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
