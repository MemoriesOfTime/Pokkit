package nl.rutgerkok.pokkit.startup;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class loader that only loads classes from the given URLs, and doesn't fall
 * back on the global class loader except for when the class is not found. This
 * allows the given URLs to override literally any class.
 *
 * <p>
 * Keep in mind: findClass - always from disk, loadClass: checks in cache first.
 */
final class UrlFirstClassLoader extends URLClassLoader {

	/**
	 * Packages that must always be loaded from Nukkit, not from the bundled
	 * libraries. Nukkit-MOT ships Guava, Gson, SnakeYAML and SLF4J; loading a
	 * second copy would give the same types two class identities and break
	 * interop with Nukkit APIs (for example when a Guava collection is passed
	 * to Nukkit).
	 *
	 * <p>
	 * Some dependencies bundle these libraries internally (notably the Geyser
	 * common jar pulled in by the Floodgate API), so they cannot be excluded
	 * at build time. Delegating these packages to the parent loader has the
	 * same effect as the shade filters this build used to have.
	 * </p>
	 *
	 * <p>
	 * Keep this list in sync with the {@code excludeGroupIds} configuration of
	 * {@code maven-dependency-plugin} in {@code pom.xml}: everything Nukkit
	 * provides is excluded from the bundled jars where possible, and listed
	 * here for dependencies that bundle it internally.
	 * </p>
	 */
	private static final String[] PARENT_FIRST_PREFIXES = { "com.google.common.", "com.google.thirdparty.",
			"com.google.gson.", "org.yaml.snakeyaml.", "org.checkerframework.", "org.slf4j." };

	private final Map<String, Class<?>> loaded = new HashMap<>();

	public UrlFirstClassLoader(URL[] urls, ClassLoader fallback) {
		super(urls, fallback);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name.startsWith(getClass().getPackage() + ".")) {
			// Class in same package, load using global class loader
			throw new ClassNotFoundException(name);
		}

		// Load from cache
		Class<?> cached = loaded.get(name);
		if (cached != null) {
			if (resolve) {
				resolveClass(cached);
			}
			return cached;
		}

		if (isParentFirst(name)) {
			// Delegate to Nukkit before looking in the bundled libraries: these
			// packages must have a single class identity in the server.
			Class<?> fromParent = super.loadClass(name, resolve);
			loaded.put(name, fromParent);
			return fromParent;
		}

		try {
			// Load from JAR file
			Class<?> loadedFromJar = this.findClass(name);
			loaded.put(name, loadedFromJar);
			if (resolve) {
				resolveClass(loadedFromJar);
			}
			return loadedFromJar;
		} catch (ClassNotFoundException e) {
			// Fall back on global class loader for Java standard library
			Class<?> fromGlobal = super.loadClass(name, resolve);
			loaded.put(name, fromGlobal);
			if (resolve) {
				resolveClass(fromGlobal);
			}
			return fromGlobal;
		}
	}

	private static boolean isParentFirst(String name) {
		for (String prefix : PARENT_FIRST_PREFIXES) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}