package me.tehbeard.BeardStat.StatCollectors;


import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;

import org.bukkit.inventory.ItemStack;


/**
 * The base stat collector for blocks events
 * @author James
 *
 */
public class BlockStatCollector implements IStatCollector {

	public void onPlayerBucketFill(Player player,Material bucket){
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("block","fill"+ bucket.toString()).incrementStat(1);

	}

	public void onPlayerBucketEmpty(Player player,Material bucket){
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("block","empty"+ bucket.toString()).incrementStat(1);
	}

	public void onPlayerBlockInteract(Player player,Action action, ItemStack item, Block clickedBlock, BlockFace clickedFace, Result result){
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


		}
	}

	public void onBlockPlace(Player player,Block placed,BlockState replaceTarget){
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("blockcreate",placed.getType().toString().toLowerCase().replace("_","")).incrementStat(1);
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("stats","totalblockcreate").incrementStat(1);
	}

	public void onBlockBreak(Player player,Block block){
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("stats","totalblockdestroy").incrementStat(1);
		PlayerStatManager.getPlayerBlob(player.getName()).getStat("blockdestroy",block.getType().toString().toLowerCase().replace("_","")).incrementStat(1);
	}


}
