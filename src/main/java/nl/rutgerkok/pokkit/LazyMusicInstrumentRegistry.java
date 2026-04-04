package nl.rutgerkok.pokkit;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

/**
 * Lazy MusicInstrument registry that avoids triggering MusicInstrument class
 * initialization during Registry initialization (which would cause a circular
 * dependency: Registry.<clinit> -> getRegistry(MusicInstrument) ->
 * PokkitMusicInstrument extends MusicInstrument -> MusicInstrument.<clinit> ->
 * Registry.INSTRUMENT (still null)).
 */
public final class LazyMusicInstrumentRegistry implements Registry<MusicInstrument> {

	private final Map<NamespacedKey, MusicInstrument> map = new LinkedHashMap<>();
	private final String[] keys;

	public LazyMusicInstrumentRegistry(String[] keys) {
		this.keys = keys;
	}

	@Override
	public MusicInstrument get(NamespacedKey key) {
		MusicInstrument existing = map.get(key);
		if (existing != null) {
			return existing;
		}
		for (String k : keys) {
			if (NamespacedKey.minecraft(k).equals(key)) {
				MusicInstrument instrument = new PokkitMusicInstrument(key);
				map.put(key, instrument);
				return instrument;
			}
		}
		return null;
	}

	@Override
	public Iterator<MusicInstrument> iterator() {
		ensureAllLoaded();
		return map.values().iterator();
	}

	@Override
	public Stream<MusicInstrument> stream() {
		ensureAllLoaded();
		return map.values().stream();
	}

	private void ensureAllLoaded() {
		for (String key : keys) {
			NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
			map.computeIfAbsent(namespacedKey, PokkitMusicInstrument::new);
		}
	}
}
