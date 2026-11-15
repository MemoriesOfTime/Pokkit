package nl.rutgerkok.pokkit.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.nukkit.permission.Permission;
import nl.rutgerkok.pokkit.plugin.PokkitPluginDescription.ParsedPermission;

public class PluginDescriptionTest {

	private static Map<String, Object> data(Object children) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("default", "op");
		data.put("children", children);
		return data;
	}

	@Test
	public void testChildrenAsList() {
		// Bukkit allows "children: [a, b]", where every entry means true.
		// Nukkit throws "'children' key is of wrong type" for this form.
		List<ParsedPermission> nested = new ArrayList<>();
		ParsedPermission permission = PokkitPluginDescription.parsePermissionData("test.command",
				data(Arrays.asList("test.command.help", "test.command.info")), Permission.DEFAULT_OP, nested);

		assertEquals(Boolean.TRUE, permission.children().get("test.command.help"));
		assertEquals(Boolean.TRUE, permission.children().get("test.command.info"));
		assertEquals(2, permission.children().size());
		assertTrue(nested.isEmpty());
	}

	@Test
	public void testChildrenAsMapKeepsFalseValues() {
		// Nukkit's own parser turns every child into true, which is wrong for
		// "children: {a: false}".
		Map<String, Object> children = new LinkedHashMap<>();
		children.put("test.command.help", true);
		children.put("test.command.info", false);

		ParsedPermission permission = PokkitPluginDescription.parsePermissionData("test.command", data(children),
				Permission.DEFAULT_OP, new ArrayList<>());

		assertEquals(Boolean.TRUE, permission.children().get("test.command.help"));
		assertEquals(Boolean.FALSE, permission.children().get("test.command.info"));
	}

	@Test
	public void testNestedPermissionDefinition() {
		Map<String, Object> childDefinition = new LinkedHashMap<>();
		childDefinition.put("default", "true");
		Map<String, Object> children = new LinkedHashMap<>();
		children.put("test.command.help", childDefinition);

		List<ParsedPermission> nested = new ArrayList<>();
		ParsedPermission permission = PokkitPluginDescription.parsePermissionData("test.command", data(children),
				Permission.DEFAULT_OP, nested);

		assertEquals(Boolean.TRUE, permission.children().get("test.command.help"));
		assertEquals(1, nested.size());
		assertEquals("test.command.help", nested.get(0).name());
		assertEquals(Permission.DEFAULT_TRUE, nested.get(0).defaultValue());
	}

	@Test
	public void testDefaultAndDescription() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("default", true);
		data.put("description", "Runs the command");

		ParsedPermission permission = PokkitPluginDescription.parsePermissionData("test.command", data,
				Permission.DEFAULT_OP, new ArrayList<>());

		assertEquals(Permission.DEFAULT_TRUE, permission.defaultValue());
		assertEquals("Runs the command", permission.description());
		assertFalse(permission.children().containsKey("missing"));
	}

	@Test
	public void testUnknownDefaultFallsBack() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("default", "definitely-not-a-default");

		ParsedPermission permission = PokkitPluginDescription.parsePermissionData("test.command", data,
				Permission.DEFAULT_OP, new ArrayList<>());

		// Nukkit's Permission.getByName maps unknown values to false.
		assertEquals(Permission.DEFAULT_FALSE, permission.defaultValue());
	}
}
