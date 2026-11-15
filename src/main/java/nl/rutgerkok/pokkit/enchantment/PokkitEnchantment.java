package nl.rutgerkok.pokkit.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

/**
 * Translates between Bukkit enchantments and Nukkit enchantments.
 *
 * <p>
 * The index of {@link #NUKKIT_ENCHANTMENT_KEYS} is the Nukkit enchantment id,
 * so the table doubles as the id mapping.
 */
public class PokkitEnchantment {

	/**
	 * Bukkit keys of all enchantments Nukkit knows about, indexed by Nukkit
	 * enchantment id.
	 */
	private static final String[] NUKKIT_ENCHANTMENT_KEYS = {
			"protection",             // 0
			"fire_protection",        // 1
			"feather_falling",        // 2
			"blast_protection",       // 3
			"projectile_protection",  // 4
			"thorns",                 // 5
			"respiration",            // 6
			"depth_strider",          // 7
			"aqua_affinity",          // 8
			"sharpness",              // 9
			"smite",                  // 10
			"bane_of_arthropods",     // 11
			"knockback",              // 12
			"fire_aspect",            // 13
			"looting",                // 14
			"efficiency",             // 15
			"silk_touch",             // 16
			"unbreaking",             // 17
			"fortune",                // 18
			"power",                  // 19
			"punch",                  // 20
			"flame",                  // 21
			"infinity",               // 22
			"luck_of_the_sea",        // 23
			"lure",                   // 24
			"frost_walker",           // 25
			"mending",                // 26
			"binding_curse",          // 27
			"vanishing_curse",        // 28
			"impaling",               // 29
			"riptide",                // 30
			"loyalty",                // 31
			"channeling",             // 32
			"multishot",              // 33
			"piercing",               // 34
			"quick_charge",           // 35
			"soul_speed",             // 36
			"swift_sneak",            // 37
			"wind_burst",             // 38
			"density",                // 39
			"breach",                 // 40
			"lunge"                   // 41
	};

	private static final Enchantment[] nukkitToBukkit = new Enchantment[NUKKIT_ENCHANTMENT_KEYS.length];
	private static boolean initialized = false;

	private static synchronized void ensureInitialized() {
		if (initialized) {
			return;
		}
		for (int id = 0; id < NUKKIT_ENCHANTMENT_KEYS.length; id++) {
			NamespacedKey key = NamespacedKey.minecraft(NUKKIT_ENCHANTMENT_KEYS[id]);
			Enchantment bukkit = Registry.ENCHANTMENT.get(key);
			if (bukkit == null) {
				bukkit = new PokkitEnchantmentEntry(key, getNukkitEnchantment(key));
			}
			nukkitToBukkit[id] = bukkit;
		}
		initialized = true;
	}

	public static void registerNukkitEnchantmentsInBukkit() {
		// In 1.20.6+, enchantments are pre-registered in the Bukkit Registry
	}

	/**
	 * Gets the Nukkit enchantment that corresponds to the given Bukkit key.
	 *
	 * @param key
	 *            The Bukkit key.
	 * @return The Nukkit enchantment, or null if there is no equivalent.
	 */
	public static cn.nukkit.item.enchantment.Enchantment getNukkitEnchantment(NamespacedKey key) {
		int id = nukkitId(key);
		if (id < 0) {
			return null;
		}
		try {
			return cn.nukkit.item.enchantment.Enchantment.get(id);
		} catch (RuntimeException e) {
			// Nukkit enchantments have not been initialized yet.
			return null;
		}
	}

	public static Enchantment toBukkit(int nukkit) {
		ensureInitialized();
		if (nukkit < 0 || nukkit >= nukkitToBukkit.length) {
			return null;
		}
		return nukkitToBukkit[nukkit];
	}

	public static int toNukkit(Enchantment enchantment) {
		if (enchantment == null) {
			return -1;
		}
		return nukkitId(enchantment.getKey());
	}

	private static int nukkitId(NamespacedKey key) {
		if (key == null) {
			return -1;
		}
		for (int id = 0; id < NUKKIT_ENCHANTMENT_KEYS.length; id++) {
			if (NamespacedKey.minecraft(NUKKIT_ENCHANTMENT_KEYS[id]).equals(key)) {
				return id;
			}
		}
		return -1;
	}
}
