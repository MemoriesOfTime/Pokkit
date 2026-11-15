package nl.rutgerkok.pokkit.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import nl.rutgerkok.pokkit.enchantment.PokkitEnchantment;
import nl.rutgerkok.pokkit.inventory.custom.PokkitCustomInventory;
import nl.rutgerkok.pokkit.item.ItemMap;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

public class ItemTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
		// The server normally initializes these registries on startup; without
		// them Item.get(id) returns an untyped Item, so armour would not be
		// recognized as armour and enchantment checks would all fail.
		cn.nukkit.block.Block.init();
		try {
			cn.nukkit.item.RuntimeItems.init();
		} catch (IllegalStateException alreadyInitialized) {
			// Another test class in the same JVM already initialized them.
		}
		cn.nukkit.item.Item.init();
		cn.nukkit.item.enchantment.Enchantment.init();
		PokkitEnchantment.registerNukkitEnchantmentsInBukkit();
	}

	@Test
	public void testBlockItemsCanBeConverted() {
		for (Material material : new Material[] { Material.STONE, Material.COBBLESTONE, Material.GLASS,
				Material.OAK_PLANKS, Material.WHITE_WOOL, Material.CRIMSON_PLANKS, Material.ROOTED_DIRT }) {
			Item nukkit = PokkitItemStack.toNukkitCopy(new ItemStack(material));
			assertFalse(material + " must not convert to air", nukkit.getId() == Item.AIR);
			assertEquals(material, PokkitItemStack.toBukkitCopy(nukkit).getType());
		}
	}

	@Test
	public void testItemVariants() {
		Item redDye = ItemMap.fromBukkit(Material.RED_DYE);
		assertEquals(ItemID.DYE, redDye.getId());
		assertEquals(1, redDye.getDamage());

		Item boneMeal = ItemMap.fromBukkit(Material.BONE_MEAL);
		assertEquals(ItemID.DYE, boneMeal.getId());
		assertEquals(15, boneMeal.getDamage());

		assertEquals(Material.BONE_MEAL, ItemMap.fromNukkitOrNull(Item.get(ItemID.DYE, 15)));
		assertEquals(Material.LAPIS_LAZULI, ItemMap.fromNukkitOrNull(Item.get(ItemID.DYE, 4)));

		Item playerHead = ItemMap.fromBukkit(Material.PLAYER_HEAD);
		assertEquals(ItemID.SKULL, playerHead.getId());
		assertEquals(3, playerHead.getDamage());
		assertEquals(Material.PLAYER_HEAD, ItemMap.fromNukkitOrNull(Item.get(ItemID.SKULL, 3)));
		assertEquals(Material.CREEPER_HEAD, ItemMap.fromNukkitOrNull(Item.get(ItemID.SKULL, 4)));
	}

	@Test
	public void testDurabilityRoundTrip() {
		Item pickaxe = Item.get(ItemID.IRON_PICKAXE);
		pickaxe.setDamage(37);

		ItemStack bukkit = PokkitItemStack.toBukkitCopy(pickaxe);
		assertEquals(Material.IRON_PICKAXE, bukkit.getType());
		assertEquals(37, ((Damageable) bukkit.getItemMeta()).getDamage());

		ItemStack source = new ItemStack(Material.IRON_PICKAXE);
		Damageable meta = (Damageable) source.getItemMeta();
		meta.setDamage(42);
		source.setItemMeta(meta);
		assertEquals(42, PokkitItemStack.toNukkitCopy(source).getDamage());
	}

	@Test
	public void testSetItemMetaKeepsDamage() {
		ItemStack stack = new ItemStack(Material.DIAMOND_AXE);
		Damageable meta = (Damageable) stack.getItemMeta();
		meta.setDamage(100);
		meta.setDisplayName("Chopper");
		stack.setItemMeta(meta);

		ItemMeta readBack = stack.getItemMeta();
		assertEquals(100, ((Damageable) readBack).getDamage());
		assertEquals("Chopper", readBack.getDisplayName());
		assertEquals(100, PokkitItemStack.toNukkitCopy(stack).getDamage());
		assertTrue(readBack.getAsString().contains("Damage:100"));
		assertEquals("[minecraft:damage=100]", readBack.getAsComponentString());
	}

	@Test
	public void testPotionTypeRoundTrip() {
		ItemStack potion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setBasePotionType(PotionType.STRONG_HEALING);
		potion.setItemMeta(meta);

		assertEquals(PotionType.STRONG_HEALING, ((PotionMeta) potion.getItemMeta()).getBasePotionType());

		Item nukkit = PokkitItemStack.toNukkitCopy(potion);
		assertEquals(22, nukkit.getDamage());

		ItemStack back = PokkitItemStack.toBukkitCopy(nukkit);
		assertEquals(Material.SPLASH_POTION, back.getType());
		assertEquals(PotionType.STRONG_HEALING, ((PotionMeta) back.getItemMeta()).getBasePotionType());
	}

	@Test
	public void testTippedArrowRoundTrip() {
		ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
		PotionMeta meta = (PotionMeta) arrow.getItemMeta();
		meta.setBasePotionType(PotionType.STRONG_HEALING);
		arrow.setItemMeta(meta);

		Item nukkit = PokkitItemStack.toNukkitCopy(arrow);
		// Nukkit stores tipped arrows as arrow with damage = potion id + 1.
		assertEquals(ItemID.ARROW, nukkit.getId());
		assertEquals(23, nukkit.getDamage());

		ItemStack back = PokkitItemStack.toBukkitCopy(nukkit);
		assertEquals(Material.TIPPED_ARROW, back.getType());
		assertEquals(PotionType.STRONG_HEALING, ((PotionMeta) back.getItemMeta()).getBasePotionType());
	}

	@Test
	public void testMetaTypes() {
		assertTrue(Bukkit.getItemFactory().getItemMeta(Material.CHEST) instanceof BlockStateMeta);
		assertFalse(Bukkit.getItemFactory().getItemMeta(Material.CHEST) instanceof SkullMeta);
		assertFalse(Bukkit.getItemFactory().getItemMeta(Material.FURNACE) instanceof SkullMeta);
		assertFalse(Bukkit.getItemFactory().getItemMeta(Material.WHITE_BANNER) instanceof SkullMeta);
		assertTrue(Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD) instanceof SkullMeta);
	}

	@Test
	public void testUnknownEnchantmentDoesNotCrash() {
		Item sword = Item.get(ItemID.DIAMOND_SWORD);
		CompoundTag tag = new CompoundTag();
		ListTag<CompoundTag> ench = new ListTag<>("ench");
		ench.add(new CompoundTag().putShort("id", 200).putShort("lvl", 3));
		tag.putList(ench);
		sword.setNamedTag(tag);

		ItemStack stack = PokkitItemStack.toBukkitCopy(sword);
		Map<Enchantment, Integer> enchants = stack.getItemMeta().getEnchants();
		assertTrue(enchants.isEmpty());
	}

	@Test
	public void testEnchantmentProperties() {
		assertEquals(1, Enchantment.MENDING.getMaxLevel());
		assertEquals(3, Enchantment.FORTUNE.getMaxLevel());
		assertEquals(4, Enchantment.PROTECTION.getMaxLevel());
		assertTrue(Enchantment.MENDING.isTreasure());
		assertTrue(Enchantment.BINDING_CURSE.isCursed());
		assertEquals(3, Enchantment.SWEEPING_EDGE.getMaxLevel());
		assertTrue(Enchantment.SHARPNESS.conflictsWith(Enchantment.SMITE));
		assertFalse(Enchantment.PROTECTION.canEnchantItem(new ItemStack(Material.DIAMOND_SWORD)));
	}

	@Test
	public void testLeatherColor() {
		ItemStack armor = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
		// CraftBukkit reports the default leather color for uncolored armor.
		assertEquals(Color.fromRGB(0xA06540), meta.getColor());
		meta.setColor(Color.fromRGB(0x123456));
		armor.setItemMeta(meta);

		Item nukkit = PokkitItemStack.toNukkitCopy(armor);
		ItemStack back = PokkitItemStack.toBukkitCopy(nukkit);
		assertEquals(Color.fromRGB(0x123456), ((LeatherArmorMeta) back.getItemMeta()).getColor());
	}

	@Test
	public void testItemFlagsAndDefaults() {
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);
		meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		assertTrue(meta.hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP));

		assertNull(meta.getDisplayName());
		assertNull(PokkitItemStack.toBukkitCopy(null));
	}

	@Test
	public void testBannerAndSpawnEggVariants() {
		Item banner = ItemMap.fromBukkit(Material.RED_BANNER);
		assertEquals(ItemID.BANNER, banner.getId());
		assertEquals(14, banner.getDamage());
		assertEquals(Material.RED_BANNER, PokkitItemStack.toBukkitCopy(banner).getType());

		Item cowEgg = ItemMap.fromBukkit(Material.COW_SPAWN_EGG);
		assertEquals(ItemID.SPAWN_EGG, cowEgg.getId());
		assertEquals(Material.COW_SPAWN_EGG, ItemMap.fromNukkitOrNull(cowEgg));
	}

	@Test
	public void testEnchantedBookMeta() {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		org.bukkit.inventory.meta.EnchantmentStorageMeta meta = (org.bukkit.inventory.meta.EnchantmentStorageMeta) book
				.getItemMeta();
		assertTrue(meta.addStoredEnchant(Enchantment.SHARPNESS, 3, true));
		book.setItemMeta(meta);

		Item nukkit = PokkitItemStack.toNukkitCopy(book);
		ItemStack back = PokkitItemStack.toBukkitCopy(nukkit);
		org.bukkit.inventory.meta.EnchantmentStorageMeta readBack = (org.bukkit.inventory.meta.EnchantmentStorageMeta) back
				.getItemMeta();
		assertTrue(readBack.hasStoredEnchant(Enchantment.SHARPNESS));
		assertEquals(3, readBack.getStoredEnchantLevel(Enchantment.SHARPNESS));
	}

	@Test
	public void testBookMeta() {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		org.bukkit.inventory.meta.BookMeta meta = (org.bukkit.inventory.meta.BookMeta) book.getItemMeta();
		assertTrue(meta.setTitle("Guide"));
		meta.setAuthor("Pokkit");
		meta.addPage("Line 1", "Line 2");
		meta.setGeneration(org.bukkit.inventory.meta.BookMeta.Generation.COPY_OF_ORIGINAL);
		book.setItemMeta(meta);

		ItemStack back = PokkitItemStack.toBukkitCopy(PokkitItemStack.toNukkitCopy(book));
		org.bukkit.inventory.meta.BookMeta readBack = (org.bukkit.inventory.meta.BookMeta) back.getItemMeta();
		assertEquals("Guide", readBack.getTitle());
		assertEquals("Pokkit", readBack.getAuthor());
		assertEquals(2, readBack.getPageCount());
		assertEquals("Line 1", readBack.getPage(1));
		assertEquals(org.bukkit.inventory.meta.BookMeta.Generation.COPY_OF_ORIGINAL, readBack.getGeneration());

		ItemStack quill = new ItemStack(Material.WRITABLE_BOOK);
		org.bukkit.inventory.meta.WritableBookMeta writable = (org.bukkit.inventory.meta.WritableBookMeta) quill
				.getItemMeta();
		writable.addPage("Draft");
		quill.setItemMeta(writable);
		org.bukkit.inventory.meta.WritableBookMeta writableBack = (org.bukkit.inventory.meta.WritableBookMeta) PokkitItemStack
				.toBukkitCopy(PokkitItemStack.toNukkitCopy(quill)).getItemMeta();
		assertEquals(1, writableBack.getPageCount());
		assertEquals("Draft", writableBack.getPage(1));
	}

	@Test
	public void testPersistentDataContainer() {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = stack.getItemMeta();
		org.bukkit.persistence.PersistentDataContainer container = meta.getPersistentDataContainer();

		org.bukkit.NamespacedKey stringKey = new org.bukkit.NamespacedKey("pokkit", "string");
		org.bukkit.NamespacedKey arrayKey = new org.bukkit.NamespacedKey("pokkit", "array");
		org.bukkit.NamespacedKey childKey = new org.bukkit.NamespacedKey("pokkit", "child");

		container.set(stringKey, org.bukkit.persistence.PersistentDataType.STRING, "hello");
		container.set(arrayKey, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY, new byte[] { 1, 2, 3 });

		org.bukkit.persistence.PersistentDataContainer child = container.getAdapterContext()
				.newPersistentDataContainer();
		child.set(stringKey, org.bukkit.persistence.PersistentDataType.STRING, "nested");
		container.set(childKey, org.bukkit.persistence.PersistentDataType.TAG_CONTAINER, child);

		stack.setItemMeta(meta);
		ItemMeta readBack = PokkitItemStack.toBukkitCopy(PokkitItemStack.toNukkitCopy(stack)).getItemMeta();

		org.bukkit.persistence.PersistentDataContainer readContainer = readBack.getPersistentDataContainer();
		assertEquals("hello", readContainer.get(stringKey, org.bukkit.persistence.PersistentDataType.STRING));
		org.junit.Assert.assertArrayEquals(new byte[] { 1, 2, 3 },
				readContainer.get(arrayKey, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY));

		org.bukkit.persistence.PersistentDataContainer readChild = readContainer.get(childKey,
				org.bukkit.persistence.PersistentDataType.TAG_CONTAINER);
		assertTrue(readChild != null);
		assertEquals("nested", readChild.get(stringKey, org.bukkit.persistence.PersistentDataType.STRING));

		// A value of another type must not be returned as an empty value.
		org.bukkit.NamespacedKey intKey = new org.bukkit.NamespacedKey("pokkit", "int");
		container.set(intKey, org.bukkit.persistence.PersistentDataType.INTEGER, 5);
		assertNull(container.get(intKey, org.bukkit.persistence.PersistentDataType.STRING));
		assertFalse(container.has(intKey, org.bukkit.persistence.PersistentDataType.STRING));
		assertTrue(container.has(intKey, org.bukkit.persistence.PersistentDataType.INTEGER));
		assertEquals(Integer.valueOf(5),
				container.get(intKey, org.bukkit.persistence.PersistentDataType.INTEGER));
	}

	@Test
	public void testAllMaterialsRoundTrip() {
		for (Material material : Material.values()) {
			if (material == Material.AIR || material.isLegacy()) {
				continue;
			}
			Item nukkit = ItemMap.fromBukkit(material);
			if (nukkit.getId() == Item.AIR) {
				continue;
			}
			assertEquals("Round trip failed for " + material, material, ItemMap.fromNukkitOrNull(nukkit));
		}
	}

	@Test
	public void testOverwriteNukkitKeepsVariant() {
		Item dye = Item.get(ItemID.DYE, 1);
		PokkitItemStack.overwriteNukkit(new ItemStack(Material.BONE_MEAL), dye);
		assertEquals(15, dye.getDamage());

		Item stone = Item.get(1, 0);
		PokkitItemStack.overwriteNukkit(new ItemStack(Material.GRANITE), stone);
		assertEquals(1, stone.getDamage());

		Item wool = Item.get(35, 0);
		PokkitItemStack.overwriteNukkit(new ItemStack(Material.RED_WOOL), wool);
		assertEquals(14, wool.getDamage());

		Item pickaxe = Item.get(ItemID.IRON_PICKAXE);
		ItemStack damaged = new ItemStack(Material.IRON_PICKAXE);
		Damageable meta = (Damageable) damaged.getItemMeta();
		meta.setDamage(50);
		damaged.setItemMeta(meta);
		PokkitItemStack.overwriteNukkit(damaged, pickaxe);
		assertEquals(50, pickaxe.getDamage());
	}

	@Test
	public void testEnchantItemAlwaysEnchants() {
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack result = Bukkit.getItemFactory().enchantItem(sword, 30, true);
		assertFalse("enchantItem must apply an enchantment", result.getItemMeta().getEnchants().isEmpty());
	}

	@Test
	public void testEnchantmentRegistryIteration() {
		int count = 0;
		for (Enchantment enchantment : org.bukkit.Registry.ENCHANTMENT) {
			assertTrue(enchantment != null);
			count++;
		}
		assertTrue(count >= 42);
		// The instance returned by the registry must be the same instance as
		// the constant, otherwise enchantment maps cannot be looked up.
		assertSame(Enchantment.MENDING,
				org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft("mending")));
	}

	@Test
	public void testItemTypeDoesNotRecurse() {
		// Material.isX() looks the material up in Registry.ITEM, so the
		// ItemType implementation must not call back into those methods.
		org.bukkit.inventory.ItemType itemType = Material.APPLE.asItemType();
		assertTrue(itemType != null);
		assertTrue(itemType.isEdible());
		assertFalse(itemType.isFuel());
		assertFalse(itemType.isRecord());
		assertFalse(itemType.isCompostable());
		assertTrue(Material.COAL.asItemType().isFuel());
		assertTrue(Material.MUSIC_DISC_13.asItemType().isRecord());
		assertEquals(0f, itemType.getCompostChance(), 0f);
		assertNull(itemType.getCraftingRemainingItem());
		assertNull(itemType.getCreativeCategory());
		assertTrue(itemType.getDefaultAttributeModifiers(org.bukkit.inventory.EquipmentSlot.HAND).isEmpty());

		assertTrue(Material.STONE.asItemType().hasBlockType());
		assertSame(Material.STONE.asBlockType(), Material.STONE.asItemType().getBlockType());
		// BlockMap knows this block even though the ItemType registry does
		// not contain it yet.
		assertTrue(nl.rutgerkok.pokkit.inventory.PokkitItemType.of("crimson_planks").hasBlockType());
		assertFalse(Material.DIAMOND_SWORD.asItemType().hasBlockType());
		assertEquals(64, Material.APPLE.asItemType().getMaxStackSize());
		assertEquals(1561, Material.DIAMOND_SWORD.asItemType().getMaxDurability());
	}

	@Test
	public void testItemEntityDoesNotRecurse() {
		nl.rutgerkok.pokkit.entity.PokkitItemEntity entity = new nl.rutgerkok.pokkit.entity.PokkitItemEntity(
				Item.get(1), new cn.nukkit.level.Location(0, 0, 0));
		assertEquals(1.0, entity.getBoundingBox().getMaxY(), 0.0);
		entity.setRotation(90, 0);
		assertEquals(Material.STONE, entity.getItemStack().getType());

		entity.setItemStack(new ItemStack(Material.DIRT));
		assertEquals(Material.DIRT, entity.getItemStack().getType());
	}

	@Test
	public void testFactoryHelpers() {
		assertEquals(Material.DIAMOND_SWORD,
				Bukkit.getItemFactory().createItemStack("minecraft:diamond_sword").getType());
		assertEquals(Material.COW_SPAWN_EGG, Bukkit.getItemFactory().getSpawnEgg(EntityType.COW));
	}

	@Test
	public void testIsApplicableHandlesNull() {
		// Both parameters are nullable in the ItemFactory contract.
		assertFalse(Bukkit.getItemFactory().isApplicable(null, Material.STONE));
		assertFalse(Bukkit.getItemFactory().isApplicable(null, (Material) null));
		assertFalse(Bukkit.getItemFactory().isApplicable(
				Bukkit.getItemFactory().getItemMeta(Material.STONE), (Material) null));
		assertFalse(Bukkit.getItemFactory().isApplicable(
				Bukkit.getItemFactory().getItemMeta(Material.STONE), (ItemStack) null));
		assertTrue(Bukkit.getItemFactory().isApplicable(
				Bukkit.getItemFactory().getItemMeta(Material.STONE), Material.STONE));
		assertFalse(Bukkit.getItemFactory().isApplicable(
				Bukkit.getItemFactory().getItemMeta(Material.CHEST), Material.STONE));
	}

	@Test
	public void testBookFirstPage() {
		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		org.bukkit.inventory.meta.WritableBookMeta meta = (org.bukkit.inventory.meta.WritableBookMeta) book
				.getItemMeta();
		// CraftBukkit pads with empty pages, so a plugin can start at page 1.
		meta.setPage(1, "first");
		assertEquals(1, meta.getPageCount());
		assertEquals("first", meta.getPage(1));
		meta.setPage(3, "third");
		assertEquals(3, meta.getPageCount());
		assertEquals("", meta.getPage(2));
		assertEquals("third", meta.getPage(3));
	}

	@Test
	public void testSpawnEggNameAliases() {
		assertEquals(Material.EVOKER_SPAWN_EGG,
				ItemMap.fromNukkitOrNull(ItemMap.fromBukkit(Material.EVOKER_SPAWN_EGG)));
		assertEquals(Material.TROPICAL_FISH_SPAWN_EGG,
				ItemMap.fromNukkitOrNull(ItemMap.fromBukkit(Material.TROPICAL_FISH_SPAWN_EGG)));
		assertEquals(Material.VILLAGER_SPAWN_EGG,
				ItemMap.fromNukkitOrNull(ItemMap.fromBukkit(Material.VILLAGER_SPAWN_EGG)));
	}

	@Test
	public void testItemsKeepTheirNukkitType() {
		// Item.get(id) only returns a typed item (armour, tool) once the
		// Nukkit registries are loaded, which the server does on startup.
		assertTrue(PokkitItemStack.toNukkitCopy(new ItemStack(Material.DIAMOND_CHESTPLATE)).isArmor());
		assertTrue(PokkitItemStack.toNukkitCopy(new ItemStack(Material.DIAMOND_PICKAXE)).isTool());
		assertTrue(Enchantment.PROTECTION.canEnchantItem(new ItemStack(Material.DIAMOND_CHESTPLATE)));
		assertTrue(Enchantment.SHARPNESS.canEnchantItem(new ItemStack(Material.DIAMOND_SWORD)));
		assertFalse(Enchantment.PROTECTION.canEnchantItem(new ItemStack(Material.DIAMOND_SWORD)));
	}

	@Test
	public void testStringIdentifiedItems() {
		// String items all share item id 255, so they must be resolved
		// through their namespace id.
		assertEquals(Material.COPPER_INGOT, ItemMap.fromNukkitOrNull(new cn.nukkit.item.ItemCopperIngot()));
	}

	@Test
	public void testUnknownItemDoesNotBecomeBlock() {
		// Block id 478 is crimson roots, but item id 478 does not exist. A
		// block table lookup for such an id would silently return that block.
		assertNull(ItemMap.fromNukkitOrNull(Item.get(478)));
	}

	@Test
	public void testDamagedItemHasItemMeta() {
		// CraftBukkit treats a damage value as item meta; without this
		// hasItemMeta() returns false and the damage is dropped when the stack
		// is serialized.
		ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta meta = pickaxe.getItemMeta();
		((Damageable) meta).setDamage(10);
		pickaxe.setItemMeta(meta);

		assertTrue(pickaxe.hasItemMeta());
		assertFalse(Bukkit.getItemFactory().equals(pickaxe.getItemMeta(), null));
		assertEquals(10, ((Damageable) pickaxe.getItemMeta()).getDamage());
		assertFalse(new ItemStack(Material.DIAMOND_PICKAXE).hasItemMeta());
	}

	@Test
	public void testItemNameIsSeparateFromDisplayName() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("Display");
		assertFalse("A display name must not count as an item name", meta.hasItemName());
		assertNull(meta.getItemName());

		meta.setItemName("Internal");
		assertEquals("Display", meta.getDisplayName());
		assertEquals("Internal", meta.getItemName());
		assertTrue(meta.getAsString().contains("item_name"));
	}

	@Test
	public void testAirHasNoItemMeta() {
		assertNull(Bukkit.getItemFactory().getItemMeta(Material.AIR));
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
		assertNull(Bukkit.getItemFactory().asMetaFor(meta, Material.AIR));
		assertFalse(Bukkit.getItemFactory().isApplicable(meta, Material.AIR));
	}

	@Test
	public void testMetaCanMoveBetweenSkullTypes() {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner("Notch");

		ItemStack skeleton = new ItemStack(Material.SKELETON_SKULL);
		assertTrue("Skull meta must be applicable to other skull items", skeleton.setItemMeta(skull));
		assertEquals("Notch", ((SkullMeta) skeleton.getItemMeta()).getOwner());
	}

	@Test
	public void testMetaCanMoveBetweenContainerTypes() {
		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta meta = chest.getItemMeta();
		meta.setDisplayName("Loot");

		ItemStack trapped = new ItemStack(Material.TRAPPED_CHEST);
		assertTrue(trapped.setItemMeta(meta));
		assertEquals("Loot", trapped.getItemMeta().getDisplayName());
	}

	@Test
	public void testLeatherMetaRejectedForOtherArmor() {
		ItemStack leather = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) leather.getItemMeta();
		meta.setColor(Color.fromRGB(0x112233));

		// CraftBukkit refuses to apply leather armor meta to other armor.
		assertFalse(new ItemStack(Material.DIAMOND_HELMET).setItemMeta(meta));
		assertTrue(leather.setItemMeta(meta));
	}

	@Test
	public void testSkullMetaIsNotBlockStateMeta() {
		ItemMeta skull = new ItemStack(Material.PLAYER_HEAD).getItemMeta();
		assertTrue(skull instanceof SkullMeta);
		assertFalse("SkullMeta does not extend BlockStateMeta in Bukkit", skull instanceof BlockStateMeta);

		ItemMeta shulker = new ItemStack(Material.SHULKER_BOX).getItemMeta();
		assertTrue("Shulker boxes carry block entity data", shulker instanceof BlockStateMeta);
	}

	@Test
	public void testAttributeModifierWithNameOnly() {
		// The modern AttributeModifier constructor takes a name instead of a
		// UUID; getUniqueId() throws for such modifiers.
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		AttributeModifier modifier = new AttributeModifier(new org.bukkit.NamespacedKey("test", "modifier"),
				5.0, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlotGroup.HAND);
		assertTrue(meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier));

		assertTrue(meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE).size() == 1);
		assertTrue(meta.getAttributeModifiers(EquipmentSlot.HAND).size() == 1);
		assertTrue(meta.removeAttributeModifier(EquipmentSlot.HAND));
		assertFalse(meta.hasAttributeModifiers());
	}

	@Test
	public void testItemStackSurvivesYamlRoundTrip() throws Exception {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 3);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("Excalibur");
		meta.setLore(java.util.Arrays.asList("line1", "line2"));
		meta.addEnchant(Enchantment.SHARPNESS, 5, true);
		meta.setUnbreakable(true);
		((Damageable) meta).setDamage(77);
		meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey("test", "key"),
				PersistentDataType.STRING, "value");
		stack.setItemMeta(meta);

		YamlConfiguration config = new YamlConfiguration();
		config.set("item", stack);
		YamlConfiguration loaded = new YamlConfiguration();
		loaded.loadFromString(config.saveToString());

		ItemStack back = loaded.getItemStack("item");
		assertNotNull(back);
		assertEquals(Material.DIAMOND_SWORD, back.getType());
		assertEquals(3, back.getAmount());
		ItemMeta read = back.getItemMeta();
		assertEquals("Excalibur", read.getDisplayName());
		assertEquals(java.util.Arrays.asList("line1", "line2"), read.getLore());
		assertEquals(5, read.getEnchantLevel(Enchantment.SHARPNESS));
		assertTrue(read.isUnbreakable());
		assertEquals(77, ((Damageable) read).getDamage());
		assertEquals("value", read.getPersistentDataContainer()
				.get(new org.bukkit.NamespacedKey("test", "key"), PersistentDataType.STRING));
	}

	@Test
	public void testBookAndSkullSurviveYamlRoundTrip() throws Exception {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle("My Book");
		bookMeta.setAuthor("Author");
		bookMeta.addPage("Page one", "Page two");
		book.setItemMeta(bookMeta);

		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner("Notch");
		skull.setItemMeta(skullMeta);

		YamlConfiguration config = new YamlConfiguration();
		config.set("book", book);
		config.set("skull", skull);
		YamlConfiguration loaded = new YamlConfiguration();
		loaded.loadFromString(config.saveToString());

		BookMeta readBook = (BookMeta) loaded.getItemStack("book").getItemMeta();
		assertEquals("My Book", readBook.getTitle());
		assertEquals("Author", readBook.getAuthor());
		assertEquals(2, readBook.getPageCount());
		assertEquals("Page one", readBook.getPage(1));

		SkullMeta readSkull = (SkullMeta) loaded.getItemStack("skull").getItemMeta();
		assertEquals("Notch", readSkull.getOwner());
	}

	@Test
	public void testZeroAmountIsPreserved() {
		// An empty Nukkit stack must not turn into a stack of one item.
		Item nukkit = Item.get(1, 0, 0);
		ItemStack bukkit = PokkitItemStack.toBukkitCopy(nukkit);
		assertNotNull(bukkit);
		assertEquals(0, bukkit.getAmount());
	}

	@Test
	public void testAddItemMergesIntoExistingStacks() {
		org.bukkit.inventory.Inventory inventory = PokkitCustomInventory.create(null,
				org.bukkit.event.inventory.InventoryType.CHEST, "test", 27);
		inventory.setItem(0, new ItemStack(Material.STONE, 60));

		assertTrue(inventory.addItem(new ItemStack(Material.STONE, 10)).isEmpty());
		// Merging used to modify a copy of the Nukkit stack, so the four items
		// that fit in the existing stack were silently lost.
		assertEquals(64, inventory.getItem(0).getAmount());
		assertEquals(6, inventory.getItem(1).getAmount());
	}

	@Test
	public void testSetContentsClearsRemainingSlots() {
		org.bukkit.inventory.Inventory inventory = PokkitCustomInventory.create(null,
				org.bukkit.event.inventory.InventoryType.CHEST, "test", 27);
		inventory.setItem(0, new ItemStack(Material.STONE));
		inventory.setItem(5, new ItemStack(Material.DIRT));

		inventory.setContents(new ItemStack[] { new ItemStack(Material.GOLD_INGOT, 2) });

		assertEquals(27, inventory.getContents().length);
		assertEquals(Material.GOLD_INGOT, inventory.getItem(0).getType());
		assertEquals(Material.AIR, inventory.getItem(5).getType());
		assertEquals(Material.AIR, inventory.getItem(26).getType());
	}

	@Test
	public void testPlayerInventorySlotLayout() {
		// PlayerInventory needs a holder for events, but reading the armour
		// slots does not: Nukkit stores them at 36..39 in the slot map.
		cn.nukkit.inventory.PlayerInventory nukkit = new cn.nukkit.inventory.PlayerInventory(null);
		nukkit.slots.put(36, Item.get(ItemID.LEATHER_HELMET));
		nukkit.slots.put(39, Item.get(ItemID.LEATHER_BOOTS));

		PokkitPlayerInventory inventory = new PokkitPlayerInventory(nukkit);

		// Bukkit counts the four armour slots and the off-hand slot.
		assertEquals(41, inventory.getSize());
		assertEquals(36, inventory.getStorageContents().length);

		// Bukkit slot 36 is the boots, 39 the helmet (the reverse of Nukkit).
		assertEquals(Material.LEATHER_BOOTS, inventory.getItem(36).getType());
		assertEquals(Material.LEATHER_HELMET, inventory.getItem(39).getType());

		ItemStack[] armor = inventory.getArmorContents();
		assertEquals(4, armor.length);
		assertEquals(Material.LEATHER_BOOTS, armor[0].getType());
		assertEquals(Material.LEATHER_HELMET, armor[3].getType());

		ItemStack[] contents = inventory.getContents();
		assertEquals(41, contents.length);
		assertEquals(Material.LEATHER_BOOTS, contents[36].getType());
		assertEquals(Material.LEATHER_HELMET, contents[39].getType());
	}

	@Test
	public void testSetArmorContentsUsesBukkitOrder() {
		// Setting fewer than four items used to shift the whole set, because
		// the Nukkit index was derived from the length of the Bukkit array: a
		// single item ended up in the helmet slot. Bukkit index 0 is always
		// the boots, so it must map to the last Nukkit slot.
		Item[] bootsOnly = PokkitPlayerInventory
				.toNukkitArmor(new ItemStack[] { new ItemStack(Material.DIAMOND_BOOTS) });
		assertEquals(4, bootsOnly.length);
		assertEquals(Material.DIAMOND_BOOTS, PokkitItemStack.toBukkitCopy(bootsOnly[3]).getType());
		assertEquals(Material.AIR, PokkitItemStack.toBukkitCopy(bootsOnly[0]).getType());

		Item[] two = PokkitPlayerInventory.toNukkitArmor(new ItemStack[] {
				new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.IRON_LEGGINGS) });
		assertEquals(Material.IRON_LEGGINGS, PokkitItemStack.toBukkitCopy(two[2]).getType());
		assertEquals(Material.DIAMOND_BOOTS, PokkitItemStack.toBukkitCopy(two[3]).getType());
		assertEquals(Material.AIR, PokkitItemStack.toBukkitCopy(two[0]).getType());
		assertEquals(Material.AIR, PokkitItemStack.toBukkitCopy(two[1]).getType());

		Item[] full = PokkitPlayerInventory.toNukkitArmor(new ItemStack[] {
				new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.IRON_LEGGINGS),
				new ItemStack(Material.GOLDEN_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET) });
		assertEquals(Material.LEATHER_HELMET, PokkitItemStack.toBukkitCopy(full[0]).getType());
		assertEquals(Material.GOLDEN_CHESTPLATE, PokkitItemStack.toBukkitCopy(full[1]).getType());
		assertEquals(Material.IRON_LEGGINGS, PokkitItemStack.toBukkitCopy(full[2]).getType());
		assertEquals(Material.DIAMOND_BOOTS, PokkitItemStack.toBukkitCopy(full[3]).getType());

		// An empty array clears every armour slot.
		for (Item empty : PokkitPlayerInventory.toNukkitArmor(new ItemStack[0])) {
			assertEquals(Material.AIR, PokkitItemStack.toBukkitCopy(empty).getType());
		}
	}

	@Test
	public void testUnsafeValuesUseProductionImplementation() {
		// The test server must not stub out UnsafeValues: doing so once hid a
		// crash in ItemStack.deserialize, because the stub resolved material
		// names while the production implementation threw.
		assertTrue(Bukkit.getUnsafe() instanceof nl.rutgerkok.pokkit.PokkitUnsafe);
	}

	@Test
	public void testMaterialNamesFromConfiguration() {
		nl.rutgerkok.pokkit.PokkitUnsafe unsafe = new nl.rutgerkok.pokkit.PokkitUnsafe();
		assertEquals(Material.DIAMOND_SWORD, unsafe.getMaterial("DIAMOND_SWORD", 1));
		assertEquals(Material.STONE, unsafe.getMaterial("minecraft:stone", 1));

		// Like CraftBukkit, names that do not exist in the current API are
		// rejected instead of being silently converted.
		try {
			unsafe.getMaterial("WOOL", 1);
			throw new AssertionError("Pre-1.13 names must not be accepted for version >= 0");
		} catch (IllegalArgumentException expected) {
		}
		try {
			unsafe.getMaterial("NOT_A_MATERIAL", 1);
			throw new AssertionError("Unknown materials must be rejected");
		} catch (IllegalArgumentException expected) {
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLegacyMaterialDataIsConverted() {
		// The data value of a legacy material selects the modern variant;
		// without conversion red wool silently became white wool.
		ItemStack redWool = new ItemStack(Material.LEGACY_WOOL, 1, (short) 14);
		assertEquals(Material.RED_WOOL, redWool.getType());

		Item nukkit = PokkitItemStack.toNukkitCopy(redWool);
		assertEquals(35, nukkit.getId());
		assertEquals(14, nukkit.getDamage());
		assertEquals(Material.RED_WOOL, PokkitItemStack.toBukkitCopy(nukkit).getType());

		nl.rutgerkok.pokkit.PokkitUnsafe unsafe = new nl.rutgerkok.pokkit.PokkitUnsafe();
		assertEquals(Material.RED_WOOL,
				unsafe.fromLegacy(new org.bukkit.material.MaterialData(Material.LEGACY_WOOL, (byte) 14), true));
		assertEquals(Material.IRON_SWORD, unsafe.fromLegacy(Material.LEGACY_IRON_SWORD));
		assertEquals(Material.DIAMOND_SWORD, unsafe.fromLegacy(Material.DIAMOND_SWORD));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDeprecatedItemDataDoesNotThrow() {
		// ItemStack#getData maps the material back to a legacy material. That
		// used to throw "Cannot get data class of Modern Material" for every
		// modern material.
		assertNotNull(new ItemStack(Material.RED_WOOL).getData());
		assertNotNull(new ItemStack(Material.GRASS_BLOCK).getData());
		assertNotNull(new ItemStack(Material.DIAMOND_SWORD).getData());
	}

	@Test
	public void testTranslationKeys() {
		assertEquals("item.minecraft.diamond_sword",
				new ItemStack(Material.DIAMOND_SWORD).getTranslationKey());
		assertEquals("item.minecraft.stone", new ItemStack(Material.STONE).getTranslationKey());
	}

	@Test
	public void testPotionDataConversion() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setBasePotionType(PotionType.LONG_SWIFTNESS);
		potion.setItemMeta(meta);

		// PotionData cannot be created from an extended type directly, so the
		// flags have to be split off.
		org.bukkit.potion.PotionData data = ((PotionMeta) potion.getItemMeta()).getBasePotionData();
		assertEquals(PotionType.SWIFTNESS, data.getType());
		assertTrue(data.isExtended());
		assertFalse(data.isUpgraded());

		// And the flags must be folded back into the type when setting.
		PotionMeta read = (PotionMeta) potion.getItemMeta();
		read.setBasePotionData(new org.bukkit.potion.PotionData(PotionType.SWIFTNESS, false, true));
		potion.setItemMeta(read);
		assertEquals(PotionType.STRONG_SWIFTNESS, ((PotionMeta) potion.getItemMeta()).getBasePotionType());
	}

	@Test
	public void testFalseFlagsDoNotCreateItemMeta() {
		ItemStack stack = new ItemStack(Material.STONE);
		ItemMeta meta = stack.getItemMeta();
		meta.setFireResistant(false);
		meta.setHideTooltip(false);
		// Setting a flag to false must not turn an empty meta into a
		// non-empty one, otherwise every item would be serialized with meta.
		assertTrue(Bukkit.getItemFactory().equals(meta, null));
	}

	@Test
	public void testAddItemRespectsBukkitMaxStackSize() {
		org.bukkit.inventory.Inventory inventory = PokkitCustomInventory.create(null,
				org.bukkit.event.inventory.InventoryType.CHEST, "test", 27);

		// Signs stack to 16 in Bukkit, but to 64 in Nukkit.
		assertTrue(inventory.addItem(new ItemStack(Material.OAK_SIGN, 20)).isEmpty());
		assertEquals(16, inventory.getItem(0).getAmount());
		assertEquals(4, inventory.getItem(1).getAmount());

		// Cake cannot stack at all in Bukkit.
		assertTrue(inventory.addItem(new ItemStack(Material.CAKE, 2)).isEmpty());
		assertEquals(1, inventory.getItem(2).getAmount());
		assertEquals(1, inventory.getItem(3).getAmount());
	}

	@Test
	public void testPotionSurvivesYamlRoundTrip() throws Exception {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setBasePotionType(PotionType.STRONG_SWIFTNESS);
		potion.setItemMeta(meta);

		ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
		PotionMeta arrowMeta = (PotionMeta) arrow.getItemMeta();
		arrowMeta.setBasePotionType(PotionType.LONG_POISON);
		arrow.setItemMeta(arrowMeta);

		YamlConfiguration config = new YamlConfiguration();
		config.set("potion", potion);
		config.set("arrow", arrow);
		YamlConfiguration loaded = new YamlConfiguration();
		loaded.loadFromString(config.saveToString());

		assertEquals(PotionType.STRONG_SWIFTNESS,
				((PotionMeta) loaded.getItemStack("potion").getItemMeta()).getBasePotionType());
		assertEquals(PotionType.LONG_POISON,
				((PotionMeta) loaded.getItemStack("arrow").getItemMeta()).getBasePotionType());
	}

	@Test
	public void testBucketsShareOneNukkitItem() {
		// Bedrock stores all buckets in item 325 and selects the content with
		// the damage value. Every bucket used to turn into air.
		assertVariant(Material.WATER_BUCKET, ItemID.BUCKET, 8);
		assertVariant(Material.LAVA_BUCKET, ItemID.BUCKET, 10);
		assertVariant(Material.MILK_BUCKET, ItemID.BUCKET, 1);
		assertVariant(Material.COD_BUCKET, ItemID.BUCKET, 2);
		assertVariant(Material.SALMON_BUCKET, ItemID.BUCKET, 3);
		assertVariant(Material.TROPICAL_FISH_BUCKET, ItemID.BUCKET, 4);
		assertVariant(Material.PUFFERFISH_BUCKET, ItemID.BUCKET, 5);
		assertVariant(Material.POWDER_SNOW_BUCKET, ItemID.BUCKET, 11);
		assertVariant(Material.AXOLOTL_BUCKET, ItemID.BUCKET, 12);
		assertVariant(Material.TADPOLE_BUCKET, ItemID.BUCKET, 13);

		// The empty bucket still maps to damage 0.
		assertVariant(Material.BUCKET, ItemID.BUCKET, 0);
	}

	@Test
	public void testBoatsUseTheDamageAsWoodType() {
		assertVariant(Material.OAK_BOAT, ItemID.BOAT, 0);
		assertVariant(Material.SPRUCE_BOAT, ItemID.BOAT, 1);
		assertVariant(Material.BIRCH_BOAT, ItemID.BOAT, 2);
		assertVariant(Material.JUNGLE_BOAT, ItemID.BOAT, 3);
		assertVariant(Material.ACACIA_BOAT, ItemID.BOAT, 4);
		assertVariant(Material.DARK_OAK_BOAT, ItemID.BOAT, 5);
		assertVariant(Material.MANGROVE_BOAT, ItemID.BOAT, 6);
		assertVariant(Material.BAMBOO_RAFT, ItemID.BOAT, 7);
		assertVariant(Material.CHERRY_BOAT, ItemID.BOAT, 8);
		assertVariant(Material.BAMBOO_CHEST_RAFT, ItemID.BAMBOO_CHEST_RAFT, 0);
	}

	@Test
	public void testBedsAndModernDyes() {
		assertVariant(Material.WHITE_BED, ItemID.BED, 0);
		assertVariant(Material.RED_BED, ItemID.BED, 14);
		assertVariant(Material.BLACK_BED, ItemID.BED, 15);

		// The classic dyes keep their old damage values, the four dyes that
		// were split off in 1.20 use 16..19.
		assertVariant(Material.INK_SAC, ItemID.DYE, 0);
		assertVariant(Material.RED_DYE, ItemID.DYE, 1);
		assertVariant(Material.BONE_MEAL, ItemID.DYE, 15);
		assertVariant(Material.BLACK_DYE, ItemID.DYE, 16);
		assertVariant(Material.BROWN_DYE, ItemID.DYE, 17);
		assertVariant(Material.BLUE_DYE, ItemID.DYE, 18);
		assertVariant(Material.WHITE_DYE, ItemID.DYE, 19);
		assertVariant(Material.GLOW_INK_SAC, ItemID.DYE, 20);
	}

	private static void assertVariant(Material material, int nukkitId, int damage) {
		Item nukkit = ItemMap.fromBukkit(material);
		assertEquals(material + " must map to item " + nukkitId, nukkitId, nukkit.getId());
		assertEquals(material + " must use damage " + damage, damage, nukkit.getDamage());
		assertEquals(material + " must survive the round trip", material, ItemMap.fromNukkitOrNull(nukkit));
	}
}
