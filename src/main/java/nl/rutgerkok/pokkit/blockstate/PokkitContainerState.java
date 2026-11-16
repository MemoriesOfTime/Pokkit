package nl.rutgerkok.pokkit.blockstate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.inventory.PokkitLiveInventory;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Base class for container block states (chests, furnaces, hoppers, ...).
 *
 * <p>
 * The returned inventory is live: changes are written to the block entity. Use
 * {@link #getSnapshotInventory()} for a detached copy.
 */
public abstract class PokkitContainerState extends PokkitTileState implements Container, Nameable, Lockable {

	private final int size;
	private final String defaultTitle;
	private Inventory snapshotInventory;

	protected PokkitContainerState(Location locationOrNull, PokkitBlockData blockData, int size, String defaultTitle) {
		super(locationOrNull, blockData);
		this.size = size;
		this.defaultTitle = defaultTitle;
	}

	protected cn.nukkit.inventory.Inventory nukkitInventory() {
		if (!isPlaced()) {
			return null;
		}
		BlockEntity entity = tileEntity();
		if (entity instanceof InventoryHolder holder) {
			return holder.getInventory();
		}
		return null;
	}

	@Override
	public Inventory getInventory() {
		cn.nukkit.inventory.Inventory inventory = nukkitInventory();
		if (inventory != null) {
			return new PokkitLiveInventory(inventory);
		}
		// Bukkit documents that an unplaced state returns its captured
		// snapshot, not a brand-new empty inventory.
		return getSnapshotInventory();
	}

	@Override
	public Inventory getSnapshotInventory() {
		if (snapshotInventory == null) {
			snapshotInventory = Bukkit.createInventory(null, size, getTitle());
			cn.nukkit.inventory.Inventory inventory = nukkitInventory();
			if (inventory != null) {
				snapshotInventory.setContents(new PokkitLiveInventory(inventory).getContents());
			}
		}
		return snapshotInventory;
	}

	@Override
	public void saveToBlockEntity(BlockEntity blockEntity) {
		super.saveToBlockEntity(blockEntity);
		if (snapshotInventory == null || !(blockEntity instanceof InventoryHolder holder)) {
			return;
		}
		// Applying the snapshot is part of BlockState#update().
		new PokkitLiveInventory(holder.getInventory()).setContents(snapshotInventory.getContents());
	}

	public Inventory getBlockInventory() {
		return getInventory();
	}

	/**
	 * Gets the title of the inventory, which is the custom name when one was
	 * set.
	 *
	 * @return The title.
	 */
	protected String getTitle() {
		String customName = getCustomName();
		return customName == null ? defaultTitle : customName;
	}

	@Override
	public String getCustomName() {
		BlockEntity entity = tileEntity();
		if (entity instanceof BlockEntityNameable nameable && nameable.hasName()) {
			return nameable.getName();
		}
		return null;
	}

	@Override
	public void setCustomName(String name) {
		BlockEntity entity = tileEntity();
		if (entity instanceof BlockEntityNameable nameable) {
			nameable.setName(name);
		}
	}

	@Override
	public boolean isLocked() {
		String lock = getLock();
		return lock != null && !lock.isEmpty();
	}

	@Override
	public String getLock() {
		CompoundTag tag = tileTag();
		return tag.contains("Lock") ? tag.getString("Lock") : null;
	}

	@Override
	public void setLock(String lock) {
		if (lock == null || lock.isEmpty()) {
			tileTag().remove("Lock");
		} else {
			tileTag().putString("Lock", lock);
		}
	}

	public LootTable getLootTable() {
		// Nukkit does not expose loot tables for block entities.
		return null;
	}

	public void setLootTable(LootTable table) {
		if (table != null) {
			throw Pokkit.unsupported();
		}
	}

	public long getSeed() {
		return 0;
	}

	public void setSeed(long seed) {
		// No loot table, so the seed has no effect.
	}

	/**
	 * Nukkit has no server-side chest animation, so opening and closing are
	 * no-ops.
	 */
	public void open() {
	}

	public void close() {
	}
}
