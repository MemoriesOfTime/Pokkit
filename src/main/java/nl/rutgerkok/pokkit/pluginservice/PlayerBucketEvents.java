package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import nl.rutgerkok.pokkit.item.ItemMap;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import nl.rutgerkok.pokkit.world.PokkitBlock;
import nl.rutgerkok.pokkit.world.PokkitBlockFace;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerBucketEvents extends EventTranslator {

	private Material getBucketMaterial(cn.nukkit.item.Item bucket) {
		Material material = ItemMap.fromNukkitOrNull(bucket);
		if (material != null) {
			return material;
		}
		return Material.BUCKET;
	}

	@EventHandler(ignoreCancelled = false)
	public void onBucketEmpty(cn.nukkit.event.player.PlayerBucketEmptyEvent event) {
		if (canIgnore(PlayerBucketEmptyEvent.getHandlerList())) {
			return;
		}

		Player player = PokkitPlayer.toBukkit(event.getPlayer());
		Block clickedBlock = PokkitBlock.toBukkit(event.getBlockClicked());
		BlockFace face = PokkitBlockFace.toBukkit(event.getBlockFace());
		Material bucket = getBucketMaterial(event.getBucket());
		ItemStack itemInHand = PokkitItemStack.toBukkitCopy(event.getItem());

		PlayerBucketEmptyEvent bukkitEvent = new PlayerBucketEmptyEvent(
				player, clickedBlock, clickedBlock.getRelative(face),
				face, bucket, itemInHand);

		callCancellable(event, bukkitEvent);

		if (!event.isCancelled()) {
			cn.nukkit.item.Item nukkitItem = PokkitItemStack.toNukkitCopy(bukkitEvent.getItemStack());
			if (nukkitItem != null) {
				event.setItem(nukkitItem);
			}
		}
	}

	@EventHandler(ignoreCancelled = false)
	public void onBucketFill(cn.nukkit.event.player.PlayerBucketFillEvent event) {
		if (canIgnore(PlayerBucketFillEvent.getHandlerList())) {
			return;
		}

		Player player = PokkitPlayer.toBukkit(event.getPlayer());
		Block clickedBlock = PokkitBlock.toBukkit(event.getBlockClicked());
		BlockFace face = PokkitBlockFace.toBukkit(event.getBlockFace());
		Material bucket = getBucketMaterial(event.getBucket());
		ItemStack itemInHand = PokkitItemStack.toBukkitCopy(event.getItem());

		PlayerBucketFillEvent bukkitEvent = new PlayerBucketFillEvent(
				player, clickedBlock, clickedBlock.getRelative(face),
				face, bucket, itemInHand);

		callCancellable(event, bukkitEvent);

		if (!event.isCancelled()) {
			cn.nukkit.item.Item nukkitItem = PokkitItemStack.toNukkitCopy(bukkitEvent.getItemStack());
			if (nukkitItem != null) {
				event.setItem(nukkitItem);
			}
		}
	}
}
