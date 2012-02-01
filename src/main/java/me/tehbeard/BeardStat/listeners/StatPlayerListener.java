package me.tehbeard.BeardStat.listeners;

import java.util.Date;
import java.util.List;

import me.tehbeard.BeardStat.BeardStat;

import me.tehbeard.BeardStat.containers.PlayerStatManager;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

/**
 * Calls the stat manager to trigger events
 * @author James
 *
 */
public class StatPlayerListener implements Listener {

	List<String> worlds;


	public StatPlayerListener(List<String> worlds){
		this.worlds = worlds;
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		// TODO Auto-generated method stub
		if(event.getAnimationType()==PlayerAnimationType.ARM_SWING){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","armswing").incrementStat(1);
		}

	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		// TODO Auto-generated method stub
		PlayerStatManager.getPlayerBlob(event.getPlayer().getName());
		PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","login").incrementStat(1);
		PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","lastlogin").setValue( (int)((new Date()).getTime()/1000L));
		BeardStat.loginTimes.put(event.getPlayer().getName(), (new Date()).getTime());

	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","chatletters").incrementStat(event.getMessage().length());
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","chat").incrementStat(1);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(
					event.getPlayer().getName()).getStat("itemdrop",event.getItemDrop().getItemStack().getType().toString().toLowerCase().replace("_","")).incrementStat(event.getItemDrop().getItemStack().getAmount()
							);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerFish(PlayerFishEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","fishcaught").incrementStat(1);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","kicks").incrementStat(1);
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","lastlogout").setValue( (int)((new Date()).getTime()/1000L));
			calc_timeonline(event.getPlayer().getName());
		}

	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {

		PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","lastlogout").setValue( (int)((new Date()).getTime()/1000L));
		calc_timeonline(event.getPlayer().getName());

	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		if(event.isCancelled()==false &&
				event.getTo().getBlockX() != event.getFrom().getBlockX() && 
				event.getTo().getBlockY() != event.getFrom().getBlockY() && 
				event.getTo().getBlockZ() != event.getFrom().getBlockZ() && 
				!worlds.contains(event.getPlayer().getWorld().getName())){

			Location from,to;
			Player player = event.getPlayer();
			from = event.getFrom();
			to = event.getTo();
			if(from.getWorld().equals(to.getWorld())){
				if(from.distance(to) < 5){
					PlayerStatManager.getPlayerBlob(player.getName()).getStat("stats","move").incrementStat((int)from.distance(to));
				}
			}
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("itempickup",event.getItem().getItemStack().getType().toString().toLowerCase().replace("_","")).incrementStat(event.getItem().getItemStack().getAmount());

		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerPortal(PlayerPortalEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("stats","portal").incrementStat(1);
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			TeleportCause teleportCause = event.getCause();
			Player player = event.getPlayer();
			if(teleportCause == TeleportCause.ENDER_PEARL){
				PlayerStatManager.getPlayerBlob(player.getName()).getStat("itemuse","enderpearl").incrementStat(1);
			}
			else
			{
				PlayerStatManager.getPlayerBlob(player.getName()).getStat("stats","teleport").incrementStat(1);
			}

		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerBucketFill(PlayerBucketFillEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("block","fill"+ event.getBucket().toString()).incrementStat(1);

		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
			PlayerStatManager.getPlayerBlob(event.getPlayer().getName()).getStat("block","empty"+ event.getBucket().toString()).incrementStat(1);

		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event){

		if(event.getClickedBlock()!=null){
			if(event.isCancelled()==false && !worlds.contains(event.getPlayer().getWorld().getName())){
				Player player = event.getPlayer();
				Action action = event.getAction();
				ItemStack item = event.getItem();
				Block clickedBlock = event.getClickedBlock();
				Result result = event.useItemInHand();
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
								item.getType()==Material.SIGN ||
								item.getType()==Material.BUCKET||
								item.getType()==Material.WATER_BUCKET||
								item.getType()==Material.LAVA_BUCKET
								){
							PlayerStatManager.getPlayerBlob(player.getName()).getStat("itemuse",item.getType().toString().toLowerCase().replace("_","")).incrementStat(1);
						}
					}
					if(clickedBlock.getType() == Material.CAKE_BLOCK||
							(clickedBlock.getType() == Material.TNT && item.getType()==Material.FLINT_AND_STEEL)){
						PlayerStatManager.getPlayerBlob(player.getName()).getStat("itemuse",clickedBlock.getType().toString().toLowerCase().replace("_","")).incrementStat(1);
					}
					if(clickedBlock.getType().equals(Material.CHEST)){
						PlayerStatManager.getPlayerBlob(player.getName()).getStat("stats","openchest").incrementStat(1);
					}

				}



			}
		}
	}
	private void calc_timeonline(String player){
		if( BeardStat.loginTimes.containsKey(player)){
			long seconds = ((new Date()).getTime() - BeardStat.loginTimes.get(player))/1000L;
			PlayerStatManager.getPlayerBlob(player).getStat("stats","playedfor").incrementStat(Integer.parseInt(""+seconds));

			BeardStat.loginTimes.remove(player);		
		}
		else
		{
			BeardStat.printDebugCon("Attempted to calculate time for a player who doesn't have a record!");
		}
	}





}