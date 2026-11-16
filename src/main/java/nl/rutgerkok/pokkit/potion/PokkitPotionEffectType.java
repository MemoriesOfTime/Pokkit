package nl.rutgerkok.pokkit.potion;

import java.util.Objects;

import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import cn.nukkit.potion.Effect;
import cn.nukkit.utils.ServerException;

/**
 * Converts potion effect types.
 *
 */
public class PokkitPotionEffectType {

	/**
	 * The id used for effects that do not exist in Bedrock edition. The
	 * regular Bedrock ids stop at 36, so the client ignores this one. It must
	 * not be one of the Java ids: Java id 24 (glowing) would otherwise be
	 * applied as levitation, 26 (luck) as conduit power, and so on.
	 */
	private static final int UNSUPPORTED_ID = 255;

	/**
	 * Gets the Bedrock effect id for the given Bukkit effect type.
	 *
	 * @param type
	 *            The Bukkit effect type.
	 * @return The Bedrock id, or 0 when Bedrock has no equivalent effect.
	 */
	public static int toNukkitId(PotionEffectType type) {
		Objects.requireNonNull(type, "type");
		if (type instanceof PokkitEffectType pokkitType) {
			return pokkitType.getNukkitId();
		}
		// The first 23 effects use the same id in Java and Bedrock. Plugins can
		// supply their own PotionEffectType implementations, so fall back to
		// that shared range only.
		int id = type.getId();
		return id >= 1 && id <= 23 ? id : 0;
	}

	/**
	 * Gets the Bukkit effect type for the given Bedrock effect id.
	 *
	 * @param id
	 *            The Bedrock effect id.
	 * @return The Bukkit effect type, or null when Bukkit has no equivalent
	 *         effect.
	 */
	public static PotionEffectType fromNukkitId(int id) {
		if (id <= 0) {
			return null;
		}
		for (PotionEffectType type : Registry.EFFECT) {
			if (type instanceof PokkitEffectType pokkitType && pokkitType.getNukkitId() == id) {
				return type;
			}
		}
		// Bedrock-only effects (fatal poison) end up here.
		return null;
	}

	@SuppressWarnings("deprecation")
	public static Effect toNukkit(PotionEffectType type) {
		int id = toNukkitId(type);
		if (id <= 0) {
			// Bedrock has no equivalent for this effect (glowing, luck, ...).
			return unsupported(type);
		}
		try {
			return Effect.getEffect(id);
		} catch (ServerException e) {
			return unsupported(type);
		}
	}

	private static Effect unsupported(PotionEffectType type) {
		return new Effect(UNSUPPORTED_ID, "bukkit_" + type.getKey().getKey(), 9, 0, 0);
	}

}
