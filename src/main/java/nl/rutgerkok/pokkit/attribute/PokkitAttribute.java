package nl.rutgerkok.pokkit.attribute;

import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

/**
 * Attribute entry for the Bukkit attribute registry.
 */
public final class PokkitAttribute implements Attribute {

	private final NamespacedKey key;
	private final int ordinal;

	public PokkitAttribute(String key, int ordinal) {
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
	public String getTranslationKey() {
		return "attribute.name." + key.getKey();
	}

	@Override
	public int compareTo(Attribute other) {
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
