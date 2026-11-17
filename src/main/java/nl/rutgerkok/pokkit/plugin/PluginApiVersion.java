package nl.rutgerkok.pokkit.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares the {@code api-version} declared by a Bukkit plugin with the API
 * version that Pokkit implements.
 *
 * <p>
 * Spigot refuses to load plugins that target a newer API than the server
 * implements. Pokkit calls {@code JavaPluginLoader} directly, which does not
 * perform this check, so it must do so itself. Without this check such a plugin
 * loads successfully and then fails later with a confusing
 * {@link NoSuchMethodError} or {@link ClassNotFoundException}.
 */
public final class PluginApiVersion {

	/**
	 * Highest Bukkit API version that Pokkit implements.
	 */
	public static final String SUPPORTED = "1.21.11";

	private static final Pattern LEADING_VERSION = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)*)");

	private PluginApiVersion() {
		// Utility class
	}

	/**
	 * Compares two version strings numerically, so that {@code 1.21.11} is
	 * greater than {@code 1.21.4} (unlike a lexicographic comparison).
	 *
	 * @param left
	 *            First version.
	 * @param right
	 *            Second version.
	 * @return A negative number, zero or a positive number if {@code left} is
	 *         lower than, equal to or higher than {@code right}.
	 * @throws IllegalArgumentException
	 *             If a version does not start with a numeric version.
	 */
	static int compare(String left, String right) {
		return compare(parse(left), parse(right));
	}

	static int compare(int[] left, int[] right) {
		int length = Math.max(left.length, right.length);
		for (int i = 0; i < length; i++) {
			int leftPart = i < left.length ? left[i] : 0;
			int rightPart = i < right.length ? right[i] : 0;
			if (leftPart != rightPart) {
				return Integer.compare(leftPart, rightPart);
			}
		}
		return 0;
	}

	/**
	 * Parses a version into its numeric components. A suffix such as
	 * {@code -R0.1} is ignored, because only the Minecraft version matters for
	 * API compatibility.
	 *
	 * @param version
	 *            Version to parse.
	 * @return The numeric components.
	 * @throws IllegalArgumentException
	 *             If the version does not start with a number.
	 */
	static int[] parse(String version) {
		Matcher matcher = LEADING_VERSION.matcher(version);
		if (!matcher.find()) {
			throw new IllegalArgumentException("Not a version: " + version);
		}
		String[] parts = matcher.group(1).split("\\.");
		int[] result = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}
		return result;
	}

	/**
	 * Tests whether a plugin may load on this server.
	 *
	 * @param pluginApiVersion
	 *            The {@code api-version} from the plugin's {@code plugin.yml}.
	 *            May be {@code null} for legacy plugins.
	 * @return {@code true} if the plugin may load, {@code false} if it targets
	 *         a newer API than Pokkit implements.
	 */
	public static boolean isSupported(String pluginApiVersion) {
		if (pluginApiVersion == null || pluginApiVersion.isBlank()) {
			// Legacy plugin without api-version. Spigot loads these too.
			return true;
		}
		try {
			return compare(pluginApiVersion, SUPPORTED) <= 0;
		} catch (IllegalArgumentException e) {
			// Unknown format. Refusing to load over a formatting detail would
			// be worse than trying; Spigot's own parser is not stricter for
			// plugins that predate the api-version key.
			return true;
		}
	}

}
