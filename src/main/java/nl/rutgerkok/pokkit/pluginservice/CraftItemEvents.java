package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import nl.rutgerkok.pokkit.inventory.PokkitInventory;
import nl.rutgerkok.pokkit.inventory.PokkitInventoryView;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public final class CraftItemEvents extends EventTranslator {

	private static class NukkitRecipeWrapper implements Recipe {
		private final ItemStack result;

		NukkitRecipeWrapper(ItemStack result) {
			this.result = result;
		}

		@Override
		public ItemStack getResult() {
			return result;
		}
	}

	@EventHandler(ignoreCancelled = false)
	public void onCraftItem(cn.nukkit.event.inventory.CraftItemEvent event) {
		if (canIgnore(CraftItemEvent.getHandlerList())) {
			return;
		}

		Player player = PokkitPlayer.toBukkit(event.getPlayer());
		if (player == null) {
			return;
		}

		ItemStack result;
		CraftingTransaction transaction = event.getTransaction();
		if (transaction != null) {
			result = PokkitItemStack.toBukkitCopy(transaction.getPrimaryOutput());
		} else {
			result = PokkitItemStack.toBukkitCopy(event.getRecipe() != null
					? event.getRecipe().getResult() : null);
		}

		Recipe recipe = new NukkitRecipeWrapper(result);

		Inventory bukkitInventory = player.getOpenInventory().getTopInventory();
		InventoryView view = new PokkitInventoryView(bukkitInventory, player);

		CraftItemEvent bukkitEvent = new CraftItemEvent(
				recipe, view,
				InventoryType.SlotType.RESULT, 0,
				ClickType.LEFT, InventoryAction.PLACE_ONE);

		callCancellable(event, bukkitEvent);
	}
}
