package nl.rutgerkok.pokkit.pluginservice;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.event.EventHandler;
import cn.nukkit.item.Item;
import nl.rutgerkok.pokkit.PokkitGameMode;
import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.GameMode;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerLifecycleEvents extends EventTranslator {

	@EventHandler(ignoreCancelled = false)
	public void onRespawn(cn.nukkit.event.player.PlayerRespawnEvent event) {
		if (canIgnore(PlayerRespawnEvent.getHandlerList())) {
			return;
		}

		PlayerRespawnEvent bukkitEvent = new PlayerRespawnEvent(
				PokkitPlayer.toBukkit(event.getPlayer()),
				PokkitLocation.toBukkit(event.getRespawnPosition()),
				false);
		callUncancellable(bukkitEvent);
		event.setRespawnPosition(PokkitLocation.toNukkit(bukkitEvent.getRespawnLocation()));
	}

	@EventHandler(ignoreCancelled = false)
	public void onPlayerDeath(cn.nukkit.event.player.PlayerDeathEvent event) {
		if (canIgnore(PlayerDeathEvent.getHandlerList())) {
			return;
		}

		List<ItemStack> drops = new ArrayList<>();
		for (Item item : event.getDrops()) {
			drops.add(PokkitItemStack.toBukkitCopy(item));
		}

		String deathMessage = event.getDeathMessage().getText();
		PlayerDeathEvent bukkitEvent = new PlayerDeathEvent(
				PokkitPlayer.toBukkit(event.getEntity()),
				DamageSource.builder(DamageType.GENERIC).build(),
				drops,
				event.getExperience(),
				deathMessage);
		callUncancellable(bukkitEvent);

		event.setDeathMessage(bukkitEvent.getDeathMessage());
		event.setDrops(bukkitEvent.getDrops().stream()
				.map(PokkitItemStack::toNukkitCopy)
				.toArray(Item[]::new));
		event.setExperience(bukkitEvent.getDroppedExp());
		event.setKeepInventory(bukkitEvent.getKeepInventory());
		event.setKeepExperience(bukkitEvent.getKeepLevel());
	}

	@EventHandler(ignoreCancelled = false)
	public void onGameModeChange(cn.nukkit.event.player.PlayerGameModeChangeEvent event) {
		if (canIgnore(PlayerGameModeChangeEvent.getHandlerList())) {
			return;
		}

		GameMode newGameMode = PokkitGameMode.toBukkit(event.getNewGamemode());
		PlayerGameModeChangeEvent bukkitEvent = new PlayerGameModeChangeEvent(
				PokkitPlayer.toBukkit(event.getPlayer()), newGameMode);
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onTeleport(cn.nukkit.event.player.PlayerTeleportEvent event) {
		if (canIgnore(PlayerTeleportEvent.getHandlerList())) {
			return;
		}

		PlayerTeleportEvent.TeleportCause cause = toBukkitCause(event.getCause());
		PlayerTeleportEvent bukkitEvent = new PlayerTeleportEvent(
				PokkitPlayer.toBukkit(event.getPlayer()),
				PokkitLocation.toBukkit(event.getFrom()),
				PokkitLocation.toBukkit(event.getTo()),
				cause);
		callCancellable(event, bukkitEvent);
		event.setTo(PokkitLocation.toNukkit(bukkitEvent.getTo()));
	}

	@EventHandler(ignoreCancelled = false)
	public void onFoodLevelChange(cn.nukkit.event.player.PlayerFoodLevelChangeEvent event) {
		if (canIgnore(FoodLevelChangeEvent.getHandlerList())) {
			return;
		}

		FoodLevelChangeEvent bukkitEvent = new FoodLevelChangeEvent(
				PokkitPlayer.toBukkit(event.getPlayer()),
				event.getFoodLevel());
		callCancellable(event, bukkitEvent);
		event.setFoodLevel(bukkitEvent.getFoodLevel());
	}

	private PlayerTeleportEvent.TeleportCause toBukkitCause(
			cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause nukkit) {
		switch (nukkit) {
		case COMMAND:
			return PlayerTeleportEvent.TeleportCause.COMMAND;
		case PLUGIN:
			return PlayerTeleportEvent.TeleportCause.PLUGIN;
		case NETHER_PORTAL:
			return PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
		case END_PORTAL:
			return PlayerTeleportEvent.TeleportCause.END_PORTAL;
		case END_GATEWAY:
			return PlayerTeleportEvent.TeleportCause.END_GATEWAY;
		case ENDER_PEARL:
			return PlayerTeleportEvent.TeleportCause.ENDER_PEARL;
		case CHORUS_FRUIT:
			return PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT;
		default:
			return PlayerTeleportEvent.TeleportCause.UNKNOWN;
		}
	}
}
