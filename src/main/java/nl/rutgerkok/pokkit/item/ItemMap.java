package nl.rutgerkok.pokkit.item;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;
import cn.nukkit.utils.Identifier;
import nl.rutgerkok.pokkit.blockdata.BlockMap;

@SuppressWarnings("deprecation")
public class ItemMap {

	private static final int MAX_NUKKIT_ID = 1100;

	private static Material[] nukkitToBukkit = new Material[MAX_NUKKIT_ID];
	private static char[] bukkitToNukkit = new char[Material.values().length];

	/**
	 * Materials that share a single Nukkit item id and are distinguished by
	 * the damage value, for example all sixteen dye colors.
	 */
	private static final Map<Integer, Map<Integer, Material>> nukkitVariants = new HashMap<>();
	private static final Map<Material, int[]> bukkitVariants = new EnumMap<>(Material.class);

	/**
	 * Cache for the (damage value, material) pairs of spawn eggs. Filled
	 * lazily, because the entity registry of Nukkit is only available once the
	 * server has started.
	 */
	private static volatile Map<Integer, Material> spawnEggMaterials;
	private static volatile Map<Material, Integer> spawnEggDamages;

	static {
		registerTwoWay(ItemID.IRON_SHOVEL, Material.IRON_SHOVEL);
		registerTwoWay(ItemID.IRON_PICKAXE, Material.IRON_PICKAXE);
		registerTwoWay(ItemID.IRON_AXE, Material.IRON_AXE);
		registerTwoWay(ItemID.FLINT_AND_STEEL, Material.FLINT_AND_STEEL);
		registerTwoWay(ItemID.APPLE, Material.APPLE);
		registerTwoWay(ItemID.BOW, Material.BOW);
		registerTwoWay(ItemID.ARROW, Material.ARROW);
		// Tipped arrows share the arrow id: damage 0 is a normal arrow and
		// damage N + 1 is an arrow tipped with potion N.
		registerVariant(ItemID.ARROW, 1, Material.TIPPED_ARROW);
		registerTwoWay(ItemID.COAL, Material.COAL);
		registerTwoWay(ItemID.DIAMOND, Material.DIAMOND);
		registerTwoWay(ItemID.IRON_INGOT, Material.IRON_INGOT);
		registerTwoWay(ItemID.GOLD_INGOT, Material.GOLD_INGOT);
		registerTwoWay(ItemID.IRON_SWORD, Material.IRON_SWORD);
		registerTwoWay(ItemID.WOODEN_SWORD, Material.WOODEN_SWORD);
		registerTwoWay(ItemID.WOODEN_SHOVEL, Material.WOODEN_SHOVEL);
		registerTwoWay(ItemID.WOODEN_PICKAXE, Material.WOODEN_PICKAXE);
		registerTwoWay(ItemID.WOODEN_AXE, Material.WOODEN_AXE);
		registerTwoWay(ItemID.STONE_SWORD, Material.STONE_SWORD);
		registerTwoWay(ItemID.STONE_SHOVEL, Material.STONE_SHOVEL);
		registerTwoWay(ItemID.STONE_PICKAXE, Material.STONE_PICKAXE);
		registerTwoWay(ItemID.STONE_AXE, Material.STONE_AXE);
		registerTwoWay(ItemID.DIAMOND_SWORD, Material.DIAMOND_SWORD);
		registerTwoWay(ItemID.DIAMOND_SHOVEL, Material.DIAMOND_SHOVEL);
		registerTwoWay(ItemID.DIAMOND_PICKAXE, Material.DIAMOND_PICKAXE);
		registerTwoWay(ItemID.DIAMOND_AXE, Material.DIAMOND_AXE);
		registerTwoWay(ItemID.STICK, Material.STICK);
		registerTwoWay(ItemID.BOWL, Material.BOWL);
		registerTwoWay(ItemID.MUSHROOM_STEW, Material.MUSHROOM_STEW);
		registerTwoWay(ItemID.GOLDEN_SWORD, Material.GOLDEN_SWORD);
		registerTwoWay(ItemID.GOLDEN_SHOVEL, Material.GOLDEN_SHOVEL);
		registerTwoWay(ItemID.GOLDEN_PICKAXE, Material.GOLDEN_PICKAXE);
		registerTwoWay(ItemID.GOLDEN_AXE, Material.GOLDEN_AXE);
		registerTwoWay(ItemID.STRING, Material.STRING);
		registerTwoWay(ItemID.FEATHER, Material.FEATHER);
		registerTwoWay(ItemID.GUNPOWDER, Material.GUNPOWDER);
		registerTwoWay(ItemID.WOODEN_HOE, Material.WOODEN_HOE);
		registerTwoWay(ItemID.STONE_HOE, Material.STONE_HOE);
		registerTwoWay(ItemID.IRON_HOE, Material.IRON_HOE);
		registerTwoWay(ItemID.DIAMOND_HOE, Material.DIAMOND_HOE);
		registerTwoWay(ItemID.GOLDEN_HOE, Material.GOLDEN_HOE);
		registerTwoWay(ItemID.WHEAT_SEEDS, Material.WHEAT_SEEDS);
		registerTwoWay(ItemID.WHEAT, Material.WHEAT);
		registerTwoWay(ItemID.BREAD, Material.BREAD);
		registerTwoWay(ItemID.LEATHER_CAP, Material.LEATHER_HELMET);
		registerTwoWay(ItemID.LEATHER_TUNIC, Material.LEATHER_CHESTPLATE);
		registerTwoWay(ItemID.LEATHER_PANTS, Material.LEATHER_LEGGINGS);
		registerTwoWay(ItemID.LEATHER_BOOTS, Material.LEATHER_BOOTS);
		registerTwoWay(ItemID.CHAIN_HELMET, Material.CHAINMAIL_HELMET);
		registerTwoWay(ItemID.CHAIN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE);
		registerTwoWay(ItemID.CHAIN_LEGGINGS, Material.CHAINMAIL_LEGGINGS);
		registerTwoWay(ItemID.CHAIN_BOOTS, Material.CHAINMAIL_BOOTS);
		registerTwoWay(ItemID.IRON_HELMET, Material.IRON_HELMET);
		registerTwoWay(ItemID.IRON_CHESTPLATE, Material.IRON_CHESTPLATE);
		registerTwoWay(ItemID.IRON_LEGGINGS, Material.IRON_LEGGINGS);
		registerTwoWay(ItemID.IRON_BOOTS, Material.IRON_BOOTS);
		registerTwoWay(ItemID.DIAMOND_HELMET, Material.DIAMOND_HELMET);
		registerTwoWay(ItemID.DIAMOND_CHESTPLATE, Material.DIAMOND_CHESTPLATE);
		registerTwoWay(ItemID.DIAMOND_LEGGINGS, Material.DIAMOND_LEGGINGS);
		registerTwoWay(ItemID.DIAMOND_BOOTS, Material.DIAMOND_BOOTS);
		registerTwoWay(ItemID.GOLD_HELMET, Material.GOLDEN_HELMET);
		registerTwoWay(ItemID.GOLD_CHESTPLATE, Material.GOLDEN_CHESTPLATE);
		registerTwoWay(ItemID.GOLD_LEGGINGS, Material.GOLDEN_LEGGINGS);
		registerTwoWay(ItemID.GOLD_BOOTS, Material.GOLDEN_BOOTS);
		registerTwoWay(ItemID.FLINT, Material.FLINT);
		registerTwoWay(ItemID.RAW_PORKCHOP, Material.PORKCHOP);
		registerTwoWay(ItemID.COOKED_PORKCHOP, Material.COOKED_PORKCHOP);
		registerTwoWay(ItemID.PAINTING, Material.PAINTING);
		registerTwoWay(ItemID.GOLDEN_APPLE, Material.GOLDEN_APPLE);
		registerTwoWay(ItemID.SIGN, Material.OAK_SIGN);
		registerTwoWay(ItemID.WOODEN_DOOR, Material.OAK_DOOR);
		registerTwoWay(ItemID.BUCKET, Material.BUCKET);
		registerTwoWay(ItemID.MINECART, Material.MINECART);
		registerTwoWay(ItemID.SADDLE, Material.SADDLE);
		registerTwoWay(ItemID.IRON_DOOR, Material.IRON_DOOR);
		registerTwoWay(ItemID.REDSTONE, Material.REDSTONE);
		registerTwoWay(ItemID.SNOWBALL, Material.SNOWBALL);
		registerTwoWay(ItemID.BOAT, Material.OAK_BOAT);
		registerTwoWay(ItemID.LEATHER, Material.LEATHER);
		registerTwoWay(ItemID.KELP, Material.KELP);
		registerTwoWay(ItemID.BRICK, Material.BRICK);
		registerTwoWay(ItemID.CLAY, Material.CLAY);
		registerTwoWay(ItemID.SUGAR_CANE, Material.SUGAR_CANE);
		registerTwoWay(ItemID.PAPER, Material.PAPER);
		registerTwoWay(ItemID.BOOK, Material.BOOK);
		registerTwoWay(ItemID.SLIMEBALL, Material.SLIME_BALL);
		registerTwoWay(ItemID.MINECART_WITH_CHEST, Material.CHEST_MINECART);
		registerTwoWay(ItemID.EGG, Material.EGG);
		registerTwoWay(ItemID.COMPASS, Material.COMPASS);
		registerTwoWay(ItemID.FISHING_ROD, Material.FISHING_ROD);
		registerTwoWay(ItemID.CLOCK, Material.CLOCK);
		registerTwoWay(ItemID.GLOWSTONE_DUST, Material.GLOWSTONE_DUST);
		registerTwoWay(ItemID.RAW_FISH, Material.COD);
		registerTwoWay(ItemID.COOKED_FISH, Material.COOKED_COD);
		registerBukkitToNukkit(ItemID.DYE, Material.ORANGE_DYE);
		registerTwoWay(ItemID.BONE, Material.BONE);
		registerTwoWay(ItemID.SUGAR, Material.SUGAR);
		registerTwoWay(ItemID.CAKE, Material.CAKE);
		registerTwoWay(ItemID.BED, Material.RED_BED);
		registerTwoWay(ItemID.REPEATER, Material.REPEATER);
		registerTwoWay(ItemID.COOKIE, Material.COOKIE);
		registerTwoWay(ItemID.MAP, Material.FILLED_MAP);
		registerTwoWay(ItemID.SHEARS, Material.SHEARS);
		// Material.MELON is the melon block; Nukkit uses the same id for the
		// melon slice item, so the block is resolved through BlockMap.
		registerTwoWay(ItemID.MELON_SLICE, Material.MELON_SLICE);
		registerTwoWay(ItemID.PUMPKIN_SEEDS, Material.PUMPKIN_SEEDS);
		registerTwoWay(ItemID.MELON_SEEDS, Material.MELON_SEEDS);
		registerTwoWay(ItemID.RAW_BEEF, Material.BEEF);
		registerTwoWay(ItemID.COOKED_BEEF, Material.COOKED_BEEF);
		registerTwoWay(ItemID.RAW_CHICKEN, Material.CHICKEN);
		registerTwoWay(ItemID.COOKED_CHICKEN, Material.COOKED_CHICKEN);
		registerTwoWay(ItemID.ROTTEN_FLESH, Material.ROTTEN_FLESH);
		registerTwoWay(ItemID.ENDER_PEARL, Material.ENDER_PEARL);
		registerTwoWay(ItemID.BLAZE_ROD, Material.BLAZE_ROD);
		registerTwoWay(ItemID.GHAST_TEAR, Material.GHAST_TEAR);
		registerTwoWay(ItemID.GOLD_NUGGET, Material.GOLD_NUGGET);
		registerTwoWay(ItemID.NETHER_WART, Material.NETHER_WART);
		registerTwoWay(ItemID.POTION, Material.POTION);
		registerTwoWay(ItemID.GLASS_BOTTLE, Material.GLASS_BOTTLE);
		registerTwoWay(ItemID.SPIDER_EYE, Material.SPIDER_EYE);
		registerTwoWay(ItemID.FERMENTED_SPIDER_EYE, Material.FERMENTED_SPIDER_EYE);
		registerTwoWay(ItemID.BLAZE_POWDER, Material.BLAZE_POWDER);
		registerTwoWay(ItemID.MAGMA_CREAM, Material.MAGMA_CREAM);
		registerTwoWay(ItemID.BREWING_STAND, Material.BREWING_STAND);
		registerTwoWay(ItemID.CAULDRON, Material.CAULDRON);
		registerTwoWay(ItemID.ENDER_EYE, Material.ENDER_EYE);
		registerTwoWay(ItemID.GLISTERING_MELON, Material.GLISTERING_MELON_SLICE);
		// Spawn eggs are resolved through the Nukkit entity registry, see
		// spawnEggMaterial(int). This entry is only a fallback.
		registerBukkitToNukkit(ItemID.SPAWN_EGG, Material.PIG_SPAWN_EGG);
		registerTwoWay(ItemID.EXPERIENCE_BOTTLE, Material.EXPERIENCE_BOTTLE);
		registerTwoWay(ItemID.FIRE_CHARGE, Material.FIRE_CHARGE);
		registerTwoWay(ItemID.BOOK_AND_QUILL, Material.WRITABLE_BOOK);
		registerTwoWay(ItemID.WRITTEN_BOOK, Material.WRITTEN_BOOK);
		registerTwoWay(ItemID.EMERALD, Material.EMERALD);
		registerTwoWay(ItemID.ITEM_FRAME, Material.ITEM_FRAME);
		registerTwoWay(ItemID.FLOWER_POT, Material.FLOWER_POT);
		registerTwoWay(ItemID.CARROT, Material.CARROT);
		// CARROTS/POTATOES are blocks; the ids collide with the carrot and
		// potato items, so the blocks are resolved through BlockMap.
		registerTwoWay(ItemID.POTATO, Material.POTATO);
		registerTwoWay(ItemID.BAKED_POTATO, Material.BAKED_POTATO);
		registerTwoWay(ItemID.POISONOUS_POTATO, Material.POISONOUS_POTATO);
		registerTwoWay(ItemID.EMPTY_MAP, Material.MAP);
		registerTwoWay(ItemID.GOLDEN_CARROT, Material.GOLDEN_CARROT);
		registerTwoWay(ItemID.SKULL, Material.SKELETON_SKULL);
		registerTwoWay(ItemID.CARROT_ON_A_STICK, Material.CARROT_ON_A_STICK);
		registerTwoWay(ItemID.NETHER_STAR, Material.NETHER_STAR);
		registerTwoWay(ItemID.PUMPKIN_PIE, Material.PUMPKIN_PIE);
		registerTwoWay(ItemID.FIREWORKS, Material.FIREWORK_ROCKET);
		registerTwoWay(ItemID.FIREWORKSCHARGE, Material.FIREWORK_STAR);
		registerTwoWay(ItemID.ENCHANTED_BOOK, Material.ENCHANTED_BOOK);
		registerTwoWay(ItemID.COMPARATOR, Material.COMPARATOR);
		registerTwoWay(ItemID.NETHER_BRICK, Material.NETHER_BRICK);
		registerTwoWay(ItemID.QUARTZ, Material.QUARTZ);
		registerTwoWay(ItemID.MINECART_WITH_TNT, Material.TNT_MINECART);
		registerTwoWay(ItemID.MINECART_WITH_HOPPER, Material.HOPPER_MINECART);
		registerTwoWay(ItemID.PRISMARINE_SHARD, Material.PRISMARINE_SHARD);
		registerTwoWay(ItemID.HOPPER, Material.HOPPER);
		registerTwoWay(ItemID.RAW_RABBIT, Material.RABBIT);
		registerTwoWay(ItemID.COOKED_RABBIT, Material.COOKED_RABBIT);
		registerTwoWay(ItemID.RABBIT_STEW, Material.RABBIT_STEW);
		registerTwoWay(ItemID.RABBIT_FOOT, Material.RABBIT_FOOT);
		registerTwoWay(ItemID.RABBIT_HIDE, Material.RABBIT_HIDE);
		registerTwoWay(ItemID.IRON_HORSE_ARMOR, Material.IRON_HORSE_ARMOR);
		registerTwoWay(ItemID.GOLD_HORSE_ARMOR, Material.GOLDEN_HORSE_ARMOR);
		registerTwoWay(ItemID.DIAMOND_HORSE_ARMOR, Material.DIAMOND_HORSE_ARMOR);
		registerTwoWay(ItemID.LEAD, Material.LEAD);
		registerTwoWay(ItemID.NAME_TAG, Material.NAME_TAG);
		registerTwoWay(ItemID.PRISMARINE_CRYSTALS, Material.PRISMARINE_CRYSTALS);
		registerTwoWay(ItemID.RAW_MUTTON, Material.MUTTON);
		registerTwoWay(ItemID.COOKED_MUTTON, Material.COOKED_MUTTON);
		registerTwoWay(ItemID.END_CRYSTAL, Material.END_CRYSTAL);
		registerTwoWay(ItemID.SPRUCE_DOOR, Material.SPRUCE_DOOR);
		registerTwoWay(ItemID.BIRCH_DOOR, Material.BIRCH_DOOR);
		registerTwoWay(ItemID.JUNGLE_DOOR, Material.JUNGLE_DOOR);
		registerTwoWay(ItemID.ACACIA_DOOR, Material.ACACIA_DOOR);
		registerTwoWay(ItemID.DARK_OAK_DOOR, Material.DARK_OAK_DOOR);
		registerTwoWay(ItemID.CHORUS_FRUIT, Material.CHORUS_FRUIT);
		registerTwoWay(ItemID.POPPED_CHORUS_FRUIT, Material.POPPED_CHORUS_FRUIT);
		registerTwoWay(ItemID.DRAGON_BREATH, Material.DRAGON_BREATH);
		registerTwoWay(ItemID.SPLASH_POTION, Material.SPLASH_POTION);
		registerTwoWay(ItemID.LINGERING_POTION, Material.LINGERING_POTION);
		registerTwoWay(ItemID.ELYTRA, Material.ELYTRA);
		registerTwoWay(ItemID.SHULKER_SHELL, Material.SHULKER_SHELL);
		registerTwoWay(ItemID.BEETROOT, Material.BEETROOT);
		registerTwoWay(ItemID.BEETROOT_SEEDS, Material.BEETROOT_SEEDS);
		registerTwoWay(ItemID.BEETROOT_SOUP, Material.BEETROOT_SOUP);
		registerTwoWay(ItemID.RAW_SALMON, Material.SALMON);
		registerTwoWay(ItemID.CLOWNFISH, Material.TROPICAL_FISH);
		registerTwoWay(ItemID.PUFFERFISH, Material.PUFFERFISH);
		registerTwoWay(ItemID.COOKED_SALMON, Material.COOKED_SALMON);
		registerTwoWay(ItemID.GOLDEN_APPLE_ENCHANTED, Material.ENCHANTED_GOLDEN_APPLE);
		registerTwoWay(ItemID.RECORD_13, Material.MUSIC_DISC_13);
		registerTwoWay(ItemID.RECORD_CAT, Material.MUSIC_DISC_CAT);
		registerTwoWay(ItemID.RECORD_BLOCKS, Material.MUSIC_DISC_BLOCKS);
		registerTwoWay(ItemID.RECORD_CHIRP, Material.MUSIC_DISC_CHIRP);
		registerTwoWay(ItemID.RECORD_FAR, Material.MUSIC_DISC_FAR);
		registerTwoWay(ItemID.RECORD_MALL, Material.MUSIC_DISC_MALL);
		registerTwoWay(ItemID.RECORD_MELLOHI, Material.MUSIC_DISC_MELLOHI);
		registerTwoWay(ItemID.RECORD_STAL, Material.MUSIC_DISC_STAL);
		registerTwoWay(ItemID.RECORD_STRAD, Material.MUSIC_DISC_STRAD);
		registerTwoWay(ItemID.RECORD_WARD, Material.MUSIC_DISC_WARD);
		registerTwoWay(ItemID.RECORD_11, Material.MUSIC_DISC_11);
		registerTwoWay(ItemID.RECORD_WAIT, Material.MUSIC_DISC_WAIT);
		registerTwoWay(ItemID.TRIDENT, Material.TRIDENT);
		registerTwoWay(ItemID.TURTLE_SHELL, Material.TURTLE_HELMET);
		registerTwoWay(ItemID.DRIED_KELP, Material.DRIED_KELP);
		registerTwoWay(ItemID.ARMOR_STAND, Material.ARMOR_STAND);
		registerTwoWay(ItemID.COMMAND_BLOCK_MINECART, Material.COMMAND_BLOCK_MINECART);
		registerTwoWay(ItemID.TOTEM, Material.TOTEM_OF_UNDYING);
		registerTwoWay(ItemID.IRON_NUGGET, Material.IRON_NUGGET);
		registerTwoWay(ItemID.NAUTILUS_SHELL, Material.NAUTILUS_SHELL);
		registerTwoWay(ItemID.HEART_OF_THE_SEA, Material.HEART_OF_THE_SEA);
		registerTwoWay(ItemID.SCUTE, Material.TURTLE_SCUTE);
		registerTwoWay(ItemID.PHANTOM_MEMBRANE, Material.PHANTOM_MEMBRANE);
		registerTwoWay(ItemID.SHIELD, Material.SHIELD);

		// Unsupported items
		registerTwoWay(ItemID.LEATHER_HORSE_ARMOR, Material.LEATHER_HORSE_ARMOR);
		registerTwoWay(ItemID.CROSSBOW, Material.CROSSBOW);
		registerTwoWay(ItemID.SUSPICIOUS_STEW, Material.SUSPICIOUS_STEW);
		registerTwoWay(ItemID.HONEYCOMB, Material.HONEYCOMB);
		registerTwoWay(ItemID.HONEY_BOTTLE, Material.HONEY_BOTTLE);

		// New items (1.17+)
		registerTwoWay(ItemID.SWEET_BERRIES, Material.SWEET_BERRIES);
		registerTwoWay(ItemID.GLOW_BERRIES, Material.GLOW_BERRIES);
		registerTwoWay(ItemID.CAMPFIRE, Material.CAMPFIRE);
		registerTwoWay(ItemID.SOUL_CAMPFIRE, Material.SOUL_CAMPFIRE);
		registerTwoWay(ItemID.CHAIN, Material.IRON_CHAIN);
		registerTwoWay(ItemID.AMETHYST_SHARD, Material.AMETHYST_SHARD);
		registerTwoWay(ItemID.SPYGLASS, Material.SPYGLASS);
		registerTwoWay(ItemID.WARPED_FUNGUS_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK);
		registerTwoWay(ItemID.GLOW_ITEM_FRAME, Material.GLOW_ITEM_FRAME);

		// Netherite
		registerTwoWay(ItemID.NETHERITE_INGOT, Material.NETHERITE_INGOT);
		registerTwoWay(ItemID.NETHERITE_SWORD, Material.NETHERITE_SWORD);
		registerTwoWay(ItemID.NETHERITE_SHOVEL, Material.NETHERITE_SHOVEL);
		registerTwoWay(ItemID.NETHERITE_PICKAXE, Material.NETHERITE_PICKAXE);
		registerTwoWay(ItemID.NETHERITE_AXE, Material.NETHERITE_AXE);
		registerTwoWay(ItemID.NETHERITE_HOE, Material.NETHERITE_HOE);
		registerTwoWay(ItemID.NETHERITE_HELMET, Material.NETHERITE_HELMET);
		registerTwoWay(ItemID.NETHERITE_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
		registerTwoWay(ItemID.NETHERITE_LEGGINGS, Material.NETHERITE_LEGGINGS);
		registerTwoWay(ItemID.NETHERITE_BOOTS, Material.NETHERITE_BOOTS);
		registerTwoWay(ItemID.NETHERITE_SCRAP, Material.NETHERITE_SCRAP);

		// New signs and doors (crimson/warped)
		registerTwoWay(ItemID.CRIMSON_SIGN, Material.CRIMSON_SIGN);
		registerTwoWay(ItemID.WARPED_SIGN, Material.WARPED_SIGN);
		registerTwoWay(ItemID.CRIMSON_DOOR, Material.CRIMSON_DOOR);
		registerTwoWay(ItemID.WARPED_DOOR, Material.WARPED_DOOR);
		registerTwoWay(ItemID.SPRUCE_SIGN, Material.SPRUCE_SIGN);
		registerTwoWay(ItemID.BIRCH_SIGN, Material.BIRCH_SIGN);
		registerTwoWay(ItemID.JUNGLE_SIGN, Material.JUNGLE_SIGN);
		registerTwoWay(ItemID.ACACIA_SIGN, Material.ACACIA_SIGN);
		registerTwoWay(ItemID.DARKOAK_SIGN, Material.DARK_OAK_SIGN);
		registerTwoWay(ItemID.MANGROVE_SIGN, Material.MANGROVE_SIGN);
		registerTwoWay(ItemID.BAMBOO_SIGN, Material.BAMBOO_SIGN);
		registerTwoWay(ItemID.CHERRY_SIGN, Material.CHERRY_SIGN);

		// Chest boats
		registerTwoWay(ItemID.OAK_CHEST_BOAT, Material.OAK_CHEST_BOAT);
		registerTwoWay(ItemID.BIRCH_CHEST_BOAT, Material.BIRCH_CHEST_BOAT);
		registerTwoWay(ItemID.JUNGLE_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT);
		registerTwoWay(ItemID.SPRUCE_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT);
		registerTwoWay(ItemID.ACACIA_CHEST_BOAT, Material.ACACIA_CHEST_BOAT);
		registerTwoWay(ItemID.DARK_OAK_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT);
		registerTwoWay(ItemID.MANGROVE_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT);
		registerTwoWay(ItemID.CHERRY_CHEST_BOAT, Material.CHERRY_CHEST_BOAT);

		// New music discs
		registerTwoWay(ItemID.RECORD_5, Material.MUSIC_DISC_5);
		registerTwoWay(ItemID.DISC_FRAGMENT_5, Material.DISC_FRAGMENT_5);
		registerTwoWay(ItemID.RECORD_RELIC, Material.MUSIC_DISC_RELIC);
		registerTwoWay(ItemID.RECORD_PIGSTEP, Material.MUSIC_DISC_PIGSTEP);
		registerTwoWay(ItemID.RECORD_OTHERSIDE, Material.MUSIC_DISC_OTHERSIDE);

		// Nether Sprouts
		registerTwoWay(ItemID.NETHER_SPROUTS, Material.NETHER_SPROUTS);

		// Legacy items
		registerBukkitToNukkit(ItemID.IRON_SHOVEL, Material.LEGACY_IRON_SPADE);
		registerBukkitToNukkit(ItemID.IRON_PICKAXE, Material.LEGACY_IRON_PICKAXE);
		registerBukkitToNukkit(ItemID.IRON_AXE, Material.LEGACY_IRON_AXE);
		registerBukkitToNukkit(ItemID.FLINT_AND_STEEL, Material.LEGACY_FLINT_AND_STEEL);
		registerBukkitToNukkit(ItemID.APPLE, Material.LEGACY_APPLE);
		registerBukkitToNukkit(ItemID.BOW, Material.LEGACY_BOW);
		registerBukkitToNukkit(ItemID.ARROW, Material.LEGACY_ARROW);
		registerBukkitToNukkit(ItemID.COAL, Material.LEGACY_COAL);
		registerBukkitToNukkit(ItemID.DIAMOND, Material.LEGACY_DIAMOND);
		registerBukkitToNukkit(ItemID.IRON_INGOT, Material.LEGACY_IRON_INGOT);
		registerBukkitToNukkit(ItemID.GOLD_INGOT, Material.LEGACY_GOLD_INGOT);
		registerBukkitToNukkit(ItemID.IRON_SWORD, Material.LEGACY_IRON_SWORD);
		registerBukkitToNukkit(ItemID.WOODEN_SWORD, Material.LEGACY_WOOD_SWORD);
		registerBukkitToNukkit(ItemID.WOODEN_SHOVEL, Material.LEGACY_WOOD_SPADE);
		registerBukkitToNukkit(ItemID.WOODEN_PICKAXE, Material.LEGACY_WOOD_PICKAXE);
		registerBukkitToNukkit(ItemID.WOODEN_AXE, Material.LEGACY_WOOD_AXE);
		registerBukkitToNukkit(ItemID.STONE_SWORD, Material.LEGACY_STONE_SWORD);
		registerBukkitToNukkit(ItemID.STONE_SHOVEL, Material.LEGACY_STONE_SPADE);
		registerBukkitToNukkit(ItemID.STONE_PICKAXE, Material.LEGACY_STONE_PICKAXE);
		registerBukkitToNukkit(ItemID.STONE_AXE, Material.LEGACY_STONE_AXE);
		registerBukkitToNukkit(ItemID.DIAMOND_SWORD, Material.LEGACY_DIAMOND_SWORD);
		registerBukkitToNukkit(ItemID.DIAMOND_SHOVEL, Material.LEGACY_DIAMOND_SPADE);
		registerBukkitToNukkit(ItemID.DIAMOND_PICKAXE, Material.LEGACY_DIAMOND_PICKAXE);
		registerBukkitToNukkit(ItemID.DIAMOND_AXE, Material.LEGACY_DIAMOND_AXE);
		registerBukkitToNukkit(ItemID.STICK, Material.LEGACY_STICK);
		registerBukkitToNukkit(ItemID.BOWL, Material.LEGACY_BOWL);
		registerBukkitToNukkit(ItemID.MUSHROOM_STEW, Material.LEGACY_MUSHROOM_SOUP);
		registerBukkitToNukkit(ItemID.GOLD_SWORD, Material.LEGACY_GOLD_SWORD);
		registerBukkitToNukkit(ItemID.GOLD_SHOVEL, Material.LEGACY_GOLD_SPADE);
		registerBukkitToNukkit(ItemID.GOLD_PICKAXE, Material.LEGACY_GOLD_PICKAXE);
		registerBukkitToNukkit(ItemID.GOLD_AXE, Material.LEGACY_GOLD_AXE);
		registerBukkitToNukkit(ItemID.STRING, Material.LEGACY_STRING);
		registerBukkitToNukkit(ItemID.FEATHER, Material.LEGACY_FEATHER);
		registerBukkitToNukkit(ItemID.GUNPOWDER, Material.LEGACY_SULPHUR);
		registerBukkitToNukkit(ItemID.WOODEN_HOE, Material.LEGACY_WOOD_HOE);
		registerBukkitToNukkit(ItemID.STONE_HOE, Material.LEGACY_STONE_HOE);
		registerBukkitToNukkit(ItemID.IRON_HOE, Material.LEGACY_IRON_HOE);
		registerBukkitToNukkit(ItemID.DIAMOND_HOE, Material.LEGACY_DIAMOND_HOE);
		registerBukkitToNukkit(ItemID.GOLD_HOE, Material.LEGACY_GOLD_HOE);
		registerBukkitToNukkit(ItemID.SEEDS, Material.LEGACY_SEEDS);
		registerBukkitToNukkit(ItemID.WHEAT, Material.LEGACY_WHEAT);
		registerBukkitToNukkit(ItemID.BREAD, Material.LEGACY_BREAD);
		registerBukkitToNukkit(ItemID.LEATHER_CAP, Material.LEGACY_LEATHER_HELMET);
		registerBukkitToNukkit(ItemID.LEATHER_TUNIC, Material.LEGACY_LEATHER_CHESTPLATE);
		registerBukkitToNukkit(ItemID.LEATHER_PANTS, Material.LEGACY_LEATHER_LEGGINGS);
		registerBukkitToNukkit(ItemID.LEATHER_BOOTS, Material.LEGACY_LEATHER_BOOTS);
		registerBukkitToNukkit(ItemID.CHAIN_HELMET, Material.LEGACY_CHAINMAIL_HELMET);
		registerBukkitToNukkit(ItemID.CHAIN_CHESTPLATE, Material.LEGACY_CHAINMAIL_CHESTPLATE);
		registerBukkitToNukkit(ItemID.CHAIN_LEGGINGS, Material.LEGACY_CHAINMAIL_LEGGINGS);
		registerBukkitToNukkit(ItemID.CHAIN_BOOTS, Material.LEGACY_CHAINMAIL_BOOTS);
		registerBukkitToNukkit(ItemID.IRON_HELMET, Material.LEGACY_IRON_HELMET);
		registerBukkitToNukkit(ItemID.IRON_CHESTPLATE, Material.LEGACY_IRON_CHESTPLATE);
		registerBukkitToNukkit(ItemID.IRON_LEGGINGS, Material.LEGACY_IRON_LEGGINGS);
		registerBukkitToNukkit(ItemID.IRON_BOOTS, Material.LEGACY_IRON_BOOTS);
		registerBukkitToNukkit(ItemID.DIAMOND_HELMET, Material.LEGACY_DIAMOND_HELMET);
		registerBukkitToNukkit(ItemID.DIAMOND_CHESTPLATE, Material.LEGACY_DIAMOND_CHESTPLATE);
		registerBukkitToNukkit(ItemID.DIAMOND_LEGGINGS, Material.LEGACY_DIAMOND_LEGGINGS);
		registerBukkitToNukkit(ItemID.DIAMOND_BOOTS, Material.LEGACY_DIAMOND_BOOTS);
		registerBukkitToNukkit(ItemID.GOLD_HELMET, Material.LEGACY_GOLD_HELMET);
		registerBukkitToNukkit(ItemID.GOLD_CHESTPLATE, Material.LEGACY_GOLD_CHESTPLATE);
		registerBukkitToNukkit(ItemID.GOLD_LEGGINGS, Material.LEGACY_GOLD_LEGGINGS);
		registerBukkitToNukkit(ItemID.GOLD_BOOTS, Material.LEGACY_GOLD_BOOTS);
		registerBukkitToNukkit(ItemID.FLINT, Material.LEGACY_FLINT);
		registerBukkitToNukkit(ItemID.RAW_PORKCHOP, Material.LEGACY_PORK);
		registerBukkitToNukkit(ItemID.COOKED_PORKCHOP, Material.LEGACY_GRILLED_PORK);
		registerBukkitToNukkit(ItemID.PAINTING, Material.LEGACY_PAINTING);
		registerBukkitToNukkit(ItemID.GOLDEN_APPLE, Material.LEGACY_GOLDEN_APPLE);
		registerBukkitToNukkit(ItemID.SIGN, Material.LEGACY_SIGN);
		registerBukkitToNukkit(ItemID.WOODEN_DOOR, Material.LEGACY_WOOD_DOOR);
		registerBukkitToNukkit(ItemID.BUCKET, Material.LEGACY_BUCKET);
		registerBukkitToNukkit(ItemID.MINECART, Material.LEGACY_MINECART);
		registerBukkitToNukkit(ItemID.SADDLE, Material.LEGACY_SADDLE);
		registerBukkitToNukkit(ItemID.IRON_DOOR, Material.LEGACY_IRON_DOOR);
		registerBukkitToNukkit(ItemID.REDSTONE, Material.LEGACY_REDSTONE);
		registerBukkitToNukkit(ItemID.SNOWBALL, Material.LEGACY_SNOW_BALL);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT);
		registerBukkitToNukkit(ItemID.LEATHER, Material.LEGACY_LEATHER);
		registerBukkitToNukkit(ItemID.BRICK, Material.LEGACY_CLAY_BRICK);
		registerBukkitToNukkit(ItemID.CLAY, Material.LEGACY_CLAY_BALL);
		registerBukkitToNukkit(ItemID.SUGAR_CANE, Material.LEGACY_SUGAR_CANE);
		registerBukkitToNukkit(ItemID.PAPER, Material.LEGACY_PAPER);
		registerBukkitToNukkit(ItemID.BOOK, Material.LEGACY_BOOK);
		registerBukkitToNukkit(ItemID.SLIMEBALL, Material.LEGACY_SLIME_BALL);
		registerBukkitToNukkit(ItemID.MINECART_WITH_CHEST, Material.LEGACY_STORAGE_MINECART);
		registerBukkitToNukkit(ItemID.EGG, Material.LEGACY_EGG);
		registerBukkitToNukkit(ItemID.COMPASS, Material.LEGACY_COMPASS);
		registerBukkitToNukkit(ItemID.FISHING_ROD, Material.LEGACY_FISHING_ROD);
		registerBukkitToNukkit(ItemID.CLOCK, Material.LEGACY_WATCH);
		registerBukkitToNukkit(ItemID.GLOWSTONE_DUST, Material.LEGACY_GLOWSTONE_DUST);
		registerBukkitToNukkit(ItemID.RAW_FISH, Material.LEGACY_RAW_FISH);
		registerBukkitToNukkit(ItemID.COOKED_FISH, Material.LEGACY_COOKED_FISH);
		registerBukkitToNukkit(ItemID.BONE, Material.LEGACY_BONE);
		registerBukkitToNukkit(ItemID.SUGAR, Material.LEGACY_SUGAR);
		registerBukkitToNukkit(ItemID.CAKE, Material.LEGACY_CAKE);
		registerBukkitToNukkit(ItemID.BED, Material.LEGACY_BED);
		registerBukkitToNukkit(ItemID.REPEATER, Material.LEGACY_DIODE);
		registerBukkitToNukkit(ItemID.COOKIE, Material.LEGACY_COOKIE);
		registerBukkitToNukkit(ItemID.MAP, Material.LEGACY_MAP);
		registerBukkitToNukkit(ItemID.SHEARS, Material.LEGACY_SHEARS);
		registerBukkitToNukkit(ItemID.MELON, Material.LEGACY_MELON);
		registerBukkitToNukkit(ItemID.PUMPKIN_SEEDS, Material.LEGACY_PUMPKIN_SEEDS);
		registerBukkitToNukkit(ItemID.MELON_SEEDS, Material.LEGACY_MELON_SEEDS);
		registerBukkitToNukkit(ItemID.RAW_BEEF, Material.LEGACY_RAW_BEEF);
		registerBukkitToNukkit(ItemID.COOKED_BEEF, Material.LEGACY_COOKED_BEEF);
		registerBukkitToNukkit(ItemID.RAW_CHICKEN, Material.LEGACY_RAW_CHICKEN);
		registerBukkitToNukkit(ItemID.COOKED_CHICKEN, Material.LEGACY_COOKED_CHICKEN);
		registerBukkitToNukkit(ItemID.ROTTEN_FLESH, Material.LEGACY_ROTTEN_FLESH);
		registerBukkitToNukkit(ItemID.ENDER_PEARL, Material.LEGACY_ENDER_PEARL);
		registerBukkitToNukkit(ItemID.BLAZE_ROD, Material.LEGACY_BLAZE_ROD);
		registerBukkitToNukkit(ItemID.GHAST_TEAR, Material.LEGACY_GHAST_TEAR);
		registerBukkitToNukkit(ItemID.GOLD_NUGGET, Material.LEGACY_GOLD_NUGGET);
		registerBukkitToNukkit(ItemID.NETHER_WART, Material.LEGACY_NETHER_STALK);
		registerBukkitToNukkit(ItemID.POTION, Material.LEGACY_POTION);
		registerBukkitToNukkit(ItemID.GLASS_BOTTLE, Material.LEGACY_GLASS_BOTTLE);
		registerBukkitToNukkit(ItemID.SPIDER_EYE, Material.LEGACY_SPIDER_EYE);
		registerBukkitToNukkit(ItemID.FERMENTED_SPIDER_EYE, Material.LEGACY_FERMENTED_SPIDER_EYE);
		registerBukkitToNukkit(ItemID.BLAZE_POWDER, Material.LEGACY_BLAZE_POWDER);
		registerBukkitToNukkit(ItemID.MAGMA_CREAM, Material.LEGACY_MAGMA_CREAM);
		registerBukkitToNukkit(ItemID.BREWING_STAND, Material.LEGACY_BREWING_STAND_ITEM);
		registerBukkitToNukkit(ItemID.CAULDRON, Material.LEGACY_CAULDRON_ITEM);
		registerBukkitToNukkit(ItemID.ENDER_EYE, Material.LEGACY_EYE_OF_ENDER);
		registerBukkitToNukkit(ItemID.GLISTERING_MELON, Material.LEGACY_SPECKLED_MELON);
		registerBukkitToNukkit(ItemID.SPAWN_EGG, Material.LEGACY_MONSTER_EGG);
		registerBukkitToNukkit(ItemID.EXPERIENCE_BOTTLE, Material.LEGACY_EXP_BOTTLE);
		registerBukkitToNukkit(ItemID.FIRE_CHARGE, Material.LEGACY_FIREBALL);
		registerBukkitToNukkit(ItemID.BOOK_AND_QUILL, Material.LEGACY_BOOK_AND_QUILL);
		registerBukkitToNukkit(ItemID.WRITTEN_BOOK, Material.LEGACY_WRITTEN_BOOK);
		registerBukkitToNukkit(ItemID.EMERALD, Material.LEGACY_EMERALD);
		registerBukkitToNukkit(ItemID.ITEM_FRAME, Material.LEGACY_ITEM_FRAME);
		registerBukkitToNukkit(ItemID.FLOWER_POT, Material.LEGACY_FLOWER_POT_ITEM);
		registerBukkitToNukkit(ItemID.CARROT, Material.LEGACY_CARROT_ITEM);
		registerBukkitToNukkit(ItemID.POTATO, Material.LEGACY_POTATO_ITEM);
		registerBukkitToNukkit(ItemID.BAKED_POTATO, Material.LEGACY_BAKED_POTATO);
		registerBukkitToNukkit(ItemID.POISONOUS_POTATO, Material.LEGACY_POISONOUS_POTATO);
		registerBukkitToNukkit(ItemID.EMPTY_MAP, Material.LEGACY_EMPTY_MAP);
		registerBukkitToNukkit(ItemID.GOLDEN_CARROT, Material.LEGACY_GOLDEN_CARROT);
		registerBukkitToNukkit(ItemID.SKULL, Material.LEGACY_SKULL_ITEM);
		registerBukkitToNukkit(ItemID.CARROT_ON_A_STICK, Material.LEGACY_CARROT_STICK);
		registerBukkitToNukkit(ItemID.NETHER_STAR, Material.LEGACY_NETHER_STAR);
		registerBukkitToNukkit(ItemID.PUMPKIN_PIE, Material.LEGACY_PUMPKIN_PIE);
		registerBukkitToNukkit(ItemID.FIREWORKS, Material.LEGACY_FIREWORK);
		registerBukkitToNukkit(ItemID.ENCHANTED_BOOK, Material.LEGACY_ENCHANTED_BOOK);
		registerBukkitToNukkit(ItemID.COMPARATOR, Material.LEGACY_REDSTONE_COMPARATOR);
		registerBukkitToNukkit(ItemID.NETHER_BRICK, Material.LEGACY_NETHER_BRICK_ITEM);
		registerBukkitToNukkit(ItemID.QUARTZ, Material.LEGACY_QUARTZ);
		registerBukkitToNukkit(ItemID.MINECART_WITH_TNT, Material.LEGACY_EXPLOSIVE_MINECART);
		registerBukkitToNukkit(ItemID.MINECART_WITH_HOPPER, Material.LEGACY_HOPPER_MINECART);
		registerBukkitToNukkit(ItemID.PRISMARINE_SHARD, Material.LEGACY_PRISMARINE_SHARD);
		registerBukkitToNukkit(ItemID.PRISMARINE_CRYSTALS, Material.LEGACY_PRISMARINE_CRYSTALS);
		registerBukkitToNukkit(ItemID.RAW_RABBIT, Material.LEGACY_RABBIT);
		registerBukkitToNukkit(ItemID.COOKED_RABBIT, Material.LEGACY_COOKED_RABBIT);
		registerBukkitToNukkit(ItemID.RABBIT_STEW, Material.LEGACY_RABBIT_STEW);
		registerBukkitToNukkit(ItemID.RABBIT_FOOT, Material.LEGACY_RABBIT_FOOT);
		registerBukkitToNukkit(ItemID.RABBIT_HIDE, Material.LEGACY_RABBIT_HIDE);
		registerBukkitToNukkit(ItemID.IRON_HORSE_ARMOR, Material.LEGACY_IRON_BARDING);
		registerBukkitToNukkit(ItemID.GOLD_HORSE_ARMOR, Material.LEGACY_GOLD_BARDING);
		registerBukkitToNukkit(ItemID.DIAMOND_HORSE_ARMOR, Material.LEGACY_DIAMOND_BARDING);
		registerBukkitToNukkit(ItemID.LEAD, Material.LEGACY_LEASH);
		registerBukkitToNukkit(ItemID.NAME_TAG, Material.LEGACY_NAME_TAG);
		registerBukkitToNukkit(ItemID.RAW_MUTTON, Material.LEGACY_MUTTON);
		registerBukkitToNukkit(ItemID.COOKED_MUTTON, Material.LEGACY_COOKED_MUTTON);
		registerBukkitToNukkit(ItemID.END_CRYSTAL, Material.LEGACY_END_CRYSTAL);
		registerBukkitToNukkit(ItemID.SPRUCE_DOOR, Material.LEGACY_SPRUCE_DOOR_ITEM);
		registerBukkitToNukkit(ItemID.BIRCH_DOOR, Material.LEGACY_BIRCH_DOOR_ITEM);
		registerBukkitToNukkit(ItemID.JUNGLE_DOOR, Material.LEGACY_JUNGLE_DOOR_ITEM);
		registerBukkitToNukkit(ItemID.ACACIA_DOOR, Material.LEGACY_ACACIA_DOOR_ITEM);
		registerBukkitToNukkit(ItemID.DARK_OAK_DOOR, Material.LEGACY_DARK_OAK_DOOR_ITEM);
		registerBukkitToNukkit(ItemID.CHORUS_FRUIT, Material.LEGACY_CHORUS_FRUIT);
		registerBukkitToNukkit(ItemID.POPPED_CHORUS_FRUIT, Material.LEGACY_CHORUS_FRUIT_POPPED);
		registerBukkitToNukkit(ItemID.BEETROOT, Material.LEGACY_BEETROOT);
		registerBukkitToNukkit(ItemID.BEETROOT_SEEDS, Material.LEGACY_BEETROOT_SEEDS);
		registerBukkitToNukkit(ItemID.BEETROOT_SOUP, Material.LEGACY_BEETROOT_SOUP);
		registerBukkitToNukkit(ItemID.DRAGON_BREATH, Material.LEGACY_DRAGONS_BREATH);
		registerBukkitToNukkit(ItemID.SPLASH_POTION, Material.LEGACY_SPLASH_POTION);
		registerBukkitToNukkit(ItemID.ARROW, Material.LEGACY_SPECTRAL_ARROW);
		registerBukkitToNukkit(ItemID.ARROW, Material.LEGACY_TIPPED_ARROW);
		registerBukkitToNukkit(ItemID.LINGERING_POTION, Material.LEGACY_LINGERING_POTION);
		registerBukkitToNukkit(ItemID.ELYTRA, Material.LEGACY_ELYTRA);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT_SPRUCE);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT_BIRCH);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT_JUNGLE);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT_ACACIA);
		registerBukkitToNukkit(ItemID.BOAT, Material.LEGACY_BOAT_DARK_OAK);
		registerBukkitToNukkit(ItemID.SHULKER_SHELL, Material.LEGACY_SHULKER_SHELL);
		registerBukkitToNukkit(ItemID.WRITTEN_BOOK, Material.LEGACY_KNOWLEDGE_BOOK);
		registerBukkitToNukkit(ItemID.RECORD_13, Material.LEGACY_GOLD_RECORD);
		registerBukkitToNukkit(ItemID.RECORD_CAT, Material.LEGACY_GREEN_RECORD);
		registerBukkitToNukkit(ItemID.RECORD_BLOCKS, Material.LEGACY_RECORD_3);
		registerBukkitToNukkit(ItemID.RECORD_CHIRP, Material.LEGACY_RECORD_4);
		registerBukkitToNukkit(ItemID.RECORD_FAR, Material.LEGACY_RECORD_5);
		registerBukkitToNukkit(ItemID.RECORD_MALL, Material.LEGACY_RECORD_6);
		registerBukkitToNukkit(ItemID.RECORD_MELLOHI, Material.LEGACY_RECORD_7);
		registerBukkitToNukkit(ItemID.RECORD_STAL, Material.LEGACY_RECORD_8);
		registerBukkitToNukkit(ItemID.RECORD_STRAD, Material.LEGACY_RECORD_9);
		registerBukkitToNukkit(ItemID.RECORD_WARD, Material.LEGACY_RECORD_10);
		registerBukkitToNukkit(ItemID.RECORD_11, Material.LEGACY_RECORD_11);
		registerBukkitToNukkit(ItemID.RECORD_WAIT, Material.LEGACY_RECORD_12);
		registerBukkitToNukkit(ItemID.BANNER, Material.LEGACY_BANNER);
		registerBukkitToNukkit(ItemID.ARMOR_STAND, Material.LEGACY_ARMOR_STAND);
		registerBukkitToNukkit(ItemID.COMMAND_BLOCK_MINECART, Material.LEGACY_COMMAND_MINECART);
		registerBukkitToNukkit(ItemID.TOTEM, Material.LEGACY_TOTEM);
		registerBukkitToNukkit(ItemID.IRON_NUGGET, Material.LEGACY_IRON_NUGGET);

		// Items that share one Nukkit id and use the damage value as a variant
		// selector. Without these, all variants would collapse into a single
		// material (or turn into air).
		Material[] dyes = {
				Material.INK_SAC, Material.RED_DYE, Material.GREEN_DYE, Material.COCOA_BEANS,
				Material.LAPIS_LAZULI, Material.PURPLE_DYE, Material.CYAN_DYE, Material.LIGHT_GRAY_DYE,
				Material.GRAY_DYE, Material.PINK_DYE, Material.LIME_DYE, Material.YELLOW_DYE,
				Material.LIGHT_BLUE_DYE, Material.MAGENTA_DYE, Material.ORANGE_DYE, Material.BONE_MEAL
		};
		for (int damage = 0; damage < dyes.length; damage++) {
			registerVariant(ItemID.DYE, damage, dyes[damage]);
		}

		Material[] banners = {
				Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER,
				Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER,
				Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER,
				Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER,
				Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER,
				Material.BLACK_BANNER
		};
		for (int damage = 0; damage < banners.length; damage++) {
			registerVariant(ItemID.BANNER, damage, banners[damage]);
		}

		registerVariant(ItemID.SKULL, 0, Material.SKELETON_SKULL);
		registerVariant(ItemID.SKULL, 1, Material.WITHER_SKELETON_SKULL);
		registerVariant(ItemID.SKULL, 2, Material.ZOMBIE_HEAD);
		registerVariant(ItemID.SKULL, 3, Material.PLAYER_HEAD);
		registerVariant(ItemID.SKULL, 4, Material.CREEPER_HEAD);
		registerVariant(ItemID.SKULL, 5, Material.DRAGON_HEAD);

		registerVariant(ItemID.COAL, 0, Material.COAL);
		registerVariant(ItemID.COAL, 1, Material.CHARCOAL);

		// Bedrock stores every bucket in one item, with the content in the
		// damage value. Without this table water, lava and milk buckets all
		// turned into air.
		registerVariant(ItemID.BUCKET, 1, Material.MILK_BUCKET);
		registerVariant(ItemID.BUCKET, 2, Material.COD_BUCKET);
		registerVariant(ItemID.BUCKET, 3, Material.SALMON_BUCKET);
		registerVariant(ItemID.BUCKET, 4, Material.TROPICAL_FISH_BUCKET);
		registerVariant(ItemID.BUCKET, 5, Material.PUFFERFISH_BUCKET);
		registerVariant(ItemID.BUCKET, 8, Material.WATER_BUCKET);
		registerVariant(ItemID.BUCKET, 10, Material.LAVA_BUCKET);
		registerVariant(ItemID.BUCKET, 11, Material.POWDER_SNOW_BUCKET);
		registerVariant(ItemID.BUCKET, 12, Material.AXOLOTL_BUCKET);
		registerVariant(ItemID.BUCKET, 13, Material.TADPOLE_BUCKET);

		// The wood type of a boat is stored in the damage value as well.
		registerVariant(ItemID.BOAT, 1, Material.SPRUCE_BOAT);
		registerVariant(ItemID.BOAT, 2, Material.BIRCH_BOAT);
		registerVariant(ItemID.BOAT, 3, Material.JUNGLE_BOAT);
		registerVariant(ItemID.BOAT, 4, Material.ACACIA_BOAT);
		registerVariant(ItemID.BOAT, 5, Material.DARK_OAK_BOAT);
		registerVariant(ItemID.BOAT, 6, Material.MANGROVE_BOAT);
		registerVariant(ItemID.BOAT, 7, Material.BAMBOO_RAFT);
		registerVariant(ItemID.BOAT, 8, Material.CHERRY_BOAT);

		Material[] beds = {
				Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED,
				Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED,
				Material.PINK_BED, Material.GRAY_BED, Material.LIGHT_GRAY_BED,
				Material.CYAN_BED, Material.PURPLE_BED, Material.BLUE_BED,
				Material.BROWN_BED, Material.GREEN_BED, Material.RED_BED,
				Material.BLACK_BED
		};
		for (int damage = 0; damage < beds.length; damage++) {
			registerVariant(ItemID.BED, damage, beds[damage]);
		}

		// Dyes that got their own item after 1.20 use the next damage values,
		// the older ink sac, cocoa beans, lapis lazuli and bone meal keep the
		// values they always had.
		registerVariant(ItemID.DYE, 16, Material.BLACK_DYE);
		registerVariant(ItemID.DYE, 17, Material.BROWN_DYE);
		registerVariant(ItemID.DYE, 18, Material.BLUE_DYE);
		registerVariant(ItemID.DYE, 19, Material.WHITE_DYE);
		registerVariant(ItemID.DYE, 20, Material.GLOW_INK_SAC);

		// Items that have their own Nukkit id but were missing from the
		// two-way table.
		registerTwoWay(ItemID.BAMBOO_CHEST_RAFT, Material.BAMBOO_CHEST_RAFT);
	}

	/**
	 * Gets the Nukkit item.
	 * @param type The Bukkit material.
	 * @return The Nukkit item.
	 */
	public static Item fromBukkit(Material type) {
		if (type == null) {
			return Item.get(Item.AIR);
		}

		int[] variant = bukkitVariants.get(type);
		if (variant != null) {
			return Item.get(variant[0], variant[1]);
		}

		if (type.name().endsWith("_SPAWN_EGG")) {
			Integer damage = spawnEggDamages().get(type);
			if (damage != null) {
				return Item.get(ItemID.SPAWN_EGG, damage);
			}
		}

		char nukkitId = bukkitToNukkit[type.ordinal()];
		if (nukkitId != 0 || type == Material.AIR) {
			return Item.get(nukkitId);
		}

		// Not an item that is explicitly listed, but it may still be a block.
		// Those are maintained by BlockMap, so reuse that table instead of
		// duplicating hundreds of mappings here. Note that Material.isItem()
		// cannot be used here: it looks the material up in the ItemType
		// registry, which does not contain every block (yet).
		Item blockItem = fromBlockOrNull(type);
		if (blockItem != null) {
			return blockItem;
		}

		return Item.get(Item.AIR);
	}

	private static Item fromBlockOrNull(Material material) {
		Block block = BlockMap.getNukkitBlockOrNull(material);
		if (block == null || block.getId() == 0) {
			return null;
		}
		return Item.getBlockItem(block.getId(), block.getDamage());
	}

	/**
	 * Gets the material from the Nukkit item, or null if not found.
	 * @param nukkit The Nukkit item, may be null.
	 * @return The material, or null if not found.
	 */
	public static Material fromNukkitOrNull(Item nukkit) {
		if (nukkit == null) {
			return null;
		}
		if (nukkit instanceof StringItem) {
			// All string-identified items share id 255, which must not be
			// resolved through the numeric tables.
			return fromNukkitStringOrNull(nukkit);
		}
		int nukkitId = nukkit.getId();
		if (nukkitId < 0) {
			// Blocks with an id above 255 are stored as negative item ids.
			return BlockMap.getMaterialOrNull(255 - nukkitId, nukkit.getDamage());
		}

		if (nukkitId == ItemID.SPAWN_EGG) {
			Material spawnEgg = spawnEggMaterial(nukkit.getDamage());
			if (spawnEgg != null) {
				return spawnEgg;
			}
		}

		if (nukkitId == ItemID.ARROW && nukkit.getDamage() > 0) {
			// Any damage above 0 is a tipped arrow, see ItemArrow.
			return Material.TIPPED_ARROW;
		}

		Map<Integer, Material> variants = nukkitVariants.get(nukkitId);
		if (variants != null) {
			Material variant = variants.get(nukkit.getDamage());
			if (variant != null) {
				return variant;
			}
		}

		if (nukkitId < nukkitToBukkit.length) {
			Material material = nukkitToBukkit[nukkitId];
			if (material != null) {
				return material;
			}
		}

		// Not listed as an item, but it may be a block item. Only ids below
		// 256 can be block ids; the item form of higher blocks uses a
		// negative id, and ids above 255 belong to other items. Looking those
		// up in the block table would silently return an unrelated block.
		if (nukkitId < 256) {
			return BlockMap.getMaterialOrNull(nukkitId, nukkit.getDamage());
		}
		return null;
	}

	/**
	 * Gets the material from a Nukkit string-identified item by matching its
	 * namespace ID (e.g. "minecraft:stone") against Bukkit Material names.
	 *
	 * @param nukkit The Nukkit item, may be null.
	 * @return The material, or null if not found.
	 */
	public static Material fromNukkitStringOrNull(Item nukkit) {
		if (nukkit == null) {
			return null;
		}
		if (nukkit instanceof StringItem stringItem) {
			String namespaceId = stringItem.getNamespaceId();
			if (namespaceId != null && namespaceId.startsWith("minecraft:")) {
				String key = namespaceId.substring("minecraft:".length()).toUpperCase(Locale.ROOT);
				return Material.matchMaterial(key);
			}
		}
		return null;
	}

	/**
	 * Maps a spawn egg damage value (a Nukkit entity network id) to the
	 * corresponding Bukkit material.
	 *
	 * @param damage
	 *            The damage value of the spawn egg.
	 * @return The material, or null if the damage value is unknown.
	 */
	private static Material spawnEggMaterial(int damage) {
		return spawnEggMaterialMap().get(damage);
	}

	private static Map<Integer, Material> spawnEggMaterialMap() {
		Map<Integer, Material> cache = spawnEggMaterials;
		if (cache != null) {
			return cache;
		}
		Map<Integer, Material> result = new HashMap<>();
		// The vanilla entity network ids are small; 512 leaves plenty of room
		// for newer entities without having to hardcode a table.
		for (int damage = 0; damage < 512; damage++) {
			Material material = resolveSpawnEggMaterial(damage);
			if (material != null) {
				result.put(damage, material);
			}
		}
		if (result.isEmpty()) {
			// Entity registry not initialized; don't cache the empty result.
			return result;
		}
		spawnEggMaterials = result;
		return result;
	}

	private static Material resolveSpawnEggMaterial(int damage) {
		try {
			Identifier identifier = Entity.getIdentifier(damage);
			if (identifier == null) {
				return null;
			}
			// A few Bedrock identifiers differ from the Bukkit material names.
			String path = switch (identifier.getPath()) {
			case "zombie_pigman" -> "zombified_piglin";
			case "evocation_illager" -> "evoker";
			case "tropicalfish" -> "tropical_fish";
			case "villager_v2" -> "villager";
			case "zombie_villager_v2" -> "zombie_villager";
			default -> identifier.getPath();
			};
			return Material.matchMaterial(path.toUpperCase(Locale.ROOT) + "_SPAWN_EGG");
		} catch (RuntimeException e) {
			// The Nukkit entity registry is not available (yet).
			return null;
		}
	}

	private static Map<Material, Integer> spawnEggDamages() {
		Map<Material, Integer> cache = spawnEggDamages;
		if (cache != null) {
			return cache;
		}
		Map<Material, Integer> result = new EnumMap<>(Material.class);
		for (Map.Entry<Integer, Material> entry : spawnEggMaterialMap().entrySet()) {
			result.putIfAbsent(entry.getValue(), entry.getKey());
		}
		if (result.isEmpty()) {
			// Entity registry not initialized; don't cache the empty result.
			return result;
		}
		spawnEggDamages = result;
		return result;
	}

	private static void registerBukkitToNukkit(int nukkitId, Material bukkit) {
		bukkitToNukkit[bukkit.ordinal()] = (char) nukkitId;
	}

	private static void registerNukkitToBukkit(int nukkitId, Material bukkit) {
		nukkitToBukkit[nukkitId] = bukkit;
	}

	private static void registerTwoWay(int nukkitId, Material bukkit) {
		registerBukkitToNukkit(nukkitId, bukkit);
		registerNukkitToBukkit(nukkitId, bukkit);
	}

	private static void registerVariant(int nukkitId, int damage, Material bukkit) {
		nukkitVariants.computeIfAbsent(nukkitId, key -> new HashMap<>()).put(damage, bukkit);
		bukkitVariants.put(bukkit, new int[] { nukkitId, damage });
	}
}
