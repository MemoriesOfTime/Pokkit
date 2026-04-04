package org.bukkit.craftbukkit.v1_99_R9;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.BanList;
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
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Server.Spigot;
import org.bukkit.Warning.WarningState;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
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
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import com.google.common.base.Preconditions;

import nl.rutgerkok.pokkit.tag.PokkitBlockTag;
import nl.rutgerkok.pokkit.tag.PokkitItemTag;

public final class CraftServers extends Server.Spigot implements Server {

	public static CraftServers instance;
	
	public CraftServers() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(this);
        }
        instance = this;
	}
	
	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getBukkitVersion() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Collection<? extends Player> getOnlinePlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxPlayers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getIp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorldType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getGenerateStructures() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getAllowEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getAllowNether() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasWhitelist() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWhitelist(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reloadWhitelist() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int broadcastMessage(String message) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUpdateFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getUpdateFolderFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getConnectionThrottle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicksPerAnimalSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicksPerMonsterSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicksPerWaterSpawns() {
		return 0;
	}

	@Override
	public int getTicksPerAmbientSpawns() {
		return 0;
	}

	@Override
	public Player getPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player getPlayerExact(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Player> matchPlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player getPlayer(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginManager getPluginManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BukkitScheduler getScheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServicesManager getServicesManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<World> getWorlds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World createWorld(WorldCreator creator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public World getWorld(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld(UUID uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapView getMap(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapView createMap(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack createExplorerMap(World world, Location location, StructureType structureType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack createExplorerMap(World world, Location location, StructureType structureType, int radius,
			boolean findUnexplored) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return java.util.logging.Logger.getLogger(this.getClass().getName());
	}

	@Override
	public PluginCommand getPluginCommand(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void savePlayers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addRecipe(Recipe recipe) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Recipe> recipeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearRecipes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetRecipes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeRecipe(NamespacedKey namespacedKey) {
		return false;
	}

	@Override
	public Map<String, String[]> getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSpawnRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSpawnRadius(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getOnlineMode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getAllowFlight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHardcore() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int broadcast(String message, String permission) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getIPBans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void banIP(String address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbanIP(String address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<OfflinePlayer> getBannedPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BanList getBanList(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OfflinePlayer> getOperators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameMode getDefaultGameMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultGameMode(GameMode mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConsoleCommandSender getConsoleSender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getWorldContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Messenger getMessenger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HelpMap getHelpMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, InventoryType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size, String title) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merchant createMerchant(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMonsterSpawnLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAnimalSpawnLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWaterAnimalSpawnLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAmbientSpawnLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPrimaryThread() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMotd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShutdownMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WarningState getWarningState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemFactory getItemFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScoreboardManager getScoreboardManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CachedServerIcon getServerIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CachedServerIcon loadServerIcon(BufferedImage image) throws IllegalArgumentException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdleTimeout(int threshold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIdleTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChunkData createChunkData(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyedBossBar createBossBar(NamespacedKey key, String title, BarColor color, BarStyle style,
			BarFlag... flags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<KeyedBossBar> getBossBars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyedBossBar getBossBar(NamespacedKey key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeBossBar(NamespacedKey key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Entity getEntity(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Advancement getAdvancement(NamespacedKey key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Advancement> advancementIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockData createBlockData(Material material) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockData createBlockData(Material material, Consumer<? super BlockData> consumer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockData createBlockData(String data) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockData createBlockData(Material material, String data) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LootTable getLootTable(NamespacedKey key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> selectEntities(CommandSender sender, String selector) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnsafeValues getUnsafe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spigot spigot() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Keyed> org.bukkit.Registry<T> getRegistry(Class<T> aClass) {
		if (aClass == org.bukkit.block.BlockType.class) {
			return (org.bukkit.Registry<T>) nl.rutgerkok.pokkit.PokkitRegistries.createBlockTypeRegistry();
		} else if (aClass == org.bukkit.enchantments.Enchantment.class) {
			return (org.bukkit.Registry<T>) nl.rutgerkok.pokkit.PokkitRegistries.createEnchantmentRegistry();
		} else if (aClass == org.bukkit.inventory.ItemType.class) {
			return (org.bukkit.Registry<T>) nl.rutgerkok.pokkit.PokkitRegistries.createItemTypeRegistry();
		} else if (aClass == org.bukkit.MusicInstrument.class) {
			return (org.bukkit.Registry<T>) nl.rutgerkok.pokkit.PokkitRegistries.createMusicInstrumentRegistry();
		}
		return (org.bukkit.Registry<T>) new nl.rutgerkok.pokkit.PokkitRegistry<>();
	}

	@Override public org.bukkit.structure.StructureManager getStructureManager() { return null; }
	@Override public void setMaxPlayers(int maxPlayers) {}
	@Override public int getSimulationDistance() { return getViewDistance(); }
	@Override public int getMaxWorldSize() { return 29999984; }
	@Override public boolean isLoggingIPs() { return true; }
	@Override public java.util.List<String> getInitialEnabledPacks() { return java.util.Collections.emptyList(); }
	@Override public java.util.List<String> getInitialDisabledPacks() { return java.util.Collections.emptyList(); }
	@Override public org.bukkit.packs.DataPackManager getDataPackManager() { return null; }
	@Override public org.bukkit.ServerTickManager getServerTickManager() { return null; }
	@Override public org.bukkit.packs.ResourcePack getServerResourcePack() { return null; }
	@Override public String getResourcePack() { return null; }
	@Override public String getResourcePackHash() { return null; }
	@Override public String getResourcePackPrompt() { return null; }
	@Override public boolean isResourcePackRequired() { return false; }
	@Override public boolean isWhitelistEnforced() { return false; }
	@Override public void setWhitelistEnforced(boolean enforced) {}
	@Override public int getTicksPerWaterAmbientSpawns() { return 0; }
	@Override public int getTicksPerWaterUndergroundCreatureSpawns() { return 0; }
	@Override public int getTicksPerSpawns(org.bukkit.entity.SpawnCategory cat) { return 0; }
	@Override public int getWaterAmbientSpawnLimit() { return 0; }
	@Override public int getWaterUndergroundCreatureSpawnLimit() { return 0; }
	@Override public int getSpawnLimit(org.bukkit.entity.SpawnCategory cat) { return 0; }
	@Override public boolean shouldSendChatPreviews() { return false; }
	@Override public boolean isEnforcingSecureProfiles() { return false; }
	@Override public boolean isAcceptingTransfers() { return false; }
	@Override public boolean getHideOnlinePlayers() { return false; }
	@Override public org.bukkit.profile.PlayerProfile createPlayerProfile(java.util.UUID uuid, String name) { return null; }
	@Override public org.bukkit.profile.PlayerProfile createPlayerProfile(java.util.UUID uuid) { return null; }
	@Override public org.bukkit.profile.PlayerProfile createPlayerProfile(String name) { return null; }
	@Override public void banIP(java.net.InetAddress addr) {}
	@Override public void unbanIP(java.net.InetAddress addr) {}
	@Override public org.bukkit.WorldBorder createWorldBorder() { return null; }
	@Override public void setMotd(String motd) {}
	@Override public org.bukkit.inventory.Recipe getRecipe(NamespacedKey key) { return null; }
	@Override public org.bukkit.inventory.Recipe getCraftingRecipe(org.bukkit.inventory.ItemStack[] matrix, org.bukkit.World world) { return null; }
	@Override public org.bukkit.inventory.ItemStack craftItem(org.bukkit.inventory.ItemStack[] matrix, org.bukkit.World world, org.bukkit.entity.Player player) { return null; }
	@Override public org.bukkit.inventory.ItemStack craftItem(org.bukkit.inventory.ItemStack[] matrix, org.bukkit.World world) { return null; }
	@Override public org.bukkit.scoreboard.Criteria getScoreboardCriteria(String name) { return null; }
	@Override public int getMaxChainedNeighborUpdates() { return 0; }
	@Override public org.bukkit.entity.EntityFactory getEntityFactory() { return null; }
	@Override public org.bukkit.ServerLinks getServerLinks() { return null; }
	@Override public org.bukkit.configuration.file.YamlConfiguration getConfig() { return null; }
	@Override public void restart() {}
	@Override public void broadcast(net.md_5.bungee.api.chat.BaseComponent component) {}
	@Override public void broadcast(net.md_5.bungee.api.chat.BaseComponent... components) {}


	@Override public org.bukkit.inventory.ItemCraftResult craftItemResult(org.bukkit.inventory.ItemStack[] matrix, org.bukkit.World world, org.bukkit.entity.Player player) { return null; }
	@Override public org.bukkit.inventory.ItemCraftResult craftItemResult(org.bukkit.inventory.ItemStack[] matrix, org.bukkit.World world) { return null; }

}