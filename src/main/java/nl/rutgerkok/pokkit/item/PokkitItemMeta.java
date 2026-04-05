package nl.rutgerkok.pokkit.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import nl.rutgerkok.pokkit.enchantment.PokkitEnchantment;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataContainer;

import nl.rutgerkok.pokkit.Pokkit;

public class PokkitItemMeta implements ItemMeta, Damageable {

	protected CompoundTag tag;
	private int damage;

	PokkitItemMeta(CompoundTag tag, int damage) {
		this.tag = Objects.requireNonNull(tag, "tag");
		this.damage = damage;
	}

	@Override
	public boolean addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		if (level > enchantment.getMaxLevel() && !ignoreLevelRestriction) {
			return false;
		}

		short nukkitEnchantmentId = (short) PokkitEnchantment.toNukkit(enchantment);

		ListTag<CompoundTag> enchTag;
		if (!hasEnchants()) {
			enchTag = new ListTag<>("ench");
			tag.putList(enchTag);
		} else {
			enchTag = tag.getList("ench", CompoundTag.class);
		}

		// Try to modify an existing enchantment first
		boolean modifiedATag = false;
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			if (entry.getShort("id") == nukkitEnchantmentId) {
				// The add method actually replaces a tag
				enchTag.add(i, new CompoundTag()
						.putShort("id", nukkitEnchantmentId)
						.putShort("lvl", level));
				modifiedATag = true;
				break;
			}
		}

		// Else, add this enchantment as a new enchantment
		if (!modifiedATag) {
			enchTag.add(new CompoundTag()
					.putShort("id", nukkitEnchantmentId)
					.putShort("lvl", level));
		}

		return true;
	}

	@Override
	public void addItemFlags(ItemFlag... itemFlags) {
		int flags = tag.contains("HideFlags") ? tag.getInt("HideFlags") : 0;
		for (ItemFlag flag : itemFlags) {
			flags |= getFlagBit(flag);
		}
		tag.putInt("HideFlags", flags);
	}

	@Override
	public PokkitItemMeta clone() {
		try {
			PokkitItemMeta meta = (PokkitItemMeta) super.clone();
			meta.damage = this.damage;
			meta.tag = this.tag.copy();
			return meta;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokkitItemMeta other = (PokkitItemMeta) obj;
		if (!tag.equals(other.tag))
			return false;
		if (damage != other.damage) {
			return false;
		}
		return true;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public String getDisplayName() {
		if (!tag.contains("display")) {
			return "";
		}
		CompoundTag displayTag = tag.getCompound("display");
		return displayTag.getString("Name");
	}

	@Override
	public int getEnchantLevel(Enchantment enchantment) {
		int nukkitEnchantmentId = PokkitEnchantment.toNukkit(enchantment);

		if (!hasEnchants()) {
			return 0;
		}

		ListTag<CompoundTag> enchTag = tag.getList("ench", CompoundTag.class);
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			if (entry.getShort("id") == nukkitEnchantmentId) {
				return entry.getShort("lvl");
			}
		}

		return 0;
	}

	@Override
	public Map<Enchantment, Integer> getEnchants() {
		if (!hasEnchants()) {
			return Collections.emptyMap();
		}

		ImmutableMap.Builder<Enchantment, Integer> map = new ImmutableMap.Builder<>();

		ListTag<CompoundTag> enchTag = tag.getList("ench", CompoundTag.class);
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			map.put(PokkitEnchantment.toBukkit(entry.getShort("id")), entry.getShort("lvl"));
		}

		return map.build();
	}

	@Override
	public Set<ItemFlag> getItemFlags() {
		int flags = tag.contains("HideFlags") ? tag.getInt("HideFlags") : 0;
		EnumSet<ItemFlag> set = EnumSet.noneOf(ItemFlag.class);
		for (ItemFlag flag : ItemFlag.values()) {
			if ((flags & getFlagBit(flag)) != 0) {
				set.add(flag);
			}
		}
		return set;
	}

	@Override
	public String getLocalizedName() {
		return null; // Silently unsupported
	}

	@Override
	public List<String> getLore() {
		if (!hasLore()) {
			return null;
		}

		List<String> lore = new ArrayList<>();
		CompoundTag displayTag = tag.getCompound("display");
		ListTag<? extends Tag> loreListTag = displayTag.getList("Lore");

		for (Tag tag : loreListTag.getAll()) {
			if (!(tag instanceof StringTag)) {
				continue;
			}

			lore.add(((StringTag) tag).data);
		}

		return lore;
	}

	/**
	 * Accesses the NBT tag of this item meta.
	 *
	 * @return The NBT tag.
	 */
	public CompoundTag getTag() {
		return tag;
	}

	@Override
	public boolean hasConflictingEnchant(Enchantment ench) {
		for (Enchantment onItem : getEnchants().keySet()) {
			if (onItem.conflictsWith(ench)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasDamage() {
		return this.damage != 0;
	}

	@Override
	public boolean hasDisplayName() {
		if (!tag.contains("display")) {
			return false;
		}
		CompoundTag displayTag = tag.getCompound("display");
		return displayTag.contains("Name");
	}

	@Override
	public boolean hasEnchant(Enchantment enchantment) {
		int nukkitEnchantmentId = PokkitEnchantment.toNukkit(enchantment);

		if (!hasEnchants()) {
			return false;
		}

		ListTag<CompoundTag> enchTag = tag.getList("ench", CompoundTag.class);
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			if (entry.getShort("id") == nukkitEnchantmentId) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasEnchants() {
		CompoundTag tag = this.tag;

		if (tag.contains("ench")) {
			Tag enchTag = tag.get("ench");
			if (enchTag instanceof ListTag) {
				return ((ListTag<?>) enchTag).size() > 0;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}

	@Override
	public boolean hasItemFlag(ItemFlag flag) {
		int flags = tag.contains("HideFlags") ? tag.getInt("HideFlags") : 0;
		return (flags & getFlagBit(flag)) != 0;
	}

	@Override
	public boolean hasLocalizedName() {
		return false; // Silently unsupported
	}

	@Override
	public boolean hasLore() {
		if (!tag.contains("display")) {
			return false;
		}
		CompoundTag displayTag = tag.getCompound("display");
		if (!displayTag.contains("Lore")) {
			return false;
		}

		Tag loreTag = displayTag.get("Lore");
		if (loreTag instanceof ListTag) {
			return ((ListTag<?>) loreTag).size() > 0;
		}

		return false;
	}

	boolean isApplicable(Material material) {
		return true;
	}

	@Override
	public boolean isUnbreakable() {
		return tag.getBoolean("Unbreakable");
	}

	@Override
	public boolean removeEnchant(Enchantment enchantment) {
		if (!hasEnchants()) {
			return false;
		}

		int nukkitEnchantmentId = PokkitEnchantment.toNukkit(enchantment);

		ListTag<CompoundTag> enchTag = tag.getList("ench", CompoundTag.class);
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			if (entry.getShort("id") == nukkitEnchantmentId) {
				// Found enchantment of right type, remove
				enchTag.remove(i);

				if (enchTag.size() == 0) {
					// No enchantments remain, remove tag
					tag.remove("ench");
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void removeItemFlags(ItemFlag... itemFlags) {
		int flags = tag.contains("HideFlags") ? tag.getInt("HideFlags") : 0;
		for (ItemFlag flag : itemFlags) {
			flags &= ~getFlagBit(flag);
		}
		if (flags == 0) {
			tag.remove("HideFlags");
		} else {
			tag.putInt("HideFlags", flags);
		}
	}

	private static int getFlagBit(ItemFlag flag) {
		switch (flag) {
			case HIDE_ENCHANTS: return 1;
			case HIDE_ATTRIBUTES: return 2;
			case HIDE_UNBREAKABLE: return 4;
			case HIDE_DESTROYS: return 8;
			case HIDE_PLACED_ON: return 16;
			case HIDE_DYE: return 32;
			case HIDE_ARMOR_TRIM: return 64;
			default: return 0;
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new java.util.LinkedHashMap<>();
		if (hasDisplayName()) data.put("display-name", getDisplayName());
		if (hasLore()) data.put("lore", getLore());
		if (damage > 0) data.put("damage", damage);
		return data;
	}

	@Override
	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public void setDisplayName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			if (!hasDisplayName()) {
				return;
			}

			// Remove custom name
			CompoundTag displayTag = tag.getCompound("display");
			displayTag.remove("Name");
			setOrRemoveChildTag(tag, "display", displayTag);
			return;
		}

		CompoundTag displayTag = tag.getCompound("display");
		displayTag.putString("Name", name);
		tag.putCompound("display", displayTag);
	}

	@Override
	public void setLocalizedName(String name) {
	}

	@Override
	public void setLore(List<String> lore) {
		CompoundTag displayTag = tag.getCompound("display");

		if (lore == null || lore.isEmpty()) {
			// Remove the lore
			displayTag.remove("Lore");
		} else {
			// Set the lore
			ListTag<StringTag> loreListTag = new ListTag<>("Lore");
			for (String line : lore) {
				loreListTag.add(new StringTag("", line));
			}
			displayTag.putList(loreListTag);
		}

		setOrRemoveChildTag(tag, "display", displayTag);
	}

	@Override
	public boolean hasCustomModelData() {
		return tag.contains("CustomModelData");
	}

	@Override
	public int getCustomModelData() {
		return tag.getInt("CustomModelData");
	}

	@Override
	public void setCustomModelData(Integer integer) {
		if (integer == null) {
			tag.remove("CustomModelData");
		} else {
			tag.putInt("CustomModelData", integer);
		}
	}

	/**
	 * Sets the child in the parent when the child is not empty. If the child is
	 * empty, the child tag is removed from the parent.
	 *
	 * @param parent
	 *            The parent tag.
	 * @param name
	 *            The name of the child tag.
	 * @param child
	 *            The child tag.
	 */
	protected void setOrRemoveChildTag(CompoundTag parent, String name, CompoundTag child) {
		if (child.isEmpty()) {
			parent.remove(name);
		} else {
			parent.putCompound(name, child);
		}
	}

	@Override
	public void setUnbreakable(boolean unbreakable) {
		if (unbreakable) {
			tag.putBoolean("Unbreakable", true);
		} else {
			tag.remove("Unbreakable");
		}
	}

	@Override
    public void setAttributeModifiers(Multimap modifiers) {
		if (modifiers == null || modifiers.isEmpty()) {
			tag.remove("AttributeModifiers");
			return;
		}
		ListTag<CompoundTag> list = new ListTag<>("AttributeModifiers");
		for (Object entry : modifiers.entries()) {
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) entry;
			Attribute attr = (Attribute) e.getKey();
			AttributeModifier mod = (AttributeModifier) e.getValue();
			CompoundTag attrTag = new CompoundTag()
					.putString("AttributeName", attr.getKey().toString())
					.putString("Name", mod.getName() != null ? mod.getName() : mod.getUniqueId().toString())
					.putDouble("Amount", mod.getAmount())
					.putInt("Operation", mod.getOperation().ordinal())
					.putLong("UUIDMost", mod.getUniqueId().getMostSignificantBits())
					.putLong("UUIDLeast", mod.getUniqueId().getLeastSignificantBits());
			if (mod.getSlot() != null) {
				attrTag.putString("Slot", mod.getSlot().name().toLowerCase());
			}
			list.add(attrTag);
		}
		tag.putList(list);
	}

	@Override
	public boolean hasAttributeModifiers() {
		return tag.contains("AttributeModifiers") && tag.getList("AttributeModifiers", CompoundTag.class).size() > 0;
	}

	@Override
	public boolean addAttributeModifier(Attribute a, AttributeModifier m) {
		ListTag<CompoundTag> list = tag.contains("AttributeModifiers")
				? tag.getList("AttributeModifiers", CompoundTag.class)
				: new ListTag<>("AttributeModifiers");
		CompoundTag attrTag = new CompoundTag()
				.putString("AttributeName", a.getKey().toString())
				.putString("Name", m.getName() != null ? m.getName() : m.getUniqueId().toString())
				.putDouble("Amount", m.getAmount())
				.putInt("Operation", m.getOperation().ordinal())
				.putLong("UUIDMost", m.getUniqueId().getMostSignificantBits())
				.putLong("UUIDLeast", m.getUniqueId().getLeastSignificantBits());
		if (m.getSlot() != null) {
			attrTag.putString("Slot", m.getSlot().name().toLowerCase());
		}
		list.add(attrTag);
		if (!tag.contains("AttributeModifiers")) {
			tag.putList(list);
		}
		return true;
	}

	@Override
	public boolean removeAttributeModifier(Attribute a) {
		if (!tag.contains("AttributeModifiers")) return false;
		ListTag<CompoundTag> list = tag.getList("AttributeModifiers", CompoundTag.class);
		String attrName = a.getKey().toString();
		boolean removed = false;
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getString("AttributeName").equals(attrName)) {
				list.remove(i);
				removed = true;
			}
		}
		if (list.size() == 0) tag.remove("AttributeModifiers");
		return removed;
	}

	@Override
	public boolean removeAttributeModifier(Attribute a, AttributeModifier m) {
		if (!tag.contains("AttributeModifiers")) return false;
		ListTag<CompoundTag> list = tag.getList("AttributeModifiers", CompoundTag.class);
		String attrName = a.getKey().toString();
		boolean removed = false;
		for (int i = list.size() - 1; i >= 0; i--) {
			CompoundTag entry = list.get(i);
			if (entry.getString("AttributeName").equals(attrName)
					&& entry.getLong("UUIDMost") == m.getUniqueId().getMostSignificantBits()
					&& entry.getLong("UUIDLeast") == m.getUniqueId().getLeastSignificantBits()) {
				list.remove(i);
				removed = true;
			}
		}
		if (list.size() == 0) tag.remove("AttributeModifiers");
		return removed;
	}

	@Override
	public CustomItemTagContainer getCustomTagContainer() {
		return null;
	}

	@Override
	public void setVersion(int i) {

	}

	@Override
	public boolean removeAttributeModifier(EquipmentSlot slot) {
		if (!tag.contains("AttributeModifiers")) return false;
		ListTag<CompoundTag> list = tag.getList("AttributeModifiers", CompoundTag.class);
		String slotName = slot.name().toLowerCase();
		boolean removed = false;
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getString("Slot").equals(slotName)) {
				list.remove(i);
				removed = true;
			}
		}
		if (list.size() == 0) tag.remove("AttributeModifiers");
		return removed;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
		Multimap<Attribute, AttributeModifier> map = com.google.common.collect.LinkedListMultimap.create();
		if (!tag.contains("AttributeModifiers")) return map;
		ListTag<CompoundTag> list = tag.getList("AttributeModifiers", CompoundTag.class);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.get(i);
			Attribute attr = resolveAttribute(entry.getString("AttributeName"));
			if (attr == null) continue;
			AttributeModifier mod = nbtToModifier(entry);
			if (mod != null) map.put(attr, mod);
		}
		return map;
	}

	@Override
	public Collection<AttributeModifier> getAttributeModifiers(Attribute attribute) {
		return getAttributeModifiers().get(attribute);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		Multimap<Attribute, AttributeModifier> map = com.google.common.collect.LinkedListMultimap.create();
		if (!tag.contains("AttributeModifiers")) return map;
		ListTag<CompoundTag> list = tag.getList("AttributeModifiers", CompoundTag.class);
		String slotName = slot.name().toLowerCase();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.get(i);
			if (!entry.getString("Slot").equals(slotName)) continue;
			Attribute attr = resolveAttribute(entry.getString("AttributeName"));
			if (attr == null) continue;
			AttributeModifier mod = nbtToModifier(entry);
			if (mod != null) map.put(attr, mod);
		}
		return map;
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return new nl.rutgerkok.pokkit.persistence.PokkitPersistentDataContainer(tag);
	}

	@Override
	public String getAsComponentString() {
		return "{}";
	}

	@Override
	public String getAsString() {
		return "{}";
	}

	@Override
	public boolean hasItemName() {
		if (!tag.contains("display")) return false;
		CompoundTag displayTag = tag.getCompound("display");
		return displayTag.contains("Name");
	}

	@Override
	public String getItemName() {
		if (!tag.contains("display")) return null;
		CompoundTag displayTag = tag.getCompound("display");
		if (!displayTag.contains("Name")) return null;
		return displayTag.getString("Name");
	}

	@Override
	public void setItemName(String name) {
		if (name != null) {
			CompoundTag displayTag = tag.getCompound("display");
			displayTag.putString("Name", name);
			tag.putCompound("display", displayTag);
		}
	}

	@Override
	public boolean hasEnchantmentGlintOverride() {
		return tag.contains("EnchantmentGlintOverride");
	}

	@Override
	public Boolean getEnchantmentGlintOverride() {
		if (!tag.contains("EnchantmentGlintOverride")) return null;
		return tag.getBoolean("EnchantmentGlintOverride");
	}

	@Override
	public void setEnchantmentGlintOverride(Boolean override) {
		if (override == null) {
			tag.remove("EnchantmentGlintOverride");
		} else {
			tag.putBoolean("EnchantmentGlintOverride", override);
		}
	}

	@Override
	public boolean isHideTooltip() {
		return tag.getBoolean("HideTooltip");
	}

	@Override
	public void setHideTooltip(boolean hideTooltip) {
		tag.putBoolean("HideTooltip", hideTooltip);
	}

	@Override
	public boolean isFireResistant() {
		return tag.getBoolean("FireResistant");
	}

	@Override
	public void setFireResistant(boolean fireResistant) {
		tag.putBoolean("FireResistant", fireResistant);
	}

	@Override
	public boolean hasMaxStackSize() {
		return tag.contains("MaxStackSize");
	}

	@Override
	public int getMaxStackSize() {
		return tag.contains("MaxStackSize") ? tag.getInt("MaxStackSize") : 64;
	}

	@Override
	public void setMaxStackSize(Integer maxStackSize) {
		if (maxStackSize == null) {
			tag.remove("MaxStackSize");
		} else {
			tag.putInt("MaxStackSize", maxStackSize);
		}
	}

	@Override
	public boolean hasRarity() {
		return tag.contains("Rarity");
	}

	@Override
	public ItemRarity getRarity() {
		if (!tag.contains("Rarity")) return null;
		try {
			return ItemRarity.valueOf(tag.getString("Rarity"));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void setRarity(ItemRarity rarity) {
		if (rarity == null) {
			tag.remove("Rarity");
		} else {
			tag.putString("Rarity", rarity.name());
		}
	}

	@Override
	public boolean hasFood() {
		return false;
	}

	@Override
	public org.bukkit.inventory.meta.components.FoodComponent getFood() {
		return null;
	}

	@Override
	public void setFood(org.bukkit.inventory.meta.components.FoodComponent food) {
	}

	@Override
	public boolean hasTool() {
		return false;
	}

	@Override
	public org.bukkit.inventory.meta.components.ToolComponent getTool() {
		return null;
	}

	@Override
	public void setTool(org.bukkit.inventory.meta.components.ToolComponent tool) {
	}

	@Override
	public boolean hasMaxDamage() {
		return false;
	}

	@Override
	public int getMaxDamage() {
		return 0;
	}

	@Override
	public void setMaxDamage(Integer maxDamage) {
	}

	@Override
	public void removeEnchantments() {
		if (tag.contains("ench")) {
			tag.remove("ench");
		}
	}

	private static Attribute resolveAttribute(String name) {
		if (name == null) return null;
		for (Attribute attr : Attribute.values()) {
			if (attr.getKey() != null && attr.getKey().toString().equals(name)) return attr;
		}
		if (name.startsWith("minecraft:")) {
			String key = name.substring("minecraft:".length());
			for (Attribute attr : Attribute.values()) {
				if (attr.getKey() != null && attr.getKey().getKey().equals(key)) return attr;
			}
		}
		if (name.startsWith("generic.") || name.startsWith("minecraft:generic.")) {
			String key = name.replace("generic.", "").replace("minecraft:generic.", "");
			for (Attribute attr : Attribute.values()) {
				if (attr.getKey() != null && attr.getKey().getKey().equals(key)) return attr;
			}
		}
		return null;
	}

	@Override
	public void setJukeboxPlayable(org.bukkit.inventory.meta.components.JukeboxPlayableComponent component) {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean hasJukeboxPlayable() {
		return false;
	}

	@Override
	public org.bukkit.inventory.meta.components.JukeboxPlayableComponent getJukeboxPlayable() {
		return null;
	}

	private static AttributeModifier nbtToModifier(CompoundTag entry) {
		try {
			UUID uuid = new UUID(entry.getLong("UUIDMost"), entry.getLong("UUIDLeast"));
			String name = entry.getString("Name");
			double amount = entry.getDouble("Amount");
			int op = entry.getInt("Operation");
			AttributeModifier.Operation operation = op < AttributeModifier.Operation.values().length
					? AttributeModifier.Operation.values()[op]
					: AttributeModifier.Operation.ADD_NUMBER;
			String slotStr = entry.getString("Slot");
			EquipmentSlot slot = null;
			if (slotStr != null && !slotStr.isEmpty()) {
				try { slot = EquipmentSlot.valueOf(slotStr.toUpperCase()); } catch (IllegalArgumentException ignored) {}
			}
			if (slot != null) {
				return new AttributeModifier(uuid, name, amount, operation, slot);
			}
			return new AttributeModifier(uuid, name, amount, operation);
		} catch (Exception e) {
			return null;
		}
	}
}
