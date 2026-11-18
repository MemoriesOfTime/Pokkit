package nl.rutgerkok.pokkit.startup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginClassLoader;

public final class NukkitHook extends PluginBase {

	/**
	 * The Pokkit object, loaded using another class loader. (so no
	 * communication is possible, except using reflection).
	 */
	private Object pokkit;

	/**
	 * Silently ignore unsupported methods when possible
	 */
	public static boolean debugSilentlyUnsupported;

	@Override
	public void onDisable() {
		try {
			pokkit.getClass().getMethod("onDisable", PluginBase.class).invoke(pokkit, this);
		} catch (Throwable t) {
			this.getLogger().error("Error disabling plugin", t);
		}
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		debugSilentlyUnsupported = getConfig().getBoolean("debugSilentlyUnsupported");

		try {
			pokkit.getClass().getMethod("onEnable", PluginBase.class).invoke(pokkit, this);
		} catch (Throwable t) {
			this.getLogger().error("Error enabling plugin", t);
		}
	}

	@Override
	public void onLoad() {
		try {
			PluginClassLoader nukkitLoader = (PluginClassLoader) getClass().getClassLoader();

			// The bundled libraries are loaded from a class loader that Nukkit
			// never sees. Putting them in this plugin jar would expose them to
			// every other plugin through JavaPluginLoader#getClassByName, which
			// scans the class loader URLs of all plugins.
			URL[] libraryUrls = extractBundledLibraries();
			URL[] nukkitUrls = nukkitLoader.getURLs();
			URL[] urls = new URL[nukkitUrls.length + libraryUrls.length];
			System.arraycopy(nukkitUrls, 0, urls, 0, nukkitUrls.length);
			System.arraycopy(libraryUrls, 0, urls, nukkitUrls.length, libraryUrls.length);

			UrlFirstClassLoader classLoader = new UrlFirstClassLoader(urls, nukkitLoader);

			pokkit = classLoader.loadClass("nl.rutgerkok.pokkit.Pokkit").getDeclaredConstructor().newInstance();
			pokkit.getClass().getMethod("onLoad", PluginBase.class).invoke(pokkit, this);
		} catch (Throwable t) {
			this.getLogger().error("Error loading plugin", t);
		}
	}

	/**
	 * Extracts the dependency jars embedded in this plugin jar to the plugin
	 * data folder and returns them as URLs.
	 *
	 * <p>
	 * The jars are stored under {@code META-INF/pokkit/lib/} inside this plugin
	 * jar. They are extracted to {@code plugins/Pokkit/lib/}, which is never
	 * added to the Nukkit {@link PluginClassLoader}; only
	 * {@link UrlFirstClassLoader} sees them. See {@link BundledLibraries} for
	 * the extraction and cleanup details.
	 * </p>
	 *
	 * @return URLs of the extracted jars, in a stable order.
	 * @throws IOException
	 *             If the plugin jar or one of its entries cannot be read, or
	 *             the library folder cannot be written to.
	 */
	private URL[] extractBundledLibraries() throws IOException {
		List<URL> urls = BundledLibraries.extract(getFile(), new File(getDataFolder(), "lib"),
				getLogger()::debug);
		return urls.toArray(new URL[0]);
	}
}
