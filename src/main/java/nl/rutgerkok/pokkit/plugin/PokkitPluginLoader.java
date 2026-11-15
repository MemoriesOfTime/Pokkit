package nl.rutgerkok.pokkit.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.google.common.base.Preconditions;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;

/**
 * Plugin loader for Nukkit that loads Bukkit plugins.
 *
 * <p>
 * Nukkit requires the instance to be created using reflection, which makes it
 * difficult for us to obtain the instance. We work around this by letting the
 * constructor upload the instance to a global variable, so that it can then be
 * retrieved exactly once.
 */
public final class PokkitPluginLoader implements cn.nukkit.plugin.PluginLoader {

	class RecognizeJarsInDir implements Closeable {

		private RecognizeJarsInDir() {
			Preconditions.checkState(!recognizeJars, "No two Bukkit plugin loading allowers may be active at the same time");
			recognizeJars = true;
		}

		@Override
		public void close() {
			recognizeJars = false;
		}

	}

	private static PokkitPluginLoader temporaryInstance;

	static PokkitPluginLoader getInstanceBack() {
		Preconditions.checkState(temporaryInstance != null, "No temporary instance available");
		PokkitPluginLoader returnValue = temporaryInstance;
		temporaryInstance = null;
		return returnValue;
	}

	private JavaPluginLoader bukkit;
	private boolean recognizeJars = false;

	/**
	 * Plugin manager used by {@link JavaPluginLoader} for dependency checks.
	 */
	private SimplePluginManager dependencyManager;

	/**
	 * Direct dependencies of the Bukkit plugins that have been seen so far.
	 * Needed because the {@link SimplePluginManager} that
	 * {@link JavaPluginLoader} talks to has an empty dependency graph. Like
	 * Bukkit, soft dependencies count for class sharing. Nukkit's plugin
	 * description does not expose the {@code provides} section, so plugins that
	 * provide another plugin name are not part of the graph.
	 */
	private final Map<String, Set<String>> dependencies = new ConcurrentHashMap<>();

	public PokkitPluginLoader(cn.nukkit.Server server) {
		// Required for instantiation
		temporaryInstance = this;
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		getPluginLoader().disablePlugin(PokkitPlugin.toBukkit(plugin));
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		getPluginLoader().enablePlugin(PokkitPlugin.toBukkit(plugin));
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
		try (JarFile jar = new JarFile(file)) {
			JarEntry entry = jar.getJarEntry("plugin.yml");
			if (entry == null) {
				return null;
			}

			InputStream stream = jar.getInputStream(entry);
			PluginDescription description = new PokkitPluginDescription(stream);
			// Remember the dependencies, so that plugins that depend on each
			// other can share classes. Like Bukkit, soft dependencies count
			// for class sharing.
			Set<String> pluginDependencies = new HashSet<>(description.getDepend());
			pluginDependencies.addAll(description.getSoftDepend());
			dependencies.put(description.getName(), pluginDependencies);
			return description;
		} catch (Exception e) {
			Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not load plugin " + file.getName(), e);
			return null;
		}
	}

	@Override
	public PluginDescription getPluginDescription(String fileName) {
		return getPluginDescription(new File(fileName));
	}

	@Override
	public Pattern[] getPluginFilters() {
		if (!this.recognizeJars) {
			// Normally, we don't want to recognize plugins, to give room to the
			// Nukkit plugin loader
			return new Pattern[0];
		}
		return new Pattern[] { Pattern.compile("^.+\\.jar$") };
	}

	@SuppressWarnings("deprecation")
	private JavaPluginLoader getPluginLoader() {
		if (bukkit == null) {
			bukkit = new JavaPluginLoader(createServerProxy());
		}
		return bukkit;
	}

	/**
	 * Creates a Server proxy that returns a {@link SimplePluginManager} from
	 * {@code getPluginManager()}. This is needed because Spigot's
	 * {@link JavaPluginLoader} casts the plugin manager to
	 * {@link SimplePluginManager}.
	 */
	private Server createServerProxy() {
		final Server realServer = Bukkit.getServer();
		final SimplePluginManager spm = getDependencyManager();
		return (Server) Proxy.newProxyInstance(
				Server.class.getClassLoader(),
				new Class<?>[] { Server.class },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("getPluginManager")) {
							return spm;
						}
						return method.invoke(realServer, args);
					}
				});
	}

	/**
	 * Gets the plugin manager that {@link JavaPluginLoader} uses for its
	 * dependency checks. That manager has no plugins of its own, so the loaded
	 * plugins and their dependency graph are copied into it through
	 * reflection. Without this, every plugin with a hard dependency (for
	 * example WorldGuard, which depends on WorldEdit) fails to load.
	 *
	 * @return The manager, never null.
	 */
	private SimplePluginManager getDependencyManager() {
		if (dependencyManager == null) {
			Server realServer = Bukkit.getServer();
			dependencyManager = new SimplePluginManager(realServer, new SimpleCommandMap(realServer));
			fillDependencyGraph(dependencyManager);
		}
		return dependencyManager;
	}

	@SuppressWarnings("unchecked")
	private void fillDependencyGraph(SimplePluginManager manager) {
		if (DEPENDENCY_GRAPH_FIELD == null) {
			return;
		}
		try {
			Object graph = DEPENDENCY_GRAPH_FIELD.get(manager);
			if (graph instanceof com.google.common.graph.MutableGraph<?> mutableGraph) {
				com.google.common.graph.MutableGraph<String> typedGraph =
						(com.google.common.graph.MutableGraph<String>) mutableGraph;
				dependencies.forEach((plugin, pluginDependencies) -> {
					typedGraph.addNode(plugin);
					for (String dependency : pluginDependencies) {
						typedGraph.putEdge(plugin, dependency);
					}
				});
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			// Not fatal: only class sharing between dependent plugins is
			// affected.
			logWarning("Could not build the plugin dependency graph", e);
		}
	}

	/**
	 * Makes a loaded plugin visible to the dependency checks of
	 * {@link JavaPluginLoader}.
	 *
	 * @param plugin
	 *            The plugin that was just loaded.
	 */
	@SuppressWarnings("unchecked")
	private void registerForDependencyChecks(org.bukkit.plugin.Plugin plugin) {
		if (LOOKUP_NAMES_FIELD == null || PLUGINS_FIELD == null) {
			return;
		}
		try {
			Map<String, org.bukkit.plugin.Plugin> lookupNames =
					(Map<String, org.bukkit.plugin.Plugin>) LOOKUP_NAMES_FIELD.get(dependencyManager);
			lookupNames.put(plugin.getName().replace(' ', '_'), plugin);
			List<org.bukkit.plugin.Plugin> plugins =
					(List<org.bukkit.plugin.Plugin>) PLUGINS_FIELD.get(dependencyManager);
			plugins.add(plugin);
		} catch (ReflectiveOperationException | RuntimeException e) {
			logWarning("Could not register " + plugin.getName() + " for dependency checks", e);
		}
	}

	private static void logWarning(String message, Throwable cause) {
		try {
			java.util.logging.Logger logger = Bukkit.getLogger();
			if (logger != null) {
				logger.log(Level.WARNING, "[Pokkit] " + message, cause);
			}
		} catch (RuntimeException ignored) {
			// The Bukkit server may not be available yet during class
			// initialisation.
		}
	}

	private static final java.lang.reflect.Field LOOKUP_NAMES_FIELD = field("lookupNames");
	private static final java.lang.reflect.Field PLUGINS_FIELD = field("plugins");
	private static final java.lang.reflect.Field DEPENDENCY_GRAPH_FIELD = field("dependencyGraph");

	private static java.lang.reflect.Field field(String name) {
		try {
			java.lang.reflect.Field field = SimplePluginManager.class.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException | RuntimeException e) {
			// A different server implementation may not have this field. The
			// dependency fix is then simply skipped instead of preventing the
			// server from starting.
			logWarning("Could not access SimplePluginManager#" + name, e);
			return null;
		}
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		JavaPlugin bukkitPlugin = (JavaPlugin) getPluginLoader().loadPlugin(file);
		// Make the plugin visible to the dependency checks of the next
		// plugin, so that hard dependencies resolve in any file order.
		registerForDependencyChecks(bukkitPlugin);
		return new PokkitPlugin(bukkitPlugin, getPluginDescription(file), this);
	}

	@Override
	public Plugin loadPlugin(String filename) throws Exception {
		return loadPlugin(new File(filename));
	}

	/**
	 * Temporarily allows loading of Bukkit plugins from a directory by letting
	 * this plugin loader recognize all JAR files through the
	 * {@link #getPluginFilters()} method. Close the returned instance when you
	 * are done loading Bukkit plugins from a directory.
	 *
	 * @return Instance that must be loaded when you are done loading Bukkit
	 *         plugins.
	 */
	RecognizeJarsInDir recognizeJarFiles() {
		return new RecognizeJarsInDir();
	}

}
