package nl.rutgerkok.pokkit.damage;

import org.bukkit.Sound;
import org.bukkit.damage.DamageEffect;

/**
 * Damage effect, which only determines the sound that is played when an entity
 * takes damage.
 */
public final class PokkitDamageEffect implements DamageEffect {

	public static final PokkitDamageEffect HURT = new PokkitDamageEffect(Sound.ENTITY_PLAYER_HURT);
	public static final PokkitDamageEffect THORNS = new PokkitDamageEffect(Sound.ENCHANT_THORNS_HIT);
	public static final PokkitDamageEffect DROWNING = new PokkitDamageEffect(Sound.ENTITY_PLAYER_HURT_DROWN);
	public static final PokkitDamageEffect BURNING = new PokkitDamageEffect(Sound.ENTITY_PLAYER_HURT_ON_FIRE);
	public static final PokkitDamageEffect POKING = new PokkitDamageEffect(Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH);
	public static final PokkitDamageEffect FREEZING = new PokkitDamageEffect(Sound.ENTITY_PLAYER_HURT_FREEZE);

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

	private final Sound sound;

	private PokkitDamageEffect(Sound sound) {
		this.sound = sound;
	}

	@Override
	public Sound getSound() {
		return sound;
	}

	@Override
	public String toString() {
		return "PokkitDamageEffect{" + sound + "}";
	}
}
