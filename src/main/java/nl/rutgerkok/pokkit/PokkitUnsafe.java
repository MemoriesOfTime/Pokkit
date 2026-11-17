package nl.rutgerkok.pokkit;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;

import cn.nukkit.item.Item;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.item.ItemMap;
import nl.rutgerkok.pokkit.item.PokkitItemMeta;

@SuppressWarnings("deprecation")
public class PokkitUnsafe implements UnsafeValues {

	/**
	 * Maps Nukkit item ids to a legacy material with the same id, used to
	 * translate modern materials back to legacy ones. Built lazily, because it
	 * requires the item tables to be initialised.
	 */
	private static volatile Map<Integer, Material> legacyByNukkitId;

	/**
	 * Legacy material names that cannot be derived from the modern name by
	 * simply removing the {@code LEGACY_} prefix.
	 */
	private static final Map<String, Material> LEGACY_NAME_ALIASES = Map.of(
			"INK_SACK", Material.INK_SAC,
			"MONSTER_EGG", Material.CHICKEN_SPAWN_EGG,
			"FIREWORK_CHARGE", Material.FIREWORK_STAR,
			"POWERED_MINECART", Material.FURNACE_MINECART);

	@Override
	public void checkSupported(PluginDescriptionFile pdf) {
		// Empty!
	}

	@Override
	public Material fromLegacy(Material material) {
		if (material == null || !material.isLegacy()) {
			return material;
		}
		return fromLegacy(new MaterialData(material, (byte) 0), true);
	}

	@Override
	public BlockData fromLegacy(Material material, byte data) {
		return PokkitBlockData.createBlockData(material, data);
	}

	/**
	 * Translates a legacy material and its data value to the modern material
	 * that represents the same item. {@code new ItemStack(Material.LEGACY_WOOL,
	 * 1, (short) 14)} must for example become {@code RED_WOOL}, not white wool.
	 *
	 * <p>
	 * The conversion reuses the item tables: the legacy material is converted
	 * to a Nukkit item, the data value is applied as item damage, and the
	 * resulting item is converted back to a material.
	 */
	@Override
	public Material fromLegacy(MaterialData material) {
		return fromLegacy(material, true);
	}

	@Override
	public Material fromLegacy(MaterialData material, boolean itemPriority) {
		if (material == null) {
			return null;
		}
		Material legacy = material.getItemType();
		if (legacy == null || !legacy.isLegacy()) {
			return legacy;
		}
		Item nukkit = ItemMap.fromBukkit(legacy);
		if (nukkit.getId() == Item.AIR) {
			// Not every legacy material has an entry in the item table, but
			// many of them share a name with a modern material.
			Material modernName = modernNameFor(legacy);
			if (modernName != null) {
				nukkit = ItemMap.fromBukkit(modernName);
			}
		}
		if (nukkit.getId() == Item.AIR) {
			// No Nukkit equivalent, keep the legacy material.
			return legacy;
		}
		nukkit.setDamage(material.getData() & 0xFF);
		Material modern = ItemMap.fromNukkitOrNull(nukkit);
		return modern != null ? modern : legacy;
	}

	/**
	 * Derives the modern material that has the same name as the given legacy
	 * material, ignoring the {@code LEGACY_} prefix.
	 *
	 * @param legacy
	 *            The legacy material.
	 * @return The modern material, or null if there is none.
	 */
	private static Material modernNameFor(Material legacy) {
		String name = legacy.name().substring(Material.LEGACY_PREFIX.length());
		Material alias = LEGACY_NAME_ALIASES.get(name);
		if (alias != null) {
			return alias;
		}
		Material modern = Material.matchMaterial(name);
		return modern != null && !modern.isLegacy() ? modern : null;
	}

	@Override
	public Material getMaterial(String name, int version) {
		if (name == null) {
			throw new IllegalArgumentException("Material name cannot be null");
		}
		// Like CraftBukkit, only modern material names are accepted here.
		// Configurations from before 1.13 are handled by the caller through
		// the legacy conversion path (version < 0).
		Material material = Material.matchMaterial(name);
		if (material == null) {
			throw new IllegalArgumentException("No material for " + name);
		}
		return material;
	}

	@Override
	public int getDataVersion() {
		return 1;
	}

	@Override
	public Advancement loadAdvancement(NamespacedKey key, String advancementJson) {
		throw Pokkit.unsupported();
	}

	@Override
	public ItemStack modifyItemStack(ItemStack stack, String arguments) {
		// NBT not yet supported
		if (stack == null || stack.getType() == Material.AIR) {
			return stack;
		}
		ItemMeta itemMeta = stack.getItemMeta();
		if (itemMeta instanceof PokkitItemMeta pokkitMeta) {
			pokkitMeta.getTag().putString("Unknown", arguments);
			stack.setItemMeta(itemMeta);
		}
		return stack;
	}

	@Override
	public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
		return clazz; // Not implemented yet
	}

	@Override
	public boolean removeAdvancement(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public Material toLegacy(Material material) {
		if (material == null || material.isLegacy()) {
			return material;
		}
		// Most materials simply gained a LEGACY_ prefix.
		Material direct = Material.matchMaterial(Material.LEGACY_PREFIX + material.name());
		if (direct != null) {
			return direct;
		}
		// A few materials were renamed instead.
		Material alias = legacyAliasFor(material);
		if (alias != null) {
			return alias;
		}
		// Coloured variants (wool, carpet, concrete, ...) and renamed
		// materials have no one-to-one legacy name; fall back to the legacy
		// material with the same Nukkit item id.
		Material byId = legacyForNukkitId(ItemMap.fromBukkit(material));
		return byId != null ? byId : material;
	}

	/**
	 * Gets the legacy material that was renamed to the given modern material.
	 *
	 * @param material
	 *            The modern material.
	 * @return The legacy material, or null if the name did not change.
	 */
	private static Material legacyAliasFor(Material material) {
		if (material.name().endsWith("_SPAWN_EGG")) {
			return Material.LEGACY_MONSTER_EGG;
		}
		for (Map.Entry<String, Material> entry : LEGACY_NAME_ALIASES.entrySet()) {
			if (entry.getValue() == material) {
				return Material.matchMaterial(Material.LEGACY_PREFIX + entry.getKey());
			}
		}
		return null;
	}

	/**
	 * Gets a legacy material that maps to the same Nukkit item as the given
	 * item.
	 *
	 * @param item
	 *            The Nukkit item.
	 * @return The legacy material, or null if there is none.
	 */
	private static Material legacyForNukkitId(Item item) {
		if (item.getId() == Item.AIR) {
			return null;
		}
		Map<Integer, Material> map = legacyByNukkitId;
		if (map == null) {
			map = new HashMap<>();
			for (Material material : Material.values()) {
				if (!material.isLegacy() || material == Material.LEGACY_AIR) {
					continue;
				}
				Item legacyItem = ItemMap.fromBukkit(material);
				if (legacyItem.getId() != Item.AIR) {
					map.putIfAbsent(legacyItem.getId(), material);
				}
			}
			legacyByNukkitId = map;
		}
		return map.get(item.getId());
	}

	@Override
	public <B extends org.bukkit.Keyed> B get(org.bukkit.Registry<B> registry, org.bukkit.NamespacedKey key) {
		return registry == null ? null : registry.get(key);
	}

	@Override
	public String get(Class<?> type, String name) {
		return null;
	}

	@Override
	public com.google.common.collect.Multimap<org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier> getDefaultAttributeModifiers(Material material, org.bukkit.inventory.EquipmentSlot slot) {
		// Returning an empty map would silently report that the item has no
		// default modifiers, which is wrong for almost every tool and armour
		// piece. Throw instead, like the rest of the unimplemented API.
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.inventory.CreativeCategory getCreativeCategory(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getBlockTranslationKey(Material material) {
		return material == null ? null : "block." + material.getKey().getNamespace() + "."
				+ material.getKey().getKey();
	}

	@Override
	public String getItemTranslationKey(Material material) {
		return material == null ? null : "item." + material.getKey().getNamespace() + "."
				+ material.getKey().getKey();
	}

	@Override
	public String getTranslationKey(org.bukkit.entity.EntityType entityType) {
		return entityType == null ? null : "entity." + entityType.getKey().getNamespace() + "."
				+ entityType.getKey().getKey();
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		return itemStack == null ? null : getItemTranslationKey(itemStack.getType());
	}

	@Override
	public String getTranslationKey(org.bukkit.attribute.Attribute attribute) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.FeatureFlag getFeatureFlag(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.potion.PotionType.InternalPotionData getInternalPotionData(NamespacedKey key) {
		if (key == null) {
			return null;
		}
		return new InternalPotionData(key.getKey());
	}

	/**
	 * Minimal potion data implementation. Only the properties that can be
	 * derived from the potion type itself are filled in; the effect type is
	 * left null because the potion effect registry is not available.
	 */
	private static final class InternalPotionData implements org.bukkit.potion.PotionType.InternalPotionData {

		private final String key;

		InternalPotionData(String key) {
			this.key = key;
		}

		@Override
		public org.bukkit.potion.PotionEffectType getEffectType() {
			// The effect registry is not implemented, so no effect can be
			// returned. PotionType#getEffectType is documented as nullable.
			return null;
		}

		@Override
		public java.util.List<org.bukkit.potion.PotionEffect> getPotionEffects() {
			// Durations and amplifiers are not tracked, an empty list is
			// better than wrong data.
			return java.util.Collections.emptyList();
		}

		@Override
		public boolean isInstant() {
			String base = baseKey();
			return "healing".equals(base) || "harming".equals(base);
		}

		@Override
		public boolean isUpgradeable() {
			return isBaseType() && exists("strong_" + key);
		}

		@Override
		public boolean isExtendable() {
			return isBaseType() && exists("long_" + key);
		}

		@Override
		public int getMaxLevel() {
			return isUpgradeable() ? 2 : 1;
		}

		private boolean isBaseType() {
			return !key.startsWith("long_") && !key.startsWith("strong_");
		}

		private String baseKey() {
			if (key.startsWith("long_")) {
				return key.substring("long_".length());
			}
			if (key.startsWith("strong_")) {
				return key.substring("strong_".length());
			}
			return key;
		}

		private static boolean exists(String name) {
			try {
				org.bukkit.potion.PotionType.valueOf(name.toUpperCase(java.util.Locale.ROOT));
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	}

	@Override
	public org.bukkit.damage.DamageEffect getDamageEffect(String name) {
		return nl.rutgerkok.pokkit.damage.PokkitDamageEffect.getByName(name);
	}

	@Override
	public org.bukkit.damage.DamageSource.Builder createDamageSourceBuilder(org.bukkit.damage.DamageType damageType) {
		return nl.rutgerkok.pokkit.damage.PokkitDamageSource.builder(damageType);
	}

	@Override
	public org.bukkit.block.Biome getCustomBiome() {
		// Biome.CUSTOM is filled from this method while Biome is still being
		// initialised. A plain entry is enough: Pokkit has no custom biome
		// registry that plugins can add to.
		return new nl.rutgerkok.pokkit.world.biome.PokkitBiomeType("custom", 0);
	}

	@Override
	public org.bukkit.entity.Villager.ReputationType createReputationType(String name) {
		return ReputationType.byId(name);
	}

	@Override
	public org.bukkit.entity.Villager.ReputationEvent createReputationEvent(String name) {
		return ReputationEvent.byId(name);
	}

	/**
	 * Converts a reputation name to a lowercase id. The Bukkit constants are
	 * created by passing ids such as {@code major_negative} and
	 * {@code bukkit_gossip} to this class.
	 *
	 * @param name
	 *            The reputation name.
	 * @return The normalized id.
	 */
	private static String normalizeReputationName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Reputation name cannot be null");
		}
		return name.trim().toLowerCase(Locale.ROOT);
	}

	/**
	 * Vanilla villager reputation types. The constants of
	 * {@link org.bukkit.entity.Villager.ReputationType} are created by this
	 * class, so the enum below provides their implementation. The values are
	 * the maximum value and the weight of the vanilla gossip types.
	 */
	private enum ReputationType implements org.bukkit.entity.Villager.ReputationType {

		MAJOR_NEGATIVE("major_negative", 100, -5),
		MINOR_NEGATIVE("minor_negative", 200, -1),
		MINOR_POSITIVE("minor_positive", 200, 1),
		MAJOR_POSITIVE("major_positive", 100, 5),
		TRADING("trading", 25, 1);

		private final String id;
		private final int maxValue;
		private final int weight;

		ReputationType(String id, int maxValue, int weight) {
			this.id = id;
			this.maxValue = maxValue;
			this.weight = weight;
		}

		@Override
		public int getMaxValue() {
			return maxValue;
		}

		@Override
		public int getWeight() {
			return weight;
		}

		static ReputationType byId(String name) {
			String id = normalizeReputationName(name);
			for (ReputationType type : values()) {
				if (type.id.equals(id)) {
					return type;
				}
			}
			throw new IllegalArgumentException("Unknown reputation type " + name);
		}
	}

	/**
	 * Vanilla villager reputation events. The constants of
	 * {@link org.bukkit.entity.Villager.ReputationEvent} are created by this
	 * class, so the enum below provides their implementation.
	 */
	private enum ReputationEvent implements org.bukkit.entity.Villager.ReputationEvent {

		ZOMBIE_VILLAGER_CURED("zombie_villager_cured"),
		TRADE("trade"),
		VILLAGER_HURT("villager_hurt"),
		VILLAGER_KILLED("villager_killed"),
		GOSSIP("bukkit_gossip"),
		DECAY("bukkit_decay"),
		GOLEM_KILLED("golem_killed"),
		UNSPECIFIED("bukkit_unspecified");

		private final String id;

		ReputationEvent(String id) {
			this.id = id;
		}

		static ReputationEvent byId(String name) {
			String id = normalizeReputationName(name);
			for (ReputationEvent event : values()) {
				if (event.id.equals(id)) {
					return event;
				}
			}
			throw new IllegalArgumentException("Unknown reputation event " + name);
		}
	}
}
