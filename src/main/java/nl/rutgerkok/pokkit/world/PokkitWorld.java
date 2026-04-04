package nl.rutgerkok.pokkit.world;

import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.SimpleAxisAlignedBB;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.entity.PokkitItemEntity;
import org.bukkit.*;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_99_R9.CraftServer;
import org.bukkit.entity.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import java.util.function.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.PokkitSound;
import nl.rutgerkok.pokkit.PokkitVector;
import nl.rutgerkok.pokkit.UniqueIdConversion;
import nl.rutgerkok.pokkit.entity.PokkitEntity;
import nl.rutgerkok.pokkit.entity.PokkitEntityLightningStrike;
import nl.rutgerkok.pokkit.entity.PokkitEntityTranslator;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.metadata.WorldMetadataStore;
import nl.rutgerkok.pokkit.particle.PokkitParticle;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import nl.rutgerkok.pokkit.world.biome.PokkitBiome;

import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;

public final class PokkitWorld implements World {

	/**
	 * World cache. If Nukkit ever adds world unload support, we'll need to
	 * remove the corresponding PokkitWorld instance from here too.
	 */
	private static final HashMap<Level, PokkitWorld> pokkitWorldCache = new HashMap<>();

	public static PokkitWorld toBukkit(Level level) {
		if (level == null) {
			return null;
		}
		// Some plugins uses World as a reference, if we do new PokkitWorld(level), the references aren't going to be the same
		// http://i.imgur.com/erAyiV8.png
		// This caches the PokkitWorld so this issue doesn't happen
		if (pokkitWorldCache.containsKey(level)) {
			return pokkitWorldCache.get(level);
		}
		PokkitWorld pokkitWorld = new PokkitWorld(level);
		pokkitWorldCache.put(level, pokkitWorld);
		return pokkitWorld;
	}

	public static Level toNukkit(World world) {
		if (world == null) {
			return null;
		}
		return ((PokkitWorld) world).nukkit;
	}

	private final World.Spigot spigot;
	private final Level nukkit;
	private final Map<Long, Set<Plugin>> pluginChunkTickets = new HashMap<>();
	private final Set<Long> forceLoadedChunks = new HashSet<>();

	private PokkitWorld(Level nukkit) {
		this.nukkit = Objects.requireNonNull(nukkit);
		this.spigot = new World.Spigot() {
			@Override
			public LightningStrike strikeLightning(Location loc, boolean isSilent) {
				return strike(loc, false);
			}

			@Override
			public LightningStrike strikeLightningEffect(Location loc, boolean isSilent) {
				return strike(loc, true);
			}
		};
	}

	@Override
	public boolean canGenerateStructures() {
		return nukkit.getGenerator() != null;
	}

	@Override
	public boolean isHardcore() {
		return cn.nukkit.Server.getInstance().isHardcore();
	}

	@Override
	public void setHardcore(boolean b) {
		cn.nukkit.Server.getInstance().setPropertyBoolean("hardcore", b);
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power) {
		return this.createExplosion(null, x, y, z, power, false, true);
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
		return this.createExplosion(null, x, y, z, power, setFire, true);
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
		return this.createExplosion(null, x, y, z, power, setFire, breakBlocks);
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, Entity entity) {
		return this.createExplosion(null, x, y, z, power, setFire, breakBlocks);
	}

	private boolean createExplosion(Level level, double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
		// Base function called by all other createExplosion functions
		// Nukkit does not yet support setFire
		if (level == null) {
			level = nukkit;
		}
		Explosion explosion = new Explosion(
			new Position(x, y, z, level),
			power,
			(cn.nukkit.entity.Entity) null);
		if (breakBlocks) {
			explosion.explodeA();
		}
		explosion.explodeB();
		return true;
	}

	@Override
	public boolean createExplosion(Location loc, float power) {
		cn.nukkit.level.Location l = PokkitLocation.toNukkit(loc);
		return this.createExplosion(l.level, l.x, l.y, l.z, power, false, true);
	}

	@Override
	public boolean createExplosion(Location loc, float power, boolean setFire) {
		cn.nukkit.level.Location l = PokkitLocation.toNukkit(loc);
		return this.createExplosion(l.level, l.x, l.y, l.z, power, setFire, true);
	}

	@Override
	public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks) {
		cn.nukkit.level.Location l = PokkitLocation.toNukkit(loc);
		return this.createExplosion(l.level, l.x, l.y, l.z, power, setFire, breakBlocks);
	}

	@Override
	public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
		cn.nukkit.level.Location l = PokkitLocation.toNukkit(loc);
		return this.createExplosion(l.level, l.x, l.y, l.z, power, setFire, breakBlocks);
	}

	@Override
	public Item dropItem(Location location, ItemStack item) {
		cn.nukkit.level.Location nkl = PokkitLocation.toNukkit(location);
		cn.nukkit.item.Item nki = PokkitItemStack.toNukkitCopy(item);
		nukkit.dropItem(nkl, nki, new Vector3(0, 0, 0));
		return new PokkitItemEntity(nki, nkl);
	}

	@Override
	public Item dropItemNaturally(Location location, ItemStack item) {
		cn.nukkit.level.Location nkl = PokkitLocation.toNukkit(location);
		cn.nukkit.item.Item nki = PokkitItemStack.toNukkitCopy(item);
		nukkit.dropItem(nkl, nki);
		return new PokkitItemEntity(nki, nkl);
	}

	@Override
	public boolean generateTree(Location location, TreeType type) {
		switch (type) {
			case TREE:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 0);
				return true;
			case REDWOOD:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 1);
				return true;
			case BIRCH:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 2);
				return true;
			case SMALL_JUNGLE:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 3);
				return true;
			case ACACIA:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 4);
				return true;
			case DARK_OAK:
				ObjectTree.growTree(nukkit, location.getBlockX(), location.getBlockY(), location.getBlockZ(), new NukkitRandom(), 5);
				return true;
			default:
				return false;
		}
	}

	@Override
	@Deprecated
	public boolean generateTree(Location loc, TreeType type, org.bukkit.BlockChangeDelegate delegate) {
		return generateTree(loc, type);
	}

	@Override
	public boolean getAllowAnimals() {
		return cn.nukkit.Server.getInstance().spawnAnimals;
	}

	@Override
	public boolean getAllowMonsters() {
		return cn.nukkit.Server.getInstance().spawnMonsters;
	}

	@Override
	public int getAmbientSpawnLimit() {
		return -1;
	}

	@Override
	public int getAnimalSpawnLimit() {
		return cn.nukkit.Server.getInstance().spawnAnimals ? -1 : 0;
	}

	@Override
	public Biome getBiome(int x, int z) {
		return PokkitBiome.toBukkit(nukkit.getBiomeId(x, z));
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		return PokkitBiome.toBukkit(nukkit.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 15, y, z & 15));
	}

	@Override
	public PokkitBlock getBlockAt(int x, int y, int z) {
		return PokkitBlock.toBukkit(nukkit.getBlock(new Vector3(x, y, z)));
	}

	@Override
	public Block getBlockAt(Location location) {
		return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	@Override
	public Chunk getChunkAt(Block block) {
		return PokkitChunk.of(block);
	}

	@Override
	public Chunk getChunkAt(int x, int z) {
		return new PokkitChunk(this, x, z);
	}

	@Override
	public Chunk getChunkAt(Location location) {
		return PokkitChunk.of(location);

	}

	@Override
	public Difficulty getDifficulty() {
		switch (Server.getInstance().getDifficulty()) {
			case 0:
				return Difficulty.PEACEFUL;
			case 1:
				return Difficulty.EASY;
			case 2:
				return Difficulty.NORMAL;
			default:
				return Difficulty.HARD;
		}
	}

	@Override
	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
		return new PokkitChunkSnapshot(x, z, nukkit.getName(), nukkit.getProvider().getEmptyChunk(x, z));
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> entitiesInChunk = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : nukkit.getEntities()) {
			entitiesInChunk.add(PokkitEntity.toBukkit(entity));
		}
		return entitiesInChunk;
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
		Collection<T> entitiesInChunk = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : nukkit.getEntities()) {
			Entity bukkit = PokkitEntity.toBukkit(entity);

			c:
			for (Class<?> cls : classes) {
				if (cls.isAssignableFrom(bukkit.getClass())) {
					entitiesInChunk.add((T) bukkit);
					break c;
				}
			}
		}
		return entitiesInChunk;
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls) {
		List<T> entitiesInChunk = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : nukkit.getEntities()) {
			Entity bukkit = PokkitEntity.toBukkit(entity);
			if (cls.isAssignableFrom(bukkit.getClass())) {
				entitiesInChunk.add((T) bukkit);
			}
		}
		return entitiesInChunk;
	}

	@Override
	public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
		List<Entity> entitiesInChunk = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : nukkit.getEntities()) {
			Entity bukkit = PokkitEntity.toBukkit(entity);

			c:
			for (Class<?> cls : classes) {
				if (cls.isAssignableFrom(bukkit.getClass())) {
					entitiesInChunk.add(bukkit);
					break c;
				}
			}
		}
		return entitiesInChunk;
	}

	@Override
	public Environment getEnvironment() {
		int dimId = nukkit.getDimension();
		switch (dimId) {
			case 1: return Environment.NETHER;
			case 2: return Environment.THE_END;
			default: return Environment.NORMAL;
		}
	}

	@Override
	public long getFullTime() {
		return nukkit.getTime();
	}

	@Override
	public <T> T getGameRuleDefault(GameRule<T> rule) {
		return getGameRuleValue0(rule, GameRules.getDefault());
	}

	@Override
	public String[] getGameRules() {
		cn.nukkit.level.GameRule[] rules = this.nukkit.getGameRules().getRules();
		String[] ruleStrings = new String[rules.length];
		for (int i = 0; i < ruleStrings.length; i++) {
			ruleStrings[i] = rules[i].getName();
		}
		return ruleStrings;
	}

	@Override
	public <T> T getGameRuleValue(GameRule<T> rule) {
		return getGameRuleValue0(rule, this.nukkit.getGameRules());
	}

	@Override
	public String getGameRuleValue(String stringRule) {
		if (stringRule == null) {
			return null;
		}
		GameRule<?> rule = GameRule.getByName(stringRule);
		if (rule == null) {
			return null;
		}
		return Objects.toString(getGameRuleValue(rule));
	}

	@SuppressWarnings("unchecked")
	private <T> T getGameRuleValue0(GameRule<T> rule, GameRules source) {
		cn.nukkit.level.GameRule nukkitRule = PokkitGameRule.toNukkit(rule);
		if (nukkitRule == null) {
			if (rule.getType().equals(Integer.class)) {
				return (T) Integer.valueOf(0);
			}
			if (rule.getType().equals(Boolean.class)) {
				return (T) Boolean.FALSE;
			}
			throw new IllegalArgumentException("Unknown GameRule with unknown type: " + rule);
		}

		Class<?> type = rule.getType();
		if (type.equals(Boolean.class)) {
			return (T) Boolean.valueOf(source.getBoolean(nukkitRule));
		}
		if (type.equals(Integer.class)) {
			return (T) Integer.valueOf(source.getInteger(nukkitRule));
		}
		throw new IllegalArgumentException("GameRule with unknown type: " + rule + " of type " + type);
	}

	@Override
	public ChunkGenerator getGenerator() {
		return null;
	}

	@Override
	public Block getHighestBlockAt(int x, int z) {
		return PokkitBlock.toBukkit(nukkit.getBlock(new Vector3(x, getHighestBlockYAt(x, z), z)));
	}

	@Override
	public Block getHighestBlockAt(Location location) {
		return PokkitBlock.toBukkit(nukkit.getBlock(new Vector3(location.getX(), getHighestBlockYAt(location.getBlockX(), location.getBlockZ()), location.getZ())));
	}

	@Override
	public int getHighestBlockYAt(int x, int z, HeightMap heightMap) {
		return nukkit.getHighestBlockAt(x, z);
	}

	@Override
	public int getHighestBlockYAt(Location location, HeightMap heightMap) {
		return nukkit.getHighestBlockAt(location.getBlockX(), location.getBlockZ());
	}

	@Override
	public Block getHighestBlockAt(int x, int z, HeightMap heightMap) {
		return getBlockAt(x, getHighestBlockYAt(x, z, heightMap), z);
	}

	@Override
	public Block getHighestBlockAt(Location location, HeightMap heightMap) {
		return getBlockAt(location.getBlockX(), getHighestBlockYAt(location, heightMap), location.getBlockZ());
	}

	@Override
	public int getHighestBlockYAt(int x, int z) {
		return nukkit.getHighestBlockAt(x, z);
	}

	@Override
	public int getHighestBlockYAt(Location location) {
		return nukkit.getHighestBlockAt(location.getBlockX(), location.getBlockZ());
	}

	@Override
	public double getHumidity(int x, int z) {
		int biomeId = nukkit.getBiomeId(x, z);
		cn.nukkit.level.biome.Biome biome = EnumBiome.getBiome(biomeId);
		if (biome != null) {
			if (biome.isFreezing()) return 0.3;
			if (biome.canRain()) return 0.8;
			return 0.0;
		}
		return 0.5;
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return getHumidity(x, z);
	}

	@Override
	public boolean getKeepSpawnInMemory() {
		return nukkit.isSpawnChunk(nukkit.getSpawnLocation().getChunkX(), nukkit.getSpawnLocation().getChunkZ());
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return org.bukkit.Bukkit.getMessenger().getIncomingChannels();
	}

	@Override
	public List<LivingEntity> getLivingEntities() {
		List<LivingEntity> livingEntities = new ArrayList<>();

		for (cn.nukkit.entity.Entity entity : nukkit.getEntities()) {
			if (entity instanceof cn.nukkit.entity.EntityLiving) {
		 		livingEntities.add((LivingEntity) PokkitEntity.toBukkit(entity));
			}
		}

		return livingEntities;
	}

	@Override
	public Chunk[] getLoadedChunks() {
		ArrayList<Chunk> loadedChunks = new ArrayList<>();

		for (FullChunk chunk : nukkit.getChunks().values()) {
			if (chunk.isLoaded()) {
				loadedChunks.add(new PokkitChunk(toBukkit(nukkit), chunk.getX(), chunk.getZ()));
			}
		}

		return loadedChunks.toArray(new Chunk[loadedChunks.size()]);
	}

	@Override
	public int getMaxHeight() {
		return nukkit.getDimensionData().getMaxHeight();
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return getWorldMetadata().getMetadata(this, metadataKey);
	}

	@Override
	public int getMonsterSpawnLimit() {
		return cn.nukkit.Server.getInstance().spawnMonsters ? -1 : 0;
	}

	@Override
	public String getName() {
		return nukkit.getName();
	}

	@Override
	public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
		return getNearbyEntities(location, x, y, z, null);
	}

	@Override
	public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z, Predicate<? super Entity> predicate) {
		if (predicate == null) {
			cn.nukkit.entity.Entity[] entities = nukkit.getNearbyEntities(new SimpleAxisAlignedBB(location.getX()-0.5*x, location.getY()-0.5*y, location.getZ()-0.5*z, location.getX()+0.5*x, location.getY()+0.5*y, location.getZ()+0.5*z));
			Collection<Entity> out = new ArrayList<>();
			for (cn.nukkit.entity.Entity entity : entities) {
				out.add(PokkitEntity.toBukkit(entity));
			}
			return out;
		} else {
			cn.nukkit.entity.Entity[] entities = nukkit.getNearbyEntities(new SimpleAxisAlignedBB(location.getX()-0.5*x, location.getY()-0.5*y, location.getZ()-0.5*z, location.getX()+0.5*x, location.getY()+0.5*y, location.getZ()+0.5*z));
			Collection<Entity> out = new ArrayList<>();
			for (cn.nukkit.entity.Entity entity : entities) {
				Entity bukkitEntity = PokkitEntity.toBukkit(entity);
				if (predicate.test(bukkitEntity)) {
					out.add(bukkitEntity);
				}
			}
			return out;
		}
	}

	@Override
	public Collection<Entity> getNearbyEntities(BoundingBox boundingBox) {
		return getNearbyEntities(boundingBox, null);
	}

	@Override
	public Collection<Entity> getNearbyEntities(BoundingBox boundingBox, Predicate<? super Entity> filter) {
		if (filter == null) {
			cn.nukkit.entity.Entity[] entities = nukkit.getNearbyEntities(new SimpleAxisAlignedBB(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()));
			Collection<Entity> out = new ArrayList<>();
			for (cn.nukkit.entity.Entity entity : entities) {
				out.add(PokkitEntity.toBukkit(entity));
			}
			return out;
		} else {
			cn.nukkit.entity.Entity[] entities = nukkit.getNearbyEntities(new SimpleAxisAlignedBB(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()));
			Collection<Entity> out = new ArrayList<>();
			for (cn.nukkit.entity.Entity entity : entities) {
				Entity bukkitEntity = PokkitEntity.toBukkit(entity);
				if (filter.test(bukkitEntity)) {
					out.add(bukkitEntity);
				}
			}
			return out;
		}
	}

	@Override
	public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance) {
		return rayTraceEntitiesInternal(start, direction, maxDistance, null);
	}

	@Override
	public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize) {
		return rayTraceEntitiesInternal(start, direction, maxDistance, null);
	}

	@Override
	public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, Predicate<? super Entity> predicate) {
		return rayTraceEntitiesInternal(start, direction, maxDistance, predicate);
	}

	@Override
	public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<? super Entity> predicate) {
		return rayTraceEntitiesInternal(start, direction, maxDistance, predicate);
	}

	@Override
	public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance) {
		return rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER);
	}

	@Override
	public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
		return rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, false);
	}

	@Override
	public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
		if (start == null || direction == null || maxDistance < 0) return null;
		Vector pos = start.toVector();
		Vector step = direction.clone().normalize().multiply(0.5);
		int steps = (int) (maxDistance / 0.5);
		for (int i = 0; i < steps; i++) {
			pos.add(step);
			Block block = getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
			if (block.getType().isSolid()) {
				return new RayTraceResult(pos.clone(), block, null);
			}
		}
		return null;
	}

	@Override
	public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double entityMaxDistance, Predicate<? super Entity> predicate) {
		RayTraceResult blockHit = rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks);
		RayTraceResult entityHit = rayTraceEntities(start, direction, entityMaxDistance, predicate);
		if (blockHit == null) return entityHit;
		if (entityHit == null) return blockHit;
		double blockDist = start.toVector().distance(blockHit.getHitPosition());
		double entityDist = start.toVector().distance(entityHit.getHitPosition());
		return blockDist <= entityDist ? blockHit : entityHit;
	}

	private RayTraceResult rayTraceEntitiesInternal(Location start, Vector direction, double maxDistance, Predicate<? super Entity> predicate) {
		if (start == null || direction == null || maxDistance < 0) return null;
		Vector startPos = start.toVector();
		Vector dirNorm = direction.clone().normalize();
		Entity closest = null;
		double closestDist = maxDistance * maxDistance;
		Vector closestPos = null;
		for (Entity entity : getEntities()) {
			if (predicate != null && !predicate.test(entity)) continue;
			org.bukkit.Location eloc = entity.getLocation();
			Vector toEntity = eloc.toVector().subtract(startPos);
			double dot = toEntity.dot(dirNorm);
			if (dot < 0 || dot > maxDistance) continue;
			Vector closestPoint = startPos.clone().add(dirNorm.clone().multiply(dot));
			double distSq = closestPoint.distanceSquared(eloc.toVector());
			double threshold = 0.5;
			if (entity instanceof LivingEntity) {
				threshold = ((LivingEntity) entity).getEyeHeight() / 2.0 + 0.3;
			}
			if (distSq < threshold * threshold && distSq < closestDist) {
				closestDist = distSq;
				closest = entity;
				closestPos = closestPoint;
			}
		}
		if (closest != null) {
			return new RayTraceResult(closestPos, closest);
		}
		return null;
	}

	@Override
	public List<Player> getPlayers() {
		return nukkit.getPlayers().values().stream().map(PokkitPlayer::toBukkit).collect(Collectors.toList());
	}

	private final List<BlockPopulator> populators = new ArrayList<>();

	@Override
	public List<BlockPopulator> getPopulators() {
		return populators;
	}

	@Override
	public boolean getPVP() {
		return nukkit.getServer().getPropertyBoolean("pvp");
	}

	@Override
	public int getSeaLevel() {
		return nukkit.getDimensionData().getMinHeight() + 64;
	}

	@Override
	public long getSeed() {
		return nukkit.getSeed();
	}

	@Override
	public Location getSpawnLocation() {
		return PokkitLocation.toBukkit(nukkit.getSpawnLocation());
	}

	@Override
	public double getTemperature(int x, int z) {
		int biomeId = nukkit.getBiomeId(x, z);
		cn.nukkit.level.biome.Biome biome = EnumBiome.getBiome(biomeId);
		if (biome != null && biome.isFreezing()) {
			return 0.1;
		}
		return 0.6;
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return getTemperature(x, z);
	}

	@Override
	public int getThunderDuration() {
		return nukkit.getThunderTime();
	}

	@Override
	public long getTicksPerAnimalSpawns() {
		return cn.nukkit.Server.getInstance().spawnAnimals ? 400 : 0;
	}

	@Override
	public long getTicksPerMonsterSpawns() {
		return cn.nukkit.Server.getInstance().spawnMonsters ? 1 : 0;
	}

	@Override
	public long getTime() {
		return this.nukkit.getTime();
	}

	@Override
	public UUID getUID() {
		return UniqueIdConversion.levelIndexToId(nukkit.getId());
	}

	@Override
	public int getWaterAnimalSpawnLimit() {
		return nukkit.getServer().spawnAnimals ? -1 : 0;
	}

	@Override
	public int getWeatherDuration() {
		return nukkit.getRainTime();
	}

	@Override
	public WorldBorder getWorldBorder() {
		return new org.bukkit.WorldBorder() {
			@Override
			public org.bukkit.World getWorld() { return PokkitWorld.this; }
			@Override
			public void reset() {}
			@Override
			public double getSize() { return 60000000; }
			@Override
			public void setSize(double size) {}
			@Override
			public void setSize(double size, long seconds) {}
			@Override
			public void setSize(double size, java.util.concurrent.TimeUnit unit, long duration) {}
			@Override
			public Location getCenter() { return new Location(PokkitWorld.this, 0, 0, 0); }
			@Override
			public void setCenter(double x, double z) {}
			@Override
			public void setCenter(Location location) {}
			@Override
			public double getDamageBuffer() { return 5; }
			@Override
			public void setDamageBuffer(double blocks) {}
			@Override
			public double getDamageAmount() { return 0.2; }
			@Override
			public void setDamageAmount(double damage) {}
			@Override
			public int getWarningDistance() { return 5; }
			@Override
			public void setWarningDistance(int distance) {}
			@Override
			public int getWarningTime() { return 15; }
			@Override
			public void setWarningTime(int time) {}
			@Override
			public boolean isInside(Location location) { return true; }
			@Override
			public double getMaxSize() { return 60000000; }
			@Override
			public double getMaxCenterCoordinate() { return 30000000; }
		};
	}

	@Override
	public File getWorldFolder() {
		return new File(nukkit.getServer().getDataPath() + "worlds" + File.separator + nukkit.getFolderName());
	}

	private WorldMetadataStore getWorldMetadata() {
		return ((CraftServer) Bukkit.getServer()).getMetadata().getWorldMetadata();
	}

	@Override
	public WorldType getWorldType() {
		return nukkit.getGenerator() instanceof Flat ? WorldType.FLAT : WorldType.NORMAL;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getWorldMetadata().hasMetadata(this, metadataKey);
	}

	@Override
	public boolean hasStorm() {
		return nukkit.isRaining();
	}

	@Override
	public boolean isAutoSave() {
		return nukkit.getAutoSave();
	}

	@Override
	public boolean isChunkInUse(int x, int z) {
		return nukkit.isChunkInUse(x, z);
	}

	@Override
	public boolean isChunkLoaded(Chunk chunk) {
		return nukkit.isChunkLoaded(chunk.getX(), chunk.getZ());
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return nukkit.isChunkLoaded(x, z);
	}

	@Override
	public boolean isGameRule(String rule) {
		return cn.nukkit.level.GameRule.parseString(rule).isPresent();
	}

	@Override
	public boolean isThundering() {
		return nukkit.isThundering();
	}

	@Override
	public void loadChunk(Chunk chunk) {
		nukkit.loadChunk(chunk.getX(), chunk.getZ());
	}

	@Override
	public void loadChunk(int x, int z) {
		nukkit.loadChunk(x, z);
	}

	@Override
	public boolean loadChunk(int x, int z, boolean generate) {
		return nukkit.loadChunk(x, z, true);
	}

	private void playEffect0(Location location, Effect effect, int data) {
		if (location == null || effect == null) return;
		LevelEventPacket pk = new LevelEventPacket();
		pk.x = (float) location.getX();
		pk.y = (float) location.getY();
		pk.z = (float) location.getZ();
		switch (effect) {
			case MOBSPAWNER_FLAMES:
				pk.evid = LevelEventPacket.EVENT_PARTICLE_POINT_CLOUD;
				break;
			case POTION_BREAK:
				pk.evid = LevelEventPacket.EVENT_PARTICLE_EVAPORATE;
				break;
			case EXTINGUISH:
				pk.evid = LevelEventPacket.EVENT_SOUND_FIZZ;
				break;
			case RECORD_PLAY:
				pk.evid = LevelEventPacket.EVENT_SOUND_PLAY_RECORDING;
				pk.data = data;
				break;
			case SMOKE:
			case PORTAL_TRAVEL:
			case ENDERDRAGON_GROWL:
			case GHAST_SHRIEK:
			case GHAST_SHOOT:
			case BLAZE_SHOOT:
			default:
				return;
		}
		nukkit.addChunkPacket(location.getChunk().getX(), location.getChunk().getZ(), pk);
	}

	@Override
	public void playEffect(Location location, Effect effect, int data) {
		playEffect0(location, effect, data);
	}

	@Override
	public void playEffect(Location location, Effect effect, int data, int radius) {
		playEffect0(location, effect, data);
	}

	@Override
	public <T> void playEffect(Location location, Effect effect, T data) {
		playEffect0(location, effect, data instanceof Number ? ((Number) data).intValue() : 0);
	}

	@Override
	public <T> void playEffect(Location location, Effect effect, T data, int radius) {
		playEffect0(location, effect, data instanceof Number ? ((Number) data).intValue() : 0);
	}

	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch) {
		if (location == null || sound == null) {
			return;
		}

		cn.nukkit.level.Sound nukkitSound = PokkitSound.toNukkit(location, sound, pitch);
		if (nukkitSound == null) {
			return;
		}
		Vector3 pos = PokkitVector.toNukkit(location.toVector());
		nukkit.addSound(pos, nukkitSound, volume, pitch);
	}

	@Override
	public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
		// Ignore the category
		playSound(location, sound, volume, pitch);
	}

	@Override
	public void playSound(Location location, String soundString, float volume, float pitch) {
		if (location == null || soundString == null) {
			return;
		}
		try {
			Sound sound = Sound.valueOf(soundString.replace("minecraft:", "").toUpperCase());
			playSound(location, sound, volume, pitch);
		} catch (IllegalArgumentException e) {
			// Ignore non-existing sound
		}
	}

	@Override
	public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
		// Ignore the category
		playSound(location, sound, volume, pitch);
	}

	@Override
	public boolean refreshChunk(int x, int z) {
		if (!nukkit.isChunkLoaded(x, z)) {
			return false;
		}
		Map<Integer, cn.nukkit.Player> chunkPlayers = nukkit.getChunkPlayers(x, z);
		for (cn.nukkit.Player player : chunkPlayers.values()) {
			nukkit.requestChunk(x, z, player);
		}
		return true;
	}

	@Override
	public boolean isChunkForceLoaded(int x, int z) {
		return forceLoadedChunks.contains(chunkKey(x, z));
	}

	@Override
	public void setChunkForceLoaded(int x, int z, boolean force) {
		long key = chunkKey(x, z);
		if (force) {
			forceLoadedChunks.add(key);
			if (!nukkit.isChunkLoaded(x, z)) {
				nukkit.loadChunk(x, z);
			}
		} else {
			forceLoadedChunks.remove(key);
		}
	}

	@Override
	public Collection<Chunk> getForceLoadedChunks() {
		List<Chunk> chunks = new ArrayList<>();
		for (long key : forceLoadedChunks) {
			int x = (int) (key >> 32);
			int z = (int) key;
			chunks.add(getChunkAt(x, z));
		}
		return chunks;
	}

	@Override
	public boolean addPluginChunkTicket(int x, int z, Plugin plugin) {
		long key = chunkKey(x, z);
		Set<Plugin> tickets = pluginChunkTickets.computeIfAbsent(key, k -> new HashSet<>());
		return tickets.add(plugin);
	}

	@Override
	public boolean removePluginChunkTicket(int x, int z, Plugin plugin) {
		long key = chunkKey(x, z);
		Set<Plugin> ticket = pluginChunkTickets.get(key);
		if (ticket != null && ticket.remove(plugin)) {
			if (ticket.isEmpty()) {
				pluginChunkTickets.remove(key);
			}
			return true;
		}
		return false;
	}

	@Override
	public void removePluginChunkTickets(Plugin plugin) {
		Iterator<Map.Entry<Long, Set<Plugin>>> it = pluginChunkTickets.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, Set<Plugin>> entry = it.next();
			entry.getValue().remove(plugin);
			if (entry.getValue().isEmpty()) {
				it.remove();
			}
		}
	}

	@Override
	public Collection<Plugin> getPluginChunkTickets(int x, int z) {
		Set<Plugin> tickets = pluginChunkTickets.get(chunkKey(x, z));
		return tickets != null ? Collections.unmodifiableSet(tickets) : Collections.emptySet();
	}

	@Override
	public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
		Map<Plugin, Collection<Chunk>> result = new HashMap<>();
		for (Map.Entry<Long, Set<Plugin>> entry : pluginChunkTickets.entrySet()) {
			long key = entry.getKey();
			int x = (int) (key >> 32);
			int z = (int) key;
			Chunk chunk = getChunkAt(x, z);
			for (Plugin plugin : entry.getValue()) {
				result.computeIfAbsent(plugin, p -> new ArrayList<>()).add(chunk);
			}
		}
		return result;
	}

	private long chunkKey(int x, int z) {
		return ((long) x & 0xFFFFFFFFL) << 32 | ((long) z & 0xFFFFFFFFL);
	}

	@Override
	public boolean regenerateChunk(int x, int z) {
		nukkit.regenerateChunk(x, z);
		return true;
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		getWorldMetadata().removeMetadata(this, metadataKey, owningPlugin);
	}

	@Override
	public void save() {
		nukkit.save();
	}

	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		for (Player player : getPlayers()) {
			player.sendPluginMessage(source, channel, message);
		}
	}

	@Override
	public void setAmbientSpawnLimit(int limit) {
	}

	@Override
	public void setAnimalSpawnLimit(int limit) {
	}

	@Override
	public void setAutoSave(boolean value) {
		nukkit.setAutoSave(value);
	}

	@Override
	public void setBiome(int x, int z, Biome biome) {
		nukkit.setBiomeId(x, z, (byte) PokkitBiome.toNukkit(biome));
	}

	@Override
	public void setBiome(int i, int i1, int i2, Biome biome) {
		nukkit.setBiomeId(i, i2, (byte) PokkitBiome.toNukkit(biome));
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		Server.getInstance().setPropertyInt("difficulty", difficulty.getValue());
	}

	@Override
	public void setFullTime(long time) {
		nukkit.setTime((int) time);
	}

	@Override
	public <T> boolean setGameRule(GameRule<T> rule, T newValue) {
		cn.nukkit.level.GameRule nukkitRule = PokkitGameRule.toNukkit(rule);
		if (nukkitRule == null) {
			return false;
		}
		if (newValue instanceof Boolean) {
			nukkit.getGameRules().setGameRule(nukkitRule, ((Boolean)newValue).booleanValue());
			return true;
		}
		if (newValue instanceof Integer) {
			nukkit.getGameRules().setGameRule(nukkitRule, ((Integer)newValue).intValue());
			return true;
		}
		return false;
	}

	@Override
	public boolean setGameRuleValue(String rule, String value) {
		cn.nukkit.level.GameRule gameRule = cn.nukkit.level.GameRule.parseString(rule).orElse(null);
		if (gameRule == null) {
			return false;
		}
		try {
			nukkit.getGameRules().setGameRules(gameRule, value);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public void setKeepSpawnInMemory(boolean keepLoaded) {
		cn.nukkit.math.Vector3 spawn = nukkit.getSpawnLocation();
		int spawnChunkX = spawn.getChunkX();
		int spawnChunkZ = spawn.getChunkZ();
		int radius = keepLoaded ? 4 : 0;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (keepLoaded) {
					nukkit.loadChunk(spawnChunkX + x, spawnChunkZ + z);
				}
			}
		}
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		getWorldMetadata().setMetadata(this, metadataKey, newMetadataValue);
	}

	@Override
	public void setMonsterSpawnLimit(int limit) {
	}

	@Override
	public void setPVP(boolean pvp) {
		Server.getInstance().setPropertyBoolean("pvp", pvp);
	}

	@Override
	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
		cn.nukkit.Server.getInstance().spawnMonsters = allowMonsters;
		cn.nukkit.Server.getInstance().spawnAnimals = allowAnimals;
	}

	@Override
	public boolean setSpawnLocation(int x, int y, int z) {
		nukkit.setSpawnLocation(new Vector3(x, y, z));
		return true;
	}

	@Override
	public boolean setSpawnLocation(Location location) {
		if (!location.getWorld().equals(this)) {
			return false;
		}
		nukkit.setSpawnLocation(PokkitLocation.toNukkit(location));
		return true;
	}

	@Override
	public void setStorm(boolean hasStorm) {
		nukkit.setThundering(hasStorm);
	}

	@Override
	public void setThunderDuration(int duration) {
		nukkit.setThunderTime(duration);
	}

	@Override
	public void setThundering(boolean thundering) {
		nukkit.setThundering(thundering);
	}

	@Override
	public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
	}

	@Override
	public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
	}

	@Override
	public long getTicksPerWaterSpawns() {
		return 0;
	}

	@Override
	public void setTicksPerWaterSpawns(int i) {

	}

	@Override
	public long getTicksPerAmbientSpawns() {
		return 0;
	}

	@Override
	public void setTicksPerAmbientSpawns(int i) {

	}

	@Override
	public void setTime(long time) {
		nukkit.setTime((int)time);
	}

	@Override
	public void setWaterAnimalSpawnLimit(int limit) {
	}

	@Override
	public void setWeatherDuration(int duration) {
		nukkit.setRainTime(duration);
	}

	private static final Map<Class<? extends Entity>, EntityType> CLASS_TO_ENTITY_TYPE = new HashMap<>();
	static {
		CLASS_TO_ENTITY_TYPE.put(Arrow.class, EntityType.ARROW);
		CLASS_TO_ENTITY_TYPE.put(Snowball.class, EntityType.SNOWBALL);
		CLASS_TO_ENTITY_TYPE.put(EnderPearl.class, EntityType.ENDER_PEARL);
		CLASS_TO_ENTITY_TYPE.put(ExperienceOrb.class, EntityType.EXPERIENCE_ORB);
		CLASS_TO_ENTITY_TYPE.put(FallingBlock.class, EntityType.FALLING_BLOCK);
		CLASS_TO_ENTITY_TYPE.put(LightningStrike.class, EntityType.LIGHTNING_BOLT);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Item.class, EntityType.ITEM);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Pig.class, EntityType.PIG);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Cow.class, EntityType.COW);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Sheep.class, EntityType.SHEEP);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Chicken.class, EntityType.CHICKEN);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Wolf.class, EntityType.WOLF);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Creeper.class, EntityType.CREEPER);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Villager.class, EntityType.VILLAGER);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Rabbit.class, EntityType.RABBIT);
		CLASS_TO_ENTITY_TYPE.put(org.bukkit.entity.Ocelot.class, EntityType.OCELOT);
		CLASS_TO_ENTITY_TYPE.put(Boat.class, EntityType.BOAT);
		CLASS_TO_ENTITY_TYPE.put(Minecart.class, EntityType.MINECART);
		CLASS_TO_ENTITY_TYPE.put(ThrownPotion.class, EntityType.POTION);
		CLASS_TO_ENTITY_TYPE.put(ThrownExpBottle.class, EntityType.EXPERIENCE_BOTTLE);
		CLASS_TO_ENTITY_TYPE.put(TNTPrimed.class, EntityType.TNT);
		CLASS_TO_ENTITY_TYPE.put(Trident.class, EntityType.TRIDENT);
		CLASS_TO_ENTITY_TYPE.put(Painting.class, EntityType.PAINTING);
	}

	@Override
	public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
		EntityType type = CLASS_TO_ENTITY_TYPE.get(clazz);
		if (type == null) {
			throw new IllegalArgumentException("Cannot spawn entity of type " + clazz.getName());
		}
		Entity entity = spawnEntity(location, type);
		if (entity == null || !clazz.isInstance(entity)) {
			return null;
		}
		return clazz.cast(entity);
	}

	public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<? super T> consumer) throws IllegalArgumentException {
		T entity = spawn(location, clazz);
		if (consumer != null && entity != null) {
			consumer.accept(entity);
		}
		return entity;
	}

	@Override
	public Arrow spawnArrow(Location location, Vector direction, float speed, float spread) {
		Entity entity = spawnEntity(location, EntityType.ARROW);
		if (entity instanceof Arrow) {
			Arrow arrow = (Arrow) entity;
			arrow.setVelocity(direction.normalize().multiply(speed));
			return arrow;
		}
		return null;
	}

	@Override
	public <T extends AbstractArrow> T spawnArrow(Location location, Vector vector, float v, float v1, Class<T> aClass) {
		Entity entity = spawnEntity(location, EntityType.ARROW);
		if (aClass.isInstance(entity)) {
			return aClass.cast(entity);
		}
		return null;
	}

	@Override
	public Entity spawnEntity(Location loc, EntityType type) {
		try {
			BaseFullChunk chunk = nukkit.getChunk(loc.getChunk().getX(), loc.getChunk().getZ());
			CompoundTag nbt = new CompoundTag();
			nbt.putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", loc.getX() + 0.5))
                    .add(new DoubleTag("", loc.getY())).add(new DoubleTag("", loc.getZ() + 0.5)))
            .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
            .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                    .add(new FloatTag("", 0)));
			cn.nukkit.entity.Entity ent = cn.nukkit.entity.Entity.createEntity(PokkitEntityTranslator.getEntity(type), chunk, nbt);
			ent.spawnToAll();
			return PokkitEntity.toBukkit(ent);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, BlockData data) throws IllegalArgumentException {
		cn.nukkit.block.Block block = PokkitBlockData.toNukkit(data);
		CompoundTag nbt = new CompoundTag()
				.putList(new ListTag<DoubleTag>("Pos")
						.add(new DoubleTag("", location.getX() + 0.5))
						.add(new DoubleTag("", location.getY()))
						.add(new DoubleTag("", location.getZ() + 0.5)))
				.putList(new ListTag<DoubleTag>("Motion")
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0)))

				.putList(new ListTag<FloatTag>("Rotation")
						.add(new FloatTag("", 0))
						.add(new FloatTag("", 0)))
				.putInt("TileID", block.getId())
				.putByte("Data", block.getDamage());

		cn.nukkit.entity.Entity ent = cn.nukkit.entity.Entity.createEntity(EntityFallingBlock.NETWORK_ID, nukkit.getChunk((int) location.getX() >> 4, (int) location.getZ() >> 4), nbt);
		ent.spawnToAll();
		return (FallingBlock) nl.rutgerkok.pokkit.entity.PokkitEntity.toBukkit(ent);
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, Material material, byte data)
			throws IllegalArgumentException {
		return spawnFallingBlock(location, PokkitBlockData.createBlockData(material, data));
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location,
			@SuppressWarnings("deprecation") org.bukkit.material.MaterialData materialData)
			throws IllegalArgumentException {
		return spawnFallingBlock(location, materialData.getItemType(), materialData.getData());
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count) {
		spawnParticle(particle, x, y, z, count, 0, 0, 0);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 0);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data, boolean force) {
		int id;

		id = PokkitParticle.toNukkit(particle);

		SplittableRandom random = new SplittableRandom();

		int index = 0;
		while (count > index) {
			double realOffsetX;
			double realOffsetY;
			double realOffsetZ;
			if (offsetX != 0) {
				realOffsetX = offsetX / 2;
				x = x + random.nextDouble(-realOffsetX, realOffsetX);
			}
			if (offsetY != 0) {
				realOffsetY = offsetY / 2;
				y = y + random.nextDouble(-realOffsetY, realOffsetY);
			}
			if (offsetZ != 0) {
				realOffsetZ = offsetZ / 2;
				z = z + random.nextDouble(-realOffsetZ, realOffsetZ);
			}

			GenericParticle nukkitParticle = new GenericParticle(new Vector3(x, y, z), id);

			nukkit.addParticle(nukkitParticle);
			index++;
		}
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, T data) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 0, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
		spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count) {
		spawnParticle(particle, location, count, 0, 0, 0);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ) {
		spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, 0);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, double extra) {
		spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, null);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, double extra, T data) {
		spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data, false);

	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, double extra, T data, boolean force) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ,
				extra, data, force);

	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY,
			double offsetZ, T data) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, 0, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, 0, 0, 0, 0, data);
	}

	@Override
	public Spigot spigot() {
		return spigot;
	}

	@Override
	public Raid locateNearestRaid(Location location, int i) {
		return null;
	}

	@Override
	public List<Raid> getRaids() {
		return Collections.emptyList();
	}

	@Override
	public DragonBattle getEnderDragonBattle() {
		return null;
	}

	public LightningStrike strike(Location loc, boolean effect) {
		BaseFullChunk chunk = nukkit.getChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int y = loc.getBlockY();
        int bId = nukkit.getBlockIdAt(x, y, z);
        if (bId != cn.nukkit.block.Block.TALL_GRASS && bId != cn.nukkit.block.Block.WATER)
            y += 1;
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", x))
                        .add(new DoubleTag("", y)).add(new DoubleTag("", z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)));

        EntityLightning bolt = new EntityLightning(chunk, nbt);
        bolt.setEffect(effect);
        bolt.spawnToAll();
        return PokkitEntityLightningStrike.toBukkit(bolt);
	}

	@Override
	public LightningStrike strikeLightning(Location loc) {
		return strike(loc, true);
	}

	@Override
	public LightningStrike strikeLightningEffect(Location loc) {
		return strike(loc, false);
	}

	@Override
	public boolean unloadChunk(Chunk chunk) {
		return nukkit.unloadChunk(chunk.getX(), chunk.getZ());
	}

	@Override
	public boolean unloadChunk(int x, int z) {
		return nukkit.unloadChunk(x, z);
	}

	@Override
	public boolean unloadChunk(int x, int z, boolean save) {
		return nukkit.unloadChunk(x, z, true, save);
	}

	@Override
	public boolean unloadChunkRequest(int x, int z) {
		return nukkit.unloadChunkRequest(x, z);
	}

	@Override
	public Location locateNearestStructure(Location location, StructureType type, int i, boolean b) {
		return null;
	}

	@Override
	public org.bukkit.util.StructureSearchResult locateNearestStructure(Location location, org.bukkit.generator.structure.StructureType structureType, int radius, boolean findUnexplored) {
		return null;
	}

	@Override
	public org.bukkit.util.StructureSearchResult locateNearestStructure(Location location, org.bukkit.generator.structure.Structure structure, int radius, boolean findUnexplored) {
		return null;
	}

	@Override
	public int getViewDistance() {
		return nukkit.getServer().getViewDistance();
	}

	@Override
	public boolean isChunkGenerated(int x, int z) {
		return nukkit.isChunkGenerated(x, z);
	}

	@Override
	public Collection<org.bukkit.generator.structure.GeneratedStructure> getStructures(int x, int z, org.bukkit.generator.structure.Structure structure) {
		return Collections.emptyList();
	}

	@Override
	public Collection<org.bukkit.generator.structure.GeneratedStructure> getStructures(int x, int z) {
		return Collections.emptyList();
	}

	@Override
	public Set<org.bukkit.FeatureFlag> getFeatureFlags() {
		return Collections.emptySet();
	}

	@Override
	public org.bukkit.util.BiomeSearchResult locateNearestBiome(Location location, int radius, int horizontalRadius, int verticalRadius, Biome... biomes) {
		return null;
	}

	@Override
	public org.bukkit.util.BiomeSearchResult locateNearestBiome(Location location, int radius, Biome... biomes) {
		return null;
	}

	@Override
	public long getGameTime() {
		return getFullTime();
	}

	@Override
	public void setClearWeatherDuration(int duration) {
		if (!nukkit.isRaining()) {
			nukkit.setRainTime(duration);
		}
	}

	@Override
	public org.bukkit.generator.BiomeProvider getBiomeProvider() {
		return null;
	}

	@Override
	public boolean hasCeiling() {
		return getEnvironment() == Environment.NETHER;
	}

	@Override
	public boolean isPiglinSafe() {
		return getEnvironment() == Environment.NETHER;
	}

	@Override
	public boolean hasRaids() {
		return getEnvironment() == Environment.NORMAL;
	}

	@Override
	public org.bukkit.NamespacedKey getKey() {
		return NamespacedKey.minecraft(getName().toLowerCase().replace(" ", "_"));
	}

	@Override
	public void setType(int x, int y, int z, org.bukkit.Material material) {
		getBlockAt(x, y, z).setType(material);
	}

	@Override
	public void setType(Location location, org.bukkit.Material material) {
		getBlockAt(location).setType(material);
	}

	@Override
	public org.bukkit.block.data.BlockData getBlockData(int x, int y, int z) {
		return getBlockAt(x, y, z).getBlockData();
	}

	@Override
	public org.bukkit.block.data.BlockData getBlockData(Location location) {
		return getBlockAt(location).getBlockData();
	}

	@Override
	public Item dropItem(Location location, ItemStack item, Consumer<? super Item> consumer) {
		return dropItem(location, item);
	}

	@Override
	public <T extends Entity> T createEntity(Location location, Class<T> aClass) {
		return spawn(location, aClass);
	}

	@Override
	public Collection<Player> getPlayersSeeingChunk(int x, int z) {
		Map<Integer, cn.nukkit.Player> chunkPlayers = nukkit.getChunkPlayers(x, z);
		return chunkPlayers.values().stream().map(PokkitPlayer::toBukkit).collect(Collectors.toList());
	}

	@Override
	public Collection<Player> getPlayersSeeingChunk(Chunk chunk) {
		return getPlayersSeeingChunk(chunk.getX(), chunk.getZ());
	}

	@Override
	public void setTicksPerWaterAmbientSpawns(int ticks) {
	}

	@Override
	public long getTicksPerWaterAmbientSpawns() {
		return 1;
	}

	@Override
	public int getWaterAmbientSpawnLimit() {
		return -1;
	}

	@Override
	public void setWaterAmbientSpawnLimit(int limit) {
	}

    @Override
    public org.bukkit.Chunk getChunkAt(int p0, int p1, boolean p2) { return getChunkAt(p0, p1); }
    @Override
    public boolean setSpawnLocation(int p0, int p1, int p2, float p3) {
        nukkit.setSpawnLocation(new Vector3(p0, p1, p2));
        return true;
    }
    @Override
    public boolean isClearWeather() { return !nukkit.isRaining() && !nukkit.isThundering();}
    @Override
    public int getClearWeatherDuration() { return nukkit.isRaining() ? 0 : nukkit.getRainTime();}
    @Override
    public int getLogicalHeight() { return nukkit.getDimensionData().getHeight();}
    @Override
    public boolean isNatural() { return getEnvironment() == Environment.NORMAL;}
    @Override
    public boolean isBedWorks() { return getEnvironment() == Environment.NORMAL;}
    @Override
    public boolean hasSkyLight() { return getEnvironment() == Environment.NORMAL;}
    @Override
    public boolean isRespawnAnchorWorks() { return getEnvironment() == Environment.NETHER;}
    @Override
    public boolean isUltraWarm() { return getEnvironment() == Environment.NETHER;}
    @Override
    public int getSimulationDistance() { return nukkit.getServer().getViewDistance();}
    @Override
    public long getTicksPerWaterUndergroundCreatureSpawns() { return 1L;}
    @Override
    public void setTicksPerWaterUndergroundCreatureSpawns(int p0) {}
    @Override
    public long getTicksPerSpawns(org.bukkit.entity.SpawnCategory p0) { return 1L;}
    @Override
    public void setTicksPerSpawns(org.bukkit.entity.SpawnCategory p0, int p1) {}
    @Override
    public int getWaterUndergroundCreatureSpawnLimit() { return -1;}
    @Override
    public void setWaterUndergroundCreatureSpawnLimit(int p0) {}
    @Override
    public int getSpawnLimit(org.bukkit.entity.SpawnCategory cat) {
        switch (cat) {
            case MONSTER: return getMonsterSpawnLimit();
            case ANIMAL: return getAnimalSpawnLimit();
            case WATER_ANIMAL: return -1;
            case AMBIENT: return getAmbientSpawnLimit();
            case WATER_AMBIENT: return getWaterAmbientSpawnLimit();
            case WATER_UNDERGROUND_CREATURE: return getWaterUndergroundCreatureSpawnLimit();
            default: return -1;
        }
    }
    @Override
    public void setSpawnLimit(org.bukkit.entity.SpawnCategory p0, int p1) {}
    @Override
    public void playNote(org.bukkit.Location loc, org.bukkit.Instrument instrument, org.bukkit.Note note) {
        cn.nukkit.math.Vector3 pos = new cn.nukkit.math.Vector3(loc.getX(), loc.getY(), loc.getZ());
        int instrumentId = instrument.ordinal();
        int noteId = note.getId();
        nukkit.addLevelSoundEvent(pos, cn.nukkit.network.protocol.LevelSoundEventPacket.SOUND_NOTE,
                instrumentId << 8 | noteId, -1);
    }
    @Override
    public void playSound(org.bukkit.Location p0, org.bukkit.Sound p1, org.bukkit.SoundCategory p2, float p3, float p4, long p5) { playSound(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.Location p0, java.lang.String p1, org.bukkit.SoundCategory p2, float p3, float p4, long p5) { playSound(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, org.bukkit.Sound p1, org.bukkit.SoundCategory p2, float p3, float p4, long p5) { playSoundEntity(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, java.lang.String p1, org.bukkit.SoundCategory p2, float p3, float p4, long p5) { playSoundEntityString(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, org.bukkit.Sound p1, org.bukkit.SoundCategory p2, float p3, float p4) { playSoundEntity(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, java.lang.String p1, org.bukkit.SoundCategory p2, float p3, float p4) { playSoundEntityString(p0, p1, p2, p3, p4); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, org.bukkit.Sound p1, float p2, float p3) { playSoundEntity(p0, p1, SoundCategory.NEUTRAL, p2, p3); }
    @Override
    public void playSound(org.bukkit.entity.Entity p0, java.lang.String p1, float p2, float p3) { playSoundEntityString(p0, p1, SoundCategory.NEUTRAL, p2, p3); }
    @Override
    public Item dropItemNaturally(Location location, ItemStack item, Consumer<? super Item> consumer) {
        return dropItemNaturally(location, item);
    }
    @Override
    public int getMinHeight() { return nukkit.getDimensionData().getMinHeight(); }
    @Override
    public org.bukkit.persistence.PersistentDataContainer getPersistentDataContainer() { return null; }
    @Override
    public Collection<org.bukkit.Chunk> getIntersectingChunks(org.bukkit.util.BoundingBox box) {
        int minChunkX = (int) Math.floor(box.getMinX()) >> 4;
        int minChunkZ = (int) Math.floor(box.getMinZ()) >> 4;
        int maxChunkX = (int) Math.floor(box.getMaxX()) >> 4;
        int maxChunkZ = (int) Math.floor(box.getMaxZ()) >> 4;
        List<org.bukkit.Chunk> chunks = new ArrayList<>();
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (isChunkLoaded(cx, cz)) {
                    chunks.add(getChunkAt(cx, cz));
                }
            }
        }
        return chunks;
    }
    @Override
    public void setBiome(Location location, org.bukkit.block.Biome biome) { setBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), biome); }
    @Override
    public void setBlockData(Location location, org.bukkit.block.data.BlockData blockData) { getBlockAt(location).setBlockData(blockData); }
    @Override
    public void setBlockData(int x, int y, int z, org.bukkit.block.data.BlockData blockData) { getBlockAt(x, y, z).setBlockData(blockData); }
    @Override
    public org.bukkit.Material getType(Location location) { return getBlockAt(location).getType(); }
    @Override
    public org.bukkit.Material getType(int x, int y, int z) { return getBlockAt(x, y, z).getType(); }
    @Override
    public org.bukkit.block.BlockState getBlockState(Location location) { return getBlockAt(location).getState(); }
    @Override
    public org.bukkit.block.BlockState getBlockState(int x, int y, int z) { return getBlockAt(x, y, z).getState(); }
    @Override
    public org.bukkit.block.Biome getBiome(Location location) { return getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ()); }
    @Override
    public <T extends Entity> T addEntity(T entity) { return entity; }
    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz, boolean randomizeData, Consumer<? super T> consumer) { return spawn(location, clazz, consumer); }
    @Override
    public <T extends org.bukkit.entity.LivingEntity> T spawn(Location location, Class<T> clazz, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason, boolean randomizeData, Consumer<? super T> consumer) { return spawn(location, clazz, consumer); }
    @Override
    public Entity spawnEntity(Location loc, EntityType type, boolean randomizeData) { return spawnEntity(loc, type); }
    @Override
    public boolean generateTree(Location location, java.util.Random random, TreeType type, java.util.function.Predicate<? super org.bukkit.block.BlockState> statePredicate) { return generateTree(location, type); }
    @Override
    public boolean generateTree(Location location, java.util.Random random, TreeType type) { return generateTree(location, type); }
    @Override
    public boolean generateTree(Location location, java.util.Random random, TreeType type, java.util.function.Consumer<? super org.bukkit.block.BlockState> stateConsumer) { return generateTree(location, type); }

    private void playSoundEntity(org.bukkit.entity.Entity entity, org.bukkit.Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        playSound(entity.getLocation(), sound, category, volume, pitch);
    }

    private void playSoundEntityString(org.bukkit.entity.Entity entity, String sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        playSound(entity.getLocation(), sound, category, volume, pitch);
    }
}
