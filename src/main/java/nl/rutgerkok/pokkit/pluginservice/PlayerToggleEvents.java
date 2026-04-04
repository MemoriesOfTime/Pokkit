package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class PlayerToggleEvents extends EventTranslator {

	@EventHandler(ignoreCancelled = false)
	public void onToggleSneak(cn.nukkit.event.player.PlayerToggleSneakEvent event) {
		if (canIgnore(PlayerToggleSneakEvent.getHandlerList())) {
			return;
		}

		PlayerToggleSneakEvent bukkitEvent = new PlayerToggleSneakEvent(
				PokkitPlayer.toBukkit(event.getPlayer()), event.isSneaking());
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onToggleSprint(cn.nukkit.event.player.PlayerToggleSprintEvent event) {
		if (canIgnore(PlayerToggleSprintEvent.getHandlerList())) {
			return;
		}

		PlayerToggleSprintEvent bukkitEvent = new PlayerToggleSprintEvent(
				PokkitPlayer.toBukkit(event.getPlayer()), event.isSprinting());
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onToggleFlight(cn.nukkit.event.player.PlayerToggleFlightEvent event) {
		if (canIgnore(PlayerToggleFlightEvent.getHandlerList())) {
			return;
		}

		PlayerToggleFlightEvent bukkitEvent = new PlayerToggleFlightEvent(
				PokkitPlayer.toBukkit(event.getPlayer()), event.isFlying());
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onPlayerAnimation(cn.nukkit.event.player.PlayerAnimationEvent event) {
		if (canIgnore(PlayerAnimationEvent.getHandlerList())) {
			return;
		}

		PlayerAnimationEvent bukkitEvent = new PlayerAnimationEvent(
				PokkitPlayer.toBukkit(event.getPlayer()));
		callCancellable(event, bukkitEvent);
	}
}
