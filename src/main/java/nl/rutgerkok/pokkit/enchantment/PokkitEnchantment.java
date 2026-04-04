package nl.rutgerkok.pokkit.enchantment;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

public class PokkitEnchantment {

	private static final Enchantment[] nukkitToBukkit = new Enchantment[42];
	private static final Map<NamespacedKey, Integer> bukkitToNukkit = new HashMap<>();
	private static boolean initialized = false;

	private static synchronized void ensureInitialized() {
		if (initialized) {
			return;
		}
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_PROTECTION_ALL, "protection");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_PROTECTION_FIRE, "fire_protection");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_PROTECTION_FALL, "feather_falling");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_PROTECTION_EXPLOSION, "blast_protection");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_PROTECTION_PROJECTILE, "projectile_protection");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_THORNS, "thorns");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_WATER_BREATHING, "respiration");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_WATER_WORKER, "aqua_affinity");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_WATER_WALKER, "depth_strider");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_DAMAGE_ALL, "sharpness");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_DAMAGE_SMITE, "smite");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_DAMAGE_ARTHROPODS, "bane_of_arthropods");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_KNOCKBACK, "knockback");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_FIRE_ASPECT, "fire_aspect");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_LOOTING, "looting");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_EFFICIENCY, "efficiency");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_SILK_TOUCH, "silk_touch");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_DURABILITY, "unbreaking");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_FORTUNE_DIGGING, "fortune");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BOW_POWER, "power");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BOW_KNOCKBACK, "punch");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BOW_FLAME, "flame");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BOW_INFINITY, "infinity");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_FORTUNE_FISHING, "luck_of_the_sea");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_LURE, "lure");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_FROST_WALKER, "frost_walker");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_MENDING, "mending");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BINDING_CURSE, "binding_curse");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_VANISHING_CURSE, "vanishing_curse");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_TRIDENT_IMPALING, "impaling");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_TRIDENT_LOYALTY, "loyalty");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_TRIDENT_RIPTIDE, "riptide");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_TRIDENT_CHANNELING, "channeling");

		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_CROSSBOW_MULTISHOT, "multishot");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_CROSSBOW_PIERCING, "piercing");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_CROSSBOW_QUICK_CHARGE, "quick_charge");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_SOUL_SPEED, "soul_speed");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_SWIFT_SNEAK, "swift_sneak");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_DENSITY, "density");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_BREACH, "breach");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_WIND_BURST, "wind_burst");
		twoWay(cn.nukkit.item.enchantment.Enchantment.ID_LUNGE, "lunge");
		initialized = true;
	}

	public static void registerNukkitEnchantmentsInBukkit() {
		// In 1.20.6+, enchantments are pre-registered in the Bukkit Registry
	}

	public static Enchantment toBukkit(int nukkit) {
		ensureInitialized();
		if (nukkit < 0 || nukkit >= nukkitToBukkit.length) {
			return null;
		}
		return nukkitToBukkit[nukkit];
	}

	public static int toNukkit(Enchantment enchantment) {
		ensureInitialized();
		NamespacedKey bukkitId = enchantment.getKey();
		return bukkitToNukkit.getOrDefault(bukkitId, -1);
	}

	private static void twoWay(int nukkit, String bukkitKey) {
		NamespacedKey key = NamespacedKey.minecraft(bukkitKey);
		Enchantment bukkit = Registry.ENCHANTMENT.get(key);
		nukkitToBukkit[nukkit] = bukkit;

		if (bukkitToNukkit.containsKey(key)) {
			throw new RuntimeException("bukkitToNukkit already mapped");
		}

		bukkitToNukkit.put(key, nukkit);
	}

	private static void oneWay(int nukkit, String bukkitKey) {
		NamespacedKey key = NamespacedKey.minecraft(bukkitKey);
		Enchantment bukkit = Registry.ENCHANTMENT.get(key);
		nukkitToBukkit[nukkit] = bukkit;
	}
}
