package nl.rutgerkok.pokkit.blockstate;

import java.util.List;
import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.world.PokkitBlock;
import nl.rutgerkok.pokkit.world.PokkitWorld;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class PokkitBlockState implements BlockState {

	/**
	 * Gets the block state for a given block.
	 *
	 * @param block
	 *            The block.
	 * @return The block state.
	 */
	public static PokkitBlockState getBlockState(PokkitBlock block) {
		Objects.requireNonNull(block, "block");

		cn.nukkit.block.Block nukkitBlock = PokkitBlock.toNukkit(block);

		BlockEntity blockEntityOrNull = nukkitBlock.getLevel().getBlockEntity(nukkitBlock);

		PokkitBlockData blockData = (PokkitBlockData) block.getBlockData();
		Location location = block.getLocation();
		Material type = blockData.getMaterial();
		String materialName = type.name();

		if (blockEntityOrNull == null && (materialName.endsWith("_SIGN") || materialName.endsWith("_WALL_SIGN"))
				&& nukkitBlock instanceof cn.nukkit.block.BlockEntityHolder<?> holder) {
			// Signs placed through Block#setType have no block entity yet.
			blockEntityOrNull = holder.getOrCreateBlockEntity();
		}

		if (blockEntityOrNull instanceof BlockEntitySign) {
			String hiddenData = blockEntityOrNull.namedTag.getString(Pokkit.NAME);
			String[] lines = ((BlockEntitySign) blockEntityOrNull).getText();
			String[] backLines = SignBlockState.readBackLines(blockEntityOrNull.namedTag);
			if (materialName.contains("HANGING_SIGN")) {
				return new HangingSignBlockState(location, blockData, lines, backLines, hiddenData,
						((BlockEntitySign) blockEntityOrNull).getColor(), blockEntityOrNull.namedTag);
			}
			return new SignBlockState(location, blockData, lines, backLines, hiddenData,
					((BlockEntitySign) blockEntityOrNull).getColor(), blockEntityOrNull.namedTag);
		}

		if (materialName.endsWith("SHULKER_BOX")) {
			return new ShulkerBoxBlockState(location, blockData);
		}
		if (materialName.endsWith("_BED")) {
			return new BedBlockState(location, blockData);
		}
		if (materialName.endsWith("_BANNER")) {
			return new BannerBlockState(location, blockData);
		}
		if ((materialName.endsWith("_HEAD") && !materialName.equals("PISTON_HEAD"))
				|| materialName.endsWith("_SKULL")) {
			return new SkullBlockState(location, blockData);
		}
		if (materialName.equals("BEEHIVE") || materialName.equals("BEE_NEST")) {
			return new BeehiveBlockState(location, blockData);
		}

		switch (type) {
		case CHEST:
		case TRAPPED_CHEST:
			return new ChestBlockState(location, blockData);
		case BARREL:
			return new BarrelBlockState(location, blockData);
		case HOPPER:
			return new HopperBlockState(location, blockData);
		case DROPPER:
			return new DropperBlockState(location, blockData);
		case DISPENSER:
			return new DispenserBlockState(location, blockData);
		case ENDER_CHEST:
			return new EnderChestBlockState(location, blockData);
		case LECTERN:
			return new LecternBlockState(location, blockData);
		case BEACON:
			return new BeaconBlockState(location, blockData);
		case FURNACE:
			return new FurnaceBlockState(location, blockData);
		case BLAST_FURNACE:
			return new BlastFurnaceBlockState(location, blockData);
		case SMOKER:
			return new SmokerBlockState(location, blockData);
		case BREWING_STAND:
			return new BrewingStandBlockState(location, blockData);
		case CAMPFIRE:
		case SOUL_CAMPFIRE:
			return new CampfireBlockState(location, blockData);
		case DAYLIGHT_DETECTOR:
			return new DaylightDetectorBlockState(location, blockData);
		case ENCHANTING_TABLE:
			return new EnchantingTableBlockState(location, blockData);
		case JIGSAW:
			return new JigsawBlockState(location, blockData);
		case BELL:
			return new BellBlockState(location, blockData);
		case CONDUIT:
			return new ConduitBlockState(location, blockData);
		default:
			return new PlainBlockState(location, blockData);
		}
	}

	public static BlockState fromBlockData(PokkitBlockData blockData) {
		return new PlainBlockState(null, blockData);
	}

	/**
	 * Gets a virtual block state.
	 *
	 * @param material
	 *            The material.
	 * @param tag
	 *            The block entity data.
	 * @return The vitual block state.
	 */
	public static PokkitBlockState getVirtual(Material material, CompoundTag tag) {
		PokkitBlockData materialData = PokkitBlockData.createBlockData(material, 0);

		if (material == Material.OAK_SIGN) {
			String[] lines = new String[] { tag.getString("Text1"), tag.getString("Text2"), tag.getString("Text3"),
					tag.getString("Text4") };
			String hiddenData = tag.getString(Pokkit.NAME);
			return new SignBlockState(null, materialData, lines, SignBlockState.readBackLines(tag), hiddenData, null,
					tag);
		}

		return new PlainBlockState(null, materialData);
	}

	private PokkitWorld worldOrNull;
	private final int x, y, z;
	private PokkitBlockData blockData;

	protected PokkitBlockState(Location locationOrNull, PokkitBlockData blockData) {
		if (locationOrNull == null) {
			this.worldOrNull = null;
			this.x = 0;
			this.y = 0;
			this.z = 0;
		} else {
			this.worldOrNull = (PokkitWorld) locationOrNull.getWorld();
			this.x = locationOrNull.getBlockX();
			this.y = locationOrNull.getBlockY();
			this.z = locationOrNull.getBlockZ();
		}

		this.blockData = Objects.requireNonNull(blockData);
	}

	@Override
	public PokkitBlock getBlock() {
		return getWorld().getBlockAt(x, y, z);
	}

	@Override
	public BlockData getBlockData() {
		return blockData;
	}

	/**
	 * Gets the Nukkit block entity.
	 *
	 * @return The Nukkit block entity.
	 */
	public BlockEntity getBlockEntity() {
		return PokkitWorld.toNukkit(getWorld()).getBlockEntity(new Vector3(x, y, z));
	}

	@Override
	public Chunk getChunk() {
		return getWorld().getChunkAt(getLocation());
	}

	@Deprecated
	@Override
	public org.bukkit.material.MaterialData getData() {
		return blockData.toMaterialData();
	}

	@Override
	public byte getLightLevel() {
		return getBlock().getLightLevel();
	}

	@Override
	public Location getLocation() {
		return new Location(getWorld(), x, y, z);
	}

	@Override
	public Location getLocation(Location loc) {
		loc.setWorld(getWorld());
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
		loc.setYaw(0);
		loc.setPitch(0);
		return loc;
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return getBlock().getMetadata(metadataKey);
	}

	@Override
	public byte getRawData() {
		return this.blockData.getNukkitData();
	}

	@Override
	public Material getType() {
		return blockData.getMaterial();
	}


	@Override
	public PokkitWorld getWorld() {
		PokkitWorld world = this.worldOrNull;
		if (world == null) {
			throw new IllegalStateException("block state is not placed in the world");
		}
		return world;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getBlock().hasMetadata(metadataKey);
	}

	@Override
	public boolean isPlaced() {
		return this.worldOrNull != null;
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		getBlock().removeMetadata(metadataKey, owningPlugin);
	}

	/**
	 * Saves the state of this block state to the given compound tag.
	 *
	 * @param tag
	 *            The tag.
	 */
	public abstract void saveToTag(CompoundTag tag);

	/**
	 * Writes this state to the given block entity.
	 *
	 * @param blockEntity
	 *            The block entity.
	 */
	public void saveToBlockEntity(BlockEntity blockEntity) {
		saveToTag(blockEntity.namedTag);
	}

	@Override
	public void setBlockData(BlockData data) {
		this.blockData = (PokkitBlockData) Objects.requireNonNull(data, "data");
	}

	@Override
	@Deprecated
	public void setData(org.bukkit.material.MaterialData data) {
		this.blockData = PokkitBlockData.createBlockData(data.getItemType(), data.getData());
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		getBlock().setMetadata(metadataKey, newMetadataValue);
	}

	@Override
	@Deprecated
	public void setRawData(byte data) {
		this.blockData = PokkitBlockData.createBlockData(getType(), data);
	}

	@Override
	public void setType(Material type) {
		this.blockData = PokkitBlockData.createBlockData(type, 0);
	}

	@Override
	public final boolean update() {
		return update(false);
	}

	@Override
	public final boolean update(boolean force) {
		return update(force, true);
	}

	@Override
	public final boolean update(boolean force, boolean applyPhysics) {
		PokkitBlock block = getBlock();
		if (!block.getBlockData().equals(this.blockData)) {
			// Block type has changed
			if (!force) {
				return false;
			}
			block.setBlockData(blockData, applyPhysics);
		}

		// Most blocks have no block entity; there is nothing left to update
		// for those.
		BlockEntity blockEntity = getBlockEntity();
		if (blockEntity == null) {
			return true;
		}

		// Update NBT data
		saveToBlockEntity(blockEntity);
		if (!(blockEntity instanceof BlockEntitySpawnable)) {
			if (blockEntity.chunk != null) {
				blockEntity.chunk.setChanged();
			}
			return false;
		}

		// Send updates
		((BlockEntitySpawnable) blockEntity).spawnToAll();
		if (blockEntity.chunk != null) {
			blockEntity.chunk.setChanged();
		}

		return true;
	}

}