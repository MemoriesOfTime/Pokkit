# AGENTS.md - Pokkit

Pokkit is a Java 17 compatibility layer that lets Bukkit/Spigot plugins run on Nukkit (Minecraft Bedrock Edition server). It implements the Spigot API by delegating to Nukkit-MOT.

## Build Commands

```bash
mvn clean install          # Full build (compile + test + package)
mvn compile                # Compile main sources only
mvn package                # Package into JAR (target/)
mvn test-compile           # Compile test sources only
```

## Test Commands

```bash
mvn test                                            # Run all tests
mvn test -Dtest=PlayerChatEventsTest                # Run a single test class
mvn test -Dtest=PlayerChatEventsTest#testBukkitConversion  # Run a single test method
```

- Test framework: JUnit 4.13.2
- Tests live under `src/test/java/nl/rutgerkok/pokkit/`, plus a few CraftBukkit-API tests under `src/test/java/org/bukkit/craftbukkit/v1_99_R9/`
- Static imports of `org.junit.Assert.*` are used (e.g. `assertEquals`, `assertTrue`)
- Some tests require initializing Nukkit subsystems in `@BeforeClass` (e.g. `Block.init()`, `Enchantment.init()`)

## Lint / Format / Typecheck

There is no formal linting or formatting tool configured. Compilation serves as the typecheck:

```bash
mvn compile          # Typecheck main sources
mvn test-compile     # Typecheck test sources
```

## Code Style

### Formatting
- **Indentation:** Tabs for new code. Some older files (e.g. `PokkitEntity.java`, `PokkitBiome.java`) still use 4 spaces; do not reformat them wholesale.
- **Opening braces:** On the same line (K&R style)
- **Line length:** No enforced limit, but keep lines readable

### Imports
- No strict ordering enforced; loosely group by:
  1. `java.*` / `javax.*`
  2. Third-party (`cn.nukkit.*`, `com.google.*`)
  3. Project (`nl.rutgerkok.pokkit.*`)
  4. Bukkit/Spigot API (`org.bukkit.*`)
- Wildcard (star) imports are used within the project (e.g. `nl.rutgerkok.pokkit.pluginservice.*`)

### Naming Conventions
- **Adapter/wrapper classes:** Prefixed with `Pokkit` (e.g. `PokkitPlayer`, `PokkitWorld`, `PokkitItemStack`)
- **Converter classes:** Named after what they convert, with `toBukkit()` and `toNukkit()` static methods
- **Event translator classes:** Named by domain without `Pokkit` prefix (e.g. `PlayerChatEvents`, `EntityEvents`)
- **Variables:** camelCase
- **Constants:** `UPPER_SNAKE_CASE` (e.g. `ITEM_SLOT_NOT_INITIALIZED`)
- **Test methods:** camelCase starting with `test` (e.g. `testBukkitConversion`)
- **Packages:** `nl.rutgerkok.pokkit.*`, organized by domain

### Types
- Java 17 source/target; main sources do not use `var`, but pattern matching for `instanceof` is used (e.g. `instanceof PokkitItemMeta meta`)
- Standard Java generics where needed
- No annotation processor or static null analysis; `@NotNull` / `@Nullable` appear only where the Bukkit API signatures declare them. Null checks are manual (`Objects.requireNonNull()`, early returns)

### Error Handling
This is critical for Pokkit. There are several patterns:

1. **Unimplemented methods:** Use `throw Pokkit.unsupported()` - this throws `UnsupportedOperationException` with version info. **Never** use `return null; // TODO` as it causes hard-to-debug issues. `Pokkit.notImplemented()` is the config-gated variant: it logs and returns when `debugSilentlyUnsupported` is enabled in `config.yml`, and throws otherwise.

2. **Internal consistency checks:** Use `Pokkit.assertion(boolean, String)` for assertions that should always hold.

3. **Silent failures:** For unimportant features, methods may silently do nothing (e.g. particle spawning). Use sparingly.

4. **Null safety:** Manual null checks with early returns.

### Javadoc
- Present on most public methods
- Uses `@param` and `@return` tags

## Architecture

### Key Patterns
1. **Adapter pattern:** Bukkit entities are wrapped by a `Pokkit*` class around the Nukkit object. Specialized wrappers exist for common types (`PokkitPlayer`, `PokkitLivingEntity`, `PokkitItemEntity`, ...); other types fall back to the generic `PokkitEntity`.
2. **Static converters:** `toBukkit()` / `toNukkit()` methods on utility classes for type conversion
3. **Service lifecycle:** `PokkitService` interface with `onLoad()`, `onEnable()`, `onDisable()` methods
4. **Event translation:** `EventTranslator` abstract base class for Nukkit-to-Bukkit event translation
   - Uses `canIgnore(HandlerList)` to skip events with no listeners (performance optimization)
   - Uses `callCancellable()` and `callUncancellable()` to fire Bukkit events
5. **Service registration:** All services registered in an `ImmutableList` in `Pokkit.java`
   - The list order is the `onLoad` order. `FloodgateService` must stay **before** `PluginService`, because Bukkit plugins may declare `depend: [floodgate]` or call `FloodgateApi.getInstance()` while loading.

### Source Layout
```
src/main/java/nl/rutgerkok/pokkit/                  # Main source (adapters, converters, services)
src/main/java/org/bukkit/craftbukkit/v1_99_R9/      # CraftBukkit-compatible server implementation
src/main/resources/                                 # plugin.yml and config.yml
src/test/java/nl/rutgerkok/pokkit/                  # JUnit 4 tests
src/test/java/org/bukkit/craftbukkit/v1_99_R9/      # Tests for the CraftBukkit-compatible API
```

### Key Files
| File | Purpose |
|------|---------|
| `Pokkit.java` | Main entry point, service registration |
| `org/bukkit/craftbukkit/v1_99_R9/CraftServer.java` | `org.bukkit.Server` implementation (CraftBukkit-compatible, `v1_99_R9`) |
| `pluginservice/PluginService.java` | Loads and enables Bukkit plugins |
| `pluginservice/EventTranslator.java` | Base class for event translation (package-private) |
| `pluginservice/PokkitService.java` | Service lifecycle interface |
| `startup/NukkitHook.java` | Nukkit bootstrap/hook |
| `IMPL_EXAMPLES.md` | Detailed implementation examples |
| `Events.md` | Guide for adding new event listeners |

### Adding New Event Listeners
1. Create a class in `pluginservice/` extending `EventTranslator`
2. Add `@EventHandler` methods that listen to Nukkit events, convert to Bukkit events, and fire them
3. Register the service in `Pokkit.java`'s `ImmutableList`
4. See `Events.md` for step-by-step instructions

### Adding New API Methods
1. Find the `Pokkit*` adapter class for the Bukkit interface
2. Implement the method by delegating to the Nukkit equivalent
3. Use `throw Pokkit.unsupported()` if the method cannot be implemented
4. Use `Pokkit.assertion()` to validate assumptions
5. See `IMPL_EXAMPLES.md` for detailed patterns

## Dependencies
- `org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT` (compile) - Bukkit/Spigot API being implemented
- `cn.nukkit:Nukkit:MOT-SNAPSHOT` (provided) - Nukkit-MOT server runtime
- `com.google.guava:guava:33.7.2-jre` (provided) - keep in sync with Nukkit-MOT; not shaded into the JAR
- `org.xerial:sqlite-jdbc:3.51.3.0`, `com.mysql:mysql-connector-j:9.6.0` (compile) - JDBC drivers for Bukkit plugins, registered at startup
- `org.geysermc.floodgate:api:2.2.5-SNAPSHOT` (compile) - Bedrock player identification
- `commons-lang:commons-lang:2.6`, `org.apache.commons:commons-lang3:3.18.0`, `com.googlecode.json-simple:json-simple:1.1.1` (compile) - libraries Bukkit plugins expect from the server
- `org.jetbrains:annotations:24.1.0` (provided) - `@NotNull` / `@Nullable` annotations used in Bukkit API signatures
- `junit:junit:4.13.2` (test)
