package nl.rutgerkok.pokkit.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.plugin.PluginAwareness;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.LoaderOptions;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.Utils;

/**
 * Variant of {@link PluginDescription} that handles the Bukkit plugin.yml
 * format.
 *
 */
final class PokkitPluginDescription extends PluginDescription {

	private final String apiVersion;

	private static Yaml getYaml() {
		return new Yaml(new SafeConstructor(new LoaderOptions()) {
			{
				yamlConstructors.put(null, new AbstractConstruct() {
					@Override
					public Object construct(final Node node) {
						if (!node.getTag().startsWith("!@")) {
							// Unknown tag - will fail
							return SafeConstructor.undefinedConstructor.construct(node);
						}
						// Unknown awareness - provide a graceful substitution
						return new PluginAwareness() {
							@Override
							public String toString() {
								return node.toString();
							}
						};
					}
				});
				for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
					yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
						@Override
						public PluginAwareness.Flags construct(final Node node) {
							return flag;
						}
					});
				}
			}
		});
	}

	private static Map<String, Object> readStream(InputStream stream) throws IOException {
		Yaml yaml = getYaml();
		@SuppressWarnings("unchecked")
		Map<String, Object> input = (Map<String, Object>) yaml.load(Utils.readFile(stream));

		// Nukkit requires startup to be in UPPERCASE
		if (input.get("load") instanceof String) {
			input.put("load", input.get("load").toString().toUpperCase(Locale.ENGLISH));
		}
		// Nukkit requires version to be a string (and not a double like 0.1)
		input.put("version", String.valueOf(input.get("version")));

		return input;
	}

	/**
	 * Removes the permissions from the plugin.yml data, so that Nukkit does not
	 * parse them. Bukkit allows constructs that Nukkit rejects, such as
	 * {@code children: [perm.a, perm.b]}, and Nukkit throws away the boolean
	 * values of children. {@link #parsePermissions(Map)} handles them instead.
	 *
	 * @param input
	 *            The parsed plugin.yml.
	 * @return A copy without the permissions, or the input when there are none.
	 */
	private static Map<String, Object> withoutPermissions(Map<String, Object> input) {
		if (!input.containsKey("permissions")) {
			return input;
		}
		Map<String, Object> copy = new LinkedHashMap<>(input);
		copy.remove("permissions");
		return copy;
	}

	/**
	 * Parsed permission data, before it is turned into a Nukkit
	 * {@link Permission}. Kept separate so that the Bukkit parsing rules can be
	 * tested without a running server.
	 */
	record ParsedPermission(String name, String description, String defaultValue,
			Map<String, Boolean> children) {
	}

	/**
	 * Parses the permissions of a Bukkit plugin.yml, following the Bukkit
	 * rules: children may be a list of names or a map of names to booleans, and
	 * a child may define a nested permission.
	 *
	 * @param input
	 *            The parsed plugin.yml.
	 */
	private void parsePermissions(Map<String, Object> input) {
		Object rawPermissions = input.get("permissions");
		if (!(rawPermissions instanceof Map<?, ?> permissions)) {
			return;
		}
		List<ParsedPermission> parsed = new ArrayList<>();
		for (Map.Entry<?, ?> entry : permissions.entrySet()) {
			if (entry.getValue() instanceof Map<?, ?> data) {
				parsed.add(parsePermissionData(String.valueOf(entry.getKey()), castMap(data),
						Permission.DEFAULT_OP, parsed));
			}
		}
		List<Permission> nukkitPermissions = new ArrayList<>();
		for (ParsedPermission permission : parsed) {
			nukkitPermissions.add(new Permission(permission.name(), permission.description(),
					permission.defaultValue(), permission.children()));
		}
		getPermissions().clear();
		getPermissions().addAll(nukkitPermissions);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> castMap(Map<?, ?> map) {
		return (Map<String, Object>) map;
	}

	/**
	 * Parses a single permission. This mirrors Nukkit's parser, but keeps the
	 * boolean values of the children, like Bukkit does.
	 *
	 * @param name
	 *            The name of the permission.
	 * @param data
	 *            The permission data.
	 * @param defaultValue
	 *            The default value to use when the permission does not define
	 *            one.
	 * @param output
	 *            List that nested permission definitions are added to.
	 * @return The parsed permission.
	 */
	static ParsedPermission parsePermissionData(String name, Map<String, Object> data, String defaultValue,
			List<ParsedPermission> output) {
		String description = data.get("description") instanceof String text ? text : null;

		if (data.containsKey("default")) {
			String value = Permission.getByName(String.valueOf(data.get("default")));
			if (value != null) {
				defaultValue = value;
			}
		}

		Map<String, Boolean> children = new LinkedHashMap<>();
		Object rawChildren = data.get("children");
		if (rawChildren instanceof Map<?, ?> childMap) {
			for (Map.Entry<?, ?> child : childMap.entrySet()) {
				String childName = String.valueOf(child.getKey());
				Object childValue = child.getValue();
				if (childValue instanceof Map<?, ?> childDefinition) {
					// A child can define a nested permission, just like in
					// Bukkit.
					output.add(parsePermissionData(childName, castMap(childDefinition), defaultValue, output));
					children.put(childName, true);
				} else {
					children.put(childName, childValue == null || Boolean.parseBoolean(String.valueOf(childValue)));
				}
			}
		} else if (rawChildren instanceof Iterable<?> childList) {
			// Bukkit allows "children: [perm.a, perm.b]", where every entry
			// means true.
			for (Object child : childList) {
				if (child != null) {
					children.put(String.valueOf(child), true);
				}
			}
		}

		return new ParsedPermission(name, description, defaultValue, children);
	}

	/**
	 * Creates a new plugin description of a Bukkit plugin.
	 * 
	 * @param stream
	 *            The UTF-8 stream to read from.
	 * @throws IOException
	 *             If an IO error occurs.
	 */
	PokkitPluginDescription(InputStream stream) throws IOException {
		this(readStream(stream));
	}

	private PokkitPluginDescription(Map<String, Object> input) {
		super(withoutPermissions(input));
		Object rawApiVersion = input.get("api-version");
		this.apiVersion = rawApiVersion == null ? null : String.valueOf(rawApiVersion).trim();
		parsePermissions(input);
	}

	/**
	 * Gets the Bukkit API version declared in {@code plugin.yml}.
	 *
	 * @return The declared version, or {@code null} if the plugin does not
	 *         declare one.
	 */
	String getDeclaredApiVersion() {
		return apiVersion;
	}

	@Override
	public List<String> getCompatibleAPIs() {
		return Arrays.asList("1.0.0");
	}

}
