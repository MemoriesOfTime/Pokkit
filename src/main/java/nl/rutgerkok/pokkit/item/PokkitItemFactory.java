package nl.rutgerkok.pokkit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cn.nukkit.nbt.tag.CompoundTag;

public final class PokkitItemFactory implements ItemFactory {

	private static final Random RANDOM = new Random();

	public PokkitItemFactory() {
		// Register the meta implementations with the configuration
		// serialization system, so that items stored in a plugin config can be
		// read back. Every concrete class needs its own registration, because
		// the alias is resolved from the runtime class.
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitItemMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitPotionMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitSkullMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitBlockStateMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitLeatherArmorMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitEnchantedBookMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitWrittenBookMeta.class);
		org.bukkit.configuration.serialization.ConfigurationSerialization
				.registerClass(PokkitWritableBookMeta.class);
	}

	@Override
	public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
		return asMetaFor(meta, stack == null ? null : stack.getType());
	}

	@Override
	public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
		if (!(meta instanceof PokkitItemMeta pokkitMeta)) {
			throw new IllegalArgumentException("Unsupported ItemMeta implementation: "
					+ (meta == null ? "null" : meta.getClass().getName()));
		}
		if (material == null || material == Material.AIR) {
			// The ItemFactory contract explicitly says that this method
			// returns null for air.
			return null;
		}
		// Return a new meta, so that the caller cannot modify the original
		// through the returned instance. The damage value must be preserved,
		// otherwise setting a display name on a damaged tool would repair it.
		return getItemMeta(material, pokkitMeta.getTag().copy(), pokkitMeta.getDamage());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * Unlike what you would expect, this method is not a wrapper for
	 * {@link Objects#equals(Object, Object)}. Bukkit has a strange
	 * implementation: a meta equals null if it is empty. We have to copy this
	 * behaviour, for example because {@link ItemStack#hasItemMeta()} depends on
	 * it.
	 */
	@Override
	public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {

		if (meta1 == null && meta2 != null) {
			return isEqualToNull(meta2);
		}
		if (meta1 != null && meta2 == null) {
			return isEqualToNull(meta1);
		}
		return Objects.equals(meta1, meta2);
	}

	@Override
	public Color getDefaultLeatherColor() {
		return Color.fromRGB(0xA06540);
	}

	@Override
	public ItemMeta getItemMeta(Material material) {
		if (material == Material.AIR) {
			// Air has no item meta in Bukkit.
			return null;
		}
		return getItemMeta(material, new CompoundTag(), 0);
	}

	@SuppressWarnings("deprecation")
	public ItemMeta getItemMeta(Material material, CompoundTag tag, int damage) {
		if (isBlockStateMaterial(material)) {
			return new PokkitBlockStateMeta(tag, material, damage);
		}
		switch (material) {
		case POTION:
		case LEGACY_POTION:
		case SPLASH_POTION:
		case LEGACY_SPLASH_POTION:
		case LINGERING_POTION:
		case LEGACY_LINGERING_POTION:
		case TIPPED_ARROW:
		case LEGACY_TIPPED_ARROW:
			return new PokkitPotionMeta(tag, damage);
		case SKELETON_SKULL:
		case WITHER_SKELETON_SKULL:
		case ZOMBIE_HEAD:
		case CREEPER_HEAD:
		case PLAYER_HEAD:
		case DRAGON_HEAD:
		case LEGACY_SKULL_ITEM:
			return new PokkitSkullMeta(tag, material, damage);
		case LEATHER_BOOTS:
		case LEGACY_LEATHER_BOOTS:
		case LEATHER_CHESTPLATE:
		case LEGACY_LEATHER_CHESTPLATE:
		case LEATHER_HELMET:
		case LEGACY_LEATHER_HELMET:
		case LEATHER_LEGGINGS:
		case LEGACY_LEATHER_LEGGINGS:
			return new PokkitLeatherArmorMeta(tag, damage);
		case ENCHANTED_BOOK:
		case LEGACY_ENCHANTED_BOOK:
			return new PokkitEnchantedBookMeta(tag, damage);
		case WRITTEN_BOOK:
		case LEGACY_WRITTEN_BOOK:
			return new PokkitWrittenBookMeta(tag, damage);
		case WRITABLE_BOOK:
		case LEGACY_BOOK_AND_QUILL:
			return new PokkitWritableBookMeta(tag, damage);
		default:
			return new PokkitItemMeta(tag, damage);
		}
	}

	/**
	 * Tests whether items of the given material carry block entity data, which
	 * is exposed through {@link org.bukkit.inventory.meta.BlockStateMeta}.
	 *
	 * @param material
	 *            The material, may be null.
	 * @return True if the material is a block with a block state, false
	 *         otherwise.
	 */
	static boolean isBlockStateMaterial(Material material) {
		if (material == null) {
			return false;
		}
		switch (material) {
		case CHEST:
		case LEGACY_CHEST:
		case TRAPPED_CHEST:
		case LEGACY_TRAPPED_CHEST:
		case DISPENSER:
		case LEGACY_DISPENSER:
		case FURNACE:
		case LEGACY_FURNACE:
		case BREWING_STAND:
		case LEGACY_BREWING_STAND:
		case HOPPER:
		case LEGACY_HOPPER:
		case DROPPER:
		case LEGACY_DROPPER:
		case BEACON:
		case LEGACY_BEACON:
		case SPAWNER:
		case LEGACY_MOB_SPAWNER:
		case NOTE_BLOCK:
		case LEGACY_NOTE_BLOCK:
		case JUKEBOX:
		case LEGACY_JUKEBOX:
		case ENCHANTING_TABLE:
		case LEGACY_ENCHANTMENT_TABLE:
		case COMMAND_BLOCK:
		case LEGACY_COMMAND:
		case REPEATING_COMMAND_BLOCK:
		case LEGACY_COMMAND_REPEATING:
		case CHAIN_COMMAND_BLOCK:
		case LEGACY_COMMAND_CHAIN:
		case END_GATEWAY:
		case LEGACY_END_GATEWAY:
		case STRUCTURE_BLOCK:
		case LEGACY_STRUCTURE_BLOCK:
		case FLOWER_POT:
		case LEGACY_FLOWER_POT:
		case COMPARATOR:
		case LEGACY_REDSTONE_COMPARATOR:
		case SHULKER_BOX:
		case WHITE_SHULKER_BOX:
		case ORANGE_SHULKER_BOX:
		case MAGENTA_SHULKER_BOX:
		case LIGHT_BLUE_SHULKER_BOX:
		case YELLOW_SHULKER_BOX:
		case LIME_SHULKER_BOX:
		case PINK_SHULKER_BOX:
		case GRAY_SHULKER_BOX:
		case LIGHT_GRAY_SHULKER_BOX:
		case CYAN_SHULKER_BOX:
		case PURPLE_SHULKER_BOX:
		case BLUE_SHULKER_BOX:
		case BROWN_SHULKER_BOX:
		case GREEN_SHULKER_BOX:
		case RED_SHULKER_BOX:
		case BLACK_SHULKER_BOX:
		case BARREL:
		case BLAST_FURNACE:
		case SMOKER:
		case LECTERN:
		case BEEHIVE:
		case BEE_NEST:
		case CAMPFIRE:
		case SOUL_CAMPFIRE:
		case JIGSAW:
		case CONDUIT:
		case BELL:
		case SCULK_SENSOR:
		case CALIBRATED_SCULK_SENSOR:
		case DECORATED_POT:
		case CHISELED_BOOKSHELF:
		case CRAFTER:
		case TRIAL_SPAWNER:
		case VAULT:
		case SUSPICIOUS_SAND:
		case SUSPICIOUS_GRAVEL:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Tests whether the given material is a skull item.
	 *
	 * @param material
	 *            The material, may be null.
	 * @return True if the material is a skull, false otherwise.
	 */
	static boolean isSkullMaterial(Material material) {
		if (material == null) {
			return false;
		}
		switch (material) {
		case SKELETON_SKULL:
		case WITHER_SKELETON_SKULL:
		case ZOMBIE_HEAD:
		case CREEPER_HEAD:
		case PLAYER_HEAD:
		case DRAGON_HEAD:
		case PIGLIN_HEAD:
		case LEGACY_SKULL_ITEM:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean isApplicable(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
		return stack != null && isApplicable(meta, stack.getType());
	}

	@Override
	public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
		// Both parameters are nullable according to the ItemFactory
		// contract; meta that was not created by this factory is simply not
		// applicable instead of an error, like CraftBukkit does.
		if (!(meta instanceof PokkitItemMeta pokkitMeta) || material == null || material == Material.AIR) {
			return false;
		}
		return pokkitMeta.isApplicable(material);
	}

	private boolean isEqualToNull(ItemMeta meta) {
		PokkitItemMeta pokkitMeta = (PokkitItemMeta) meta;
		// The damage value is stored outside the NBT tag, but it still makes
		// the meta non-empty: a damaged tool must report hasItemMeta() == true
		// and must be serialized.
		return pokkitMeta.getTag().isEmpty() && !pokkitMeta.hasDamage();
	}

	@SuppressWarnings("deprecation")
	public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
		return material;
	}

	@Override
	public Material getSpawnEgg(EntityType type) {
		if (type == null) {
			return Material.AIR;
		}
		Material material = Material.matchMaterial(type.name().toUpperCase(Locale.ROOT) + "_SPAWN_EGG");
		return material == null ? Material.AIR : material;
	}

	@Override
	public ItemStack enchantItem(org.bukkit.entity.Entity entity, ItemStack item, int level, boolean allowTreasures) {
		return enchantItem(item, level, allowTreasures);
	}

	@Override
	public ItemStack enchantItem(org.bukkit.World world, ItemStack item, int level, boolean allowTreasures) {
		return enchantItem(item, level, allowTreasures);
	}

	@Override
	public ItemStack enchantItem(ItemStack item, int level, boolean allowTreasures) {
		if (item == null || item.getType() == Material.AIR) {
			return item;
		}

		List<Enchantment> applicable = new ArrayList<>();
		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if (nl.rutgerkok.pokkit.enchantment.PokkitEnchantment.toNukkit(enchantment) < 0) {
				// Bukkit-only enchantment that Nukkit cannot store.
				continue;
			}
			if (enchantment.isTreasure() && !allowTreasures) {
				continue;
			}
			if (enchantment.canEnchantItem(item)) {
				applicable.add(enchantment);
			}
		}
		if (applicable.isEmpty()) {
			return item;
		}

		Enchantment chosen = applicable.get(RANDOM.nextInt(applicable.size()));
		int maxLevel = Math.max(1, Math.min(chosen.getMaxLevel(), 1 + level / 10));
		int chosenLevel = 1 + RANDOM.nextInt(maxLevel);

		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(chosen, chosenLevel, true);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public ItemStack createItemStack(String materialKey) throws IllegalArgumentException {
		Material material = Material.matchMaterial(materialKey);
		if (material == null) {
			throw new IllegalArgumentException("Unknown material: " + materialKey);
		}
		return new ItemStack(material);
	}

}
