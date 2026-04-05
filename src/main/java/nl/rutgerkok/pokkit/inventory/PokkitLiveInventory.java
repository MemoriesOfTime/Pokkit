package nl.rutgerkok.pokkit.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import cn.nukkit.item.Item;

/**
 * Inventory implementation that forwards changes directly to a Nukkit
 * inventory.
 *
 * <p>
 * Note: this implementation is not to spec. inventory.getItem(i).setAmount(j)
 * doesn't update the inventory yet.
 */
public class PokkitLiveInventory extends PokkitAbstractInventory {

	protected cn.nukkit.inventory.Inventory nukkit;

	public PokkitLiveInventory(cn.nukkit.inventory.Inventory inventory) {
		this.nukkit = Objects.requireNonNull(inventory, "inventory");
	}

	@Override
	protected int addSingleStack(ItemStack item) {
		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
		int remaining = nukkitItem.count;

		// Try to disperse over the existing stacks first
		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (!atPosition.equals(nukkitItem)) {
				// Another stack type
				continue;
			}

			int maxStackSize = Math.min(atPosition.getMaxStackSize(), this.getMaxStackSize());
			if (atPosition.count >= maxStackSize) {
				// Already full
				continue;
			}

			int spaceAvailable = maxStackSize - atPosition.count;
			int transferAmount = Math.min(spaceAvailable, remaining);
			remaining -= transferAmount;
			atPosition.count += transferAmount;

			if (remaining == 0) {
				return 0;
			}
		}

		// Try to disperse over empty slots
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition.getId() != Item.AIR) {
				continue;
			}

			int transferAmount = Math.min(getMaxStackSize(), remaining);
			remaining -= transferAmount;
			Item newItem = nukkitItem.clone();
			newItem.count = transferAmount;
			nukkit.setItem(i, newItem);

			if (remaining == 0) {
				return 0;
			}
		}

		return remaining;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		HashMap<Integer, ItemStack> result = new HashMap<>();
		if (item == null) return result;
		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition != null && atPosition.equals(nukkitItem, false)) {
				result.put(i, PokkitItemStack.toBukkitCopy(atPosition));
			}
		}
		return result;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
		HashMap<Integer, ItemStack> result = new HashMap<>();
		if (material == null) return result;
		int nukkitTypeId = PokkitBlockData.createBlockData(material, 0).getNukkitId();
		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition.getId() == nukkitTypeId) {
				result.put(i, PokkitItemStack.toBukkitCopy(atPosition));
			}
		}
		return result;
	}

	@Override
	public void clear() {
		nukkit.clearAll();
	}

	@Override
	public void clear(int index) {
		nukkit.clear(index);
	}

	@Override
	public boolean contains(ItemStack item) {
		if (item == null) {
			return false;
		}

		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);

		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition != null && atPosition.equals(nukkitItem, false)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(ItemStack item, int amount) {
		if (item == null) {
			return false;
		}
		if (amount < 1) {
			return true;
		}
		int remaining = amount;

		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);

		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition != null && atPosition.equals(nukkitItem, false)) {
				remaining--;
				if (remaining <= 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean contains(Material material, int amount) throws IllegalArgumentException {
		Objects.requireNonNull(material, "material");
		int nukkitTypeId = PokkitBlockData.createBlockData(material, 0).getNukkitId();

		int remaining = amount;

		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition.getId() == nukkitTypeId) {
				remaining -= atPosition.getCount();
				if (remaining <= 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		if (item == null || amount < 1) {
			return false;
		}
		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
		int remaining = amount;
		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition != null && atPosition.equals(nukkitItem, false)) {
				remaining -= atPosition.getCount();
				if (remaining <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int first(ItemStack item) {
		if (item == null) {
			return -1;
		}
		return nukkit.first(PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		Objects.requireNonNull(material, "material");
		int nukkitTypeId = PokkitBlockData.createBlockData(material, 0).getNukkitId();

		int size = getSize();
		for (int i = 0; i < size; i++) {
			Item atPosition = nukkit.getItem(i);
			if (atPosition.getId() == nukkitTypeId) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int firstEmpty() {
		return nukkit.firstEmpty(Item.get(Item.AIR));
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[getSize()];
		nukkit.getContents().forEach((slot, item) -> {
			if (slot >= 0 && slot < contents.length) {
				contents[slot] = PokkitItemStack.toBukkitCopy(item);
			}
		});
		return contents;
	}

	@Override
	public InventoryHolder getHolder() {
	    return PokkitInventoryHolder.toBukkit(nukkit.getHolder());
	}

	@Override
	public ItemStack getItem(int index) {
		return PokkitItemStack.toBukkitCopy(nukkit.getItem(index));
	}

	@Override
	public int getMaxStackSize() {
		return nukkit.getMaxStackSize();
	}

	@Override
	public int getSize() {
		return nukkit.getSize();
	}

	@Override
	public InventoryType getType() {
		return PokkitInventoryType.toBukkit(nukkit.getType());
	}

	@Override
	public List<HumanEntity> getViewers() {
		return nukkit.getViewers().stream().map(PokkitPlayer::toBukkit).collect(Collectors.toList());
	}

	@Override
	public void remove(ItemStack item) {
		if (item == null) {
			return;
		}

		ItemStack[] contents = getContents();

		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null && contents[i].equals(item)) {
				contents[i] = null;
			}
		}

		setContents(contents);
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
		if (material == null) {
			return;
		}

		ItemStack[] contents = getContents();

		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null && contents[i].getType().equals(material)) {
				contents[i] = null;
			}
		}

		setContents(contents);
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
		HashMap<Integer, ItemStack> leftover = new HashMap<>();
		if (items == null) return leftover;
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null) continue;
			int remaining = item.getAmount();
			Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
			int size = getSize();
			for (int slot = 0; slot < size; slot++) {
				Item atPosition = nukkit.getItem(slot);
				if (!atPosition.equals(nukkitItem)) continue;
				int transfer = Math.min(remaining, atPosition.count);
				remaining -= transfer;
				atPosition.count -= transfer;
				if (atPosition.count <= 0) {
					nukkit.clear(slot);
				} else {
					nukkit.setItem(slot, atPosition);
				}
				if (remaining <= 0) break;
			}
			if (remaining > 0) {
				ItemStack leftoverItem = item.clone();
				leftoverItem.setAmount(remaining);
				leftover.put(i, leftoverItem);
			}
		}
		return leftover;
	}

	@Override
	public boolean isEmpty() {
		int size = getSize();
		for (int i = 0; i < size; i++) {
			if (nukkit.getItem(i).getId() != Item.AIR) return false;
		}
		return true;
	}

	@Override
	public void setItem(int index, ItemStack item) {
		nukkit.setItem(index, PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public void setMaxStackSize(int size) {
		nukkit.setMaxStackSize(size);
	}
}
