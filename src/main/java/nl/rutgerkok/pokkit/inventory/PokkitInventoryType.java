package nl.rutgerkok.pokkit.inventory;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.event.inventory.InventoryType;

public final class PokkitInventoryType {

	private static final Map<cn.nukkit.inventory.InventoryType, InventoryType> nukkitToBukkit = new EnumMap<>(
			cn.nukkit.inventory.InventoryType.class);
	private static final Map<InventoryType, cn.nukkit.inventory.InventoryType> bukkitToNukkit = new EnumMap<>(
			InventoryType.class);

	static {
		twoWay(cn.nukkit.inventory.InventoryType.CHEST, InventoryType.CHEST);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.DOUBLE_CHEST, InventoryType.CHEST);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.MINECART_CHEST, InventoryType.CHEST);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.CHEST_BOAT, InventoryType.CHEST);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.DONKEY, InventoryType.CHEST);
		twoWay(cn.nukkit.inventory.InventoryType.DISPENSER, InventoryType.DISPENSER);
		twoWay(cn.nukkit.inventory.InventoryType.DROPPER, InventoryType.DROPPER);
		twoWay(cn.nukkit.inventory.InventoryType.FURNACE, InventoryType.FURNACE);
		twoWay(cn.nukkit.inventory.InventoryType.WORKBENCH, InventoryType.WORKBENCH);
		twoWay(cn.nukkit.inventory.InventoryType.CRAFTING, InventoryType.CRAFTING);
		twoWay(cn.nukkit.inventory.InventoryType.ENCHANT_TABLE, InventoryType.ENCHANTING);
		twoWay(cn.nukkit.inventory.InventoryType.BREWING_STAND, InventoryType.BREWING);
		twoWay(cn.nukkit.inventory.InventoryType.PLAYER, InventoryType.PLAYER);
		bukkitToNukkit.put(InventoryType.CREATIVE, cn.nukkit.inventory.InventoryType.PLAYER);
		twoWay(cn.nukkit.inventory.InventoryType.TRADING, InventoryType.MERCHANT);
		twoWay(cn.nukkit.inventory.InventoryType.ENDER_CHEST, InventoryType.ENDER_CHEST);
		twoWay(cn.nukkit.inventory.InventoryType.ANVIL, InventoryType.ANVIL);
		twoWay(cn.nukkit.inventory.InventoryType.BEACON, InventoryType.BEACON);
		twoWay(cn.nukkit.inventory.InventoryType.HOPPER, InventoryType.HOPPER);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.MINECART_HOPPER, InventoryType.HOPPER);
		twoWay(cn.nukkit.inventory.InventoryType.SHULKER_BOX, InventoryType.SHULKER_BOX);
		twoWay(cn.nukkit.inventory.InventoryType.BARREL, InventoryType.BARREL);
		twoWay(cn.nukkit.inventory.InventoryType.BLAST_FURNACE, InventoryType.BLAST_FURNACE);
		twoWay(cn.nukkit.inventory.InventoryType.LECTERN, InventoryType.LECTERN);
		twoWay(cn.nukkit.inventory.InventoryType.SMOKER, InventoryType.SMOKER);
		twoWay(cn.nukkit.inventory.InventoryType.LOOM, InventoryType.LOOM);
		bukkitToNukkit.put(InventoryType.CARTOGRAPHY, cn.nukkit.inventory.InventoryType.CRAFTING); // Nukkit TODO
		bukkitToNukkit.put(InventoryType.GRINDSTONE, cn.nukkit.inventory.InventoryType.CRAFTING); // Nukkit TODO
		bukkitToNukkit.put(InventoryType.STONECUTTER, cn.nukkit.inventory.InventoryType.CRAFTING); // Nukkit TODO
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.SMITHING_TABLE, InventoryType.CRAFTING); // Spigot TODO
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.CAMPFIRE, InventoryType.PLAYER); // Spigot TODO
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.UI, InventoryType.PLAYER);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.OFFHAND, InventoryType.PLAYER);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.ENTITY_ARMOR, InventoryType.PLAYER);
		nukkitToBukkit.put(cn.nukkit.inventory.InventoryType.ENTITY_EQUIPMENT, InventoryType.PLAYER);
	}

	/**
	 * Gets the Bukkit inventory type.
	 *
	 * @param nukkit
	 *            The Nukkit inventory type.
	 * @return The Bukkit inventory type.
	 */
	public static InventoryType toBukkit(cn.nukkit.inventory.InventoryType nukkit) {
		return nukkitToBukkit.get(nukkit);
	}

	/**
	 * Gets the Nukkit inventory type.
	 *
	 * @param inventoryType
	 *            The Bukkit inventory type.
	 * @return The Nukkit invenotry type.
	 */
	public static cn.nukkit.inventory.InventoryType toNukkit(InventoryType inventoryType) {
		return bukkitToNukkit.get(inventoryType);
	}

	private static void twoWay(cn.nukkit.inventory.InventoryType nukkit, InventoryType bukkit) {
		nukkitToBukkit.put(nukkit, bukkit);
		bukkitToNukkit.put(bukkit, nukkit);
	}
}
