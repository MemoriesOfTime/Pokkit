# Adding Event Listeners

Not all Bukkit events are supported. Here is how to add one, using the example of the `PlayerItemConsumeEvent`.

The Nukkit event is `cn.nukkit.event.player.PlayerItemConsumeEvent`. The translator goes in `nl.rutgerkok.pokkit.pluginservice`, next to `PlayerItemConsumeEvents`, because `EventTranslator` is package-private.

Create a class named after the Bukkit event you are translating, and extend `EventTranslator`:

```java
public class PlayerItemConsumeEvents extends EventTranslator {

}
```

Now add an event handler called `onPlayerItemConsume`, with the `@EventHandler` annotation:

```java
public class PlayerItemConsumeEvents extends EventTranslator {

    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {

    }
}
```

First thing is to add the Bukkit handler exit code. `canIgnore` checks whether any Bukkit plugin is listening to the event; if not, there is no reason to convert anything:

```java
    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {
        if (canIgnore(PlayerItemConsumeEvent.getHandlerList())) {
            return;
        }
    }
```

Now convert the Nukkit objects to Bukkit ones:

```java
    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {
        if (canIgnore(PlayerItemConsumeEvent.getHandlerList())) {
            return;
        }

        Player bukkitPlayer = PokkitPlayer.toBukkit(event.getPlayer());
        ItemStack item = PokkitItemStack.toBukkitCopy(event.getItem());
    }
```

Construct the Bukkit event:

```java
    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {
        if (canIgnore(PlayerItemConsumeEvent.getHandlerList())) {
            return;
        }

        Player bukkitPlayer = PokkitPlayer.toBukkit(event.getPlayer());
        ItemStack item = PokkitItemStack.toBukkitCopy(event.getItem());
        PlayerItemConsumeEvent bukkitEvent = new PlayerItemConsumeEvent(bukkitPlayer, item);
    }
```

And finally call the event. `callCancellable` copies the cancelled state from the Nukkit event to the Bukkit event, fires the Bukkit event, and copies the (possibly changed) cancelled state back. For events that are not `Cancellable`, use `callUncancellable(bukkitEvent)` instead:

```java
    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {
        if (canIgnore(PlayerItemConsumeEvent.getHandlerList())) {
            return;
        }

        Player bukkitPlayer = PokkitPlayer.toBukkit(event.getPlayer());
        ItemStack item = PokkitItemStack.toBukkitCopy(event.getItem());
        PlayerItemConsumeEvent bukkitEvent = new PlayerItemConsumeEvent(bukkitPlayer, item);
        callCancellable(event, bukkitEvent);
    }
```

Here is the entire class, including imports:

```java
package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemConsumeEvents extends EventTranslator {

    @EventHandler(ignoreCancelled = false)
    public void onPlayerItemConsume(cn.nukkit.event.player.PlayerItemConsumeEvent event) {
        if (canIgnore(PlayerItemConsumeEvent.getHandlerList())) {
            return;
        }

        Player bukkitPlayer = PokkitPlayer.toBukkit(event.getPlayer());
        ItemStack item = PokkitItemStack.toBukkitCopy(event.getItem());
        PlayerItemConsumeEvent bukkitEvent = new PlayerItemConsumeEvent(bukkitPlayer, item);
        callCancellable(event, bukkitEvent);
    }
}
```

To have your event handler load, you need to add it to the `services` in `Pokkit.java`. The list order is the `onLoad`/`onEnable` order, and `FloodgateService` must stay before `PluginService`, because Bukkit plugins may depend on Floodgate while they load:

```java
private final List<PokkitService> services = ImmutableList.of(new MainScoreboardService(), new PermissionsYml(),
        // Floodgate must be registered before PluginService loads the
        // Bukkit plugins: those plugins may declare "depend: [floodgate]"
        // or call FloodgateApi.getInstance() from onLoad/onEnable.
        new FloodgateService(),
        new PluginService(), new PlayerBlockEvents(), new PlayerConnectEvents(), new PlayerChatEvents(),
        new PlayerInteractEvents(), new EntityEvents(), new InventoryEvents(), new PlayerItemConsumeEvents(),
        new PlayerToggleEvents(), new PlayerLifecycleEvents(), new WorldEvents(),
        new PlayerBucketEvents(), new VehicleEvents(), new InventoryDragEvents(), new CraftItemEvents(),
        ChunkPersistentDataStore.getInstance());
```
