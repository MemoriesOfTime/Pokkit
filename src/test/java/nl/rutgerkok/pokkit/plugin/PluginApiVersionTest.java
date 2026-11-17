package nl.rutgerkok.pokkit.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class PluginApiVersionTest {

	@Test
	public void testLegacyPluginWithoutApiVersion() {
		assertTrue(PluginApiVersion.isSupported(null));
		assertTrue(PluginApiVersion.isSupported(""));
		assertTrue(PluginApiVersion.isSupported("   "));
	}

	@Test
	public void testVersionsUpToSupportedAreAllowed() {
		assertTrue(PluginApiVersion.isSupported("1.8.8"));
		assertTrue(PluginApiVersion.isSupported("1.13"));
		assertTrue(PluginApiVersion.isSupported("1.21"));
		assertTrue(PluginApiVersion.isSupported("1.21.4"));
		assertTrue(PluginApiVersion.isSupported("1.21.11"));
	}

	@Test
	public void testNewerVersionsAreRejected() {
		assertFalse(PluginApiVersion.isSupported("1.21.12"));
		assertFalse(PluginApiVersion.isSupported("26.1"));
		assertFalse(PluginApiVersion.isSupported("26.3"));
	}

	@Test
	public void testComparisonIsNumeric() {
		// Lexicographic comparison would put 1.21.4 after 1.21.11.
		assertEquals(-1, PluginApiVersion.compare("1.21.4", "1.21.11"));
		assertEquals(1, PluginApiVersion.compare("1.21.11", "1.21.4"));
		assertEquals(1, PluginApiVersion.compare("26.1", "1.21.11"));
		assertEquals(0, PluginApiVersion.compare("1.21", "1.21.0"));
	}

	@Test
	public void testSuffixIsIgnored() {
		assertTrue(PluginApiVersion.isSupported("1.21.11-R0.2"));
		assertFalse(PluginApiVersion.isSupported("26.3-R0.1"));
	}

	@Test
	public void testUnknownFormatIsAllowed() {
		// Not worth refusing a plugin over, and Spigot predates the key.
		assertTrue(PluginApiVersion.isSupported("not-a-version"));
	}

	@Test
	public void testPluginDescriptionReadsApiVersion() throws Exception {
		String yml = "name: TestPlugin\nmain: com.example.TestPlugin\nversion: 1.0\napi-version: '1.21.4'\n";
		PokkitPluginDescription description = new PokkitPluginDescription(
				new ByteArrayInputStream(yml.getBytes(StandardCharsets.UTF_8)));

		assertEquals("1.21.4", description.getDeclaredApiVersion());
		assertTrue(PluginApiVersion.isSupported(description.getDeclaredApiVersion()));
	}

	@Test
	public void testPluginDescriptionWithoutApiVersion() throws Exception {
		String yml = "name: TestPlugin\nmain: com.example.TestPlugin\nversion: 1.0\n";
		PokkitPluginDescription description = new PokkitPluginDescription(
				new ByteArrayInputStream(yml.getBytes(StandardCharsets.UTF_8)));

		assertNull(description.getDeclaredApiVersion());
	}
}
