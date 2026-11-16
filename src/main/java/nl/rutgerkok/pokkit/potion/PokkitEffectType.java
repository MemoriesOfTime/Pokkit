package nl.rutgerkok.pokkit.potion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;

/**
 * A potion effect type backed by a Bedrock effect id.
 *
 * <p>
 * Bukkit's {@link PotionEffectType} constants are created from
 * {@link org.bukkit.Registry#EFFECT} while that class is being initialized, so
 * these instances must not touch the static fields of
 * {@link PotionEffectType} themselves.
 * </p>
 */
public final class PokkitEffectType extends PotionEffectType {

	private static final class Definition {

		private final int id;
		private final int nukkitId;
		private final String key;
		private final int rgb;
		private final PotionEffectTypeCategory category;
		private final boolean instant;

		private Definition(int id, int nukkitId, String key, int rgb, PotionEffectTypeCategory category,
				boolean instant) {
			this.id = id;
			this.nukkitId = nukkitId;
			this.key = key;
			this.rgb = rgb;
			this.category = category;
			this.instant = instant;
		}
	}

	/*
	 * The first id is the Java edition id used by PotionEffectType#getId(),
	 * the second one the Bedrock id used by Nukkit. They only differ for the
	 * effects added after saturation.
	 */
	private static final Definition[] DEFINITIONS = {
			new Definition(1, 1, "speed", 0x7CAFC6, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(2, 2, "slowness", 0x5A6C81, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(3, 3, "haste", 0xD9A600, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(4, 4, "mining_fatigue", 0x4A4217, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(5, 5, "strength", 0x932423, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(6, 6, "instant_health", 0xF82423, PotionEffectTypeCategory.BENEFICIAL, true),
			new Definition(7, 7, "instant_damage", 0x430A09, PotionEffectTypeCategory.HARMFUL, true),
			new Definition(8, 8, "jump_boost", 0x22FF4C, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(9, 9, "nausea", 0x551D4A, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(10, 10, "regeneration", 0xCD5CAB, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(11, 11, "resistance", 0x9146F0, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(12, 12, "fire_resistance", 0xFF9900, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(13, 13, "water_breathing", 0x2E5299, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(14, 14, "invisibility", 0x7F8392, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(15, 15, "blindness", 0x1F1F23, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(16, 16, "night_vision", 0x1F1FA1, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(17, 17, "hunger", 0x587653, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(18, 18, "weakness", 0x484D48, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(19, 19, "poison", 0x4E9331, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(20, 20, "wither", 0x352A27, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(21, 21, "health_boost", 0xF87D23, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(22, 22, "absorption", 0x2552A5, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(23, 23, "saturation", 0xF82423, PotionEffectTypeCategory.BENEFICIAL, true),
			new Definition(24, 0, "glowing", 0x94A061, PotionEffectTypeCategory.NEUTRAL, false),
			new Definition(25, 24, "levitation", 0xCEFFFF, PotionEffectTypeCategory.NEUTRAL, false),
			new Definition(26, 0, "luck", 0x339900, PotionEffectTypeCategory.NEUTRAL, false),
			new Definition(27, 0, "unluck", 0xC0A44D, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(28, 27, "slow_falling", 0xF7E7E6, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(29, 26, "conduit_power", 0x1DC2D1, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(30, 0, "dolphins_grace", 0x88A3BE, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(31, 28, "bad_omen", 0x0B6138, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(32, 29, "hero_of_the_village", 0x44FF44, PotionEffectTypeCategory.BENEFICIAL, false),
			new Definition(33, 30, "darkness", 0x1C1C1C, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(34, 31, "trial_omen", 0x16A1A1, PotionEffectTypeCategory.NEUTRAL, false),
			new Definition(35, 36, "raid_omen", 0xDE4058, PotionEffectTypeCategory.NEUTRAL, false),
			new Definition(36, 32, "wind_charged", 0xBDC9FF, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(37, 33, "weaving", 0x78695A, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(38, 34, "oozing", 0x99FF99, PotionEffectTypeCategory.HARMFUL, false),
			new Definition(39, 35, "infested", 0x8C9B8C, PotionEffectTypeCategory.HARMFUL, false) };

	/**
	 * Creates the effect type for the given key, or returns {@code null} when
	 * the key is not a known potion effect.
	 *
	 * @param key
	 *            The key.
	 * @return The effect type, or {@code null}.
	 */
	static PotionEffectType create(NamespacedKey key) {
		if (!"minecraft".equals(key.getNamespace())) {
			return null;
		}
		for (Definition definition : DEFINITIONS) {
			if (definition.key.equals(key.getKey())) {
				return new PokkitEffectType(definition);
			}
		}
		return null;
	}

	/**
	 * Returns all keys that can be created.
	 *
	 * @return All keys.
	 */
	static List<NamespacedKey> keys() {
		List<NamespacedKey> keys = new ArrayList<>(DEFINITIONS.length);
		for (Definition definition : DEFINITIONS) {
			keys.add(NamespacedKey.minecraft(definition.key));
		}
		return keys;
	}

	private final int id;
	private final int nukkitId;
	private final NamespacedKey key;
	private final Color color;
	private final PotionEffectTypeCategory category;
	private final boolean instant;

	private PokkitEffectType(Definition definition) {
		this.id = definition.id;
		this.nukkitId = definition.nukkitId;
		this.key = NamespacedKey.minecraft(definition.key);
		this.color = Color.fromRGB(definition.rgb);
		this.category = definition.category;
		this.instant = definition.instant;
	}

	@Override
	public PotionEffect createEffect(int duration, int amplifier) {
		return new PotionEffect(this, duration, amplifier);
	}

	@Override
	public PotionEffectTypeCategory getCategory() {
		return category;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public double getDurationModifier() {
		return 1.0;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getName() {
		return key.getKey();
	}

	@Override
	public String getTranslationKey() {
		return "effect.minecraft." + key.getKey();
	}

	/**
	 * Returns the Bedrock effect id, or 0 when Bedrock has no equivalent
	 * effect.
	 *
	 * @return The Bedrock effect id.
	 */
	public int getNukkitId() {
		return nukkitId;
	}

	@Override
	public boolean isInstant() {
		return instant;
	}
}
