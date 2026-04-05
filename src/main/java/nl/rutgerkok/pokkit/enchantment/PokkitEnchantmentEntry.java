package nl.rutgerkok.pokkit.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class PokkitEnchantmentEntry extends Enchantment {

	private final NamespacedKey key;
	private final String name;

	public PokkitEnchantmentEntry(NamespacedKey key) {
		this.key = key;
		this.name = key.getKey().toUpperCase().replace('_', ' ');
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@NotNull
	@Override
	public String getTranslationKey() {
		return "enchantment." + key.getNamespace() + "." + key.getKey();
	}
}
