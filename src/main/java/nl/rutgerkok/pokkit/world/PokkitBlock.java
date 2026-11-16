package nl.rutgerkok.pokkit.world;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_99_R9.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.blockstate.PokkitBlockState;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.metadata.BlockMetadataStore;
import nl.rutgerkok.pokkit.world.biome.PokkitBiome;

import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.EnumBiome;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Converts between Nukkit and Bukkit blocks.
 *
 */
public final class PokkitBlock implements Block {

	public static final PokkitBlock toBukkit(cn.nukkit.block.Block nukkit) {
		if (nukkit == null) {
			return null;
		}
		return new PokkitBlock(nukkit);
	}

	public static final cn.nukkit.block.Block toNukkit(Block block) {
		if (block == null) {
			return null;
		}
		return ((PokkitBlock) block).nukkit();
	}

	private cn.nukkit.block.Block handle;

	/**
	 * Gets the Nukkit block this handle points at.
	 *
	 * <p>
	 * The block is re-read from the level, because the level replaces the block
	 * object when a block changes. Keeping the old object would make this
	 * handle report the previous type after the block was changed through
	 * another handle, which does not happen in Bukkit.
	 *
	 * @return The Nukkit block.
	 */
	private cn.nukkit.block.Block nukkit() {
		if (handle.level != null) {
			cn.nukkit.block.Block current = handle.level.getBlock(handle);
			if (current != null) {
				handle = current;
			}
		}
		return handle;
	}

	private PokkitBlock(cn.nukkit.block.Block nukkit) {
		this.handle = Objects.requireNonNull(nukkit, "nukkit");
	}

	@Override
	public boolean breakNaturally() {
		return nukkit().level.useBreakOn(nukkit()) != null;
	}

	@Override
	public boolean breakNaturally(ItemStack tool) {
		return nukkit().level.useBreakOn(nukkit(), PokkitItemStack.toNukkitCopy(tool)) != null;
	}

	@Override
	public Biome getBiome() {
		return PokkitBiome.toBukkit(nukkit().getLevel().getBiomeId(getX(), getZ()));
	}

	@Override
	public BlockData getBlockData() {
		// Return a copy: changing the returned block data must not change the
		// block in the world, just like in Bukkit.
		return PokkitBlockData.toBukkit(nukkit()).clone();
	}

	private BlockMetadataStore getBlockMetadata() {
		return ((CraftServer) Bukkit.getServer()).getMetadata().getBlockMetadata();
	}

	@Override
	public int getBlockPower() {
		int power = 0;
		for (BlockFace face : PokkitBlockFace.CARDINAL_FACES) {
			power = Math.max(power, getBlockPower(face));
		}
		return power;
	}

	@Override
	public int getBlockPower(BlockFace face) {
		Objects.requireNonNull(face, "face");
		if (face == BlockFace.SELF) {
			// Bukkit documents SELF as "the block itself", which is the power
			// this block emits (a redstone block, wire or torch).
			int power = 0;
			for (BlockFace cardinal : PokkitBlockFace.CARDINAL_FACES) {
				cn.nukkit.math.BlockFace nukkitFace = PokkitBlockFace.toNukkitOrNull(cardinal);
				power = Math.max(power, nukkit().getWeakPower(nukkitFace));
				power = Math.max(power, nukkit().getStrongPower(nukkitFace));
			}
			return power;
		}
		cn.nukkit.math.BlockFace nukkitFace = PokkitBlockFace.toNukkitOrNull(face);
		if (nukkitFace == null) {
			// Bukkit allows the diagonals here, but redstone power only
			// travels along the six cardinal faces.
			return 0;
		}
		// The block on the given face is the one that powers this block, so
		// ask it how much power it emits towards this block.
		cn.nukkit.block.Block neighbour = nukkit().getSide(nukkitFace);
		cn.nukkit.math.BlockFace towardsThis = nukkitFace.getOpposite();
		return Math.max(neighbour.getWeakPower(towardsThis), neighbour.getStrongPower(towardsThis));
	}

	@Override
	public Chunk getChunk() {
		return PokkitChunk.of(this);
	}

	@Override
	public byte getData() {
		PokkitBlockData materialData = PokkitBlockData.toBukkit(nukkit());
		return materialData.getNukkitData();
	}

	@Override
	public Collection<ItemStack> getDrops() {
		return getDrops0(null);
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack tool) {
		// The tool is nullable: null means "without a specific tool".
		return getDrops0(tool == null ? null : PokkitItemStack.toNukkitCopy(tool));
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack itemStack, Entity entity) {
		return getDrops0(PokkitItemStack.toNukkitCopy(itemStack));
	}

	private Collection<ItemStack> getDrops0(cn.nukkit.item.Item item) {
		if (item == null) {
			item = new ItemBlock(new BlockAir(), 0, 0);
		}

		Item[] drops = nukkit().getDrops(item);
		List<ItemStack> result = new ArrayList<>();
		for (Item drop : drops) {
			ItemStack stack = PokkitItemStack.toBukkitCopy(drop);
			if (stack != null) {
				result.add(stack);
			}
		}
		return result;
	}

	@Override
	public BlockFace getFace(Block block) {
		for (BlockFace face : BlockFace.values()) {
			if (block.getX() == this.getX() + face.getModX() && block.getY() == this.getY() + face.getModY()
					&& block.getZ() == this.getZ() + face.getModZ()) {
				return face;
			}
		}
		return null;
	}

	@Override
	public double getHumidity() {
		int biomeId = nukkit().getLevel().getBiomeId(getX(), getZ());
		cn.nukkit.level.biome.Biome biome = EnumBiome.getBiome(biomeId);
		if (biome != null) {
			if (biome.isFreezing()) return 0.3;
			if (biome.canRain()) return 0.8;
			return 0.0;
		}
		return 0.5;
	}

	@Override
	public byte getLightFromBlocks() {
		return (byte) nukkit().getLevel().getBlockLightAt(getX(), getY(), getZ());
	}

	@Override
	public byte getLightFromSky() {
		return (byte) nukkit().getLevel().getBlockSkyLightAt(getX(), getY(), getZ());
	}

	@Override
	public byte getLightLevel() {
		// The light level at the block, not the light this block emits. Nukkit
		// combines the sky light (corrected for the time of day) and the block
		// light in Level#getFullLight, which is what Bukkit returns here.
		cn.nukkit.level.Level level = nukkit().getLevel();
		return level == null ? 0 : (byte) level.getFullLight(nukkit());
	}

	@Override
	public Location getLocation() {
		return new Location(PokkitWorld.toBukkit(nukkit().level), nukkit().x, nukkit().y, nukkit().z);
	}

	@Override
	public Location getLocation(Location loc) {
		loc.setWorld(PokkitWorld.toBukkit(nukkit().level));
		loc.setX(nukkit().x);
		loc.setY(nukkit().y);
		loc.setZ(nukkit().z);
		loc.setYaw(0f);
		loc.setPitch(0f);
		return loc;
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return getBlockMetadata().getMetadata(this, metadataKey);
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		// Obsidian is not pushed, torches break when moved, and so on.
		return getBlockData().getPistonMoveReaction();
	}

	@Override
	public Block getRelative(BlockFace face) {
		return getRelative(face.getModX(), face.getModY(), face.getModZ());
	}

	@Override
	public Block getRelative(BlockFace face, int distance) {
		return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
	}

	@Override
	public Block getRelative(int modX, int modY, int modZ) {
		return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
	}

	@Override
	public BlockState getState() {
		return PokkitBlockState.getBlockState(this);
	}

	@Override
	public double getTemperature() {
		int biomeId = nukkit().getLevel().getBiomeId((int) nukkit().x, (int) nukkit().z);
		cn.nukkit.level.biome.Biome biome = EnumBiome.getBiome(biomeId);
		if (biome != null && biome.isFreezing()) {
			return 0.1;
		}
		return 0.6;
	}

	@Override
	public Material getType() {
		return PokkitBlockData.toBukkit(nukkit()).getMaterial();
	}

	@Override
	public PokkitWorld getWorld() {
		return PokkitWorld.toBukkit(nukkit().level);
	}

	@Override
	public int getX() {
		return (int) nukkit().x;
	}

	@Override
	public int getY() {
		return (int) nukkit().y;
	}

	@Override
	public int getZ() {
		return (int) nukkit().z;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getBlockMetadata().hasMetadata(this, metadataKey);
	}

	@Override
	public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
		Objects.requireNonNull(face, "face");
		cn.nukkit.math.BlockFace nukkitFace = PokkitBlockFace.toNukkitOrNull(face);
		if (nukkitFace == null) {
			return false;
		}
		cn.nukkit.block.Block neighbour = nukkit().getSide(nukkitFace);
		return neighbour.getStrongPower(nukkitFace.getOpposite()) > 0;
	}

	@Override
	public boolean isBlockFacePowered(BlockFace face) {
		return getBlockPower(face) > 0;
	}

	@Override
	public boolean isBlockIndirectlyPowered() {
		return nukkit().level.isBlockIndirectlyGettingPowered(nukkit()) > 0;
	}

	@Override
	public boolean isBlockPowered() {
		return nukkit().level.isBlockPowered(nukkit());
	}

	@Override
	public boolean isEmpty() {
		return nukkit().getId() == cn.nukkit.block.Block.AIR;
	}

	@Override
	public boolean isLiquid() {
		return nukkit() instanceof cn.nukkit.block.BlockLiquid;
	}

	@Override
	public boolean isPassable() {
		return nukkit().canPassThrough();
	}

	@Override
	public RayTraceResult rayTrace(Location location, Vector direction, double maxDistance,
			FluidCollisionMode fluidCollisionMode) {
		Objects.requireNonNull(location, "location");
		Objects.requireNonNull(direction, "direction");
		Objects.requireNonNull(fluidCollisionMode, "fluidCollisionMode");
		if (maxDistance < 0) {
			throw new IllegalArgumentException("maxDistance must not be negative");
		}
		if (fluidCollisionMode == FluidCollisionMode.NEVER && isLiquid()) {
			// The caller does not want to hit fluids.
			return null;
		}

		BoundingBox box = getBoundingBox();
		if (box.getWidthX() <= 0 || box.getHeight() <= 0 || box.getWidthZ() <= 0) {
			// Air and other blocks without a collision box cannot be hit.
			return null;
		}
		if (direction.lengthSquared() == 0) {
			return null;
		}
		Vector ray = direction.clone().normalize();

		double[] origin = { location.getX(), location.getY(), location.getZ() };
		double[] delta = { ray.getX(), ray.getY(), ray.getZ() };
		double[] min = { box.getMinX(), box.getMinY(), box.getMinZ() };
		double[] max = { box.getMaxX(), box.getMaxY(), box.getMaxZ() };
		BlockFace[] negativeFaces = { BlockFace.WEST, BlockFace.DOWN, BlockFace.NORTH };
		BlockFace[] positiveFaces = { BlockFace.EAST, BlockFace.UP, BlockFace.SOUTH };

		// Slab method: clip the ray against the three axis-aligned slabs of
		// the bounding box and keep the last entry and first exit point.
		double enter = 0.0D;
		double exit = maxDistance;
		BlockFace hitFace = null;
		for (int axis = 0; axis < 3; axis++) {
			if (Math.abs(delta[axis]) < 1.0E-9D) {
				if (origin[axis] < min[axis] || origin[axis] > max[axis]) {
					return null;
				}
				continue;
			}
			double near = (min[axis] - origin[axis]) / delta[axis];
			double far = (max[axis] - origin[axis]) / delta[axis];
			BlockFace nearFace = negativeFaces[axis];
			BlockFace farFace = positiveFaces[axis];
			if (near > far) {
				double swap = near;
				near = far;
				far = swap;
				BlockFace swapFace = nearFace;
				nearFace = farFace;
				farFace = swapFace;
			}
			if (near > enter) {
				enter = near;
				hitFace = nearFace;
			}
			if (far < exit) {
				exit = far;
			}
			if (enter > exit) {
				return null;
			}
		}
		if (enter > maxDistance || exit < 0) {
			return null;
		}

		Vector hitPosition = new Vector(origin[0] + delta[0] * enter, origin[1] + delta[1] * enter,
				origin[2] + delta[2] * enter);
		return new RayTraceResult(hitPosition, this, hitFace);
	}

	@Override
	public boolean canPlace(BlockData blockData) {
		Objects.requireNonNull(blockData, "blockData");
		cn.nukkit.block.Block current = nukkit();
		if (!current.canBeReplaced()) {
			// Something solid is already occupying this position.
			return false;
		}
		cn.nukkit.level.Level level = current.getLevel();
		if (level == null) {
			return false;
		}
		// canPlaceOn checks whether the block can be placed on the support
		// below this position, which is what "can this data be placed here"
		// means for most blocks.
		cn.nukkit.block.Block placed = PokkitBlockData.toNukkit(blockData);
		cn.nukkit.block.Block support = current.getSide(cn.nukkit.math.BlockFace.DOWN);
		return placed.canPlaceOn(support, Position.fromObject(current, level));
	}

	@Override
	public BoundingBox getBoundingBox() {
		cn.nukkit.math.AxisAlignedBB box = nukkit().getBoundingBox();
		if (box == null) {
			// Air, plants and other blocks without a collision box have no
			// bounding box in Nukkit. Return an empty box at the block
			// position instead of throwing a NullPointerException.
			return new BoundingBox(getX(), getY(), getZ(), getX(), getY(), getZ());
		}
		return new BoundingBox(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(),
				box.getMaxZ());
	}

	@Override
	public org.bukkit.util.VoxelShape getCollisionShape() {
		BoundingBox box = getBoundingBox();
		boolean empty = box.getWidthX() <= 0 || box.getHeight() <= 0 || box.getWidthZ() <= 0;
		return new org.bukkit.util.VoxelShape() {
			@Override
			public java.util.Collection<org.bukkit.util.BoundingBox> getBoundingBoxes() {
				return empty ? java.util.Collections.emptyList() : java.util.Collections.singletonList(box);
			}
			@Override
			public boolean overlaps(org.bukkit.util.BoundingBox boundingBox) {
				return !empty && box.overlaps(boundingBox);
			}
		};
	}

	@Override
	public float getBreakSpeed(Player player) {
		cn.nukkit.Player nukkitPlayer = nl.rutgerkok.pokkit.player.PokkitPlayer.toNukkit(player);
		cn.nukkit.item.Item tool = nukkitPlayer.getInventory().getItemInHand();
		double breakTime = nukkit().getBreakTime(tool, nukkitPlayer);
		if (breakTime <= 0) return Float.MAX_VALUE;
		return (float) (1.0 / breakTime);
	}

	@Override
	public boolean isPreferredTool(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return false;
		cn.nukkit.item.Item nukkitItem = nl.rutgerkok.pokkit.item.PokkitItemStack.toNukkitCopy(itemStack);
		double withTool = nukkit().calculateBreakTime(nukkitItem);
		double bareHand = nukkit().calculateBreakTime(cn.nukkit.item.Item.get(cn.nukkit.item.Item.AIR));
		return withTool < bareHand;
	}

	@Override
	public boolean applyBoneMeal(BlockFace face) {
		cn.nukkit.item.Item bonemeal = cn.nukkit.item.Item.get(cn.nukkit.item.Item.DYE, cn.nukkit.item.ItemDye.BONE_MEAL);
		return nukkit().onActivate(bonemeal, null);
	}

	@Override
	public String getTranslationKey() {
		return "block." + getType().getKey().getNamespace() + "." + getType().getKey().getKey();
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
	}

	@Override
	public void setBiome(Biome biome) {
		nukkit().getLevel().setBiomeId(getX(), getZ(), (byte) PokkitBiome.toNukkit(biome));
	}

	@Override
	public void setBlockData(BlockData data) {
		setBlockData(data, true);
	}

	@Override
	public void setBlockData(BlockData data, boolean applyPhysics) {
		this.setType0((PokkitBlockData) data, applyPhysics);
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
	}

	@Override
	public void setType(Material type) {
		setType(type, true);
	}

	@Override
	public void setType(Material type, boolean applyPhysics) {
		if (type == getType()) {
			return;
		}
		if (type == null) {
			type = Material.AIR;
		}

		// Use the default state of the new material: carrying over the data
		// value of the old block would turn granite into diorite when the old
		// block was diorite.
		PokkitBlockData materialData = PokkitBlockData.createBlockData(type, 0);
		setType0(materialData, applyPhysics);
	}

	private void setType0(PokkitBlockData materialData, boolean applyPhysics) {
		int nukkitId = materialData.getNukkitId();
		int nukkitData = materialData.getNukkitData();
		nukkit().level.setBlock(nukkit(), cn.nukkit.block.Block.get(nukkitId, nukkitData), false, applyPhysics);

		// Update block reference
		handle = nukkit().level.getBlock(nukkit());
	}


}
