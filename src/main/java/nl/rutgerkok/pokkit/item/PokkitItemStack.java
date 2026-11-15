package nl.rutgerkok.pokkit.item;

import cn.nukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Class for converting Bukkit and Nukkit item stacks.
 *
 */
public final class PokkitItemStack {

	private static final Set<Integer> WARNED_UNKNOWN_IDS = java.util.concurrent.ConcurrentHashMap.newKeySet();

	private static final PokkitItemFactory getItemFactory() {
		return (PokkitItemFactory) Bukkit.getItemFactory();
	}

	/**
	 * Overwrites a Nukkit item with the data of a Bukkit item. Useful in case
	 * you want the changes to a Bukkit item show up in a Nukkit item.
	 *
	 * <p>
	 * If the stacks are of a different material, this method print a warning
	 * and do nothing to the item stack.
	 *
	 * @param bukkit
	 *            The Bukkit item.
	 * @param nukkit
	 *            The Nukkit item.
	 */
	public static void overwriteNukkit(ItemStack bukkit, cn.nukkit.item.Item nukkit) {
		cn.nukkit.item.Item nukkitType = ItemMap.fromBukkit(bukkit.getType());

		if (nukkitType.getId() == Item.AIR && bukkit.getType() != Material.AIR) {
			Bukkit.getLogger().warning("Cannot convert " + bukkit.getType() + " to a Nukkit item");
			return;
		}
		if (nukkitType.getId() != nukkit.getId()) {
			Bukkit.getLogger().info("Cannot change item material from " + bukkit.getType() + " to "
					+ nukkit);
			return;
		}

		nukkit.setCount(bukkit.getAmount());

		ItemMeta bukkitMeta = bukkit.getItemMeta();
		if (bukkitMeta instanceof PokkitItemMeta meta) {
			// Also copy the damage of the freshly created item, so that
			// variant items (dyes, wool colours, spawn eggs) change variant
			// instead of keeping the old damage value.
			applyMeta(meta, bukkit.getType(), nukkit, nukkitType.getDamage());
		} else {
			nukkit.clearNamedTag();
		}
	}

	/**
	 * Creates a Bukkit copy of the item stack. Changes to the returned item stack are not mirrored in the original Nukkit stack.
	 *
	 * @param nukkit The Nukkit stack.
	 * @return A {@link ItemStack}, or null if Nukkit has a null stack.
	 */
	public static ItemStack toBukkitCopy(cn.nukkit.item.Item nukkit) {
		if (nukkit == null) {
			return null;
		}

		Material material = ItemMap.fromNukkitOrNull(nukkit);
		if (material == null) {
			Server server = Server.getInstance();
			if (server != null && WARNED_UNKNOWN_IDS.add(nukkit.getId())) {
				server.getLogger().warning("[Pokkit] Cannot convert Nukkit item " + nukkit.getId()
						+ " (damage " + nukkit.getDamage() + ") to a Bukkit material");
			}
			return null;
		}

		ItemStack bukkit = new ItemStack(material, Math.max(0, nukkit.getCount()));
		if (material == Material.AIR) {
			// Air has no item meta, even if the Nukkit item carries NBT.
			return bukkit;
		}

		// Damage is used both for durability and for potion data, so it must
		// be carried over even if the item has no NBT at all.
		CompoundTag extra = nukkit.getNamedTag();
		int damage = toBukkitDamage(material, nukkit.getDamage());
		if (extra != null || damage != 0 || isPotion(material)) {
			bukkit.setItemMeta(getItemFactory().getItemMeta(material,
					extra != null ? extra : new CompoundTag(), damage));
		}

		return bukkit;
	}

	public static cn.nukkit.item.Item toNukkitCopy(ItemStack bukkit) {
		if (bukkit == null) {
			return Item.get(Item.AIR);
		}

		cn.nukkit.item.Item nukkit = ItemMap.fromBukkit(bukkit.getType());
		nukkit.setCount(bukkit.getAmount());

		ItemMeta bukkitMeta = bukkit.getItemMeta();
		if (bukkitMeta instanceof PokkitItemMeta meta) {
			applyMeta(meta, bukkit.getType(), nukkit, nukkit.getDamage());
		}

		return nukkit;
	}

	/**
	 * Copies the damage value and the NBT tag of the given Bukkit item meta to
	 * the Nukkit item.
	 *
	 * @param meta
	 *            The Bukkit item meta.
	 * @param material
	 *            The material of the item.
	 * @param nukkit
	 *            The Nukkit item to modify.
	 * @param variantDamage
	 *            The damage value that selects the variant of the material,
	 *            for example the dye colour. Ignored for damageable items and
	 *            potions.
	 */
	private static void applyMeta(PokkitItemMeta meta, Material material, cn.nukkit.item.Item nukkit,
			int variantDamage) {
		if (material.getMaxDurability() > 0 || isPotion(material)) {
			nukkit.setDamage(toNukkitDamage(material, meta.getDamage()));
		} else {
			nukkit.setDamage(variantDamage);
		}
		if (!meta.getTag().isEmpty()) {
			nukkit.setNamedTag(meta.getTag());
		} else {
			nukkit.clearNamedTag();
		}
	}

	/**
	 * Converts the damage value stored in a Bukkit item meta to the damage
	 * value that Nukkit expects.
	 *
	 * @param material
	 *            The material of the item.
	 * @param metaDamage
	 *            The damage value of the Bukkit item meta.
	 * @return The damage value for the Nukkit item.
	 */
	private static int toNukkitDamage(Material material, int metaDamage) {
		if (isTippedArrow(material)) {
			// Nukkit stores tipped arrows as damage 1 + potion id.
			return Math.max(0, metaDamage) + 1;
		}
		if (material.getMaxDurability() > 0 || isPotion(material)) {
			return Math.max(0, metaDamage);
		}
		return 0;
	}

	/**
	 * Gets the part of the Nukkit damage value that must be exposed through
	 * {@link Damageable#getDamage()}. Block data and item variant data are
	 * already encoded in the {@link Material}, so they must not leak into the
	 * durability value.
	 *
	 * @param material
	 *            The material of the item.
	 * @param nukkitDamage
	 *            The damage value of the Nukkit item.
	 * @return The damage value for the Bukkit item meta.
	 */
	private static int toBukkitDamage(Material material, int nukkitDamage) {
		if (nukkitDamage <= 0) {
			return 0;
		}
		if (isTippedArrow(material)) {
			return nukkitDamage - 1;
		}
		if (material.getMaxDurability() > 0) {
			return nukkitDamage;
		}
		if (isPotion(material)) {
			// Potions store their type in the damage value.
			return nukkitDamage;
		}
		return 0;
	}

	/**
	 * Tests whether the given material is a tipped arrow, which stores its
	 * potion in the damage value with an offset of one.
	 *
	 * @param material
	 *            The material.
	 * @return True for tipped arrows, false otherwise.
	 */
	private static boolean isTippedArrow(Material material) {
		return material == Material.TIPPED_ARROW || material == Material.LEGACY_TIPPED_ARROW;
	}

	/**
	 * Tests whether the given material stores its type in the damage value of
	 * the Nukkit item.
	 *
	 * @param material
	 *            The material.
	 * @return True for potions and tipped arrows, false otherwise.
	 */
	static boolean isPotion(Material material) {
		switch (material) {
		case POTION:
		case LEGACY_POTION:
		case SPLASH_POTION:
		case LEGACY_SPLASH_POTION:
		case LINGERING_POTION:
		case LEGACY_LINGERING_POTION:
		case TIPPED_ARROW:
		case LEGACY_TIPPED_ARROW:
			return true;
		default:
			return false;
		}
	}
}
