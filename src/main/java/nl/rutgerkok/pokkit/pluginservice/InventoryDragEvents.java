package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import nl.rutgerkok.pokkit.inventory.PokkitInventory;
import nl.rutgerkok.pokkit.inventory.PokkitInventoryView;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class InventoryDragEvents extends EventTranslator {

	@EventHandler(ignoreCancelled = false)
	public void onInventoryTransaction(InventoryTransactionEvent event) {
		if (canIgnore(InventoryDragEvent.getHandlerList())) {
			return;
		}

		List<InventoryAction> actions = event.getTransaction().getActionList();
		List<SlotChangeAction> slotActions = actions.stream()
				.filter(a -> a instanceof SlotChangeAction)
				.map(a -> (SlotChangeAction) a)
				.collect(Collectors.toList());

		if (slotActions.size() < 2) {
			return;
		}

		Player player = PokkitPlayer.toBukkit(event.getTransaction().getSource());
		if (player == null) {
			return;
		}

		Inventory bukkitInventory = null;
		for (SlotChangeAction slotAction : slotActions) {
			Inventory inv = PokkitInventory.toBukkit(slotAction.getInventory());
			if (bukkitInventory == null) {
				bukkitInventory = inv;
			}
		}
		if (bukkitInventory == null) {
			return;
		}

		InventoryView view = new PokkitInventoryView(bukkitInventory, player);

		Map<Integer, ItemStack> newItems = new HashMap<>();
		for (SlotChangeAction slotAction : slotActions) {
			ItemStack bukkitItem = PokkitItemStack.toBukkitCopy(slotAction.getTargetItem());
			newItems.put(slotAction.getSlot(), bukkitItem);
		}

		SlotChangeAction firstAction = slotActions.get(0);
		ItemStack oldCursor = PokkitItemStack.toBukkitCopy(firstAction.getSourceItem());
		ItemStack newCursor = PokkitItemStack.toBukkitCopy(firstAction.getTargetItem());

		Set<Integer> rawSlots = newItems.keySet();

		InventoryDragEvent bukkitEvent = new InventoryDragEvent(
				view, oldCursor, newCursor, false, newItems);

		callCancellable(event, bukkitEvent);
	}
}
