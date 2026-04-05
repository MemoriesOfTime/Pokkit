package nl.rutgerkok.pokkit.inventory;

import java.util.Objects;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class PokkitInventoryView implements InventoryView {

	private final Inventory topInventory;
	private final HumanEntity player;

	public PokkitInventoryView(Inventory top, HumanEntity player) {
		this.topInventory = Objects.requireNonNull(top, "top");
		this.player = Objects.requireNonNull(player, "player");
	}

	@Override
	public @NotNull Inventory getTopInventory() {
		return topInventory;
	}

	@Override
	public @NotNull Inventory getBottomInventory() {
		return player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public InventoryType getType() {
		InventoryType type = topInventory.getType();
		if (type == InventoryType.CRAFTING && player.getGameMode() == GameMode.CREATIVE) {
			return InventoryType.CREATIVE;
		}
		return type;
	}

	@Override
	public String getTitle() {
		if (topInventory instanceof PokkitLiveInventory) {
			return ((PokkitLiveInventory) topInventory).nukkit.getTitle();
		}
		return topInventory.getClass().getSimpleName();
	}

	@Override
	public String getOriginalTitle() {
		return getTitle();
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		if (slot >= 0 && slot < topInventory.getSize()) {
			topInventory.setItem(slot, item);
		} else if (getBottomInventory() != null) {
			int bottomSlot = slot - topInventory.getSize();
			if (bottomSlot >= 0) {
				getBottomInventory().setItem(bottomSlot, item);
			}
		}
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot >= 0 && slot < topInventory.getSize()) {
			return topInventory.getItem(slot);
		} else if (getBottomInventory() != null) {
			int bottomSlot = slot - topInventory.getSize();
			if (bottomSlot >= 0) {
				return getBottomInventory().getItem(bottomSlot);
			}
		}
		return null;
	}

	@Override
	public void setCursor(ItemStack item) {
	}

	@Override
	public ItemStack getCursor() {
		return null;
	}

	@Override
	public Inventory getInventory(int rawSlot) {
		if (rawSlot < 0) return null;
		if (rawSlot < topInventory.getSize()) return topInventory;
		return getBottomInventory();
	}

	@Override
	public int convertSlot(int rawSlot) {
		if (rawSlot < 0) return rawSlot;
		if (rawSlot < topInventory.getSize()) return rawSlot;
		return rawSlot - topInventory.getSize();
	}

	@Override
	public InventoryType.SlotType getSlotType(int slot) {
		if (slot < 0) return InventoryType.SlotType.OUTSIDE;
		if (slot < topInventory.getSize()) return InventoryType.SlotType.CONTAINER;
		int bottomSlot = slot - topInventory.getSize();
		if (bottomSlot < 9) return InventoryType.SlotType.QUICKBAR;
		return InventoryType.SlotType.CONTAINER;
	}

	@Override
	public void close() {
		player.closeInventory();
	}

	@Override
	public int countSlots() {
		int count = topInventory.getSize();
		if (getBottomInventory() != null) {
			count += getBottomInventory().getSize();
		}
		return count;
	}

	@Override
	public boolean setProperty(Property prop, int value) {
		return false;
	}
}
