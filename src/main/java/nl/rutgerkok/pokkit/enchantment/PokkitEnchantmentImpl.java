package nl.rutgerkok.pokkit.enchantment;

import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

final class PokkitEnchantmentImpl extends Enchantment {

	private final cn.nukkit.item.enchantment.Enchantment nukkit;
	private final NamespacedKey bukkitId;

	public PokkitEnchantmentImpl(cn.nukkit.item.enchantment.Enchantment nukkit, NamespacedKey bukkitId) {
		super();
		this.nukkit = Objects.requireNonNull(nukkit, "nukkit");
		this.bukkitId = bukkitId;
	}

	@Override
	public NamespacedKey getKey() {
		return bukkitId;
	}

	@Override
	public String getTranslationKey() {
		return "enchantment." + bukkitId.getNamespace() + "." + bukkitId.getKey();
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return nukkit.canEnchant(PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		cn.nukkit.item.enchantment.Enchantment nukkitOther = cn.nukkit.item.enchantment.Enchantment
				.get(PokkitEnchantment.toNukkit(other));
		return !nukkit.isCompatibleWith(nukkitOther);
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return PokkitEnchantmentTarget.toBukkit(nukkit.type);
	}

	@Override
	public int getMaxLevel() {
		return nukkit.getMaxLevel();
	}

	@Override
	public String getName() {
		return nukkit.getName();
	}

	@Override
	public int getStartLevel() {
		return nukkit.getMinLevel();
	}

	@Override
	public boolean isCursed() {
		return nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_BINDING_CURSE || nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_VANISHING_CURSE;
	}

	@Override
	public boolean isTreasure() {
		return nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_FROST_WALKER || nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_MENDING || nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_SOUL_SPEED || nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_BINDING_CURSE || nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_VANISHING_CURSE;
	}
}
