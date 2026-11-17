package nl.rutgerkok.pokkit;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import nl.rutgerkok.pokkit.enchantment.PokkitEnchantment;
import nl.rutgerkok.pokkit.enchantment.PokkitEnchantmentEntry;

/**
 * Lazy Enchantment registry that avoids triggering Enchantment class
 * initialization during Registry initialization (which would cause a circular
 * dependency: Registry.&lt;clinit&gt; -&gt; getRegistry(Enchantment) -&gt;
 * PokkitEnchantmentEntry extends Enchantment -&gt; Enchantment.&lt;clinit&gt; -&gt;
 * Registry.ENCHANTMENT (still null)).
 */
public final class LazyEnchantmentRegistry implements Registry<Enchantment> {

	private final Map<NamespacedKey, Enchantment> map = new LinkedHashMap<>();
	private final String[] keys;

	public LazyEnchantmentRegistry(String[] keys) {
		this.keys = keys;
	}

	@Override
	public Enchantment get(NamespacedKey key) {
		Enchantment existing = map.get(key);
		if (existing != null) {
			return existing;
		}
		for (String k : keys) {
			if (NamespacedKey.minecraft(k).equals(key)) {
				// Constructing the entry may initialize the Enchantment
				// class, whose static initializer registers all enchantments
				// here. Use putIfAbsent so that the instance created by that
				// initializer is kept, instead of replacing it with ours.
				Enchantment created = new PokkitEnchantmentEntry(key,
						PokkitEnchantment.getNukkitEnchantment(key));
				Enchantment registered = map.putIfAbsent(key, created);
				return registered != null ? registered : created;
			}
		}
		return null;
	}

	@Override
	public Enchantment getOrThrow(NamespacedKey key) {
		Objects.requireNonNull(key, "key");
		Enchantment enchantment = get(key);
		if (enchantment == null) {
			throw new NoSuchElementException("No enchantment for key " + key);
		}
		return enchantment;
	}

	@Override
	public Iterator<Enchantment> iterator() {
		ensureAllLoaded();
		return map.values().iterator();
	}

	@Override
	public Stream<Enchantment> stream() {
		ensureAllLoaded();
		return map.values().stream();
	}

	private void ensureAllLoaded() {
		for (String key : keys) {
			NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
			if (map.containsKey(namespacedKey)) {
				continue;
			}
			// Do not use computeIfAbsent here: creating the entry may
			// initialize Enchantment, whose static initializer calls back
			// into this map, which computeIfAbsent rejects with a
			// ConcurrentModificationException.
			Enchantment created = new PokkitEnchantmentEntry(namespacedKey,
					PokkitEnchantment.getNukkitEnchantment(namespacedKey));
			map.putIfAbsent(namespacedKey, created);
		}
	}
}
