package nl.rutgerkok.pokkit.damage;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.damage.DamageEffect;

/**
 * Damage effect, which only determines the sound that is played when an entity
 * takes damage.
 */
public final class PokkitDamageEffect implements DamageEffect {

	// Stored as keys instead of Sound constants: loading Sound initializes
	// Registry, which asks for the damage type registry while it is still
	// being initialized. Resolving the constants here would then fail with an
	// ExceptionInInitializerError.
	public static final PokkitDamageEffect HURT = new PokkitDamageEffect("entity.player.hurt");
	public static final PokkitDamageEffect THORNS = new PokkitDamageEffect("enchant.thorns.hit");
	public static final PokkitDamageEffect DROWNING = new PokkitDamageEffect("entity.player.hurt_drown");
	public static final PokkitDamageEffect BURNING = new PokkitDamageEffect("entity.player.hurt_on_fire");
	public static final PokkitDamageEffect POKING = new PokkitDamageEffect("entity.player.hurt_sweet_berry_bush");
	public static final PokkitDamageEffect FREEZING = new PokkitDamageEffect("entity.player.hurt_freeze");

	/**
	 * Gets the damage effect with the given name.
	 *
	 * @param name
	 *            The name, as used by {@code UnsafeValues#getDamageEffect}.
	 * @return The damage effect, or null if the name is unknown.
	 */
	public static PokkitDamageEffect getByName(String name) {
		if (name == null) {
			return null;
		}
		switch (name.toLowerCase(java.util.Locale.ROOT)) {
		case "hurt":
			return HURT;
		case "thorns":
			return THORNS;
		case "drowning":
			return DROWNING;
		case "burning":
			return BURNING;
		case "poking":
			return POKING;
		case "freezing":
			return FREEZING;
		default:
			return null;
		}
	}

	private final String soundKey;

	private PokkitDamageEffect(String soundKey) {
		this.soundKey = soundKey;
	}

	@Override
	public Sound getSound() {
		return Registry.SOUNDS.get(NamespacedKey.minecraft(soundKey));
	}

	@Override
	public String toString() {
		return "PokkitDamageEffect{" + soundKey + "}";
	}
}
