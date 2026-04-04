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
		return ((PokkitBlock) block).nukkit;
	}

	private cn.nukkit.block.Block nukkit;

	/**
	 * Allows for changed drops.
	 */
	private List<ItemStack> drops;

	private PokkitBlock(cn.nukkit.block.Block nukkit) {
		this.nukkit = Objects.requireNonNull(nukkit, "nukkit");
	}

	@Override
	public boolean breakNaturally() {
		return nukkit.level.useBreakOn(nukkit) != null;
	}

	@Override
	public boolean breakNaturally(ItemStack tool) {
		return nukkit.level.useBreakOn(nukkit, PokkitItemStack.toNukkitCopy(tool)) != null;
	}

	@Override
	public Biome getBiome() {
		return PokkitBiome.toBukkit(nukkit.getLevel().getBiomeId(getX(), getZ()));
	}

	@Override
	public BlockData getBlockData() {
		return PokkitBlockData.toBukkit(nukkit);
	}

	private BlockMetadataStore getBlockMetadata() {
		return ((CraftServer) Bukkit.getServer()).getMetadata().getBlockMetadata();
	}

	@Override
	public int getBlockPower() {
		int power = 0;
		for (BlockFace face : BlockFace.values()) {
			power = Math.max(power, getBlockPower(face));
		}
		return power;
	}

	@Override
	public int getBlockPower(BlockFace face) {
		return nukkit.getStrongPower(PokkitBlockFace.toNukkit(face));
	}

	@Override
	public Chunk getChunk() {
		return PokkitChunk.of(this);
	}

	@Override
	public byte getData() {
		PokkitBlockData materialData = PokkitBlockData.toBukkit(nukkit);
		return materialData.getNukkitData();
	}

	@Override
	public Collection<ItemStack> getDrops() {
		return getDrops0(null);
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack tool) {
		return getDrops0(PokkitItemStack.toNukkitCopy(tool));
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack itemStack, Entity entity) {
		return getDrops0(PokkitItemStack.toNukkitCopy(itemStack));
	}

	private Collection<ItemStack> getDrops0(cn.nukkit.item.Item item) {
		if (this.drops != null) {
			return this.drops;
		}

		if (item == null) {
			item = new ItemBlock(new BlockAir(), 0, 0);
		}

		Item[] drops = nukkit.getDrops(item);
		List<ItemStack> result = new ArrayList<>();
		for (Item drop : drops) {
			ItemStack stack = PokkitItemStack.toBukkitCopy(drop);
			result.add(stack);
		}
		this.drops = result;
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
		int biomeId = nukkit.getLevel().getBiomeId(getX(), getZ());
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
		return (byte) nukkit.getLevel().getBlockLightAt(getX(), getY(), getZ());
	}

	@Override
	public byte getLightFromSky() {
		return (byte) nukkit.getLevel().getBlockSkyLightAt(getX(), getY(), getZ());
	}

	@Override
	public byte getLightLevel() {
		return (byte) nukkit.getLightLevel();
	}

	@Override
	public Location getLocation() {
		return new Location(PokkitWorld.toBukkit(nukkit.level), nukkit.x, nukkit.y, nukkit.z);
	}

	@Override
	public Location getLocation(Location loc) {
		loc.setWorld(PokkitWorld.toBukkit(nukkit.level));
		loc.setX(nukkit.x);
		loc.setY(nukkit.y);
		loc.setZ(nukkit.z);
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
		return PistonMoveReaction.MOVE;
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
		int biomeId = nukkit.getLevel().getBiomeId((int) nukkit.x, (int) nukkit.z);
		cn.nukkit.level.biome.Biome biome = EnumBiome.getBiome(biomeId);
		if (biome != null && biome.isFreezing()) {
			return 0.1;
		}
		return 0.6;
	}

	@Override
	public Material getType() {
		return PokkitBlockData.toBukkit(nukkit).getMaterial();
	}

	@Override
	public PokkitWorld getWorld() {
		return PokkitWorld.toBukkit(nukkit.level);
	}

	@Override
	public int getX() {
		return (int) nukkit.x;
	}

	@Override
	public int getY() {
		return (int) nukkit.y;
	}

	@Override
	public int getZ() {
		return (int) nukkit.z;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getBlockMetadata().hasMetadata(this, metadataKey);
	}

	@Override
	public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
		return isBlockIndirectlyPowered();
	}

	@Override
	public boolean isBlockFacePowered(BlockFace face) {
		return isBlockPowered();
	}

	@Override
	public boolean isBlockIndirectlyPowered() {
		return nukkit.level.isBlockIndirectlyGettingPowered(nukkit) > 0;
	}

	@Override
	public boolean isBlockPowered() {
		return nukkit.level.isBlockPowered(nukkit);
	}

	@Override
	public boolean isEmpty() {
		return nukkit.getId() == cn.nukkit.block.Block.AIR;
	}

	@Override
	public boolean isLiquid() {
		return nukkit instanceof cn.nukkit.block.BlockLiquid;
	}

	@Override
	public boolean isPassable() {
		return nukkit.canPassThrough();
	}

	@Override
	public RayTraceResult rayTrace(Location location, Vector vector, double v, FluidCollisionMode fluidCollisionMode) {
		return null;
	}

	@Override
	public boolean canPlace(BlockData blockData) {
		cn.nukkit.block.Block nukkitBlock = PokkitBlockData.toNukkit(blockData);
		return nukkitBlock.canPlaceOn(nukkit, Position.fromObject(nukkit, nukkit.getLevel()));
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(nukkit.getBoundingBox().getMinX(), nukkit.getBoundingBox().getMinY(), nukkit.getBoundingBox().getMinZ(), nukkit.getBoundingBox().getMaxX(), nukkit.getBoundingBox().getMaxY(), nukkit.getBoundingBox().getMaxZ());
	}

	@Override
	public org.bukkit.util.VoxelShape getCollisionShape() {
		return new org.bukkit.util.VoxelShape() {
			@Override
			public java.util.Collection<org.bukkit.util.BoundingBox> getBoundingBoxes() {
				return java.util.Collections.singletonList(getBoundingBox());
			}
			@Override
			public boolean overlaps(org.bukkit.util.BoundingBox boundingBox) {
				return getBoundingBox().overlaps(boundingBox);
			}
		};
	}

	@Override
	public float getBreakSpeed(Player player) {
		cn.nukkit.Player nukkitPlayer = nl.rutgerkok.pokkit.player.PokkitPlayer.toNukkit(player);
		cn.nukkit.item.Item tool = nukkitPlayer.getInventory().getItemInHand();
		double breakTime = nukkit.getBreakTime(tool, nukkitPlayer);
		if (breakTime <= 0) return Float.MAX_VALUE;
		return (float) (1.0 / breakTime);
	}

	@Override
	public boolean isPreferredTool(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return false;
		cn.nukkit.item.Item nukkitItem = nl.rutgerkok.pokkit.item.PokkitItemStack.toNukkitCopy(itemStack);
		double withTool = nukkit.calculateBreakTime(nukkitItem);
		double bareHand = nukkit.calculateBreakTime(cn.nukkit.item.Item.get(cn.nukkit.item.Item.AIR));
		return withTool < bareHand;
	}

	@Override
	public boolean applyBoneMeal(BlockFace face) {
		cn.nukkit.item.Item bonemeal = cn.nukkit.item.Item.get(cn.nukkit.item.Item.DYE, cn.nukkit.item.ItemDye.BONE_MEAL);
		return nukkit.onActivate(bonemeal, null);
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
		nukkit.getLevel().setBiomeId(getX(), getZ(), (byte) PokkitBiome.toNukkit(biome));
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

		PokkitBlockData materialData = PokkitBlockData.createBlockData(type, getData());
		setType0(materialData, applyPhysics);
	}

	private void setType0(PokkitBlockData materialData, boolean applyPhysics) {
		int nukkitId = materialData.getNukkitId();
		int nukkitData = materialData.getNukkitData();
		nukkit.level.setBlock(nukkit, cn.nukkit.block.Block.get(nukkitId, nukkitData), false, applyPhysics);

		// Update block reference
		nukkit = nukkit.level.getBlock(nukkit);
	}


}
