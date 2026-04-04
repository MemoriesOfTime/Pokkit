package nl.rutgerkok.pokkit.persistence;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelSaveEvent;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.plugin.PluginBase;
import nl.rutgerkok.pokkit.pluginservice.PokkitService;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkPersistentDataStore implements PokkitService, Listener {

	private static final String DATA_FILE = "chunk_pdc.dat";

	private static final ChunkPersistentDataStore INSTANCE = new ChunkPersistentDataStore();

	public static ChunkPersistentDataStore getInstance() {
		return INSTANCE;
	}

	private final Map<String, CompoundTag> store = new ConcurrentHashMap<>();
	private File dataFile;

	public static String chunkKey(String worldName, int chunkX, int chunkZ) {
		return worldName + ";" + chunkX + ";" + chunkZ;
	}

	public PokkitPersistentDataContainer getContainer(String worldName, int chunkX, int chunkZ) {
		String key = chunkKey(worldName, chunkX, chunkZ);
		CompoundTag tag = store.computeIfAbsent(key, k -> new CompoundTag());
		return new PokkitPersistentDataContainer(tag, "");
	}

	public void save() {
		if (dataFile == null) return;
		try {
			CompoundTag root = new CompoundTag();
			for (Map.Entry<String, CompoundTag> entry : store.entrySet()) {
				if (entry.getValue().getAllTags().isEmpty()) continue;
				root.putCompound(entry.getKey(), entry.getValue());
			}
			NBTIO.write(root, dataFile, ByteOrder.LITTLE_ENDIAN);
		} catch (IOException e) {
			System.getLogger("Pokkit").log(System.Logger.Level.ERROR,
					"Failed to save chunk persistent data", e);
		}
	}

	private void loadFromFile() {
		store.clear();
		if (!dataFile.exists()) return;
		try {
			CompoundTag root = NBTIO.read(dataFile, ByteOrder.LITTLE_ENDIAN);
			for (Tag t : root.getAllTags()) {
				if (t instanceof CompoundTag) {
					store.put(t.getName(), (CompoundTag) t);
				}
			}
		} catch (IOException e) {
			System.getLogger("Pokkit").log(System.Logger.Level.ERROR,
					"Failed to load chunk persistent data", e);
		}
	}

	@EventHandler
	public void onLevelSave(LevelSaveEvent event) {
		save();
	}

	@Override
	public void onLoad(PluginBase pokkit) {
		this.dataFile = new File(pokkit.getDataFolder(), DATA_FILE);
		loadFromFile();
	}

	@Override
	public void onEnable(PluginBase pokkit) {
		pokkit.getServer().getPluginManager().registerEvents(this, pokkit);
	}

	@Override
	public void onDisable(PluginBase pokkit) {
		save();
	}
}
