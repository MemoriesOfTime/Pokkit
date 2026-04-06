package org.bukkit.craftbukkit.v1_99_R9;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cn.nukkit.permission.BanEntry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import nl.rutgerkok.pokkit.*;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.StructureType;
import org.bukkit.Tag;
import org.bukkit.UnsafeValues;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.*;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.MusicInstrument;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import net.md_5.bungee.api.chat.BaseComponent;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.boss.PokkitBossBar;
import nl.rutgerkok.pokkit.command.PokkitCommandFetcher;
import nl.rutgerkok.pokkit.command.PokkitCommandSender;
import nl.rutgerkok.pokkit.enchantment.PokkitEnchantment;
import nl.rutgerkok.pokkit.inventory.custom.PokkitCustomInventory;
import nl.rutgerkok.pokkit.item.PokkitItemFactory;
import nl.rutgerkok.pokkit.metadata.AllMetadataStore;
import nl.rutgerkok.pokkit.player.OnlinePlayerData;
import nl.rutgerkok.pokkit.player.PokkitOfflinePlayer;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import nl.rutgerkok.pokkit.plugin.PokkitPluginManager;
import nl.rutgerkok.pokkit.scheduler.PokkitScheduler;
import nl.rutgerkok.pokkit.scoreboard.PokkitScoreboardManager;
import nl.rutgerkok.pokkit.tag.PokkitBlockTag;
import nl.rutgerkok.pokkit.tag.PokkitItemTag;
import nl.rutgerkok.pokkit.world.PokkitWorld;
import nl.rutgerkok.pokkit.world.PokkitWorldType;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

public final class CraftServer extends Server.Spigot implements Server {

	public static cn.nukkit.Server toNukkit(Server server) {
		return ((CraftServer) server).nukkit;
	}

	public final cn.nukkit.Server nukkit;
	private final PokkitScheduler scheduler;
	private final PokkitPluginManager pluginManager;
	private final PokkitCommandFetcher commandFetcher;
	private final File pluginFolder;
	private final SimpleServicesManager servicesManager;
	private final ScoreboardManager scoreboardManager;
	private final OnlinePlayerData onlinePlayerData;
	private final AllMetadataStore metadataOverview;
	private final PokkitUnsafe pokkitUnsafe;
	private final PokkitItemFactory itemFactory;
	private final PokkitHelpMap helpMap;
	private final Messenger messenger;
	private final Logger logger;

	private final Map<Class<? extends Keyed>, Registry<? extends Keyed>> registries = new HashMap<>();

	/**
	 * Bukkit doesn't offer a way to dynamically register commands, so a lot of
	 * plugins resort to accessing this field using reflection. This field is
	 * the reason this class is called CraftServer, and is stored in a
	 * CraftBukkit package.
	 */
	private final SimpleCommandMap commandMap;

	public CraftServer(cn.nukkit.Server nukkitServer, Logger logger, File pluginFolder) {
		this.nukkit = Objects.requireNonNull(nukkitServer, "nukkitServer");
		this.pluginFolder = Objects.requireNonNull(pluginFolder, "pluginFolder");
		this.logger = Objects.requireNonNull(logger, "logger");

		this.commandMap = new NotSoSimpleCommandMap(this);
		this.scheduler = new PokkitScheduler(nukkitServer.getScheduler());
		this.pluginManager = new PokkitPluginManager(nukkitServer.getPluginManager(), this.commandMap);
		this.servicesManager = new SimpleServicesManager();
		this.commandFetcher = new PokkitCommandFetcher(nukkitServer::getPluginCommand);
		this.scoreboardManager = new PokkitScoreboardManager();
		this.onlinePlayerData = new OnlinePlayerData();
		this.metadataOverview = new AllMetadataStore();
		this.pokkitUnsafe = new PokkitUnsafe();
		this.itemFactory = new PokkitItemFactory();
		this.helpMap = new PokkitHelpMap();
		this.messenger = new PokkitPluginMessenger();

		PokkitEnchantment.registerNukkitEnchantmentsInBukkit();
	}

	@Override
	public boolean addRecipe(Recipe arg0) {
		return false;
	}

	@Override
	public Iterator<Advancement> advancementIterator() {
		// Advancements not implemented
		return Collections.emptyIterator();
	}

	@Override
	public void banIP(String ip) {
		nukkit.getIPBans().addBan(ip);
	}

	@Override
	public void broadcast(BaseComponent component) {
		broadcastMessage(component.toLegacyText());
	}

	@Override
	public void broadcast(BaseComponent... components) {
		StringBuilder message = new StringBuilder();
		for (BaseComponent component : components) {
			message.append(component.toLegacyText());
		}
		broadcastMessage(message.toString());
	}

	@Override
	public int broadcast(@NotNull String message, @NotNull String permission) {
		return nukkit.broadcast(message, permission);
	}

	@Override
	public int broadcastMessage(@NotNull String message) {
		return nukkit.broadcastMessage(message);
	}

	@Override
	public void clearRecipes() {
		nukkit.getCraftingManager().recipes.clear();
		nukkit.getCraftingManager().rebuildPacket();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Keyed> org.bukkit.Registry<T> getRegistry(Class<T> type) {
		Registry<? extends Keyed> registry = registries.get(type);
		if (registry != null) {
			return (org.bukkit.Registry<T>) registry;
		}
		if (type == BlockType.class) {
			registry = PokkitRegistries.createBlockTypeRegistry();
		} else if (type == Enchantment.class) {
			registry = PokkitRegistries.createEnchantmentRegistry();
		} else if (type == ItemType.class) {
			registry = PokkitRegistries.createItemTypeRegistry();
		} else if (type == MusicInstrument.class) {
			registry = PokkitRegistries.createMusicInstrumentRegistry();
		} else {
			registry = new PokkitRegistry<>();
		}
		registries.put(type, registry);
		return (org.bukkit.Registry<T>) registry;
	}

	@Override
	public BlockData createBlockData(Material material) {
		return PokkitBlockData.createBlockData(material, 0);
	}

	@Override
	public BlockData createBlockData(Material material, Consumer<? super BlockData> consumer) {
		BlockData blockData = PokkitBlockData.createBlockData(material, 0);
		consumer.accept(blockData);
		return blockData;
	}

	@Override
	public BlockData createBlockData(Material material, String data) throws IllegalArgumentException {
		try {
			int blockData = Integer.parseUnsignedInt(data);
			return PokkitBlockData.createBlockData(material, blockData);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Data \""+data+"\"is not valid block data. " + Pokkit.NAME + " expects a number as the block data, as block states do not exist yet in MCPE.");
		}
	}

	@Override
	public BlockData createBlockData(String data) throws IllegalArgumentException {
		return PokkitBlockData.createBlockData(data);
	}

	@Override
	public BossBar createBossBar(String arg0, BarColor arg1, BarStyle arg2, BarFlag... arg3) {
		return new PokkitBossBar(arg0, arg1, arg2, arg3);
	}

	@Override
	public KeyedBossBar createBossBar(NamespacedKey namespacedKey, String s, BarColor barColor, BarStyle barStyle, BarFlag... barFlags) {
		return null;
	}

	@Override
	public Iterator<KeyedBossBar> getBossBars() {
		return Collections.emptyIterator();
	}

	@Override
	public KeyedBossBar getBossBar(NamespacedKey namespacedKey) {
		return null;
	}

	@Override
	public boolean removeBossBar(NamespacedKey namespacedKey) {
		return false;
	}

	@Override
	public ChunkData createChunkData(World arg0) {
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder holder, int size) throws IllegalArgumentException {
		return createInventory(holder, size, "Chest");
	}

	@Override
	public Inventory createInventory(InventoryHolder holder, int size, String title) throws IllegalArgumentException {
		return PokkitCustomInventory.create(holder, InventoryType.CHEST, title, size);
	}

	@Override
	public Inventory createInventory(InventoryHolder holder, InventoryType type) {
		String title = (type == InventoryType.ENDER_CHEST) ? "Ender Chest" : "Chest";
		return createInventory(holder, type, title);
	}

	@Override
	public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
		return PokkitCustomInventory.create(holder, type, title, type.getDefaultSize());
	}

	@Override
	public MapView createMap(World arg0) {
		return null;
	}

	@Override
	public Merchant createMerchant(String arg0) {
		return null;
	}

	@Override
	public World createWorld(WorldCreator creator) {
		World alreadyLoaded = this.getWorld(creator.name());
		if (alreadyLoaded != null) {
			return alreadyLoaded;
		}

		if (nukkit.isLevelGenerated(creator.name())) {
			nukkit.loadLevel(creator.name());
			World alreadyGenerated = this.getWorld(creator.name());
			return alreadyGenerated;
		}

		if (creator.environment() != Environment.NORMAL) {
			throw new IllegalArgumentException("No Nether or End support yet");
		}
		if (creator.generator() != null) {
			throw new IllegalArgumentException("Custom generators are not yet supported");
		}

		Map<String, Object> options = new HashMap<>();
		options.put("preset", creator.generatorSettings());
		if (!nukkit.generateLevel(creator.name(), creator.seed(), PokkitWorldType.toNukkit(creator.type()), options)) {
			throw new RuntimeException("Failed to create world " + creator.name());
		}
		World world = this.getWorld(creator.name());
		Pokkit.assertion(world != null, "World was still null");
		return world;
	}

	@Override
	public boolean dispatchCommand(CommandSender sender, String command) throws CommandException {
		return nukkit.dispatchCommand(PokkitCommandSender.toNukkit(sender), command);
	}

	@Override
	public Advancement getAdvancement(NamespacedKey key) {
		// Advancements not implemented
		return null;
	}

	@Override
	public boolean getAllowEnd() {
		return nukkit.endEnabled;
	}

	@Override
	public boolean getAllowFlight() {
		return nukkit.getAllowFlight();
	}

	@Override
	public boolean getAllowNether() {
		return nukkit.netherEnabled;
	}

	@Override
	public int getAmbientSpawnLimit() {
		return -1;
	}

	@Override
	public int getAnimalSpawnLimit() {
		return nukkit.spawnAnimals ? -1 : 0;
	}

	@Override
	public BanList getBanList(Type type) {
		switch (type) {
			case IP:
				return PokkitBanList.toBukkit(nukkit.getIPBans());
			case NAME:
			default:
				return PokkitBanList.toBukkit(nukkit.getNameBans());
		}
	}

	@Override
	public Set<OfflinePlayer> getBannedPlayers() {
		return nukkit.getNameBans().getEntires().values()
				.stream().map(BanEntry::getName).map(this::getOfflinePlayer).collect(Collectors.toSet());
	}

	@Override
	public String getBukkitVersion() {
		return Pokkit.getBukkitVersion();
	}

	@Override
	public Map<String, String[]> getCommandAliases() {
		// Command aliases method not available in Nukkit-MOT, return empty map
		return ImmutableMap.of();
	}

	@Override
	public YamlConfiguration getConfig() {
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<String, Object> entry : nukkit.getProperties().getAll().entrySet()) {
			config.set(entry.getKey(), entry.getValue());
		}
		return config;
	}

    @Override
    public long getConnectionThrottle() {
        return nukkit.getPropertyInt("connection-throttle", 0);
    }

	@Override
	public ConsoleCommandSender getConsoleSender() {
		return (ConsoleCommandSender) PokkitCommandSender.toBukkit(nukkit.getConsoleSender());
	}

	@Override
	public GameMode getDefaultGameMode() {
		return PokkitGameMode.toBukkit(nukkit.getDefaultGamemode());
	}

	@Override
	public Entity getEntity(UUID uuid) {
		Player pl = getPlayer(uuid);
		if (pl != null) return pl;
		for (Level level : nukkit.getLevels().values()) {
			for (cn.nukkit.entity.Entity entity : level.getEntities()) {
				if (uuid.equals(entity.getUniqueId())) {
					return nl.rutgerkok.pokkit.entity.PokkitEntity.toBukkit(entity);
				}
			}
		}
		return null;
	}

	@Override
	public boolean getGenerateStructures() {
		return nukkit.getPropertyBoolean("generate-structures", true);
	}

	@Override
	public HelpMap getHelpMap() {
		return helpMap;
	}

	@Override
	public int getIdleTimeout() {
		return nukkit.getPropertyInt("player-idle-timeout", 0);
	}

	@Override
	public String getIp() {
		return nukkit.getIp();
	}

	@Override
	public Set<String> getIPBans() {
		return nukkit.getIPBans().getEntires().keySet();
	}

	@Override
	public ItemFactory getItemFactory() {
		return itemFactory;
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return getMessenger().getIncomingChannels();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public LootTable getLootTable(NamespacedKey key) {
		return null;
	}

	@Override
	public List<Entity> selectEntities(CommandSender commandSender, String s) throws IllegalArgumentException {
		return Collections.emptyList();
	}

	@Override
	public int getMaxPlayers() {
		return nukkit.getMaxPlayers();
	}

	@Override
	public Messenger getMessenger() {
		return messenger;
	}

	/**
	 * Gets all meta data that the Bukkit side of the server knows about.
	 *
	 * @return All meta data.
	 */
	public AllMetadataStore getMetadata() {
		return metadataOverview;
	}

	@Override
	public int getMonsterSpawnLimit() {
		return nukkit.spawnMonsters ? -1 : 0;
	}

	@Override
	public String getMotd() {
		return nukkit.getMotd();
	}

	@Override
	public String getName() {
		return nukkit.getName();
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		return PokkitOfflinePlayer.fromName(name);
	}

	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		return getOfflinePlayer(UniqueIdConversion.playerIdToName(uuid));
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers() {
		ArrayList<OfflinePlayer> offlineList = new ArrayList<>();

		for (File fileEntry : new File(nukkit.getDataPath() + "players").listFiles()) {
			String fileName = fileEntry.getName();
			if (!fileName.endsWith(".dat")) {
				continue;
			}
			String playerName = fileName.substring(0, fileName.length() - ".dat".length());
			offlineList.add(PokkitOfflinePlayer.fromName(playerName));
	    }

		// Now we convert the OfflinePlayer ArrayList to an array
		OfflinePlayer[] array = offlineList.toArray(new OfflinePlayer[offlineList.size()]);

		return array;
	}

	@Override
	public boolean getOnlineMode() {
		return false;
	}

	/**
	 * Gets the data of all online players.
	 *
	 * @return The online player data.
	 */
	public OnlinePlayerData getOnlinePlayerData() {
		return onlinePlayerData;
	}

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		return nukkit.getOnlinePlayers().values().stream().map(PokkitPlayer::toBukkit).collect(Collectors.toList());
	}

	@Override
	public Set<OfflinePlayer> getOperators() {
		return nukkit.getOps().getAll().keySet().stream()
				.map(this::getOfflinePlayer)
				.collect(Collectors.toSet());
	}

	@Override
	public Player getPlayer(String name) {
		return PokkitPlayer.toBukkit(nukkit.getPlayer(name));
	}

	@Override
	public Player getPlayer(UUID uuid) {
		return PokkitPlayer.toBukkit(nukkit.getOnlinePlayers().get(uuid));
	}

	@Override
	public Player getPlayerExact(String name) {
		return PokkitPlayer.toBukkit(nukkit.getPlayer(name));
	}

	@Override
	public PluginCommand getPluginCommand(String name) {
		return commandFetcher.getBukkitPluginCommand(name);
	}

	@Override
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	@Override
	public int getPort() {
		return nukkit.getPort();
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack arg0) {
		return Collections.emptyList();
	}

	@Override
	public BukkitScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	@Override
	public CachedServerIcon getServerIcon() {
		return null;
	}

	@Override
	public ServicesManager getServicesManager() {
		return servicesManager;
	}

	@Override
	public String getShutdownMessage() {
		return nukkit.getPropertyString("shutdown-message", "Server closed");
	}

	@Override
	public int getSpawnRadius() {
		return nukkit.getSpawnRadius();
	}

	@Override
	public <T extends Keyed> Tag<T> getTag(String registry, NamespacedKey tag, Class<T> clazz) {

        switch (registry) {
            case org.bukkit.Tag.REGISTRY_BLOCKS:
                Preconditions.checkArgument(clazz == org.bukkit.Material.class, "Block namespace must have material type");

                return (org.bukkit.Tag<T>) new PokkitBlockTag(tag);
            case org.bukkit.Tag.REGISTRY_ITEMS:
                Preconditions.checkArgument(clazz == org.bukkit.Material.class, "Item namespace must have material type");

                return (org.bukkit.Tag<T>) new PokkitItemTag(tag);
            default:
                return (org.bukkit.Tag<T>) new nl.rutgerkok.pokkit.tag.PokkitEmptyTag<>(tag);
        }
	}

	@Override
	public <T extends Keyed> Iterable<Tag<T>> getTags(String registry, Class<T> clazz) {
		return Collections.emptyList();
	}

	@Override
	public int getTicksPerAnimalSpawns() {
		return nukkit.spawnAnimals ? 400 : 0;
	}

	@Override
	public int getTicksPerMonsterSpawns() {
		return nukkit.spawnMonsters ? 1 : 0;
	}

	@Override
	public int getTicksPerWaterSpawns() {
		return 1;
	}

	@Override
	public int getTicksPerAmbientSpawns() {
		return 1;
	}

	@Override
	@Deprecated
	public UnsafeValues getUnsafe() {
		return pokkitUnsafe;
	}

	@Override
	public String getUpdateFolder() {
		return "update";
	}

	@Override
	public File getUpdateFolderFile() {
		return new File(this.pluginFolder, this.getUpdateFolder());
	}

	@Override
	public String getVersion() {
		return nukkit.getVersion();
	}

	@Override
	public int getViewDistance() {
		return nukkit.getViewDistance();
	}

	@Override
	public WarningState getWarningState() {
		return WarningState.DEFAULT;
	}

	@Override
	public int getWaterAnimalSpawnLimit() {
		return -1;
	}

	@Override
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		return nukkit.getWhitelist().getKeys(true).stream().map(this::getOfflinePlayer).collect(Collectors.toSet());
	}

	@Override
	public World getWorld(String worldName) {
		return PokkitWorld.toBukkit(nukkit.getLevelByName(worldName));
	}

	@Override
	public World getWorld(UUID uuid) {
		return PokkitWorld.toBukkit(nukkit.getLevel(UniqueIdConversion.levelIdToIndex(uuid)));
	}

	@Override
	public MapView getMap(int i) {
		return null;
	}

	@Override
	public File getWorldContainer() {
		return new File(nukkit.getDataPath() + "/worlds/");
	}

	@Override
	public List<World> getWorlds() {
		return nukkit.getLevels().values().stream().map(PokkitWorld::toBukkit).collect(Collectors.toList());
	}

	@Override
	public String getWorldType() {
		return nukkit.getLevelType();
	}

	@Override
	public boolean hasWhitelist() {
		return nukkit.hasWhitelist();
	}

	@Override
	public boolean isHardcore() {
		return nukkit.isHardcore();
	}

	@Override
	public boolean isPrimaryThread() {
		String threadName = Thread.currentThread().getName();
		if (threadName.contains("Async") || threadName.contains("RakNet")) {
			return false;
		}
		return true;
	}

	/**
	 * Loads the Bukkit plugins. Must be called only once.
	 *
	 * @return The plugins that were loaded.
	 */
	public Plugin[] loadPlugins() {
		return this.pluginManager.loadPlugins(pluginFolder);
	}

	@Override
	public CachedServerIcon loadServerIcon(BufferedImage arg0) throws IllegalArgumentException, Exception {
		return null;
	}

	@Override
	public CachedServerIcon loadServerIcon(File arg0) throws IllegalArgumentException, Exception {
		return null;
	}

	@Override
	public List<Player> matchPlayer(String partialName) {
		return Arrays.stream(nukkit.matchPlayer(partialName)).map(PokkitPlayer::toBukkit).collect(Collectors.toList());
	}

	@Override
	public Iterator<Recipe> recipeIterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void reload() {
		nukkit.reload();
	}

	@Override
	public void reloadData() {
		// Are there more things we can reload, without reloading all plugins or worlds?
		nukkit.reloadWhitelist();
	}

	@Override
	public void reloadWhitelist() {
		nukkit.reloadWhitelist();
	}

	@Override
	public void resetRecipes() {
		nukkit.getCraftingManager().rebuildPacket();
	}

	@Override
	public boolean removeRecipe(NamespacedKey namespacedKey) {
		return false;
	}

	@Override
	public void restart() {
		nukkit.shutdown();
	}

	@Override
	public void savePlayers() {
		nukkit.getOnlinePlayers().values().forEach(cn.nukkit.Player::save);
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
		for (Player player : getOnlinePlayers()) {
			player.sendPluginMessage(arg0, arg1, arg2);
		}
	}

	@Override
	public void setDefaultGameMode(GameMode gameMode) {
		// See nukkit.getDefaultGamemode() for setting name
		nukkit.setPropertyInt("gamemode", PokkitGameMode.toNukkit(gameMode));

		Pokkit.assertion(getDefaultGameMode() == gameMode, "Failed to set game mode");
	}

	@Override
	public void setIdleTimeout(int timeout) {
		nukkit.setPropertyInt("player-idle-timeout", timeout);
	}

	@Override
	public void setSpawnRadius(int radius) {
		// See nukkit.getSpawnRadius() for setting name
		nukkit.setPropertyInt("spawn-protection", radius);

		Pokkit.assertion(nukkit.getSpawnRadius() == radius, "Failed to set spawn radius");
	}

	@Override
	public void setWhitelist(boolean whitelist) {
		// See nukkit.hasWhitelist() for setting name
		nukkit.setPropertyBoolean("white-list", whitelist);

		Pokkit.assertion(nukkit.hasWhitelist() == whitelist, "Failed to set whitelist");
	}

	@Override
	public void shutdown() {
		nukkit.shutdown();
	}

	@Override
	public Spigot spigot() {
		return this;
	}

	@Override
	public void unbanIP(String ip) {
		nukkit.getIPBans().remove(ip);
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		World world = this.getWorld(name);
		if (world == null) {
			return false;
		}
		return unloadWorld(world, save);
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		Level level = PokkitWorld.toNukkit(world);
		if (save) {
			level.save(true);
		}
		return nukkit.unloadLevel(level, true);
	}

	@Override
	public ItemStack createExplorerMap(World w, Location l, StructureType t) {
		return null;
	}

	@Override
	public ItemStack createExplorerMap(World w, Location l, StructureType t, int i, boolean b) {
		return null;
	}

	@Override
	public org.bukkit.structure.StructureManager getStructureManager() {
		return null;
	}

	@Override
	public void setMaxPlayers(int maxPlayers) {
		nukkit.setMaxPlayers(maxPlayers);
	}

	@Override
	public int getSimulationDistance() {
		return getViewDistance();
	}

	@Override
	public int getMaxWorldSize() {
		return 29999984;
	}

	@Override
	public boolean isLoggingIPs() {
		return true;
	}

	@Override
	public List<String> getInitialEnabledPacks() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getInitialDisabledPacks() {
		return Collections.emptyList();
	}

	@Override
	public org.bukkit.packs.DataPackManager getDataPackManager() {
		return null;
	}

	@Override
	public org.bukkit.ServerTickManager getServerTickManager() {
		return null;
	}

	@Override
	public org.bukkit.packs.ResourcePack getServerResourcePack() {
		return null;
	}

	@Override
	public String getResourcePack() {
		cn.nukkit.resourcepacks.ResourcePack[] packs = nukkit.getResourcePackManager().getResourceStack();
		if (packs.length > 0) {
			return packs[0].getPackId().toString();
		}
		return null;
	}

	@Override
	public String getResourcePackHash() {
		cn.nukkit.resourcepacks.ResourcePack[] packs = nukkit.getResourcePackManager().getResourceStack();
		if (packs.length > 0) {
			byte[] sha = packs[0].getSha256();
			if (sha != null) {
				StringBuilder sb = new StringBuilder();
				for (byte b : sha) {
					sb.append(String.format("%02x", b));
				}
				return sb.toString();
			}
		}
		return null;
	}

	@Override
	public String getResourcePackPrompt() {
		return null;
	}

	@Override
	public boolean isResourcePackRequired() {
		return nukkit.forceResources;
	}

	@Override
	public boolean isWhitelistEnforced() {
		return nukkit.whitelistEnabled;
	}

	@Override
	public void setWhitelistEnforced(boolean enforced) {
		nukkit.whitelistEnabled = enforced;
	}

	@Override
	public int getTicksPerWaterAmbientSpawns() {
		return 1;
	}

	@Override
	public int getTicksPerWaterUndergroundCreatureSpawns() {
		return 1;
	}

	@Override
	public int getTicksPerSpawns(org.bukkit.entity.SpawnCategory spawnCategory) {
		switch (spawnCategory) {
			case MONSTER:
				return getTicksPerMonsterSpawns();
			case ANIMAL:
				return getTicksPerAnimalSpawns();
			case WATER_ANIMAL:
				return getTicksPerWaterSpawns();
			case AMBIENT:
				return getTicksPerAmbientSpawns();
			case WATER_AMBIENT:
				return getTicksPerWaterAmbientSpawns();
			case WATER_UNDERGROUND_CREATURE:
				return getTicksPerWaterUndergroundCreatureSpawns();
			default:
				return 1;
		}
	}

	@Override
	public int getWaterAmbientSpawnLimit() {
		return -1;
	}

	@Override
	public int getWaterUndergroundCreatureSpawnLimit() {
		return -1;
	}

	@Override
	public int getSpawnLimit(org.bukkit.entity.SpawnCategory spawnCategory) {
		switch (spawnCategory) {
			case MONSTER:
				return getMonsterSpawnLimit();
			case ANIMAL:
				return getAnimalSpawnLimit();
			case WATER_ANIMAL:
				return getWaterAnimalSpawnLimit();
			case AMBIENT:
				return getAmbientSpawnLimit();
			case WATER_AMBIENT:
				return getWaterAmbientSpawnLimit();
			case WATER_UNDERGROUND_CREATURE:
				return getWaterUndergroundCreatureSpawnLimit();
			default:
				return -1;
		}
	}

	@Override
	public boolean shouldSendChatPreviews() {
		return false;
	}

	@Override
	public boolean isEnforcingSecureProfiles() {
		return false;
	}

	@Override
	public boolean isAcceptingTransfers() {
		return false;
	}

	@Override
	public boolean getHideOnlinePlayers() {
		return false;
	}

    @Override
    public org.bukkit.profile.PlayerProfile createPlayerProfile(UUID uuid, String name) {
        return org.bukkit.Bukkit.createPlayerProfile(uuid, name);
    }

    @Override
    public org.bukkit.profile.PlayerProfile createPlayerProfile(UUID uuid) {
        return org.bukkit.Bukkit.createPlayerProfile(uuid);
    }

    @Override
    public org.bukkit.profile.PlayerProfile createPlayerProfile(String name) {
        return org.bukkit.Bukkit.createPlayerProfile(name);
    }

	@Override
	public void banIP(java.net.InetAddress address) {
		getBanList(BanList.Type.IP).addBan(address.getHostAddress(), null, (java.util.Date) null, null);
	}

	@Override
	public void unbanIP(java.net.InetAddress address) {
		getBanList(BanList.Type.IP).pardon(address.getHostAddress());
	}

	@Override
	public WorldBorder createWorldBorder() {
		return new org.bukkit.WorldBorder() {
			private double size = 60000000;
			private double centerX, centerZ;
			private double damageBuffer = 5;
			private double damageAmount = 0.2;
			private int warningDistance = 5;
			private int warningTime = 15;

			@Override public org.bukkit.World getWorld() { return null; }
			@Override public void reset() { size = 60000000; }
			@Override public double getSize() { return size; }
			@Override public void setSize(double s) { size = s; }
			@Override public void setSize(double s, long seconds) { size = s; }
			@Override public void setSize(double s, java.util.concurrent.TimeUnit unit, long duration) { size = s; }
			@Override public Location getCenter() { return new Location(null, centerX, 0, centerZ); }
			@Override public void setCenter(double x, double z) { centerX = x; centerZ = z; }
			@Override public void setCenter(Location location) { centerX = location.getX(); centerZ = location.getZ(); }
			@Override public double getDamageBuffer() { return damageBuffer; }
			@Override public void setDamageBuffer(double blocks) { damageBuffer = blocks; }
			@Override public double getDamageAmount() { return damageAmount; }
			@Override public void setDamageAmount(double damage) { damageAmount = damage; }
			@Override public int getWarningDistance() { return warningDistance; }
			@Override public void setWarningDistance(int distance) { warningDistance = distance; }
			@Override public int getWarningTime() { return warningTime; }
			@Override public void setWarningTime(int time) { warningTime = time; }
			@Override public boolean isInside(Location location) {
				double halfSize = size / 2;
				return location.getX() >= centerX - halfSize && location.getX() <= centerX + halfSize
					&& location.getZ() >= centerZ - halfSize && location.getZ() <= centerZ + halfSize;
			}
			@Override public double getMaxSize() { return 60000000; }
			@Override public double getMaxCenterCoordinate() { return 30000000; }
		};
	}

	@Override
	public void setMotd(String motd) {
		nukkit.setPropertyString("motd", motd);
	}

	@Override
	public Recipe getRecipe(NamespacedKey key) {
		return null;
	}

	@Override
	public Recipe getCraftingRecipe(ItemStack[] craftingMatrix, World world) {
		return null;
	}

	@Override
	public ItemStack craftItem(ItemStack[] craftingMatrix, World world, Player player) {
		return null;
	}

	@Override
	public ItemStack craftItem(ItemStack[] craftingMatrix, World world) {
		return null;
	}

	@Override
	public org.bukkit.inventory.ItemCraftResult craftItemResult(ItemStack[] craftingMatrix, World world, Player player) {
		return null;
	}

	@Override
	public org.bukkit.inventory.ItemCraftResult craftItemResult(ItemStack[] craftingMatrix, World world) {
		return null;
	}

	@Override
	public org.bukkit.scoreboard.Criteria getScoreboardCriteria(String name) {
		return null;
	}

	@Override
	public int getMaxChainedNeighborUpdates() {
		return 1000000;
	}

	@Override
	public org.bukkit.entity.EntityFactory getEntityFactory() {
		return null;
	}

	@Override
	public org.bukkit.ServerLinks getServerLinks() {
		return null;
	}
}
