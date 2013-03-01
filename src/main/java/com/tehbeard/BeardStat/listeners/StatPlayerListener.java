package com.tehbeard.BeardStat.listeners;

import java.util.Date;
import java.util.List;


import net.dragonzone.promise.Delegate;
import net.dragonzone.promise.Promise;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Calls the stat manager to trigger events
 * @author James
 *
 */
public class StatPlayerListener implements Listener {

    List<String> worlds;
    private PlayerStatManager playerStatManager;

    public StatPlayerListener(List<String> worlds,PlayerStatManager playerStatManager){
        this.worlds = worlds;
        this.playerStatManager = playerStatManager;
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
    	String world = event.getPlayer().getWorld().getName();
        if(event.getAnimationType()==PlayerAnimationType.ARM_SWING){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,world,"stats","armswing",1));

        }

    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","login",1));
        promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","lastlogin",(int)(System.currentTimeMillis()/1000L)));
        promiseblob.onResolve(new Delegate<Void, Promise<EntityStatBlob>>() {
        
        
            public <P extends Promise<EntityStatBlob>> Void invoke(P params) {

                if(!params.getValue().hasStat(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats", "firstlogin")){
                    params.getValue().getStat(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","firstlogin").setValue((int)(event.getPlayer().getFirstPlayed()/1000L));
                    
                }
                
                return null;
            }
        });


        BeardStat.self().getStatManager().setLoginTime(event.getPlayer().getName(), System.currentTimeMillis());

    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            int len = event.getMessage().length();
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            
            String world = event.getPlayer().getWorld().getName();
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,world,"stats","chatletters",len));
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,world,"stats","chat",1));



        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){

            MetaDataCapture.saveMetaDataMaterialStat(playerStatManager.getPlayerBlobASync(event.getPlayer().getName()), 
            		BeardStat.DEFAULT_DOMAIN,
            		event.getPlayer().getWorld().getName(),
                    "itemdrop", 
                    event.getItemDrop().getItemStack().getType(), 
                    event.getItemDrop().getItemStack().getDurability(), 
                    event.getItemDrop().getItemStack().getAmount());

        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerFish(PlayerFishEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","fishcaught",1));
        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event){
        if(event.isCancelled()==false){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","kicks",1));
            promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","lastlogout",(int)((new Date()).getTime()/1000L)));

            calc_timeonline_and_wipe(event.getPlayer().getName());
        }

    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
        promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","lastlogout",(int)((new Date()).getTime()/1000L)));
        calc_timeonline_and_wipe(event.getPlayer().getName());

    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false &&
                (event.getTo().getBlockX() != event.getFrom().getBlockX() || 
                event.getTo().getBlockY() != event.getFrom().getBlockY() || 
                event.getTo().getBlockZ() != event.getFrom().getBlockZ() )&& 
                !worlds.contains(event.getPlayer().getWorld().getName())){

            Location from;
            Location to;

            from = event.getFrom();
            to = event.getTo();

            if(from.getWorld().equals(to.getWorld())){
                final double distance = from.distance(to);
                if(distance < 8){
                    Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
                    
                    promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,from.getWorld().getName(),"stats","move",(int)Math.ceil(distance)));


                }
            }
        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){

            MetaDataCapture.saveMetaDataMaterialStat(playerStatManager.getPlayerBlobASync(event.getPlayer().getName()), 
            		BeardStat.DEFAULT_DOMAIN,
            		event.getPlayer().getWorld().getName(),
                    "itempickup", 
                    event.getItem().getItemStack().getType(), 
                    event.getItem().getItemStack().getDurability(), 
                    event.getItem().getItemStack().getAmount());


        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event){
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","portal",1));
        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            final TeleportCause teleportCause = event.getCause();

            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            if(teleportCause == TeleportCause.ENDER_PEARL){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"itemuse","enderpearl",1));
            }
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","teleport",1));

        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerBucketFill(PlayerBucketFillEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","fill"+ event.getBucket().toString().toLowerCase().replace("_",""),1));

        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","empty"+ event.getBucket().toString().toLowerCase().replace("_",""),1));

        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){


            Material material = event.getPlayer().getItemInHand().getType();
            Entity rightClicked = event.getRightClicked();

            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());


            if(material == Material.BUCKET && rightClicked instanceof Cow){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"interact", "milkcow",1));
            }

            if(material == Material.BOWL && rightClicked instanceof MushroomCow){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"interact", "milkmushroomcow",1));
            }

            if(material == Material.INK_SACK && rightClicked instanceof Sheep){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"dye", "total",1));

                /**
                 * if MetaDataable, make the item string correct
                 */

                MetaDataCapture.saveMetaDataMaterialStat(promiseblob, 
                		BeardStat.DEFAULT_DOMAIN,
                		event.getPlayer().getWorld().getName(),
                        "dye", 
                        event.getPlayer().getItemInHand().getType(), 
                        event.getPlayer().getItemInHand().getDurability(), 
                        1);

            }
            
            if(material == Material.INK_SACK && rightClicked instanceof Wolf){
            	
            	//Check it's our wolf
            	Wolf wolf = (Wolf)rightClicked;
            	if(!wolf.isTamed()){return;}
            	if(wolf.getOwner() != event.getPlayer()){return;}
            	
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"wolfdye", "total",1));

                /**
                 * if MetaDataable, make the item string correct
                 */

                MetaDataCapture.saveMetaDataMaterialStat(promiseblob, 
                		BeardStat.DEFAULT_DOMAIN,
                		event.getPlayer().getWorld().getName(),
                        "wolfdye", 
                        event.getPlayer().getItemInHand().getType(), 
                        event.getPlayer().getItemInHand().getDurability(), 
                        1);

            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void shearEvent(PlayerShearEntityEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){

            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            if(event.getEntity() instanceof Sheep){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"sheared", "sheep",1));
            }

            if(event.getEntity() instanceof MushroomCow){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"sheared", "mushroomcow",1));
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(event.getClickedBlock()!=null){
            if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
                Action action = event.getAction();
                ItemStack item = event.getItem();
                Block clickedBlock = event.getClickedBlock();
                Result result = event.useItemInHand();

                Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());

                if(item !=null &&
                        action!=null &&
                        clickedBlock!=null){

                    if(result.equals(Result.DENY)==false){
                        /*lighter
							  sign
							  tnt
							  bucket
							  waterbucket
							  lavabucket
							  cakeblock*/
                        if(item.getType()==Material.FLINT_AND_STEEL ||
                                item.getType()==Material.FLINT_AND_STEEL ||
                                item.getType()==Material.SIGN 
                                ){
                            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"itemuse",item.getType().toString().toLowerCase().replace("_",""),1));
                        }
                    }
                    if(clickedBlock.getType() == Material.CAKE_BLOCK||
                            (clickedBlock.getType() == Material.TNT && item.getType()==Material.FLINT_AND_STEEL)){
                        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"itemuse",clickedBlock.getType().toString().toLowerCase().replace("_",""),1));
                    }
                    if(clickedBlock.getType().equals(Material.CHEST)){
                        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"stats","openchest",1));
                    }
                    if(clickedBlock.getType().equals(Material.FLOWER_POT) && action == Action.RIGHT_CLICK_BLOCK && clickedBlock.getData() == 0){
                    	Material[] m = {
                    			Material.RED_ROSE,
                    			Material.YELLOW_FLOWER,
                    			Material.SAPLING,
                    			Material.RED_MUSHROOM,
                    			Material.BROWN_MUSHROOM,
                    			Material.CACTUS,
                    			Material.DEAD_BUSH};
                    	for(Material mm : m){
                    		
                    		if(mm.equals(item.getType())){
                    			MetaDataCapture.saveMetaDataMaterialStat(
                    					promiseblob, 
                    					BeardStat.DEFAULT_DOMAIN,
                                		event.getPlayer().getWorld().getName(),
                    					"plant", 
                    					mm, 
                    					item.getDurability(), 1);
                    		}
                    	}
                    	
                        
                    }


                }



            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerExp(PlayerExpChangeEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(!worlds.contains(event.getPlayer().getWorld().getName())){
            Player player = event.getPlayer();
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"exp","lifetimexp",event.getAmount()));
            promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"exp","currentexp",player.getTotalExperience() + event.getAmount()));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerExpLevel(PlayerLevelChangeEvent event){
    	if(event.getPlayer().getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        if(!worlds.contains(event.getPlayer().getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(event.getPlayer().getName());
            promiseblob.onResolve(new DelegateSet(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"exp","currentlvl",event.getNewLevel()));
            int change = event.getNewLevel() - event.getOldLevel();
            if(change > 0){
                promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getPlayer().getWorld().getName(),"exp","lifetimelvl",change));
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEnchant(EnchantItemEvent event){
        Player player = event.getEnchanter();

        if(player.getGameMode() == GameMode.CREATIVE && !BeardStat.self().getConfig().getBoolean("stats.trackcreativemode",false)){
    		return;
    	}
        
        if(event.isCancelled()==false && !worlds.contains(player.getWorld().getName())){
            Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(player.getName());
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getEnchanter().getWorld().getName(),"enchant","total",1));
            promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,event.getEnchanter().getWorld().getName(),"enchant","totallvlspent",event.getExpLevelCost()));
        }
    }

    private void calc_timeonline_and_wipe(String player){

        int seconds = BeardStat.self().getStatManager().getSessionTime(player);
        Promise<EntityStatBlob> promiseblob = playerStatManager.getPlayerBlobASync(player);
        promiseblob.onResolve(new DelegateIncrement(BeardStat.DEFAULT_DOMAIN,BeardStat.GLOBAL_WORLD,"stats","playedfor",seconds));
        BeardStat.self().getStatManager().wipeLoginTime(player);		

    }





}