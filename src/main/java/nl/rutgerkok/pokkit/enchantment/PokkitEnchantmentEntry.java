package nl.rutgerkok.pokkit.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import nl.rutgerkok.pokkit.item.PokkitItemStack;

/**
 * An enchantment in the Bukkit registry. All properties are delegated to the
 * Nukkit enchantment, so that for example
 * {@link Enchantment#getMaxLevel()} and
 * {@link Enchantment#canEnchantItem(ItemStack)} return the same values as the
 * server uses.
 *
 * <p>
 * Enchantments that do not exist in Nukkit (for example sweeping edge) fall
 * back to a generic implementation.
 */
public final class PokkitEnchantmentEntry extends Enchantment {

	private final NamespacedKey key;
	private final String name;
	private final cn.nukkit.item.enchantment.Enchantment nukkit;

	public PokkitEnchantmentEntry(NamespacedKey key) {
		this(key, null);
	}

	public PokkitEnchantmentEntry(NamespacedKey key, cn.nukkit.item.enchantment.Enchantment nukkit) {
		this.key = key;
		this.name = nukkit != null ? nukkit.getOriginalName() : prettify(key);
		this.nukkit = nukkit;
	}

	private static String prettify(NamespacedKey key) {
		String[] parts = key.getKey().split("_");
		StringBuilder result = new StringBuilder();
		for (String part : parts) {
			if (part.isEmpty()) {
				continue;
			}
			if (result.length() > 0) {
				result.append(' ');
			}
			result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
		}
		return result.toString();
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
		// Instances are only handed out by the enchantment registry.
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxLevel() {
		if (nukkit != null) {
			return nukkit.getMaxLevel();
		}
		// Bukkit-only enchantment: sweeping edge goes up to level 3.
		return "sweeping_edge".equals(key.getKey()) ? 3 : 5;
	}

	@Override
	public int getStartLevel() {
		return nukkit != null ? nukkit.getMinLevel() : 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		if (nukkit == null) {
			return EnchantmentTarget.ALL;
		}
		EnchantmentTarget target = PokkitEnchantmentTarget.toBukkit(nukkit.type);
		return target != null ? target : EnchantmentTarget.ALL;
	}

	@Override
	public boolean isTreasure() {
		if (nukkit == null) {
			return false;
		}
		return nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_FROST_WALKER
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_MENDING
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_SOUL_SPEED
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_SWIFT_SNEAK
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_BINDING_CURSE
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_VANISHING_CURSE;
	}

	@Override
	public boolean isCursed() {
		if (nukkit == null) {
			return false;
		}
		return nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_BINDING_CURSE
				|| nukkit.id == cn.nukkit.item.enchantment.Enchantment.ID_VANISHING_CURSE;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		if (nukkit == null || other == null) {
			return false;
		}
		cn.nukkit.item.enchantment.Enchantment nukkitOther = PokkitEnchantment
				.getNukkitEnchantment(other.getKey());
		return nukkitOther != null && !nukkit.isCompatibleWith(nukkitOther);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		if (nukkit == null) {
			return true;
		}
		return nukkit.canEnchant(PokkitItemStack.toNukkitCopy(item));
	}

	@NotNull
	@Override
	public String getTranslationKey() {
		return "enchantment." + key.getNamespace() + "." + key.getKey();
	}
}
