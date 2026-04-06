package nl.rutgerkok.pokkit.inventory;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import cn.nukkit.inventory.BaseInventory;

public class PokkitVirtualInventory extends PokkitAbstractInventory {

	private final List<ItemStack> contents;
	private int maxStackSize;
	private final String name;
	private final InventoryHolder holder;
	private final String title;
	private final InventoryType type;
	private final List<HumanEntity> viewers;

	public PokkitVirtualInventory(BaseInventory nukkit) {
		// Unlike ArrayList, Arrays.asList cannot be resized, which is exactly
		// what we want
		this.contents = Arrays.asList(new ItemStack[getSize()]);

		nukkit.getContents().forEach((slot, item) -> contents.set(slot, PokkitItemStack.toBukkitCopy(item)));

		this.maxStackSize = nukkit.getMaxStackSize();
		this.name = nukkit.getName();
		this.holder = PokkitInventoryHolder.toBukkit(nukkit.getHolder());
		this.title = nukkit.getTitle();
		this.type = PokkitInventoryType.toBukkit(nukkit.getType());
		this.viewers = nukkit.getViewers().stream().map(PokkitPlayer::toBukkit).collect(toList());
	}

	@Override
	protected int addSingleStack(ItemStack item) {
		int remaining = item.getAmount();

		// Try to disperse over the existing stacks first
		int size = getSize();
		for (int i = 0; i < size; i++) {
			ItemStack atPosition = contents.get(i);
			if (!item.isSimilar(atPosition)) {
				// Another stack type
				continue;
			}

			int maxStackSize = Math.min(atPosition.getMaxStackSize(), this.getMaxStackSize());
			if (atPosition.getAmount() >= maxStackSize) {
				// Already full
				continue;
			}

			int spaceAvailable = maxStackSize - atPosition.getAmount();
			int transferAmount = Math.min(spaceAvailable, remaining);
			remaining -= transferAmount;
			atPosition.setAmount(atPosition.getAmount() + transferAmount);

			if (remaining == 0) {
				return 0;
			}
		}

		// Try to disperse over empty slots
		for (int i = 0; i < size; i++) {
			ItemStack atPosition = contents.get(i);
			if (atPosition != null) {
				continue;
			}

			int transferAmount = Math.min(getMaxStackSize(), remaining);
			remaining -= transferAmount;
			ItemStack newItem = item.clone();
			newItem.setAmount(transferAmount);
			contents.set(i, newItem);

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
		for (int i = 0; i < contents.size(); i++) {
			ItemStack atPosition = contents.get(i);
			if (atPosition != null && item.isSimilar(atPosition)) {
				result.put(i, atPosition);
			}
		}
		return result;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
		HashMap<Integer, ItemStack> result = new HashMap<>();
		if (material == null) return result;
		for (int i = 0; i < contents.size(); i++) {
			ItemStack atPosition = contents.get(i);
			if (atPosition != null && atPosition.getType() == material) {
				result.put(i, atPosition);
			}
		}
		return result;
	}

	@Override
	public void clear() {
		contents.clear();
	}

	@Override
	public void clear(int index) {
		contents.set(index, null);
	}

	@Override
	public boolean contains(ItemStack item) {
		if (item == null) {
			return false;
		}

		for (ItemStack stack : contents) {
			if (stack != null && item.isSimilar(stack)) {
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
		for (ItemStack stack : contents) {
			if (stack != null && item.isSimilar(stack)) {
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
		if (amount < 1) {
			return true;
		}

		int remaining = amount;
		for (ItemStack stack : contents) {
			if (stack != null && stack.getType() == material) {
				remaining -= stack.getAmount();
				if (remaining <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		if (item == null) {
			return false;
		}
		if (amount < 1) {
			return true;
		}

		int remaining = amount;
		for (ItemStack stack : contents) {
			if (item.isSimilar(stack)) {
				remaining -= stack.getAmount();
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

		for (int i = 0; i < contents.size(); i++) {
			ItemStack stack = contents.get(i);
			if (stack != null && item.isSimilar(stack)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		Objects.requireNonNull(material);

		for (int i = 0; i < contents.size(); i++) {
			ItemStack stack = contents.get(i);
			if (stack != null && stack.getType() == material) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int firstEmpty() {
		for (int i = 0; i < contents.size(); i++) {
			ItemStack stack = contents.get(i);
			if (stack == null) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public ItemStack[] getContents() {
		return contents.toArray(new ItemStack[contents.size()]);
	}

	@Override
	public InventoryHolder getHolder() {
		return holder;
	}

	@Override
	public ItemStack getItem(int index) {
		return contents.get(index);
	}

	@Override
	public int getMaxStackSize() {
		return maxStackSize;
	}

	@Override
	public int getSize() {
		return contents.size();
	}

	@Override
	public InventoryType getType() {
		return type;
	}

	@Override
	public List<HumanEntity> getViewers() {
		return viewers;
	}

	@Override
	public void remove(ItemStack item) {
		for (int i = 0; i < contents.size(); i++) {
			ItemStack stack = contents.get(i);
			if (stack != null && item.isSimilar(stack)) {
				contents.set(i, null);
			}
		}
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
		for (int i = 0; i < contents.size(); i++) {
			ItemStack stack = contents.get(i);
			if (stack != null && stack.getType() == material) {
				contents.set(i, null);
			}
		}
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
		HashMap<Integer, ItemStack> leftover = new HashMap<>();
		if (items == null) return leftover;
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null) continue;
			int remaining = item.getAmount();
			for (int slot = 0; slot < contents.size(); slot++) {
				ItemStack atPosition = contents.get(slot);
				if (!item.isSimilar(atPosition)) continue;
				int transfer = Math.min(remaining, atPosition.getAmount());
				remaining -= transfer;
				int newAmount = atPosition.getAmount() - transfer;
				if (newAmount <= 0) {
					contents.set(slot, null);
				} else {
					atPosition.setAmount(newAmount);
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
		for (ItemStack item : contents) {
			if (item != null) return false;
		}
		return true;
	}

	@Override
	public void setItem(int index, ItemStack item) {
		contents.set(index, item);
	}

	@Override
	public void setMaxStackSize(int size) {
		this.maxStackSize = size;
	}

	public void updateToNukkit(BaseInventory nukkit) {
		Preconditions.checkArgument(nukkit.getSize() == getSize(), "inventories must be of equal size");
		nukkit.setMaxStackSize(maxStackSize);
		for (int i = 0; i < contents.size(); i++) {
			cn.nukkit.item.Item nukkitItem = PokkitItemStack.toNukkitCopy(contents.get(i));
			if (nukkitItem == null) {
				nukkit.clear(i);
			} else {
				nukkit.setItem(i, nukkitItem);
			}
		}
	}

}
