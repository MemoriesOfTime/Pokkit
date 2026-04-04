package org.bukkit.craftbukkit.v1_99_R9;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public class SimpleMapRegistry<T extends Keyed> implements Registry<T> {

	private final Map<NamespacedKey, T> map = new HashMap<>();

	public void register(T value) {
		map.put(value.getKey(), value);
	}

	@Override
	public T get(NamespacedKey key) {
		return map.get(key);
	}

	@Override
	public Stream<T> stream() {
		return map.values().stream();
	}

	@Override
	public Iterator<T> iterator() {
		return Collections.unmodifiableCollection(map.values()).iterator();
	}
}
