package nl.rutgerkok.pokkit.item;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Item meta for potions. Nukkit (like Bedrock) stores the potion type in the
 * damage value of the item, so this class translates between that value and
 * {@link PotionType}.
 */
public final class PokkitPotionMeta extends PokkitItemMeta implements PotionMeta {

	private static final Map<PotionType, Integer> TYPE_TO_ID = new EnumMap<>(PotionType.class);
	private static final Map<Integer, PotionType> ID_TO_TYPE = new HashMap<>();

	static {
		map(PotionType.WATER, 0);
		map(PotionType.MUNDANE, 1);
		map(PotionType.THICK, 3);
		map(PotionType.AWKWARD, 4);

		map(PotionType.NIGHT_VISION, 5);
		map(PotionType.LONG_NIGHT_VISION, 6);
		map(PotionType.INVISIBILITY, 7);
		map(PotionType.LONG_INVISIBILITY, 8);

		map(PotionType.LEAPING, 9);
		map(PotionType.LONG_LEAPING, 10);
		map(PotionType.STRONG_LEAPING, 11);
		map(PotionType.FIRE_RESISTANCE, 12);
		map(PotionType.LONG_FIRE_RESISTANCE, 13);

		map(PotionType.SWIFTNESS, 14);
		map(PotionType.LONG_SWIFTNESS, 15);
		map(PotionType.STRONG_SWIFTNESS, 16);
		map(PotionType.SLOWNESS, 17);
		map(PotionType.LONG_SLOWNESS, 18);
		map(PotionType.STRONG_SLOWNESS, 42);
		map(PotionType.WATER_BREATHING, 19);
		map(PotionType.LONG_WATER_BREATHING, 20);

		map(PotionType.HEALING, 21);
		map(PotionType.STRONG_HEALING, 22);
		map(PotionType.HARMING, 23);
		map(PotionType.STRONG_HARMING, 24);

		map(PotionType.POISON, 25);
		map(PotionType.LONG_POISON, 26);
		map(PotionType.STRONG_POISON, 27);
		map(PotionType.REGENERATION, 28);
		map(PotionType.LONG_REGENERATION, 29);
		map(PotionType.STRONG_REGENERATION, 30);
		map(PotionType.STRENGTH, 31);
		map(PotionType.LONG_STRENGTH, 32);
		map(PotionType.STRONG_STRENGTH, 33);
		map(PotionType.WEAKNESS, 34);
		map(PotionType.LONG_WEAKNESS, 35);

		map(PotionType.TURTLE_MASTER, 37);
		map(PotionType.LONG_TURTLE_MASTER, 38);
		map(PotionType.STRONG_TURTLE_MASTER, 39);
		map(PotionType.SLOW_FALLING, 40);
		map(PotionType.LONG_SLOW_FALLING, 41);

		map(PotionType.WIND_CHARGED, 43);
		map(PotionType.WEAVING, 44);
		map(PotionType.OOZING, 45);
		map(PotionType.INFESTED, 46);
	}

	private static void map(PotionType type, int id) {
		TYPE_TO_ID.put(type, id);
		ID_TO_TYPE.put(id, type);
	}

	PokkitPotionMeta(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
		// Bedrock potions cannot carry custom effects in item data.
		return false;
	}

	@Override
	public boolean clearCustomEffects() {
		return false;
	}

	@Override
	public PokkitPotionMeta clone() {
		return (PokkitPotionMeta) super.clone();
	}

	@Override
	@Deprecated
	public PotionData getBasePotionData() {
		PotionType type = getBasePotionType();
		if (type == null) {
			return new PotionData(PotionType.WATER);
		}
		// PotionData cannot be constructed from an extended or upgraded type:
		// it wants the base type plus the two flags.
		String name = type.name();
		boolean upgraded = name.startsWith("STRONG_");
		boolean extended = !upgraded && name.startsWith("LONG_");
		if (upgraded || extended) {
			PotionType base = potionTypeOrNull(name.substring(upgraded ? 7 : 5));
			if (base == null) {
				return new PotionData(PotionType.WATER);
			}
			try {
				return new PotionData(base, extended, upgraded);
			} catch (IllegalArgumentException notSupported) {
				// The base type is not marked as extendable or upgradeable in
				// this API version, so the flags cannot be represented.
				return new PotionData(base);
			}
		}
		return new PotionData(type);
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public List<PotionEffect> getCustomEffects() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasColor() {
		return false;
	}

	@Override
	public boolean hasCustomEffect(PotionEffectType type) {
		return false;
	}

	@Override
	public boolean hasCustomEffects() {
		return false;
	}

	@Override
	boolean isApplicable(Material material) {
		return material == Material.POTION || material == Material.SPLASH_POTION
				|| material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW
				|| material == Material.LEGACY_POTION || material == Material.LEGACY_SPLASH_POTION
				|| material == Material.LEGACY_LINGERING_POTION || material == Material.LEGACY_TIPPED_ARROW;
	}

	@Override
	public boolean removeCustomEffect(PotionEffectType type) {
		return false;
	}

	@Override
	@Deprecated
	public void setBasePotionData(PotionData data) {
		if (data == null) {
			setBasePotionType(null);
			return;
		}
		// Extended and upgraded potions are separate potion types in the
		// current API, so the flags of PotionData have to be folded back into
		// the type.
		if (data.isUpgraded() || data.isExtended()) {
			PotionType variant = potionTypeOrNull(
					(data.isUpgraded() ? "STRONG_" : "LONG_") + data.getType().name());
			if (variant != null) {
				setBasePotionType(variant);
				return;
			}
		}
		setBasePotionType(data.getType());
	}

	/**
	 * Gets a potion type by enum name, without throwing for unknown names.
	 *
	 * @param name
	 *            The enum name.
	 * @return The potion type, or null if it does not exist.
	 */
	private static PotionType potionTypeOrNull(String name) {
		try {
			return PotionType.valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void setColor(Color color) {
		// Silently unsupported: Bedrock has no custom potion color.
	}

	@Override
	public boolean setMainEffect(PotionEffectType type) {
		return false;
	}

	@Override
	public boolean hasBasePotionType() {
		// A potion without potion data is a water potion; CraftBukkit only
		// reports a base type if one was explicitly stored.
		return this.damage != 0;
	}

	@Override
	public PotionType getBasePotionType() {
		return ID_TO_TYPE.get(this.damage);
	}

	@Override
	public void setBasePotionType(PotionType type) {
		if (type == null) {
			this.damage = 0;
			return;
		}
		Integer id = TYPE_TO_ID.get(type);
		if (id == null) {
			// Bedrock has no equivalent (for example luck), so keep the
			// current type instead of silently turning into water.
			return;
		}
		this.damage = id;
	}

	@Override
	public String getAsComponentString() {
		// The damage field holds the potion type, not durability, so it must
		// not be exposed as the minecraft:damage component.
		return "[]";
	}


	/**
	 * Recreates the item meta from its serialized form. Required because
	 * Bukkit's configuration serialization looks for a method declared on the
	 * concrete class.
	 *
	 * @param args
	 *            The serialized data.
	 * @return The item meta.
	 */
	public static org.bukkit.inventory.meta.ItemMeta deserialize(java.util.Map<String, Object> args) {
		return PokkitItemMeta.deserialize(args);
	}
}
