package nl.rutgerkok.pokkit;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

/**
 * Registry that creates its entries on first use.
 *
 * <p>
 * Some Bukkit interfaces (such as {@code Sound}, {@code Attribute} and
 * {@code MenuType}) fill their static constants from a registry while the
 * interface itself is still being initialised. Creating the entries eagerly
 * would initialise those interfaces too early and make the registry field they
 * read still {@code null}. This registry avoids that by only constructing an
 * entry when it is actually requested.
 *
 * @param <T>
 *            The type of the entries.
 */
final class LazyFactoryRegistry<T extends Keyed> implements Registry<T> {

	private final Map<NamespacedKey, String> keys = new LinkedHashMap<>();
	private final Map<NamespacedKey, T> cache = new LinkedHashMap<>();
	private final BiFunction<String, Integer, T> factory;

	LazyFactoryRegistry(String[] keys, BiFunction<String, Integer, T> factory) {
		this.factory = factory;
		for (int i = 0; i < keys.length; i++) {
			this.keys.put(NamespacedKey.minecraft(keys[i]), keys[i]);
		}
	}

	@Override
	public T get(NamespacedKey key) {
		Objects.requireNonNull(key, "key");
		T cached = cache.get(key);
		if (cached != null) {
			return cached;
		}
		String keyName = keys.get(key);
		if (keyName == null) {
			return null;
		}
		T created = factory.apply(keyName, indexOf(key));
		cache.put(key, created);
		return created;
	}

	@Override
	public T getOrThrow(NamespacedKey key) {
		T value = get(key);
		if (value == null) {
			throw new NoSuchElementException("No entry for key " + key);
		}
		return value;
	}

	private int indexOf(NamespacedKey key) {
		int index = 0;
		for (NamespacedKey candidate : keys.keySet()) {
			if (candidate.equals(key)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public Iterator<T> iterator() {
		Map<NamespacedKey, T> all = new LinkedHashMap<>();
		for (NamespacedKey key : keys.keySet()) {
			T value = get(key);
			if (value != null) {
				all.put(key, value);
			}
		}
		return all.values().iterator();
	}

	@Override
	public Stream<T> stream() {
		Map<NamespacedKey, T> all = new LinkedHashMap<>();
		for (NamespacedKey key : keys.keySet()) {
			T value = get(key);
			if (value != null) {
				all.put(key, value);
			}
		}
		return all.values().stream();
	}
}
