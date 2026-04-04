package nl.rutgerkok.pokkit.floodgate;

import cn.nukkit.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.api.player.PropertyKey;
import org.geysermc.floodgate.util.DeviceOs;
import org.geysermc.floodgate.util.InputMode;
import org.geysermc.floodgate.util.LinkedPlayer;
import org.geysermc.floodgate.util.UiProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wraps a Nukkit Player as a FloodgatePlayer.
 * All players on Nukkit-MOT are Bedrock players.
 */
public final class PokkitFloodgatePlayer implements FloodgatePlayer {

	private final Player nukkitPlayer;
	private final Map<String, Object> properties = new ConcurrentHashMap<>();

	PokkitFloodgatePlayer(Player nukkitPlayer) {
		this.nukkitPlayer = nukkitPlayer;
	}

	@Override
	public String getJavaUsername() {
		return nukkitPlayer.getName();
	}

	@Override
	public UUID getJavaUniqueId() {
		return nukkitPlayer.getUniqueId();
	}

	@Override
	public UUID getCorrectUniqueId() {
		return nukkitPlayer.getUniqueId();
	}

	@Override
	public String getCorrectUsername() {
		return nukkitPlayer.getName();
	}

	@Override
	public String getVersion() {
		return nukkitPlayer.getLoginChainData().getGameVersion();
	}

	@Override
	public String getUsername() {
		return nukkitPlayer.getLoginChainData().getUsername();
	}

	@Override
	public String getXuid() {
		String xuid = nukkitPlayer.getLoginChainData().getXUID();
		return xuid != null ? xuid : "";
	}

	@Override
	public DeviceOs getDeviceOs() {
		return DeviceOs.fromId(nukkitPlayer.getLoginChainData().getDeviceOS());
	}

	@Override
	public String getLanguageCode() {
		return nukkitPlayer.getLoginChainData().getLanguageCode();
	}

	@Override
	public UiProfile getUiProfile() {
		return UiProfile.fromId(nukkitPlayer.getLoginChainData().getUIProfile());
	}

	@Override
	public InputMode getInputMode() {
		return InputMode.fromId(nukkitPlayer.getLoginChainData().getCurrentInputMode());
	}

	@Override
	public boolean isFromProxy() {
		return nukkitPlayer.getLoginChainData().getWaterdogXUID() != null;
	}

	@Override
	public LinkedPlayer getLinkedPlayer() {
		return null;
	}

	@Override
	public boolean isLinked() {
		return false;
	}

	@Override
	public boolean hasProperty(PropertyKey key) {
		return key != null && properties.containsKey(key.getKey());
	}

	@Override
	public boolean hasProperty(String key) {
		return key != null && properties.containsKey(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(PropertyKey key) {
		if (key == null) return null;
		return (T) properties.get(key.getKey());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key) {
		if (key == null) return null;
		return (T) properties.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T removeProperty(PropertyKey key) {
		if (key == null) return null;
		return (T) properties.remove(key.getKey());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T removeProperty(String key) {
		if (key == null) return null;
		return (T) properties.remove(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T addProperty(PropertyKey key, Object value) {
		if (key == null) return null;
		return (T) properties.put(key.getKey(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T addProperty(String key, Object value) {
		if (key == null) return null;
		return (T) properties.put(key, value);
	}
}
