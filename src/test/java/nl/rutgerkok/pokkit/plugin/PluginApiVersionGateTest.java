package nl.rutgerkok.pokkit.plugin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.nukkit.plugin.PluginDescription;

public class PluginApiVersionGateTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
	}

	@Test
	public void testNewerApiVersionIsRejected() throws Exception {
		File plugin = createPluginJar("26.3");
		try {
			PluginDescription description = new PokkitPluginLoader(null).getPluginDescription(plugin);
			assertNull("A plugin for a newer API must not be loaded", description);
		} finally {
			plugin.delete();
		}
	}

	@Test
	public void testSupportedApiVersionIsAccepted() throws Exception {
		File plugin = createPluginJar("1.21.4");
		try {
			PluginDescription description = new PokkitPluginLoader(null).getPluginDescription(plugin);
			assertNotNull("A plugin for a supported API must be loaded", description);
		} finally {
			plugin.delete();
		}
	}

	private static File createPluginJar(String apiVersion) throws IOException {
		File jar = Files.createTempFile("pokkit-api-version-", ".jar").toFile();
		String pluginYml = "name: ApiVersionTest\n"
				+ "main: com.example.ApiVersionTest\n"
				+ "version: 1.0\n"
				+ "api-version: '" + apiVersion + "'\n";
		try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar.toPath()))) {
			out.putNextEntry(new JarEntry("plugin.yml"));
			out.write(pluginYml.getBytes(StandardCharsets.UTF_8));
			out.closeEntry();
		}
		return jar;
	}
}
