package nl.rutgerkok.pokkit.tag;

import java.util.Collections;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

/**
 * Empty tag implementation for unsupported registries (fluids, entity_types).
 */
public class PokkitEmptyTag<T extends org.bukkit.Keyed> implements Tag<T> {

	private final NamespacedKey key;

	public PokkitEmptyTag(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public boolean isTagged(T item) {
		return false;
	}

	@Override
	public Set<T> getValues() {
		return Collections.emptySet();
	}
}
