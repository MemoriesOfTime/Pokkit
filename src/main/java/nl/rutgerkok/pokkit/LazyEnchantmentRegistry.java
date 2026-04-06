package nl.rutgerkok.pokkit;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

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
				Enchantment enchantment = new PokkitEnchantmentEntry(key);
				map.put(key, enchantment);
				return enchantment;
			}
		}
		return null;
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
			map.computeIfAbsent(namespacedKey, PokkitEnchantmentEntry::new);
		}
	}
}