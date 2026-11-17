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
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
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
		org.bukkit.plugin.Plugin bukkitPlugin = PokkitPlugin.toBukkit(plugin);
		if (hasFailed(bukkitPlugin.getName())) {
			// Disabling a plugin closes its class loader, so enabling it again
			// only produces a confusing "zip file closed" error. Real servers
			// leave a plugin that failed to enable disabled as well.
			return;
		}
		if (hasClosedClassLoader(bukkitPlugin)) {
			// Nukkit disables a plugin when its loader throws, which closes the
			// class loader. Do not touch the plugin again in that case.
			failedPlugins.add(bukkitPlugin.getName());
			return;
		}
		try {
			getPluginLoader().enablePlugin(bukkitPlugin);
		} catch (RuntimeException | Error e) {
			// JavaPluginLoader catches exceptions from onEnable and leaves the
			// plugin marked as enabled, so Nukkit will not retry those. Errors
			// that escape (a closed jar, for example) end up here.
			failedPlugins.add(bukkitPlugin.getName());
			throw e;
		}
	}

	/**
	 * Checks whether the class loader of a Bukkit plugin is already closed.
	 * {@code JavaPlugin#isEnabled()} cannot be used for this: it is set to
	 * true before {@code onEnable()} runs.
	 */
	private static boolean hasClosedClassLoader(org.bukkit.plugin.Plugin plugin) {
		if (!(plugin instanceof org.bukkit.plugin.java.JavaPlugin javaPlugin)) {
			return false;
		}
		try {
			try (InputStream stream = javaPlugin.getResource("plugin.yml")) {
				return stream == null;
			}
		} catch (RuntimeException | java.io.IOException e) {
			return true;
		}
	}

	/**
	 * Names of plugins that failed to load or enable. Their class loader is
	 * closed, so they must not be enabled a second time.
	 */
	private final Set<String> failedPlugins = ConcurrentHashMap.newKeySet();

	/**
	 * Marks a plugin as failed, because {@code onLoad()} threw. Like Bukkit,
	 * the failure is logged and the remaining plugins are still loaded.
	 *
	 * @param plugin
	 *            The plugin that failed to load.
	 * @param cause
	 *            The exception thrown by {@code onLoad()}.
	 */
	void markLoadFailure(org.bukkit.plugin.Plugin plugin, Throwable cause) {
		failedPlugins.add(plugin.getName());
		logSevere("Could not load plugin '" + plugin.getName() + "'", cause);
	}

	/**
	 * @param name
	 *            Name of a plugin, as declared in {@code plugin.yml}.
	 * @return Whether that plugin failed to load or enable.
	 */
	boolean hasFailed(String name) {
		return failedPlugins.contains(name);
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
		try (JarFile jar = new JarFile(file)) {
			JarEntry entry = jar.getJarEntry("plugin.yml");
			if (entry == null) {
				return null;
			}

			InputStream stream = jar.getInputStream(entry);
			PokkitPluginDescription description = new PokkitPluginDescription(stream);
			if (!PluginApiVersion.isSupported(description.getDeclaredApiVersion())) {
				logSevere(unsupportedApiVersionMessage(file, description.getDeclaredApiVersion()));
				return null;
			}
			// Remember the dependencies, so that plugins that depend on each
			// other can share classes. Like Bukkit, soft dependencies count
			// for class sharing.
			Set<String> pluginDependencies = new HashSet<>(description.getDepend());
			pluginDependencies.addAll(description.getSoftDepend());
			dependencies.put(description.getName(), pluginDependencies);
			// The dependency manager is created on the first plugin load, so
			// the graph it holds was built from the descriptions that were
			// known back then. Keep it up to date, otherwise isTransitiveDepend
			// rejects every dependency and JavaPluginLoader logs "loaded class
			// ... which is not a depend or softdepend of this plugin" for
			// perfectly legal class sharing.
			if (dependencyManager != null) {
				fillDependencyGraph(dependencyManager);
			}
			removeVirtualPluginDependencies(description);
			return description;
		} catch (Exception e) {
			Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not load plugin " + file.getName(), e);
			return null;
		}
	}

	/**
	 * Removes dependencies on plugins that Pokkit provides without a JAR (such
	 * as Floodgate) from the description that Nukkit reads.
	 * <p>
	 * Nukkit's plugin manager only knows real plugin JARs, so it logs
	 * "Unknown dependency" and refuses to load the plugin otherwise. Bukkit's
	 * own dependency check still runs, and is satisfied by the virtual plugin
	 * that {@link #registerVirtualPluginForDependencyChecks} adds.
	 *
	 * @param description
	 *            The description to clean up.
	 */
	private static void removeVirtualPluginDependencies(PluginDescription description) {
		description.getDepend().removeIf(PokkitPluginLoader::isVirtualPlugin);
		description.getSoftDepend().removeIf(PokkitPluginLoader::isVirtualPlugin);
		description.getLoadBefore().removeIf(PokkitPluginLoader::isVirtualPlugin);
	}

	private static boolean isVirtualPlugin(String name) {
		Server server = Bukkit.getServer();
		if (server == null || name == null) {
			return false;
		}
		PluginManager manager = server.getPluginManager();
		// Ask the registry directly instead of going through getPlugin: that
		// would build a Bukkit adapter for every (not yet loaded) dependency of
		// every plugin that is being described.
		return manager instanceof PokkitPluginManager pokkitManager && pokkitManager.isVirtualPlugin(name);
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
						// A self-edge is rejected by Guava and would abort the
						// whole graph build.
						if (!plugin.equals(dependency)) {
							typedGraph.putEdge(plugin, dependency);
						}
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
	private void registerForDependencyChecks(org.bukkit.plugin.Plugin plugin) {
		if (LOOKUP_NAMES_FIELD == null || PLUGINS_FIELD == null) {
			return;
		}
		try {
			SimplePluginManager manager = getDependencyManager();
			@SuppressWarnings("unchecked")
			Map<String, org.bukkit.plugin.Plugin> lookupNames =
					(Map<String, org.bukkit.plugin.Plugin>) LOOKUP_NAMES_FIELD.get(manager);
			lookupNames.put(plugin.getName().replace(' ', '_'), plugin);
			@SuppressWarnings("unchecked")
			List<org.bukkit.plugin.Plugin> plugins =
					(List<org.bukkit.plugin.Plugin>) PLUGINS_FIELD.get(manager);
			if (!plugins.contains(plugin)) {
				plugins.add(plugin);
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			logWarning("Could not register " + plugin.getName() + " for dependency checks", e);
		}
	}

	/**
	 * Makes a plugin that Pokkit provides without a JAR (such as Floodgate)
	 * visible to the dependency checks of {@link JavaPluginLoader}.
	 * <p>
	 * Without this, a Bukkit plugin that declares {@code depend: [floodgate]}
	 * fails to load with an {@link org.bukkit.plugin.UnknownDependencyException}
	 * even though {@link org.bukkit.plugin.PluginManager#getPlugin(String)}
	 * returns the virtual plugin, because {@link JavaPluginLoader} asks its own
	 * dependency manager instead of the real plugin manager.
	 *
	 * @param plugin
	 *            The virtual plugin.
	 */
	public void registerVirtualPluginForDependencyChecks(org.bukkit.plugin.Plugin plugin) {
		registerForDependencyChecks(plugin);
	}

	/**
	 * Reads the {@code api-version} from a plugin JAR without fully loading it.
	 *
	 * @param file
	 *            The plugin JAR.
	 * @return The declared API version, or {@code null} if the plugin does not
	 *         declare one or the file cannot be read.
	 */
	private static String readDeclaredApiVersion(File file) {
		try (JarFile jar = new JarFile(file)) {
			JarEntry entry = jar.getJarEntry("plugin.yml");
			if (entry == null) {
				return null;
			}
			try (InputStream stream = jar.getInputStream(entry)) {
				return new PokkitPluginDescription(stream).getDeclaredApiVersion();
			}
		} catch (Exception e) {
			logWarning("Could not read the api-version of " + file.getName(), e);
			return null;
		}
	}

	private static String unsupportedApiVersionMessage(File file, String declaredApiVersion) {
		return "Plugin " + file.getName() + " requires Bukkit API " + declaredApiVersion
				+ ", but Pokkit implements " + PluginApiVersion.SUPPORTED
				+ ". The plugin was not loaded, because it would fail at runtime.";
	}

	private static void logSevere(String message) {
		logSevere(message, null);
	}

	private static void logSevere(String message, Throwable cause) {
		try {
			java.util.logging.Logger logger = Bukkit.getLogger();
			if (logger == null) {
				logger = java.util.logging.Logger.getLogger("Pokkit");
			}
			// The server logger already prefixes its messages with [Pokkit].
			logger.log(Level.SEVERE, message, cause);
		} catch (RuntimeException | LinkageError ignored) {
			// The Bukkit server may not be available yet during class
			// initialisation.
		}
	}

	private static void logWarning(String message, Throwable cause) {
		try {
			java.util.logging.Logger logger = Bukkit.getLogger();
			if (logger != null) {
				logger.log(Level.WARNING, message, cause);
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
		String declaredApiVersion = readDeclaredApiVersion(file);
		if (!PluginApiVersion.isSupported(declaredApiVersion)) {
			// Nukkit normally skips the plugin after getPluginDescription
			// returned null, but loadPlugin may also be called directly.
			String message = unsupportedApiVersionMessage(file, declaredApiVersion);
			logSevere(message);
			throw new InvalidPluginException(message);
		}
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
