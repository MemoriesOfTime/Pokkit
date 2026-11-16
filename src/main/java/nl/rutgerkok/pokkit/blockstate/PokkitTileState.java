package nl.rutgerkok.pokkit.blockstate;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.persistence.PokkitPersistentDataContainer;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Base class for block states that are backed by a Nukkit block entity.
 */
public abstract class PokkitTileState extends PokkitBlockState implements TileState {

	private CompoundTag detachedTag;

	protected PokkitTileState(Location locationOrNull, PokkitBlockData blockData) {
		super(locationOrNull, blockData);
	}

	/**
	 * Gets the Nukkit block entity type of this block state, for example
	 * {@link BlockEntity#CHEST}.
	 *
	 * @return The block entity type, or null if this state has no block entity.
	 */
	protected abstract String blockEntityType();

	/**
	 * Gets the block entity backing this state, creating it when the block was
	 * placed without one (for example through {@code Block#setType}).
	 *
	 * @return The block entity, or null if it could not be created.
	 */
	protected BlockEntity tileEntity() {
		if (!isPlaced()) {
			return null;
		}
		BlockEntity entity = getBlockEntity();
		if (entity != null) {
			return entity;
		}
		String type = blockEntityType();
		if (type == null) {
			return null;
		}
		cn.nukkit.block.Block nukkitBlock = nl.rutgerkok.pokkit.world.PokkitBlock.toNukkit(getBlock());
		if (nukkitBlock instanceof cn.nukkit.block.BlockEntityHolder<?> holder) {
			return holder.getOrCreateBlockEntity();
		}
		return null;
	}

	/**
	 * Gets the NBT tag backing this state. Changes are written to the block
	 * entity, so they survive a save of the chunk.
	 *
	 * @return The tag, never null.
	 */
	protected final CompoundTag tileTag() {
		BlockEntity entity = tileEntity();
		if (entity != null && entity.namedTag != null) {
			return entity.namedTag;
		}
		if (detachedTag == null) {
			detachedTag = new CompoundTag();
		}
		return detachedTag;
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return new PokkitPersistentDataContainer(tileTag());
	}

	@Override
	public void saveToTag(cn.nukkit.nbt.tag.CompoundTag tag) {
		// The state writes directly to the block entity tag, so there is
		// nothing to copy when the chunk is saved.
	}

	@Override
	public void saveToBlockEntity(BlockEntity blockEntity) {
		if (detachedTag == null || blockEntity.namedTag == null) {
			return;
		}
		// Only copies created by copy(Location) have a detached tag. Apply
		// their tile data to the target block entity.
		for (cn.nukkit.nbt.tag.Tag entry : detachedTag.getAllTags()) {
			if (isTransientKey(entry.getName())) {
				continue;
			}
			blockEntity.namedTag.put(entry.getName(), entry.copy());
		}
		if (blockEntity.chunk != null) {
			blockEntity.chunk.setChanged();
		}
	}

	/**
	 * Keys that describe the block entity itself rather than its state. They
	 * must not be copied to another block entity.
	 */
	private static boolean isTransientKey(String key) {
		return key.equals("id") || key.equals("x") || key.equals("y") || key.equals("z")
				// Inventory contents need a full inventory copy; copying the
				// tag alone would be overwritten by the target's inventory.
				|| key.equals("Items") || key.equals("Item");
	}

	@Override
	public BlockState copy(Location location) {
		PokkitBlockData data = (PokkitBlockData) getBlockData().clone();
		try {
			// Every concrete tile state has a (Location, PokkitBlockData)
			// constructor, which keeps the copy the same type as the original.
			Constructor<? extends PokkitTileState> constructor = getClass().getDeclaredConstructor(Location.class,
					PokkitBlockData.class);
			constructor.setAccessible(true);
			PokkitTileState copy = constructor.newInstance(location, data);
			copy.detachedTag = tileTag().copy();
			return copy;
		} catch (ReflectiveOperationException e) {
			return new PlainBlockState(location, data);
		}
	}

	@Override
	public BlockState copy() {
		return copy(null);
	}
}
