package nl.rutgerkok.pokkit;

import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;

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
	public float getDuration() {
		// Only the sing goat horn plays for two seconds, all other vanilla
		// instruments play for three.
		return "sing_goat_horn".equals(key.getKey()) ? 2.0f : 3.0f;
	}

	@Override
	public float getRange() {
		// All vanilla goat horn instruments can be heard 256 blocks away.
		return 256.0f;
	}

	@Override
	public String getDescription() {
		switch (key.getKey()) {
			case "ponder_goat_horn":
				return "Ponder";
			case "sing_goat_horn":
				return "Sing";
			case "seek_goat_horn":
				return "Seek";
			case "feel_goat_horn":
				return "Feel";
			case "admire_goat_horn":
				return "Admire";
			case "call_goat_horn":
				return "Call";
			case "yearn_goat_horn":
				return "Yearn";
			case "dream_goat_horn":
				return "Dream";
			default:
				return key.getKey();
		}
	}

	@Override
	public Sound getSoundEvent() {
		// The eight vanilla goat horn instruments map one-to-one onto the eight
		// goat horn sounds.
		switch (key.getKey()) {
			case "ponder_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_0;
			case "sing_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_1;
			case "seek_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_2;
			case "feel_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_3;
			case "admire_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_4;
			case "call_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_5;
			case "yearn_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_6;
			case "dream_goat_horn":
				return Sound.ITEM_GOAT_HORN_SOUND_7;
			default:
				throw Pokkit.unsupported();
		}
	}

	@Override
	public String toString() {
		return key.toString();
	}
}
