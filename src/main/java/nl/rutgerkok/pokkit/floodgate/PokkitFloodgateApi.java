package nl.rutgerkok.pokkit.floodgate;

import cn.nukkit.Server;
import org.geysermc.cumulus.form.impl.FormDefinitions;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.event.FloodgateEventBus;
import org.geysermc.floodgate.api.link.PlayerLink;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.api.unsafe.Unsafe;
import org.geysermc.floodgate.util.LinkedPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Simple FloodgateApi implementation for Pokkit.
 * On Nukkit-MOT all players are Bedrock players.
 */
public final class PokkitFloodgateApi implements FloodgateApi {

	private final Map<UUID, FloodgatePlayer> players = new ConcurrentHashMap<>();

	/**
	 * Adds a player to the tracked players map.
	 */
	public void addPlayer(cn.nukkit.Player nukkitPlayer) {
		players.put(nukkitPlayer.getUniqueId(), new PokkitFloodgatePlayer(nukkitPlayer));
	}

	/**
	 * Removes a player from the tracked players map.
	 */
	public void removePlayer(UUID uuid) {
		players.remove(uuid);
	}

	@Override
	public String getPlayerPrefix() {
		return ".";
	}

	@Override
	public Collection<FloodgatePlayer> getPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}

	@Override
	public int getPlayerCount() {
		return players.size();
	}

	@Override
	public boolean isFloodgatePlayer(UUID uuid) {
		return players.containsKey(uuid);
	}

	@Override
	public FloodgatePlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	@Override
	public UUID createJavaPlayerId(long xuid) {
		return new UUID(0, xuid);
	}

	@Override
	public boolean isFloodgateId(UUID uuid) {
		return uuid != null && uuid.getMostSignificantBits() == 0;
	}

	private cn.nukkit.Player getNukkitPlayer(UUID uuid) {
		return Server.getInstance().getOnlinePlayers().get(uuid);
	}

	@Override
	public boolean sendForm(UUID uuid, org.geysermc.cumulus.form.Form form) {
		cn.nukkit.Player player = getNukkitPlayer(uuid);
		if (player == null) return false;

		String jsonData = FormDefinitions.instance().codecFor(form).jsonData(form);
		Consumer<String> handler = data -> {
			try {
				String responseData = "null".equals(data) ? null : data;
				FormDefinitions.instance().definitionFor(form).handleFormResponse(form, responseData);
			} catch (Exception ignored) {
			}
		};

		return player.showFormWindow(new CumulusFormWindow(jsonData, handler)) != -1;
	}

	@Override
	public boolean sendForm(UUID uuid, org.geysermc.cumulus.form.util.FormBuilder<?, ?, ?> formBuilder) {
		return sendForm(uuid, formBuilder.build());
	}

	@Override
	public boolean closeForm(UUID uuid) {
		cn.nukkit.Player player = getNukkitPlayer(uuid);
		if (player == null) return false;
		player.closeFormWindows();
		return true;
	}

	@Override
	public boolean sendForm(UUID uuid, org.geysermc.cumulus.Form<?> form) {
		cn.nukkit.Player player = getNukkitPlayer(uuid);
		if (player == null) return false;

		String jsonData = form.getJsonData();
		Consumer<String> handler = data -> {
			Consumer<String> responseHandler = form.getResponseHandler();
			if (responseHandler != null) {
				responseHandler.accept(data);
			}
		};

		return player.showFormWindow(new CumulusFormWindow(jsonData, handler)) != -1;
	}

	@Override
	public boolean sendForm(UUID uuid, org.geysermc.cumulus.util.FormBuilder<?, ?> formBuilder) {
		return sendForm(uuid, formBuilder.build());
	}

	@Override
	public boolean transferPlayer(UUID uuid, String address, int port) {
		cn.nukkit.Player player = getNukkitPlayer(uuid);
		if (player == null) return false;
		player.transfer(address, port);
		return true;
	}

	@Override
	public CompletableFuture<Long> getXuidFor(String gamertag) {
		CompletableFuture<Long> future = new CompletableFuture<>();
		future.complete(null);
		return future;
	}

	@Override
	public CompletableFuture<String> getGamertagFor(long xuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		future.complete(null);
		return future;
	}

	private final PokkitUnsafe unsafe = new PokkitUnsafe();

	@Override
	public Unsafe unsafe() {
		return unsafe;
	}
}
