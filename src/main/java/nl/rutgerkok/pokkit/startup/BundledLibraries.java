package nl.rutgerkok.pokkit.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;

/**
 * Extracts the dependency jars that are bundled in the Pokkit plugin jar to a
 * folder on disk, so that they can be loaded by a class loader that Nukkit
 * never sees.
 *
 * <p>
 * The jars are stored under {@link #RESOURCE_PREFIX} inside the plugin jar.
 * They are extracted to {@code plugins/Pokkit/lib/}, which is never added to
 * Nukkit's plugin class loader; only {@link UrlFirstClassLoader} sees them.
 * This keeps them invisible to native Nukkit plugins, which would otherwise be
 * able to load (and accidentally depend on) Pokkit's libraries through
 * Nukkit's global plugin class lookup.
 * </p>
 *
 * <p>
 * Extraction is idempotent: an already extracted file is reused when its size
 * and CRC-32 match the entry in the plugin jar, so a restart does not rewrite
 * tens of megabytes. A new version of a dependency ends up in a new file
 * (because the version is part of the file name), and jars left behind by an
 * older Pokkit version are deleted.
 * </p>
 */
final class BundledLibraries {

	/**
	 * Location in the plugin jar where the dependency jars are stored.
	 */
	static final String RESOURCE_PREFIX = "META-INF/pokkit/lib/";

	private BundledLibraries() {
	}

	/**
	 * Extracts the bundled library jars to the given folder.
	 *
	 * @param pluginJar
	 *            The jar of this plugin.
	 * @param libraryFolder
	 *            Folder to extract the libraries to. Created when missing.
	 * @param log
	 *            Receiver for debug messages about cleaned-up files.
	 * @return URLs of the extracted jars, sorted by file name.
	 * @throws IOException
	 *             If the plugin jar or one of its entries cannot be read, a
	 *             bundled entry has an invalid path, or the library folder
	 *             cannot be written to.
	 */
	static List<URL> extract(File pluginJar, File libraryFolder, Consumer<String> log) throws IOException {
		if (pluginJar == null || !pluginJar.isFile()) {
			throw new IOException("Cannot locate this plugin's jar file");
		}
		if (!libraryFolder.isDirectory() && !libraryFolder.mkdirs()) {
			throw new IOException("Could not create library folder " + libraryFolder);
		}
		Path libraryPath = libraryFolder.toPath().toAbsolutePath().normalize();

		List<URL> urls = new ArrayList<>();
		Set<String> currentFiles = new HashSet<>();

		try (JarFile jar = new JarFile(pluginJar)) {
			for (JarEntry entry : findLibraryEntries(jar)) {
				Path target = resolveTarget(entry, libraryPath);
				if (!isUpToDate(entry, target)) {
					extract(jar, entry, target);
				}

				currentFiles.add(target.getFileName().toString());
				urls.add(target.toUri().toURL());
			}
		}

		deleteStaleFiles(libraryFolder, currentFiles, log);
		log.accept("Loaded " + urls.size() + " bundled libraries from " + libraryFolder);
		return urls;
	}

	private static List<JarEntry> findLibraryEntries(JarFile jar) {
		List<JarEntry> entries = new ArrayList<>();
		Enumeration<JarEntry> enumeration = jar.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry entry = enumeration.nextElement();
			if (!entry.isDirectory() && entry.getName().startsWith(RESOURCE_PREFIX)
					&& entry.getName().endsWith(".jar")) {
				entries.add(entry);
			}
		}
		entries.sort(Comparator.comparing(JarEntry::getName));
		return entries;
	}

	/**
	 * Resolves the extraction target for an entry and rejects entries that
	 * would escape the library folder.
	 */
	private static Path resolveTarget(JarEntry entry, Path libraryPath) throws IOException {
		String fileName = entry.getName().substring(RESOURCE_PREFIX.length());
		Path target = libraryPath.resolve(fileName).normalize();
		if (!target.startsWith(libraryPath) || !libraryPath.equals(target.getParent())) {
			throw new IOException("Invalid bundled library path " + entry.getName());
		}
		return target;
	}

	/**
	 * Checks whether the extracted file still matches the entry in the plugin
	 * jar. Size and CRC-32 are compared, so a truncated or modified file is
	 * extracted again.
	 */
	private static boolean isUpToDate(JarEntry entry, Path target) throws IOException {
		if (!Files.isRegularFile(target)) {
			return false;
		}
		if (entry.getSize() < 0 || Files.size(target) != entry.getSize()) {
			return false;
		}

		long expectedCrc = entry.getCrc();
		if (expectedCrc < 0) {
			// The entry has no checksum (unusual for a jar); size must do.
			return true;
		}
		CRC32 crc = new CRC32();
		try (InputStream input = Files.newInputStream(target)) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = input.read(buffer)) != -1) {
				crc.update(buffer, 0, read);
			}
		}
		return crc.getValue() == expectedCrc;
	}

	private static void extract(JarFile jar, JarEntry entry, Path target) throws IOException {
		Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
		try (InputStream input = jar.getInputStream(entry)) {
			Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
		}
		move(temporary, target);
	}

	private static void move(Path source, Path target) throws IOException {
		try {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException e) {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Best-effort cleanup of libraries from older Pokkit versions. A failure
	 * (for example a file locked by another process) is harmless.
	 */
	private static void deleteStaleFiles(File libraryFolder, Set<String> currentFiles, Consumer<String> log) {
		File[] staleFiles = libraryFolder.listFiles((dir, name) ->
				(name.endsWith(".jar") || name.endsWith(".tmp")) && !currentFiles.contains(name));
		if (staleFiles == null) {
			return;
		}
		for (File staleFile : staleFiles) {
			if (!staleFile.delete()) {
				log.accept("Could not delete old library " + staleFile);
			}
		}
	}
}
