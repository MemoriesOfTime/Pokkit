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

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataContainer;

import nl.rutgerkok.pokkit.Pokkit;

public class PokkitItemMeta implements ItemMeta, Damageable {

	/**
	 * Key under which the full NBT tag is stored during configuration
	 * serialization.
	 */
	private static final String SERIALIZED_TAG = "pokkit-nbt";

	protected CompoundTag tag;
	protected int damage;

	PokkitItemMeta(CompoundTag tag, int damage) {
		this.tag = Objects.requireNonNull(tag, "tag");
		this.damage = damage;
	}

	@Override
	public boolean addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		if (level > enchantment.getMaxLevel() && !ignoreLevelRestriction) {
			return false;
		}

		int nukkitEnchantmentId = PokkitEnchantment.toNukkit(enchantment);
		if (nukkitEnchantmentId < 0) {
			// Custom enchantments cannot be represented in Bedrock item data.
			return false;
		}

		short nukkitEnchantmentIdShort = (short) nukkitEnchantmentId;

		ListTag<CompoundTag> enchTag;
		if (tag.contains("ench")) {
			enchTag = tag.getList("ench", CompoundTag.class);
		} else {
			enchTag = new ListTag<>("ench");
			tag.putList(enchTag);
		}

		// Try to modify an existing enchantment first
		boolean modifiedATag = false;
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			if (entry.getShort("id") == nukkitEnchantmentIdShort) {
				// The add method actually replaces a tag
				enchTag.add(i, new CompoundTag()
						.putShort("id", nukkitEnchantmentIdShort)
						.putShort("lvl", level));
				modifiedATag = true;
				break;
			}
		}

		// Else, add this enchantment as a new enchantment
		if (!modifiedATag) {
			enchTag.add(new CompoundTag()
					.putShort("id", nukkitEnchantmentIdShort)
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
			return null;
		}
		CompoundTag displayTag = tag.getCompound("display");
		return displayTag.contains("Name") ? displayTag.getString("Name") : null;
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

		Map<Enchantment, Integer> map = new java.util.LinkedHashMap<>();

		ListTag<CompoundTag> enchTag = tag.getList("ench", CompoundTag.class);
		for (int i = 0; i < enchTag.size(); i++) {
			CompoundTag entry = enchTag.get(i);
			Enchantment enchantment = PokkitEnchantment.toBukkit(entry.getShort("id"));
			if (enchantment == null) {
				// Unknown (custom) enchantment, not representable in Bukkit.
				continue;
			}
			map.put(enchantment, (int) entry.getShort("lvl"));
		}

		return Collections.unmodifiableMap(map);
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
		return 31 * tag.hashCode() + damage;
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
		return material != null;
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
			case HIDE_ADDITIONAL_TOOLTIP: return 128;
			default: return 0;
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new java.util.LinkedHashMap<>();
		if (hasDisplayName()) data.put("display-name", getDisplayName());
		if (hasItemName()) data.put("item-name", getItemName());
		if (hasLore()) data.put("lore", getLore());
		if (damage > 0) data.put("damage", damage);
		if (hasEnchants()) {
			Map<String, Integer> enchants = new java.util.LinkedHashMap<>();
			for (Map.Entry<Enchantment, Integer> entry : getEnchants().entrySet()) {
				enchants.put(entry.getKey().getKey().toString(), entry.getValue());
			}
			data.put("enchants", enchants);
		}
		if (isUnbreakable()) data.put("unbreakable", true);
		if (hasCustomModelData()) data.put("custom-model-data", getCustomModelData());
		if (!getItemFlags().isEmpty()) {
			Set<String> flags = new java.util.LinkedHashSet<>();
			for (ItemFlag flag : getItemFlags()) {
				flags.add(flag.name());
			}
			data.put("ItemFlags", flags);
		}
		// Also store the entire NBT tag. Data without a Bukkit API equivalent
		// (persistent data, book pages, skull owners, attribute modifiers,
		// banner patterns, ...) would otherwise be lost when a plugin saves an
		// item to its configuration.
		try {
			data.put(SERIALIZED_TAG, java.util.Base64.getEncoder().encodeToString(NBTIO.write(tag)));
		} catch (java.io.IOException e) {
			throw new IllegalStateException("Could not serialize item meta", e);
		}
		return data;
	}

	/**
	 * Recreates item meta that was serialized with {@link #serialize()}. This
	 * method is called by Bukkit's configuration serialization system.
	 *
	 * @param args
	 *            The serialized data.
	 * @return The item meta.
	 */
	public static ItemMeta deserialize(Map<String, Object> args) {
		CompoundTag tag;
		Object encoded = args.get(SERIALIZED_TAG);
		if (encoded instanceof String base64 && !base64.isEmpty()) {
			try {
				tag = NBTIO.read(java.util.Base64.getDecoder().decode(base64));
			} catch (java.io.IOException | IllegalArgumentException e) {
				throw new IllegalArgumentException("Could not deserialize item meta", e);
			}
		} else {
			// Readable format, also used by maps written by older versions.
			tag = new CompoundTag();

			if (args.get("display-name") instanceof String displayName && !displayName.isEmpty()) {
				CompoundTag display = new CompoundTag().putString("Name", displayName);
				tag.putCompound("display", display);
			}
			if (args.get("item-name") instanceof String itemName && !itemName.isEmpty()) {
				tag.putString("item_name", itemName);
			}
			if (args.get("lore") instanceof List<?> lines && !lines.isEmpty()) {
				ListTag<StringTag> loreTag = new ListTag<>("Lore");
				for (Object line : lines) {
					loreTag.add(new StringTag("", String.valueOf(line)));
				}
				CompoundTag display = tag.contains("display") ? tag.getCompound("display")
						: new CompoundTag();
				display.putList(loreTag);
				tag.putCompound("display", display);
			}
			if (args.get("enchants") instanceof Map<?, ?> enchants) {
				ListTag<CompoundTag> enchTag = new ListTag<>("ench");
				for (Map.Entry<?, ?> entry : enchants.entrySet()) {
					if (!(entry.getValue() instanceof Number level)) {
						continue;
					}
					Enchantment enchantment = Enchantment
							.getByKey(org.bukkit.NamespacedKey.fromString(String.valueOf(entry.getKey())));
					int id = enchantment == null ? -1 : PokkitEnchantment.toNukkit(enchantment);
					if (id < 0) {
						continue;
					}
					enchTag.add(new CompoundTag().putShort("id", (short) id)
							.putShort("lvl", level.shortValue()));
				}
				if (!enchTag.isEmpty()) {
					tag.putList(enchTag);
				}
			}
			if (Boolean.TRUE.equals(args.get("unbreakable"))) {
				tag.putBoolean("Unbreakable", true);
			}
			if (args.get("custom-model-data") instanceof Number customModelData) {
				tag.putInt("CustomModelData", customModelData.intValue());
			}
			if (args.get("ItemFlags") instanceof Collection<?> flags) {
				int bits = 0;
				for (Object flag : flags) {
					try {
						bits |= getFlagBit(ItemFlag.valueOf(String.valueOf(flag)));
					} catch (IllegalArgumentException ignored) {
						// Unknown flag, skip it.
					}
				}
				if (bits != 0) {
					tag.putInt("HideFlags", bits);
				}
			}
		}

		int damage = args.get("damage") instanceof Number number ? number.intValue() : 0;
		return new PokkitItemMeta(tag, damage);
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
			UUID uuid = uuidOf(mod);
			CompoundTag attrTag = new CompoundTag()
					.putString("AttributeName", attr.getKey().toString())
					.putString("Name", mod.getName() != null ? mod.getName() : uuid.toString())
					.putDouble("Amount", mod.getAmount())
					.putInt("Operation", mod.getOperation().ordinal())
					.putLong("UUIDMost", uuid.getMostSignificantBits())
					.putLong("UUIDLeast", uuid.getLeastSignificantBits());
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
		UUID uuid = uuidOf(m);
		for (AttributeModifier existing : getAttributeModifiers(a)) {
			if (uuidOf(existing).equals(uuid)) {
				return false;
			}
		}

		ListTag<CompoundTag> list = tag.contains("AttributeModifiers")
				? tag.getList("AttributeModifiers", CompoundTag.class)
				: new ListTag<>("AttributeModifiers");
		CompoundTag attrTag = new CompoundTag()
				.putString("AttributeName", a.getKey().toString())
				.putString("Name", m.getName() != null ? m.getName() : uuid.toString())
				.putDouble("Amount", m.getAmount())
				.putInt("Operation", m.getOperation().ordinal())
				.putLong("UUIDMost", uuid.getMostSignificantBits())
				.putLong("UUIDLeast", uuid.getLeastSignificantBits());
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
			UUID uuid = uuidOf(m);
			if (entry.getString("AttributeName").equals(attrName)
					&& entry.getLong("UUIDMost") == uuid.getMostSignificantBits()
					&& entry.getLong("UUIDLeast") == uuid.getLeastSignificantBits()) {
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
			String entrySlot = list.get(i).getString("Slot");
			// Modifiers without a slot apply to every slot, so they have to be
			// removed too.
			if (entrySlot.isEmpty() || entrySlot.equals(slotName)) {
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
			String entrySlot = entry.getString("Slot");
			// Modifiers without a slot apply to every slot.
			if (!entrySlot.isEmpty() && !entrySlot.equals(slotName)) continue;
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
		// Component strings use the [key=value] syntax. Only the damage
		// component can be expressed reliably; everything else has no
		// Bedrock equivalent that maps one-to-one.
		return damage == 0 ? "[]" : "[minecraft:damage=" + damage + "]";
	}

	@Override
	public String getAsString() {
		// An NBT string, as documented by Bukkit. The damage value is stored
		// outside the tag, so it has to be merged in manually.
		String snbt = tag.toSNBT();
		if (damage == 0) {
			return snbt;
		}
		return "{Damage:" + damage + (snbt.length() > 2 ? "," + snbt.substring(1) : "}");
	}

	@Override
	public boolean hasItemName() {
		// The item name is a separate concept from the display name since
		// Minecraft 1.20.5: it is not styled with italics and cannot be
		// changed in an anvil. Bedrock has no equivalent, so the value is
		// stored under its own key.
		return tag.contains("item_name");
	}

	@Override
	public String getItemName() {
		return tag.contains("item_name") ? tag.getString("item_name") : null;
	}

	@Override
	public void setItemName(String name) {
		if (name == null || name.isEmpty()) {
			tag.remove("item_name");
		} else {
			tag.putString("item_name", name);
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
		// Only store the flag when it is set, so that an explicit "false" does
		// not turn an otherwise empty meta into a non-empty one.
		if (hideTooltip) {
			tag.putBoolean("HideTooltip", true);
		} else {
			tag.remove("HideTooltip");
		}
	}

	@Override
	public boolean isFireResistant() {
		return tag.getBoolean("FireResistant");
	}

	@Override
	public void setFireResistant(boolean fireResistant) {
		if (fireResistant) {
			tag.putBoolean("FireResistant", true);
		} else {
			tag.remove("FireResistant");
		}
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

	/**
	 * Gets the UUID of an attribute modifier. Modifiers created from a name
	 * (the modern constructor) have no UUID; in that case a stable UUID is
	 * derived from the name, because {@link AttributeModifier#getUniqueId()}
	 * throws for such modifiers on newer API versions.
	 *
	 * @param modifier
	 *            The modifier.
	 * @return The UUID, never null.
	 */
	private static UUID uuidOf(AttributeModifier modifier) {
		try {
			UUID uuid = modifier.getUniqueId();
			if (uuid != null) {
				return uuid;
			}
		} catch (RuntimeException ignored) {
			// Name-based modifier, fall through.
		}
		String name = modifier.getName();
		return UUID.nameUUIDFromBytes((name == null ? "" : name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
