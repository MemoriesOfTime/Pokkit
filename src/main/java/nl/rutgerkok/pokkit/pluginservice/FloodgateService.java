package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import nl.rutgerkok.pokkit.floodgate.PokkitFloodgateApi;
import nl.rutgerkok.pokkit.floodgate.PokkitFloodgateEventBus;
import nl.rutgerkok.pokkit.floodgate.PokkitHandshakeHandlers;
import nl.rutgerkok.pokkit.floodgate.PokkitPacketHandlers;
import nl.rutgerkok.pokkit.floodgate.PokkitPlayerLink;
import org.geysermc.floodgate.api.InstanceHolder;

/**
 * Registers and manages the Floodgate API for Pokkit.
 * All players on Nukkit-MOT are Bedrock players, so isFloodgatePlayer()
 * returns true for all online players.
 */
public final class FloodgateService extends EventTranslator {

	private PokkitFloodgateApi api;

	@Override
	public void onLoad(cn.nukkit.plugin.PluginBase pokkit) {
		api = new PokkitFloodgateApi();

		// Register all currently online players (in case of reload)
		for (Player player : pokkit.getServer().getOnlinePlayers().values()) {
			api.addPlayer(player);
		}

		// Register the API instance globally with all implementations
		InstanceHolder.set(
				api,
				new PokkitPlayerLink(),
				new PokkitFloodgateEventBus(),
				null,  // PlatformInjector not applicable on Nukkit-MOT
				new PokkitPacketHandlers(),
				new PokkitHandshakeHandlers(),
				new java.util.UUID(0, 0)
		);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		api.addPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		api.removePlayer(event.getPlayer().getUniqueId());
	}
}
