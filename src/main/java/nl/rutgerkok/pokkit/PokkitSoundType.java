package nl.rutgerkok.pokkit;

import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;

/**
 * Sound entry for the Bukkit sound registry.
 */
public final class PokkitSoundType implements Sound {

	private final NamespacedKey key;
	private final int ordinal;

	public PokkitSoundType(String key, int ordinal) {
		this.key = NamespacedKey.minecraft(key);
		this.ordinal = ordinal;
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public NamespacedKey getKeyOrThrow() {
		return key;
	}

	@Override
	public NamespacedKey getKeyOrNull() {
		return key;
	}

	@Override
	public boolean isRegistered() {
		return true;
	}

	@Override
	public int compareTo(Sound other) {
		return key.toString().compareTo(other.getKey().toString());
	}

	@Override
	public String name() {
		return key.getKey().replace('.', '_').toUpperCase(Locale.ROOT);
	}

	@Override
	public int ordinal() {
		return ordinal;
	}

	@Override
	public String toString() {
		return name();
	}
}
