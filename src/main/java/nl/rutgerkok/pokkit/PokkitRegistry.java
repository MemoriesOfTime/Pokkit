package nl.rutgerkok.pokkit;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public final class PokkitRegistry<T extends Keyed> implements Registry<T> {

	private final Map<NamespacedKey, T> map = new LinkedHashMap<>();

	public void register(T value) {
		map.put(value.getKey(), value);
	}

	@Override
	public Iterator<T> iterator() {
		return Collections.unmodifiableCollection(map.values()).iterator();
	}

	@Override
	public T get(NamespacedKey key) {
		return map.get(key);
	}

	@Override
	public T getOrThrow(NamespacedKey key) {
		Objects.requireNonNull(key, "key");
		T value = get(key);
		if (value == null) {
			throw new NoSuchElementException("No entry for key " + key);
		}
		return value;
	}

	@Override
	public Stream<T> stream() {
		return map.values().stream();
	}

	public int size() {
		return map.size();
	}
}
