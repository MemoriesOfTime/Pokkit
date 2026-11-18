package nl.rutgerkok.pokkit.startup;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BundledLibrariesTest {

	private static final byte[] FIRST_LIBRARY = { 1, 2, 3, 4 };
	private static final byte[] SECOND_LIBRARY = { 5, 6, 7 };
	private static final Consumer<String> NO_LOG = message -> {
	};

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testExtractsLibraries() throws Exception {
		File pluginJar = createPluginJarWithLibraries("a-1.0.jar", "first", "b-2.0.jar", "second");
		File libraryFolder = folder.newFolder("lib");

		List<URL> urls = BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		assertEquals(2, urls.size());
		assertEquals("a-1.0.jar", Path.of(urls.get(0).toURI()).getFileName().toString());
		assertEquals("b-2.0.jar", Path.of(urls.get(1).toURI()).getFileName().toString());
		assertEquals("first", Files.readString(libraryFolder.toPath().resolve("a-1.0.jar")));
		assertEquals("second", Files.readString(libraryFolder.toPath().resolve("b-2.0.jar")));
	}

	@Test
	public void testIgnoresNonLibraryEntries() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put("plugin.yml", "name: Pokkit".getBytes(StandardCharsets.UTF_8));
		entries.put(BundledLibraries.RESOURCE_PREFIX + "readme.txt",
				"not a jar".getBytes(StandardCharsets.UTF_8));
		entries.put(BundledLibraries.RESOURCE_PREFIX + "nested-1.0.jar.bak", FIRST_LIBRARY);

		List<URL> urls = BundledLibraries.extract(createJar(entries), folder.newFolder("lib"), NO_LOG);

		assertTrue(urls.isEmpty());
		assertFalse(Files.exists(folder.getRoot().toPath().resolve("lib/readme.txt")));
		assertFalse(Files.exists(folder.getRoot().toPath().resolve("lib/nested-1.0.jar.bak")));
	}

	@Test
	public void testReusesUpToDateLibrary() throws IOException {
		File pluginJar = createPluginJarWithLibraries("library-1.0.jar", "content");
		File libraryFolder = folder.newFolder("lib");
		BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		Path extracted = libraryFolder.toPath().resolve("library-1.0.jar");
		FileTime marker = FileTime.fromMillis(1_000_000_000_000L);
		Files.setLastModifiedTime(extracted, marker);

		BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		assertEquals(marker.toMillis(), Files.getLastModifiedTime(extracted).toMillis());
	}

	@Test
	public void testReplacesModifiedLibrary() throws IOException {
		File pluginJar = createPluginJarWithLibraries("library-1.0.jar", "abcd");
		File libraryFolder = folder.newFolder("lib");
		BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		Path extracted = libraryFolder.toPath().resolve("library-1.0.jar");
		// Same size as the bundled entry, but different content: only a
		// checksum comparison can detect this.
		Files.writeString(extracted, "wxyz");

		BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		assertEquals("abcd", Files.readString(extracted));
	}

	@Test
	public void testDeletesStaleLibraries() throws IOException {
		File pluginJar = createPluginJarWithLibraries("library-1.0.jar", "content");
		File libraryFolder = folder.newFolder("lib");
		Files.write(libraryFolder.toPath().resolve("stale-0.9.jar"), SECOND_LIBRARY);
		Files.write(libraryFolder.toPath().resolve("leftover.jar.tmp"), SECOND_LIBRARY);

		BundledLibraries.extract(pluginJar, libraryFolder, NO_LOG);

		assertTrue(Files.exists(libraryFolder.toPath().resolve("library-1.0.jar")));
		assertFalse(Files.exists(libraryFolder.toPath().resolve("stale-0.9.jar")));
		assertFalse(Files.exists(libraryFolder.toPath().resolve("leftover.jar.tmp")));
	}

	@Test
	public void testRejectsPathTraversal() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put(BundledLibraries.RESOURCE_PREFIX + "../evil.jar", FIRST_LIBRARY);
		File libraryFolder = folder.newFolder("lib");

		assertThrows(IOException.class,
				() -> BundledLibraries.extract(createJar(entries), libraryFolder, NO_LOG));
		assertFalse(Files.exists(folder.getRoot().toPath().resolve("evil.jar")));
	}

	@Test
	public void testRejectsNestedLibraryPath() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put(BundledLibraries.RESOURCE_PREFIX + "sub/evil.jar", FIRST_LIBRARY);
		File libraryFolder = folder.newFolder("lib");

		assertThrows(IOException.class,
				() -> BundledLibraries.extract(createJar(entries), libraryFolder, NO_LOG));
		assertFalse(Files.exists(libraryFolder.toPath().resolve("sub")));
	}

	@Test
	public void testRejectsMissingPluginJar() throws IOException {
		File missingJar = new File(folder.getRoot(), "missing.jar");

		assertThrows(IOException.class,
				() -> BundledLibraries.extract(missingJar, folder.newFolder("lib"), NO_LOG));
	}

	private File createPluginJarWithLibraries(String... namesAndContents) throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		for (int i = 0; i < namesAndContents.length; i += 2) {
			entries.put(BundledLibraries.RESOURCE_PREFIX + namesAndContents[i],
					namesAndContents[i + 1].getBytes(StandardCharsets.UTF_8));
		}
		return createJar(entries);
	}

	private File createJar(Map<String, byte[]> entries) throws IOException {
		File jarFile = folder.newFile();
		try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {
			for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
				out.putNextEntry(new JarEntry(entry.getKey()));
				out.write(entry.getValue());
				out.closeEntry();
			}
		}
		return jarFile;
	}
}
