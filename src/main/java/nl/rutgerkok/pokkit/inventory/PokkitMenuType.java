package nl.rutgerkok.pokkit.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

import nl.rutgerkok.pokkit.Pokkit;

/**
 * Menu type entry for the Bukkit menu registry.
 *
 * <p>
 * Bedrock has no direct equivalent of Java Edition's menu type registry, so
 * opening a menu through this type is not supported. The entry itself is needed
 * because {@code InventoryType} and {@code MenuType} initialise their constants
 * from this registry.
 */
public final class PokkitMenuType implements MenuType.Typed<InventoryView, InventoryViewBuilder<InventoryView>> {

	private final NamespacedKey key;

	public PokkitMenuType(String key) {
		this.key = NamespacedKey.minecraft(key);
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public NamespacedKey getKeyOrThrow() {
		return key;
	}

	@Override
	public NamespacedKey getKeyOrNull() {
		return key;
	}

	@Override
	public boolean isRegistered() {
		return true;
	}

	@Override
	public Typed<InventoryView, InventoryViewBuilder<InventoryView>> typed() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends InventoryView, B extends InventoryViewBuilder<V>> Typed<V, B> typed(Class<V> viewClass) {
		if (!viewClass.isAssignableFrom(InventoryView.class)) {
			throw new IllegalArgumentException("Not a compatible inventory view: " + viewClass.getName());
		}
		return (Typed<V, B>) this;
	}

	@Override
	public Class<? extends InventoryView> getInventoryViewClass() {
		return InventoryView.class;
	}

	@Override
	public InventoryView create(HumanEntity player, String title) {
		throw Pokkit.unsupported();
	}

	@Override
	public InventoryViewBuilder<InventoryView> builder() {
		throw Pokkit.unsupported();
	}

	@Override
	public String toString() {
		return key.toString();
	}
}
