package nl.rutgerkok.pokkit.potion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

/**
 * Registry for potion effect types.
 *
 * <p>
 * The entries are created on first use. Eager creation would recurse: loading
 * {@link PotionEffectType} initializes {@link Registry#EFFECT}, which asks this
 * registry for every type, which would load {@link PotionEffectType} again.
 * </p>
 */
public final class PokkitPotionEffectRegistry implements Registry<PotionEffectType> {

	private final Map<NamespacedKey, PotionEffectType> cache = new LinkedHashMap<>();

	@Override
	public PotionEffectType get(NamespacedKey key) {
		Objects.requireNonNull(key, "key");
		PotionEffectType cached = cache.get(key);
		if (cached != null) {
			return cached;
		}
		PotionEffectType created = PokkitEffectType.create(key);
		if (created != null) {
			cache.put(key, created);
		}
		return created;
	}

	@Override
	public PotionEffectType getOrThrow(NamespacedKey key) {
		Objects.requireNonNull(key, "key");
		PotionEffectType value = get(key);
		if (value == null) {
			throw new NoSuchElementException("No entry for key " + key);
		}
		return value;
	}

	@Override
	public Iterator<PotionEffectType> iterator() {
		List<PotionEffectType> types = new ArrayList<>();
		for (NamespacedKey key : PokkitEffectType.keys()) {
			PotionEffectType type = get(key);
			if (type != null) {
				types.add(type);
			}
		}
		return types.iterator();
	}

	@Override
	public Stream<PotionEffectType> stream() {
		List<PotionEffectType> types = new ArrayList<>();
		iterator().forEachRemaining(types::add);
		return types.stream();
	}
}
