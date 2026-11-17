package nl.rutgerkok.pokkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.MenuType;

import nl.rutgerkok.pokkit.attribute.PokkitAttribute;
import nl.rutgerkok.pokkit.block.PokkitBlockType;
import nl.rutgerkok.pokkit.inventory.PokkitItemType;
import nl.rutgerkok.pokkit.inventory.PokkitMenuType;
import nl.rutgerkok.pokkit.world.biome.PokkitBiomeType;

public final class PokkitRegistries {

	private PokkitRegistries() {
	}

	private static final String[] ENCHANTMENT_KEYS = {
			"protection", "fire_protection", "feather_falling", "blast_protection",
			"projectile_protection", "respiration", "aqua_affinity", "thorns",
			"depth_strider", "frost_walker", "binding_curse", "sharpness",
			"smite", "bane_of_arthropods", "knockback", "fire_aspect",
			"looting", "sweeping_edge", "efficiency", "silk_touch",
			"unbreaking", "fortune", "power", "punch",
			"flame", "infinity", "luck_of_the_sea", "lure",
			"loyalty", "impaling", "riptide", "channeling",
			"multishot", "quick_charge", "piercing", "density",
			"breach", "wind_burst", "mending", "vanishing_curse",
			"soul_speed", "swift_sneak", "lunge"
	};

	private static final String[] INSTRUMENT_KEYS = {
			"ponder_goat_horn", "sing_goat_horn", "seek_goat_horn",
			"feel_goat_horn", "admire_goat_horn", "call_goat_horn",
			"yearn_goat_horn", "dream_goat_horn"
	};

	private static final String[] ATTRIBUTE_KEYS = {
			"armor", "armor_toughness", "attack_damage", "attack_knockback",
			"attack_speed", "block_break_speed", "block_interaction_range",
			"burning_time", "camera_distance", "entity_interaction_range",
			"explosion_knockback_resistance", "fall_damage_multiplier",
			"flying_speed", "follow_range", "gravity", "jump_strength",
			"knockback_resistance", "luck", "max_absorption", "max_health",
			"mining_efficiency", "movement_efficiency", "movement_speed",
			"oxygen_bonus", "safe_fall_distance", "scale", "sneaking_speed",
			"spawn_reinforcements", "step_height", "submerged_mining_speed",
			"sweeping_damage_ratio", "tempt_range", "water_movement_efficiency",
			"waypoint_receive_range", "waypoint_transmit_range"
	};

	private static final String[] MENU_TYPE_KEYS = {
			"anvil", "beacon", "blast_furnace", "brewing_stand",
			"cartography_table", "crafter_3x3", "crafting", "enchantment",
			"furnace", "generic_3x3", "generic_9x1", "generic_9x2",
			"generic_9x3", "generic_9x4", "generic_9x5", "generic_9x6",
			"grindstone", "hopper", "lectern", "loom",
			"merchant", "shulker_box", "smithing", "smoker",
			"stonecutter"
	};

	private static final String[] BIOME_KEYS = {
			"ocean", "plains", "desert", "windswept_hills", "forest", "taiga",
			"swamp", "mangrove_swamp", "river", "nether_wastes", "the_end", "frozen_ocean",
			"frozen_river", "snowy_plains", "mushroom_fields", "beach", "jungle", "sparse_jungle",
			"deep_ocean", "stony_shore", "snowy_beach", "birch_forest", "dark_forest", "pale_garden",
			"snowy_taiga", "old_growth_pine_taiga", "windswept_forest", "savanna", "savanna_plateau", "badlands",
			"wooded_badlands", "small_end_islands", "end_midlands", "end_highlands", "end_barrens", "warm_ocean",
			"lukewarm_ocean", "cold_ocean", "deep_lukewarm_ocean", "deep_cold_ocean", "deep_frozen_ocean", "the_void",
			"sunflower_plains", "windswept_gravelly_hills", "flower_forest", "ice_spikes", "old_growth_birch_forest", "old_growth_spruce_taiga",
			"windswept_savanna", "eroded_badlands", "bamboo_jungle", "soul_sand_valley", "crimson_forest", "warped_forest",
			"basalt_deltas", "dripstone_caves", "lush_caves", "deep_dark", "meadow", "grove",
			"snowy_slopes", "frozen_peaks", "jagged_peaks", "stony_peaks", "cherry_grove"
	};

	/**
	 * Creates the registry with all vanilla damage types. {@code DamageType}
	 * fails to initialise when one of them is missing.
	 *
	 * @return The registry.
	 */
	public static PokkitRegistry<org.bukkit.damage.DamageType> createDamageTypeRegistry() {
		PokkitRegistry<org.bukkit.damage.DamageType> registry = new PokkitRegistry<>();
		for (nl.rutgerkok.pokkit.damage.PokkitDamageType type
				: nl.rutgerkok.pokkit.damage.PokkitDamageType.all()) {
			registry.register(type);
		}
		return registry;
	}

	public static PokkitRegistry<BlockType> createBlockTypeRegistry() {
		PokkitRegistry<BlockType> registry = new PokkitRegistry<>();
		for (String key : VanillaKeys.BLOCKS) {
			registry.register(PokkitBlockType.of(key));
		}
		return registry;
	}

	public static Registry<org.bukkit.block.Biome> createBiomeRegistry() {
		return new LazyFactoryRegistry<>(BIOME_KEYS, PokkitBiomeType::new);
	}

	public static Registry<Attribute> createAttributeRegistry() {
		return new LazyFactoryRegistry<>(ATTRIBUTE_KEYS, PokkitAttribute::new);
	}

	public static Registry<MenuType> createMenuTypeRegistry() {
		return new LazyFactoryRegistry<>(MENU_TYPE_KEYS, (key, index) -> new PokkitMenuType(key));
	}

	public static Registry<org.bukkit.Sound> createSoundRegistry() {
		return new LazyFactoryRegistry<>(SoundKeys.SOUNDS, PokkitSoundType::new);
	}

	public static Registry<Enchantment> createEnchantmentRegistry() {
		return new LazyEnchantmentRegistry(ENCHANTMENT_KEYS);
	}

	public static PokkitRegistry<ItemType> createItemTypeRegistry() {
		PokkitRegistry<ItemType> registry = new PokkitRegistry<>();
		for (String key : VanillaKeys.ITEMS) {
			registry.register(PokkitItemType.of(key));
		}
		return registry;
	}

	public static Registry<org.bukkit.MusicInstrument> createMusicInstrumentRegistry() {
		return new LazyMusicInstrumentRegistry(INSTRUMENT_KEYS);
	}

	/**
	 * Creates the registry with all vanilla potion effect types.
	 * {@code PotionEffectType} fails to initialise when one of them is missing.
	 *
	 * @return The registry.
	 */
	public static Registry<org.bukkit.potion.PotionEffectType> createPotionEffectTypeRegistry() {
		return new nl.rutgerkok.pokkit.potion.PokkitPotionEffectRegistry();
	}
}
