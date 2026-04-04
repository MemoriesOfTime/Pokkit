package nl.rutgerkok.pokkit.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockstate.PokkitBlockState;
import nl.rutgerkok.pokkit.entity.PokkitEntity;
import nl.rutgerkok.pokkit.persistence.ChunkPersistentDataStore;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class PokkitChunk implements Chunk {

	private static final int CHUNK_SIZE = 16;

	public static Chunk of(FullChunk chunk) {
		return new PokkitChunk(chunk);
	}

	public static Chunk of(Block block) {
		return new PokkitChunk(block.getWorld(), block.getX() >> 4, block.getZ() >> 4);
	}

	public static Chunk of(Location location) {
		return new PokkitChunk(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
	}

	private final World world;
	private final FullChunk nukkit;

	PokkitChunk(FullChunk chunk) {
		this(PokkitWorld.toBukkit(chunk.getProvider().getLevel()), chunk.getX(), chunk.getZ());
	}

	PokkitChunk(World world, int chunkX, int chunkZ) {
		this.world = world;
		this.nukkit = PokkitWorld.toNukkit(world).getChunk(chunkX, chunkZ);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return world.getBlockAt((x & 0xf) + nukkit.getX() * CHUNK_SIZE, y, (z & 0xf) + nukkit.getZ() * CHUNK_SIZE);
	}

	@Override
	public ChunkSnapshot getChunkSnapshot() {
		return getChunkSnapshot(true, true, true);
	}

	@Override
	public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome,
			boolean includeBiomeTempRain) {
		return new PokkitChunkSnapshot(nukkit.getX(), nukkit.getZ(), world.getName(), PokkitWorld.toNukkit(world).getChunk(nukkit.getX(), nukkit.getZ()));
	}

	@Override
	public Entity[] getEntities() {
		Level level = PokkitWorld.toNukkit(world);
		List<Entity> entitiesInChunk = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : level.getChunk(nukkit.getX(), nukkit.getZ()).getEntities().values()) {
			entitiesInChunk.add(PokkitEntity.toBukkit(entity));
		}
		return entitiesInChunk.toArray(new Entity[0]);
	}

	@Override
	public BlockState[] getTileEntities() {
		return nukkit.getBlockEntities().values()
				.stream().map(blockEntity ->
						PokkitBlockState.getBlockState(PokkitBlock.toBukkit(blockEntity.getBlock())))
				.toArray(BlockState[]::new);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int getX() {
		return nukkit.getX();
	}

	@Override
	public int getZ() {
		return nukkit.getZ();
	}

	@Override
	public boolean isLoaded() {
		return world.isChunkLoaded(nukkit.getX(), nukkit.getZ());
	}

	@Override
	public boolean isSlimeChunk() {
		return false; // silently unsupported, there are no slimes yet in Nukkit
	}

	@Override
	public boolean isForceLoaded() {
		return false; // silently unsupported
	}

	@Override
	public void setForceLoaded(boolean b) {
		// silently unsupported
	}

	@Override
	public boolean addPluginChunkTicket(Plugin plugin) {
		return false;
	}

	@Override
	public boolean removePluginChunkTicket(Plugin plugin) {
		return false;
	}

	@Override
	public Collection<Plugin> getPluginChunkTickets() {
		return Collections.emptyList();
	}

	@Override
	public long getInhabitedTime() {
		return 0;
	}

	@Override
	public void setInhabitedTime(long l) {
	}

	@Override
	public boolean contains(BlockData blockData) {
		return false;
	}

	@Override
	public boolean load() {
		return world.loadChunk(nukkit.getX(), nukkit.getZ(), true);
	}

	@Override
	public boolean load(boolean generate) {
		return world.loadChunk(nukkit.getX(), nukkit.getZ(), generate);
	}

	@Override
	public boolean unload() {
		return world.unloadChunk(this);
	}

	@Override
	public boolean unload(boolean save) {
		return world.unloadChunk(nukkit.getX(), nukkit.getZ(), save);
	}

	@Override
	public Collection<Player> getPlayersSeeingChunk() {
		return world.getPlayersSeeingChunk(nukkit.getX(), nukkit.getZ());
	}

	@Override
	public Collection<org.bukkit.generator.structure.GeneratedStructure> getStructures(org.bukkit.generator.structure.Structure structure) {
		return Collections.emptyList();
	}

	@Override
	public Collection<org.bukkit.generator.structure.GeneratedStructure> getStructures() {
		return Collections.emptyList();
	}

	@Override
	public org.bukkit.Chunk.LoadLevel getLoadLevel() {
		return org.bukkit.Chunk.LoadLevel.ENTITY_TICKING;
	}

	@Override
	public boolean isEntitiesLoaded() {
		return true;
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

	@Override
	public boolean contains(org.bukkit.block.Biome biome) {
		return false;
	}

	@Override
	public org.bukkit.persistence.PersistentDataContainer getPersistentDataContainer() {
		return ChunkPersistentDataStore.getInstance().getContainer(
				world.getName(), nukkit.getX(), nukkit.getZ());
	}
}
