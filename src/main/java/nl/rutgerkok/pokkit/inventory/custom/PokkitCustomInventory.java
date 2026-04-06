package nl.rutgerkok.pokkit.inventory.custom;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.inventory.PokkitInventoryType;
import nl.rutgerkok.pokkit.inventory.PokkitLiveInventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Class for creating custom inventories. This class implements the Bukkit API
 * {@link Inventory} and acts as a wrapper around {@link NukkitCustomInventory}.
 *
 */
public final class PokkitCustomInventory extends PokkitLiveInventory implements Inventory {

	/**
	 * Creates a new custom inventory.
	 *
	 * @param holderOrNull
	 *            Holder of the inventory, may be null.
	 * @param type
	 *            Type of the inventory.
	 * @param title
	 *            The title, may not be null.
	 * @param size
	 *            The size of the inventory, must be positive. Only used for
	 *            {@link InventoryType#CHEST}; other types use their default size.
	 * @return The inventory.
	 * @throws UnsupportedOperationException
	 *             If the inventory type is not supported.
	 */
	public static @NotNull Inventory create(InventoryHolder holderOrNull, @NotNull InventoryType type, @NotNull String title, int size) {
		cn.nukkit.inventory.InventoryType nukkitType = PokkitInventoryType.toNukkit(type);
		if (nukkitType == null || !NukkitCustomInventory.isSupported(nukkitType)) {
			throw new UnsupportedOperationException(
					Pokkit.NAME + " doesn't support custom inventories of type " + type);
		}

		NukkitCustomInventory nukkit = new NukkitCustomInventory(title, holderOrNull, nukkitType);

		// Only CHEST type supports variable size (27 or 54)
		if (type == InventoryType.CHEST) {
			if (size < 1) {
				throw new IllegalArgumentException("Invalid inventory size: " + size);
			}
			// Bedrock clients only support single chest (27) and double chest (54)
			if (size != 27 && size != 54) {
				size = size <= 27 ? 27 : 54;
			}
			nukkit.setSize(size);
		}

		return new PokkitCustomInventory(nukkit);
	}

	public PokkitCustomInventory(@NotNull NukkitCustomInventory inventory) {
		super(inventory);
	}

	@Override
	public InventoryHolder getHolder() {
		return ((NukkitCustomInventory) nukkit).getHolder().bukkitHolderOrNull;
	}

}
