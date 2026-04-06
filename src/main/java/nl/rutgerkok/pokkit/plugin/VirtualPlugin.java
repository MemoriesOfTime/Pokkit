package nl.rutgerkok.pokkit.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

/**
 * A minimal Plugin implementation for virtual plugins registered by Pokkit.
 * Used for APIs like Floodgate that Pokkit provides without a real plugin JAR.
 */
public final class VirtualPlugin implements Plugin {

	private final PluginDescriptionFile description;
	private final Logger logger;

	public VirtualPlugin(String name, String version) {
		this.description = new PluginDescriptionFile(name, version, getClass().getName());
		this.logger = Logger.getLogger(name);
	}

	@Override
	public File getDataFolder() { return null; }

	@Override
	public PluginDescriptionFile getDescription() { return description; }

	@Override
	public FileConfiguration getConfig() { return null; }

	@Override
	public InputStream getResource(String filename) { return null; }

	@Override
	public void saveConfig() {}

	@Override
	public void saveDefaultConfig() {}

	@Override
	public void saveResource(String resourcePath, boolean replace) {}

	@Override
	public void reloadConfig() {}

	@Override
	public PluginLoader getPluginLoader() { return null; }

	@Override
	public Server getServer() { return null; }

	@Override
	public boolean isEnabled() { return true; }

	@Override
	public void onDisable() {}

	@Override
	public void onLoad() {}

	@Override
	public void onEnable() {}

	@Override
	public boolean isNaggable() { return false; }

	@Override
	public void setNaggable(boolean canNag) {}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) { return null; }

	@Override
	public BiomeProvider getDefaultBiomeProvider(String worldName, String id) { return null; }

	@Override
	public Logger getLogger() { return logger; }

	@Override
	public String getName() { return description.getName(); }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return false; }

	@Override
	public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return null; }
}
