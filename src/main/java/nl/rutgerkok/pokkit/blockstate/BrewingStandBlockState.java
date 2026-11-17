package nl.rutgerkok.pokkit.blockstate;

import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Campfire;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.inventory.PokkitLiveInventory;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.blockentity.BlockEntityCampfire;

final class BrewingStandBlockState extends PokkitContainerState implements BrewingStand, PokkitLockable {

	BrewingStandBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 5, "Brewing Stand");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BREWING_STAND;
	}

	private BlockEntityBrewingStand stand() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityBrewingStand stand ? stand : null;
	}

	@Override
	public int getBrewingTime() {
		BlockEntityBrewingStand stand = stand();
		return stand == null ? 0 : stand.brewTime;
	}

	@Override
	public void setBrewingTime(int brewingTime) {
		BlockEntityBrewingStand stand = stand();
		if (stand != null) {
			stand.brewTime = brewingTime;
		}
	}

	@Override
	public int getFuelLevel() {
		BlockEntityBrewingStand stand = stand();
		return stand == null ? 0 : stand.fuelAmount;
	}

	@Override
	public void setFuelLevel(int fuelLevel) {
		BlockEntityBrewingStand stand = stand();
		if (stand != null) {
			stand.fuelAmount = fuelLevel;
		}
	}

	@Override
	public BrewerInventory getInventory() {
		cn.nukkit.inventory.Inventory inventory = nukkitInventory();
		if (inventory instanceof cn.nukkit.inventory.BrewingInventory brewingInventory) {
			return new PokkitBrewerInventory(brewingInventory, this);
		}
		throw new IllegalStateException("The brewing stand at " + getLocation() + " has no inventory");
	}

	@Override
	public BrewerInventory getSnapshotInventory() {
		return getInventory();
	}
}

/**
 * Live view on a Nukkit brewing inventory.
 */
final class PokkitBrewerInventory extends PokkitLiveInventory implements BrewerInventory {

	private final cn.nukkit.inventory.BrewingInventory inventory;
	private final BrewingStandBlockState holder;

	PokkitBrewerInventory(cn.nukkit.inventory.BrewingInventory inventory, BrewingStandBlockState holder) {
		super(inventory);
		this.inventory = inventory;
		this.holder = holder;
	}

	@Override
	public ItemStack getIngredient() {
		return PokkitItemStack.toBukkitCopy(inventory.getIngredient());
	}

	@Override
	public void setIngredient(ItemStack itemStack) {
		inventory.setIngredient(PokkitItemStack.toNukkitCopy(itemStack));
	}

	@Override
	public ItemStack getFuel() {
		return PokkitItemStack.toBukkitCopy(inventory.getFuel());
	}

	@Override
	public void setFuel(ItemStack itemStack) {
		inventory.setFuel(PokkitItemStack.toNukkitCopy(itemStack));
	}

	@Override
	public BrewingStand getHolder() {
		return holder;
	}
}

final class CampfireBlockState extends PokkitTileState implements Campfire {

	CampfireBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.CAMPFIRE;
	}

	private BlockEntityCampfire campfire() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityCampfire campfire ? campfire : null;
	}

	@Override
	public int getSize() {
		BlockEntityCampfire campfire = campfire();
		return campfire == null ? 4 : campfire.getSize();
	}

	@Override
	public ItemStack getItem(int index) {
		BlockEntityCampfire campfire = campfire();
		if (campfire == null || index < 0 || index >= campfire.getSize()) {
			throw new IndexOutOfBoundsException("index " + index);
		}
		return PokkitItemStack.toBukkitCopy(campfire.getItem(index));
	}

	@Override
	public void setItem(int index, ItemStack item) {
		BlockEntityCampfire campfire = campfire();
		if (campfire == null) {
			return;
		}
		if (index < 0 || index >= campfire.getSize()) {
			throw new IndexOutOfBoundsException("index " + index);
		}
		campfire.setItem(index, PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public int getCookTime(int index) {
		return tileTag().getInt("CookTime" + index);
	}

	@Override
	public void setCookTime(int index, int cookTime) {
		tileTag().putInt("CookTime" + index, cookTime);
	}

	@Override
	public int getCookTimeTotal(int index) {
		return tileTag().getInt("CookTimeTotal" + index);
	}

	@Override
	public void setCookTimeTotal(int index, int cookTimeTotal) {
		tileTag().putInt("CookTimeTotal" + index, cookTimeTotal);
	}
}
