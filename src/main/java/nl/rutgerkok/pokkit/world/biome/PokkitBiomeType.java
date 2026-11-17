package nl.rutgerkok.pokkit.world.biome;

import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;

/**
 * Biome entry for the Bukkit biome registry.
 */
public final class PokkitBiomeType implements Biome {

	private final NamespacedKey key;
	private final int ordinal;

	public PokkitBiomeType(String key, int ordinal) {
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
	public int compareTo(Biome other) {
		return key.toString().compareTo(other.getKey().toString());
	}

	@Override
	public String name() {
		return key.getKey().toUpperCase(Locale.ROOT);
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
