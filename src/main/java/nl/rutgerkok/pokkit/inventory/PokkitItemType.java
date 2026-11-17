package nl.rutgerkok.pokkit.inventory;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Multimap;

public final class PokkitItemType implements ItemType.Typed<ItemMeta> {

	private final NamespacedKey key;

	private PokkitItemType(NamespacedKey key) {
		this.key = key;
	}

	public static PokkitItemType of(String materialName) {
		return new PokkitItemType(NamespacedKey.minecraft(materialName.toLowerCase()));
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
		// Instances are only handed out by the item registry.
		return true;
	}

	@Override
	public String getTranslationKey() {
		return "item." + key.getNamespace() + "." + key.getKey();
	}

	@Override
	public Typed<ItemMeta> typed() {
		return this;
	}

	@Override
	public <M extends ItemMeta> Typed<M> typed(Class<M> itemMetaType) {
		return null;
	}

	@Override
	public ItemStack createItemStack() {
		Material material = asMaterial();
		if (material != null) {
			return new ItemStack(material);
		}
		return null;
	}

	@Override
	public ItemStack createItemStack(int amount) {
		Material material = asMaterial();
		if (material != null) {
			return new ItemStack(material, amount);
		}
		return null;
	}

	@Override
	public ItemStack createItemStack(java.util.function.Consumer<? super ItemMeta> consumer) {
		ItemStack stack = createItemStack();
		if (stack != null) {
			ItemMeta meta = stack.getItemMeta();
			consumer.accept(meta);
			stack.setItemMeta(meta);
		}
		return stack;
	}

	@Override
	public ItemStack createItemStack(int amount, java.util.function.Consumer<? super ItemMeta> consumer) {
		ItemStack stack = createItemStack(amount);
		if (stack != null) {
			ItemMeta meta = stack.getItemMeta();
			consumer.accept(meta);
			stack.setItemMeta(meta);
		}
		return stack;
	}

	@Override
	public boolean hasBlockType() {
		Material material = asMaterial();
		// Material.isBlock() asks Registry.BLOCK, which is built from the
		// vanilla block list, so this is the same answer Bukkit gives.
		return material != null && material.isBlock();
	}

	@Override
	public BlockType getBlockType() {
		if (!hasBlockType()) {
			return null;
		}
		// Prefer the registry instance, so that identity comparisons with
		// Material#asBlockType() keep working.
		BlockType registered = org.bukkit.Registry.BLOCK.get(key);
		return registered != null ? registered : nl.rutgerkok.pokkit.block.PokkitBlockType.of(key.getKey());
	}

	@Override
	public Class<ItemMeta> getItemMetaClass() {
		return ItemMeta.class;
	}

	@Override
	public int getMaxStackSize() {
		// Do not ask Material or Nukkit here: Material delegates back to this
		// ItemType in 1.21.11, and Nukkit has different stack sizes for some
		// items (cake, for example, stacks to 64 in Bedrock but to 1 in Java).
		Integer stackSize = JavaItemData.MAX_STACK_SIZES.get(key.getKey());
		return stackSize == null ? 64 : stackSize;
	}

	@Override
	public short getMaxDurability() {
		// See getMaxStackSize. Nukkit also stores one more durability point on
		// many tools, and some newer items are missing from it entirely.
		Integer durability = JavaItemData.MAX_DURABILITIES.get(key.getKey());
		return durability == null ? 0 : durability.shortValue();
	}

	/*
	 * The remaining methods must not delegate to Material: those methods look
	 * the material up in Registry.ITEM, which returns this very class, so
	 * delegating would recurse until the stack overflows. The properties are
	 * taken from the Nukkit item instead.
	 */

	@Override
	public boolean isEdible() {
		return nukkitItem() instanceof cn.nukkit.item.ItemEdible;
	}

	@Override
	public boolean isRecord() {
		return nukkitItem() instanceof cn.nukkit.item.ItemRecord;
	}

	@Override
	public boolean isFuel() {
		Short fuelTime = nukkitItem().getFuelTime();
		return fuelTime != null && fuelTime > 0;
	}

	@Override
	public boolean isCompostable() {
		// Nukkit does not expose compost data.
		return false;
	}

	@Override
	public float getCompostChance() {
		return 0;
	}

	@Override
	public ItemType getCraftingRemainingItem() {
		// Nukkit does not expose crafting remaining items.
		return null;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(org.bukkit.inventory.EquipmentSlot slot) {
		return com.google.common.collect.ImmutableMultimap.of();
	}

	@Override
	public CreativeCategory getCreativeCategory() {
		return null;
	}

	/**
	 * Gets the Nukkit item for this type, or an air item if the material is
	 * unknown.
	 *
	 * @return The Nukkit item, never null.
	 */
	private cn.nukkit.item.Item nukkitItem() {
		Material material = asMaterial();
		return material == null ? cn.nukkit.item.Item.get(cn.nukkit.item.Item.AIR)
				: nl.rutgerkok.pokkit.item.ItemMap.fromBukkit(material);
	}

	@Override
	public boolean isEnabledByFeature(World world) {
		return true;
	}

	@Override
	public Material asMaterial() {
		return Material.matchMaterial(key.getKey());
	}
}
