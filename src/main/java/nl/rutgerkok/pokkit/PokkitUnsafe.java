package nl.rutgerkok.pokkit;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.item.PokkitItemMeta;

@SuppressWarnings("deprecation")
public class PokkitUnsafe implements UnsafeValues {

	@Override
	public void checkSupported(PluginDescriptionFile pdf) {
		// Empty!
	}

	@Override
	public Material fromLegacy(Material material) {
		return material;
	}

	@Override
	public BlockData fromLegacy(Material material, byte data) {
		return PokkitBlockData.createBlockData(material, data);
	}

	@Override
	public Material getMaterial(String s, int i) {
		throw Pokkit.unsupported();
	}

	@Override
	public Material fromLegacy(MaterialData material) {
		return material.getItemType();
	}

	@Override
	public Material fromLegacy(MaterialData material, boolean itemPriority) {
		return material.getItemType();
	}

	@Override
	public int getDataVersion() {
		return 1;
	}

	@Override
	public Advancement loadAdvancement(NamespacedKey key, String advancementJson) {
		throw Pokkit.unsupported();
	}

	@Override
	public ItemStack modifyItemStack(ItemStack stack, String arguments) {
		// NBT not yet supported
		PokkitItemMeta itemMeta = (PokkitItemMeta) stack.getItemMeta();
		itemMeta.getTag().putString("Unknown", arguments);
		stack.setItemMeta(itemMeta);
		return stack;
	}

	@Override
	public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
		return clazz; // Not implemented yet
	}

	@Override
	public boolean removeAdvancement(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public Material toLegacy(Material material) {
		return material;
	}

	@Override
	public <B extends org.bukkit.Keyed> B get(org.bukkit.Registry<B> registry, org.bukkit.NamespacedKey key) {
		return null;
	}

	@Override
	public String get(Class<?> type, String name) {
		return null;
	}

	@Override
	public com.google.common.collect.Multimap<org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier> getDefaultAttributeModifiers(Material material, org.bukkit.inventory.EquipmentSlot slot) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.inventory.CreativeCategory getCreativeCategory(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getBlockTranslationKey(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getItemTranslationKey(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getTranslationKey(org.bukkit.entity.EntityType entityType) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		throw Pokkit.unsupported();
	}

	@Override
	public String getTranslationKey(org.bukkit.attribute.Attribute attribute) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.FeatureFlag getFeatureFlag(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.potion.PotionType.InternalPotionData getInternalPotionData(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.damage.DamageEffect getDamageEffect(String name) {
		throw Pokkit.unsupported();
	}

	@Override
	public org.bukkit.damage.DamageSource.Builder createDamageSourceBuilder(org.bukkit.damage.DamageType damageType) {
		throw Pokkit.unsupported();
	}
}
