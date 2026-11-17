package nl.rutgerkok.pokkit.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;

import org.bukkit.plugin.Plugin;
import org.junit.Test;

/**
 * A plugin that throws from {@code onLoad()} must not abort the loading of the
 * other plugins. Bukkit logs the failure and continues, and Pokkit must do the
 * same: an exception that escapes here skips every service registered after
 * the plugin service, which leaves the server without Bukkit events.
 */
public class PluginLoadFailureTest {

	@Test
	public void testOnLoadErrorIsContained() {
		PokkitPluginLoader loader = new PokkitPluginLoader(null);
		Plugin plugin = createPlugin("BrokenOnLoad", new NoClassDefFoundError("net/minecraft/server/ServerLevel"));

		assertFalse(PokkitPluginManager.callOnLoad(plugin, loader));
		assertTrue("A plugin that failed to load must be marked as failed", loader.hasFailed("BrokenOnLoad"));
	}

	@Test
	public void testOnLoadExceptionIsContained() {
		PokkitPluginLoader loader = new PokkitPluginLoader(null);
		Plugin plugin = createPlugin("BrokenOnLoad", new IllegalStateException("boom"));

		assertFalse(PokkitPluginManager.callOnLoad(plugin, loader));
		assertTrue("A plugin that failed to load must be marked as failed", loader.hasFailed("BrokenOnLoad"));
	}

	@Test
	public void testHealthyPluginIsLoaded() {
		PokkitPluginLoader loader = new PokkitPluginLoader(null);
		Plugin plugin = createPlugin("Healthy", null);

		assertTrue(PokkitPluginManager.callOnLoad(plugin, loader));
		assertFalse("A plugin that loaded fine must not be marked as failed", loader.hasFailed("Healthy"));
	}

	/**
	 * Creates a plugin that only implements the members that
	 * {@code PokkitPluginManager#callOnLoad} touches.
	 *
	 * @param name
	 *            Name of the plugin.
	 * @param onLoadFailure
	 *            Exception to throw from {@code onLoad()}, or {@code null} to
	 *            load successfully.
	 * @return The plugin.
	 */
	private static Plugin createPlugin(String name, Throwable onLoadFailure) {
		return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(), new Class<?>[] { Plugin.class },
				(proxy, method, args) -> {
					if (method.getName().equals("getName")) {
						return name;
					}
					if (method.getName().equals("onLoad") && onLoadFailure != null) {
						throw onLoadFailure;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private static Object defaultValue(Class<?> type) {
		if (type == boolean.class) {
			return Boolean.FALSE;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		return null;
	}
}
