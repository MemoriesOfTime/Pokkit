package nl.rutgerkok.pokkit.blockstate;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.inventory.PokkitLiveInventory;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFurnace;

/**
 * Furnace-like block states. Blast furnaces and smokers share the furnace
 * block entity in Nukkit, so they share the implementation.
 */
class FurnaceBlockState extends PokkitContainerState implements Furnace, PokkitLockable {

	FurnaceBlockState(Location location, PokkitBlockData blockData) {
		this(location, blockData, BlockEntity.FURNACE, "Furnace");
	}

	FurnaceBlockState(Location location, PokkitBlockData blockData, String entityType, String title) {
		super(location, blockData, 3, title);
		this.entityType = entityType;
	}

	private final String entityType;

	@Override
	protected String blockEntityType() {
		return entityType;
	}

	private BlockEntityFurnace furnace() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityFurnace furnace ? furnace : null;
	}

	@Override
	public short getBurnTime() {
		return (short) getTagValue("BurnTime");
	}

	@Override
	public void setBurnTime(short burnTime) {
		setTagValue("BurnTime", burnTime);
	}

	@Override
	public short getCookTime() {
		return (short) getTagValue("CookTime");
	}

	@Override
	public void setCookTime(short cookTime) {
		setTagValue("CookTime", cookTime);
	}

	@Override
	public int getCookTimeTotal() {
		// Nukkit stores the total cook time as "MaxTime" (see
		// BlockEntityFurnace#saveNBT), not as "CookTimeTotal".
		return getTagValue("MaxTime");
	}

	@Override
	public void setCookTimeTotal(int cookTimeTotal) {
		setTagValue("MaxTime", cookTimeTotal);
	}

	private int getTagValue(String key) {
		BlockEntityFurnace furnace = furnace();
		return furnace == null || furnace.namedTag == null ? 0 : furnace.namedTag.getShort(key);
	}

	private void setTagValue(String key, int value) {
		BlockEntityFurnace furnace = furnace();
		if (furnace != null && furnace.namedTag != null) {
			furnace.namedTag.putShort(key, (short) value);
		}
	}

	@Override
	public Map<CookingRecipe<?>, Integer> getRecipesUsed() {
		return Collections.emptyMap();
	}

	@Override
	public FurnaceInventory getInventory() {
		cn.nukkit.inventory.Inventory inventory = nukkitInventory();
		if (inventory instanceof cn.nukkit.inventory.FurnaceInventory furnaceInventory) {
			return new PokkitFurnaceInventory(furnaceInventory, this);
		}
		throw new IllegalStateException("The furnace at " + getLocation() + " has no inventory");
	}

	@Override
	public FurnaceInventory getSnapshotInventory() {
		// A detached furnace inventory would need a detached block entity; the
		// live inventory is returned instead.
		return getInventory();
	}
}

final class BlastFurnaceBlockState extends FurnaceBlockState implements BlastFurnace {

	BlastFurnaceBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, BlockEntity.BLAST_FURNACE, "Blast Furnace");
	}
}

final class SmokerBlockState extends FurnaceBlockState implements Smoker {

	SmokerBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, BlockEntity.SMOKER, "Smoker");
	}
}

/**
 * Live view on a Nukkit furnace inventory.
 */
final class PokkitFurnaceInventory extends PokkitLiveInventory implements FurnaceInventory {

	private final cn.nukkit.inventory.FurnaceInventory inventory;
	private final FurnaceBlockState holder;

	PokkitFurnaceInventory(cn.nukkit.inventory.FurnaceInventory inventory, FurnaceBlockState holder) {
		super(inventory);
		this.inventory = inventory;
		this.holder = holder;
	}

	@Override
	public ItemStack getResult() {
		return PokkitItemStack.toBukkitCopy(inventory.getResult());
	}

	@Override
	public ItemStack getFuel() {
		return PokkitItemStack.toBukkitCopy(inventory.getFuel());
	}

	@Override
	public ItemStack getSmelting() {
		return PokkitItemStack.toBukkitCopy(inventory.getSmelting());
	}

	@Override
	public void setFuel(ItemStack itemStack) {
		inventory.setFuel(PokkitItemStack.toNukkitCopy(itemStack));
	}

	@Override
	public void setResult(ItemStack itemStack) {
		inventory.setResult(PokkitItemStack.toNukkitCopy(itemStack));
	}

	@Override
	public void setSmelting(ItemStack itemStack) {
		inventory.setSmelting(PokkitItemStack.toNukkitCopy(itemStack));
	}

	@Override
	public Furnace getHolder() {
		return holder;
	}
}
