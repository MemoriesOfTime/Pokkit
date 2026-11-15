package nl.rutgerkok.pokkit.item;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Item meta for enchanted books. Bedrock stores the enchantments of an
 * enchanted book in the same {@code ench} tag as the enchantments of other
 * items, so the implementation of {@link PokkitItemMeta} can be reused.
 */
public final class PokkitEnchantedBookMeta extends PokkitItemMeta implements EnchantmentStorageMeta {

	PokkitEnchantedBookMeta(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	public PokkitEnchantedBookMeta clone() {
		return (PokkitEnchantedBookMeta) super.clone();
	}

	@Override
	public boolean addStoredEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		return addEnchant(enchantment, level, ignoreLevelRestriction);
	}

	@Override
	public int getStoredEnchantLevel(Enchantment enchantment) {
		return getEnchantLevel(enchantment);
	}

	@Override
	public Map<Enchantment, Integer> getStoredEnchants() {
		return getEnchants();
	}

	@Override
	public boolean hasConflictingStoredEnchant(Enchantment enchantment) {
		return hasConflictingEnchant(enchantment);
	}

	@Override
	public boolean hasStoredEnchant(Enchantment enchantment) {
		return hasEnchant(enchantment);
	}

	@Override
	public boolean hasStoredEnchants() {
		return hasEnchants();
	}

	@Override
	public boolean removeStoredEnchant(Enchantment enchantment) {
		return removeEnchant(enchantment);
	}

	/**
	 * Recreates the item meta from its serialized form. Required because
	 * Bukkit's configuration serialization looks for a method declared on the
	 * concrete class.
	 *
	 * @param args
	 *            The serialized data.
	 * @return The item meta.
	 */
	public static org.bukkit.inventory.meta.ItemMeta deserialize(java.util.Map<String, Object> args) {
		return PokkitItemMeta.deserialize(args);
	}
}
