package nl.rutgerkok.pokkit;

import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;

public final class PokkitMusicInstrument extends MusicInstrument {

	private final NamespacedKey key;

	public PokkitMusicInstrument(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key.toString();
	}
}
