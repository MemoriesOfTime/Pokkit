package nl.rutgerkok.pokkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;

import nl.rutgerkok.pokkit.block.PokkitBlockType;
import nl.rutgerkok.pokkit.inventory.PokkitItemType;

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
