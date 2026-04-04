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
			consumer.accept(stack.getItemMeta());
		}
		return stack;
	}

	@Override
	public ItemStack createItemStack(int amount, java.util.function.Consumer<? super ItemMeta> consumer) {
		ItemStack stack = createItemStack(amount);
		if (stack != null) {
			consumer.accept(stack.getItemMeta());
		}
		return stack;
	}

	@Override
	public boolean hasBlockType() {
		return false;
	}

	@Override
	public BlockType getBlockType() {
		return null;
	}

	@Override
	public Class<ItemMeta> getItemMetaClass() {
		return ItemMeta.class;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public short getMaxDurability() {
		return 0;
	}

	@Override
	public boolean isEdible() {
		return false;
	}

	@Override
	public boolean isRecord() {
		return false;
	}

	@Override
	public boolean isFuel() {
		return false;
	}

	@Override
	public boolean isCompostable() {
		return false;
	}

	@Override
	public float getCompostChance() {
		return 0;
	}

	@Override
	public ItemType getCraftingRemainingItem() {
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

	@Override
	public boolean isEnabledByFeature(World world) {
		return true;
	}

	@Override
	public Material asMaterial() {
		return Material.matchMaterial(key.getKey());
	}
}
