package nl.rutgerkok.pokkit.blockdata;

import org.bukkit.Material;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

@SuppressWarnings("deprecation")
public final class BlockMap {

	private static final Material[][] nukkitToBukkit = new Material[2048][];
	private static final Block[] bukkitToNukkit = new Block[Material.values().length];

	static {
		for (int i = 0; i < nukkitToBukkit.length; i++)
			nukkitToBukkit[i] = new Material[1];
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AIR), Material.AIR);
		
		// Stone
		nukkitToBukkit[BlockID.STONE] = new Material[7];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 0), Material.STONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 1), Material.GRANITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 2), Material.POLISHED_GRANITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 3), Material.DIORITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 4), Material.POLISHED_DIORITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 5), Material.ANDESITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE, 6), Material.POLISHED_ANDESITE);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRASS), Material.GRASS_BLOCK);
		
		// Dirt
		nukkitToBukkit[BlockID.DIRT]= new Material[3]; 
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIRT, 0), Material.DIRT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIRT, 1), Material.COARSE_DIRT);
		// In Nukkit, Podozol has its own block ID.
		// registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIRT, 2), Material.PODZOL); 
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLESTONE), Material.COBBLESTONE);
		
		// Wooden Planks
		nukkitToBukkit[BlockID.WOODEN_PLANKS] = new Material[6];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 0), Material.OAK_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 1), Material.SPRUCE_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 2), Material.BIRCH_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 3), Material.JUNGLE_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 4), Material.ACACIA_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PLANKS, 5), Material.DARK_OAK_PLANKS);

		// Saplings
		nukkitToBukkit[BlockID.SAPLINGS] = new Material[6];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 0), Material.OAK_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 1), Material.SPRUCE_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 2), Material.BIRCH_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 3), Material.JUNGLE_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 4), Material.ACACIA_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAPLINGS, 5), Material.DARK_OAK_SAPLING);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BEDROCK), Material.BEDROCK);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.WATER), Material.WATER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STILL_WATER), Material.WATER);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.LAVA), Material.LAVA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STILL_LAVA), Material.LAVA);
		
		// Sand
		nukkitToBukkit[BlockID.SAND] = new Material[2];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAND, 0), Material.SAND);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SAND, 1), Material.RED_SAND);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRAVEL), Material.GRAVEL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GOLD_ORE), Material.GOLD_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.IRON_ORE), Material.IRON_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COAL_ORE), Material.COAL_ORE);

		// Logs
		nukkitToBukkit[BlockID.LOG] = new Material[4];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG, 0), Material.OAK_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG, 1), Material.SPRUCE_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG, 2), Material.BIRCH_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG, 3), Material.JUNGLE_LOG);
		nukkitToBukkit[BlockID.LOG2] = new Material[2];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG2, 0), Material.ACACIA_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOG2, 1), Material.DARK_OAK_LOG);
		
		// Leaves
		nukkitToBukkit[BlockID.LEAVES] = new Material[4];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES, 0), Material.OAK_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES, 1), Material.SPRUCE_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES, 2), Material.BIRCH_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES, 3), Material.JUNGLE_LEAVES);
		nukkitToBukkit[BlockID.LEAVES2] = new Material[2];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES2, 0), Material.ACACIA_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEAVES2, 1), Material.DARK_OAK_LEAVES);

		// Sponge
		nukkitToBukkit[BlockID.SPONGE] = new Material[2];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPONGE, 0), Material.SPONGE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPONGE, 1), Material.WET_SPONGE);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GLASS), Material.GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LAPIS_ORE), Material.LAPIS_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LAPIS_BLOCK), Material.LAPIS_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DISPENSER), Material.DISPENSER);

		// Sandstone
		nukkitToBukkit[BlockID.SANDSTONE] = new Material[3];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SANDSTONE, 0), Material.SANDSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SANDSTONE, 1), Material.CHISELED_SANDSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SANDSTONE, 2), Material.SMOOTH_SANDSTONE);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NOTEBLOCK), Material.NOTE_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BED_BLOCK), Material.RED_BED);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POWERED_RAIL), Material.POWERED_RAIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DETECTOR_RAIL), Material.DETECTOR_RAIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STICKY_PISTON), Material.STICKY_PISTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBWEB), Material.COBWEB);

		// Tall grass
		nukkitToBukkit[BlockID.TALL_GRASS] = new Material[3];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TALL_GRASS, 0), Material.TALL_GRASS);
		nukkitToBukkit[BlockID.TALL_GRASS][1] = Material.TALL_GRASS;
		nukkitToBukkit[BlockID.TALL_GRASS][2] = Material.FERN;
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BUSH), Material.FERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEAD_BUSH), Material.DEAD_BUSH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PISTON), Material.PISTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PISTON_HEAD), Material.PISTON_HEAD);
		
		// Wool
		nukkitToBukkit[BlockID.WOOL] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 0), Material.WHITE_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 1), Material.ORANGE_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 2), Material.MAGENTA_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 3), Material.LIGHT_BLUE_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 4), Material.YELLOW_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 5), Material.LIME_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 6), Material.PINK_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 7), Material.GRAY_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 8), Material.LIGHT_GRAY_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 9), Material.CYAN_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 10), Material.PURPLE_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 11), Material.BLUE_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 12), Material.BROWN_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 13), Material.GREEN_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 14), Material.RED_WOOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOL, 15), Material.BLACK_WOOL);		
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DANDELION), Material.DANDELION);
		
		// Small flowers
		nukkitToBukkit[BlockID.POPPY] = new Material[9];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 0), Material.POPPY);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 1), Material.BLUE_ORCHID);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 2), Material.ALLIUM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 3), Material.AZURE_BLUET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 4), Material.RED_TULIP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 5), Material.ORANGE_TULIP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 6), Material.WHITE_TULIP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 7), Material.PINK_TULIP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POPPY, 8), Material.OXEYE_DAISY);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BROWN_MUSHROOM), Material.BROWN_MUSHROOM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_MUSHROOM), Material.RED_MUSHROOM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GOLD_BLOCK), Material.GOLD_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.IRON_BLOCK), Material.IRON_BLOCK);
		
		// Stone slabs
		nukkitToBukkit[BlockID.STONE_SLAB] = new Material[8];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 0), Material.STONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 1), Material.SANDSTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 2), Material.PETRIFIED_OAK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 3), Material.COBBLESTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 4), Material.BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 5), Material.STONE_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 6), Material.NETHER_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_SLAB, 7), Material.QUARTZ_SLAB);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BRICKS), Material.BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TNT), Material.TNT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BOOKSHELF), Material.BOOKSHELF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MOSS_STONE), Material.MOSSY_COBBLESTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OBSIDIAN), Material.OBSIDIAN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TORCH), Material.TORCH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FIRE), Material.FIRE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MONSTER_SPAWNER), Material.SPAWNER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OAK_WOODEN_STAIRS), Material.OAK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHEST), Material.CHEST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REDSTONE_WIRE), Material.REDSTONE_WIRE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIAMOND_ORE), Material.DIAMOND_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIAMOND_BLOCK), Material.DIAMOND_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRAFTING_TABLE), Material.CRAFTING_TABLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WHEAT_BLOCK), Material.WHEAT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FARMLAND), Material.FARMLAND);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FURNACE), Material.FURNACE);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.BURNING_FURNACE), Material.FURNACE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SIGN_POST), Material.OAK_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOOR_BLOCK), Material.OAK_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LADDER), Material.LADDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RAIL), Material.RAIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLESTONE_STAIRS), Material.COBBLESTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WALL_SIGN), Material.OAK_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LEVER), Material.LEVER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_PRESSURE_PLATE), Material.STONE_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.IRON_DOOR_BLOCK), Material.IRON_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_PRESSURE_PLATE), Material.OAK_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REDSTONE_ORE), Material.REDSTONE_ORE);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.GLOWING_REDSTONE_ORE), Material.REDSTONE_ORE);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.UNLIT_REDSTONE_TORCH), Material.REDSTONE_TORCH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REDSTONE_TORCH), Material.REDSTONE_TORCH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BUTTON), Material.STONE_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SNOW), Material.SNOW);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ICE), Material.ICE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SNOW_BLOCK), Material.SNOW_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CACTUS), Material.CACTUS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CLAY_BLOCK), Material.CLAY);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SUGARCANE_BLOCK), Material.SUGAR_CANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUKEBOX), Material.JUKEBOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE), Material.OAK_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PUMPKIN), Material.PUMPKIN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHERRACK), Material.NETHERRACK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_SAND), Material.SOUL_SAND);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GLOWSTONE), Material.GLOWSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_PORTAL), Material.NETHER_PORTAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JACK_O_LANTERN), Material.JACK_O_LANTERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CAKE_BLOCK), Material.CAKE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.UNPOWERED_REPEATER), Material.REPEATER);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.POWERED_REPEATER), Material.REPEATER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.INVISIBLE_BEDROCK), Material.BARRIER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TRAPDOOR), Material.OAK_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MONSTER_EGG), Material.INFESTED_STONE);
		
		// Stone Bricks
		nukkitToBukkit[BlockID.STONE_BRICKS] = new Material[4];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BRICKS, 0), Material.STONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BRICKS, 1), Material.MOSSY_STONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BRICKS, 2), Material.CRACKED_STONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BRICKS, 3), Material.CHISELED_STONE_BRICKS);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BROWN_MUSHROOM_BLOCK), Material.BROWN_MUSHROOM_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_MUSHROOM_BLOCK), Material.RED_MUSHROOM_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.IRON_BARS), Material.IRON_BARS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GLASS_PANE), Material.GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MELON_BLOCK), Material.MELON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PUMPKIN_STEM), Material.PUMPKIN_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MELON_STEM), Material.MELON_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.VINE), Material.VINE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_OAK), Material.OAK_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BRICK_STAIRS), Material.BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONE_BRICK_STAIRS), Material.STONE_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MYCELIUM), Material.MYCELIUM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LILY_PAD), Material.LILY_PAD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_BRICKS), Material.NETHER_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_BRICK_FENCE), Material.NETHER_BRICK_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_BRICKS_STAIRS), Material.NETHER_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_WART_BLOCK), Material.NETHER_WART_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ENCHANTING_TABLE), Material.ENCHANTING_TABLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BREWING_STAND_BLOCK), Material.BREWING_STAND);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CAULDRON_BLOCK), Material.CAULDRON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_PORTAL), Material.END_PORTAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_PORTAL_FRAME), Material.END_PORTAL_FRAME);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_STONE), Material.END_STONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DRAGON_EGG), Material.DRAGON_EGG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REDSTONE_LAMP), Material.REDSTONE_LAMP);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.LIT_REDSTONE_LAMP), Material.REDSTONE_LAMP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DROPPER), Material.DROPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACTIVATOR_RAIL), Material.ACTIVATOR_RAIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COCOA), Material.COCOA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SANDSTONE_STAIRS), Material.SANDSTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EMERALD_ORE), Material.EMERALD_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ENDER_CHEST), Material.ENDER_CHEST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TRIPWIRE_HOOK), Material.TRIPWIRE_HOOK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TRIPWIRE), Material.TRIPWIRE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EMERALD_BLOCK), Material.EMERALD_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_WOOD_STAIRS), Material.SPRUCE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_WOOD_STAIRS), Material.BIRCH_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_WOOD_STAIRS), Material.JUNGLE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BEACON), Material.BEACON);
		
		// Cobblestone Wall
		nukkitToBukkit[BlockID.COBBLESTONE_WALL] = new Material[2];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLESTONE_WALL, 0), Material.COBBLESTONE_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLESTONE_WALL, 1), Material.MOSSY_COBBLESTONE_WALL);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FLOWER_POT_BLOCK), Material.FLOWER_POT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARROT_BLOCK), Material.CARROTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POTATO_BLOCK), Material.POTATOES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOODEN_BUTTON), Material.OAK_BUTTON);
		
		// Skulls
		nukkitToBukkit[BlockID.SKULL_BLOCK] = new Material[6];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 0), Material.SKELETON_SKULL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 1), Material.WITHER_SKELETON_SKULL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 2), Material.ZOMBIE_HEAD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 3), Material.PLAYER_HEAD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 4), Material.CREEPER_HEAD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK, 5), Material.DRAGON_HEAD);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ANVIL), Material.ANVIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TRAPPED_CHEST), Material.TRAPPED_CHEST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIGHT_WEIGHTED_PRESSURE_PLATE), Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HEAVY_WEIGHTED_PRESSURE_PLATE), Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.UNPOWERED_COMPARATOR), Material.COMPARATOR);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.POWERED_COMPARATOR), Material.COMPARATOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DAYLIGHT_DETECTOR), Material.DAYLIGHT_DETECTOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REDSTONE_BLOCK), Material.REDSTONE_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.QUARTZ_ORE), Material.NETHER_QUARTZ_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HOPPER_BLOCK), Material.HOPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.QUARTZ_BLOCK), Material.QUARTZ_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.QUARTZ_STAIRS), Material.QUARTZ_STAIRS);
		
		// Double wood slabs
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_WOOD_SLAB), Material.OAK_SLAB);
		
		// Wood slabs
		nukkitToBukkit[BlockID.WOOD_SLABS] = new Material[6];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 0), Material.OAK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 1), Material.SPRUCE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 2), Material.BIRCH_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 3), Material.JUNGLE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 4), Material.ACACIA_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB, 5), Material.DARK_OAK_SLAB);
				
		// Colored Terracotta
		nukkitToBukkit[BlockID.STAINED_HARDENED_CLAY] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 0), Material.WHITE_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 1), Material.ORANGE_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 2), Material.MAGENTA_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 3), Material.LIGHT_BLUE_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 4), Material.YELLOW_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 5), Material.LIME_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 6), Material.PINK_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 7), Material.GRAY_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 8), Material.LIGHT_GRAY_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 9), Material.CYAN_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 10), Material.PURPLE_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 11), Material.BLUE_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 12), Material.BROWN_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 13), Material.GREEN_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 14), Material.RED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY, 15), Material.BLACK_TERRACOTTA);
		
		nukkitToBukkit[BlockID.STAINED_GLASS_PANE] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 0), Material.WHITE_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 1), Material.ORANGE_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 2), Material.MAGENTA_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 3), Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 4), Material.YELLOW_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 5), Material.LIME_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 6), Material.PINK_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 7), Material.GRAY_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 8), Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 9), Material.CYAN_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 10), Material.PURPLE_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 11), Material.BLUE_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 12), Material.BROWN_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 13), Material.GREEN_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 14), Material.RED_STAINED_GLASS_PANE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE, 15), Material.BLACK_STAINED_GLASS_PANE);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_WOOD_STAIRS), Material.ACACIA_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_WOOD_STAIRS), Material.DARK_OAK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SLIME_BLOCK), Material.SLIME_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.IRON_TRAPDOOR), Material.IRON_TRAPDOOR);
		
		// Prismarine
		nukkitToBukkit[BlockID.PRISMARINE] = new Material[3];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PRISMARINE, 0), Material.PRISMARINE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PRISMARINE, 1), Material.PRISMARINE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PRISMARINE, 2), Material.DARK_PRISMARINE);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SEA_LANTERN), Material.SEA_LANTERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HAY_BALE), Material.HAY_BLOCK);
		
		// Carpet
		nukkitToBukkit[BlockID.CARPET] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 0), Material.WHITE_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 1), Material.ORANGE_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 2), Material.MAGENTA_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 3), Material.LIGHT_BLUE_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 4), Material.YELLOW_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 5), Material.LIME_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 6), Material.PINK_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 7), Material.GRAY_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 8), Material.LIGHT_GRAY_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 9), Material.CYAN_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 10), Material.PURPLE_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 11), Material.BLUE_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 12), Material.BROWN_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 13), Material.GREEN_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 14), Material.RED_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARPET, 15), Material.BLACK_CARPET);

		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TERRACOTTA), Material.TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COAL_BLOCK), Material.COAL_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PACKED_ICE), Material.PACKED_ICE);
		
		// Double plants
		nukkitToBukkit[BlockID.DOUBLE_PLANT] = new Material[6];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT, 0), Material.SUNFLOWER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT, 1), Material.LILAC);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT, 3), Material.LARGE_FERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT, 4), Material.ROSE_BUSH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT, 5), Material.PEONY);

		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.DAYLIGHT_DETECTOR_INVERTED), Material.DAYLIGHT_DETECTOR);
		
		nukkitToBukkit[BlockID.RED_SANDSTONE] = new Material[3];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE, 0), Material.RED_SANDSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE, 1), Material.CHISELED_RED_SANDSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE, 2), Material.SMOOTH_RED_SANDSTONE);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE_STAIRS), Material.RED_SANDSTONE_STAIRS);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_RED_SANDSTONE_SLAB), Material.RED_SANDSTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE_SLAB), Material.RED_SANDSTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_SPRUCE), Material.SPRUCE_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_BIRCH), Material.BIRCH_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_JUNGLE), Material.JUNGLE_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_DARK_OAK), Material.DARK_OAK_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_ACACIA), Material.ACACIA_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_DOOR_BLOCK), Material.SPRUCE_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_DOOR_BLOCK), Material.BIRCH_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_DOOR_BLOCK), Material.JUNGLE_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_DOOR_BLOCK), Material.ACACIA_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_DOOR_BLOCK), Material.DARK_OAK_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRASS_PATH), Material.DIRT_PATH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ITEM_FRAME_BLOCK), Material.ITEM_FRAME);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHORUS_FLOWER), Material.CHORUS_FLOWER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PURPUR_BLOCK), Material.PURPUR_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PURPUR_STAIRS), Material.PURPUR_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.UNDYED_SHULKER_BOX), Material.SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_BRICKS), Material.END_STONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ICE_FROSTED), Material.FROSTED_ICE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_ROD), Material.END_ROD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_GATEWAY), Material.END_GATEWAY);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MAGMA), Material.MAGMA_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLOCK_NETHER_WART_BLOCK), Material.NETHER_WART_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_NETHER_BRICK), Material.RED_NETHER_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BONE_BLOCK), Material.BONE_BLOCK);
		
		// Colored Shulker Boxes
		nukkitToBukkit[BlockID.SHULKER_BOX] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 0), Material.WHITE_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 1), Material.ORANGE_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 2), Material.MAGENTA_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 3), Material.LIGHT_BLUE_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 4), Material.YELLOW_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 5), Material.LIME_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 6), Material.PINK_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 7), Material.GRAY_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 8), Material.LIGHT_GRAY_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 9), Material.CYAN_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 10), Material.PURPLE_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 11), Material.BLUE_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 12), Material.BROWN_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 13), Material.GREEN_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 14), Material.RED_SHULKER_BOX);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX, 15), Material.BLACK_SHULKER_BOX);
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PURPLE_GLAZED_TERRACOTTA), Material.PURPLE_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WHITE_GLAZED_TERRACOTTA), Material.WHITE_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ORANGE_GLAZED_TERRACOTTA), Material.ORANGE_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MAGENTA_GLAZED_TERRACOTTA), Material.MAGENTA_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA), Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.YELLOW_GLAZED_TERRACOTTA), Material.YELLOW_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIME_GLAZED_TERRACOTTA), Material.LIME_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PINK_GLAZED_TERRACOTTA), Material.PINK_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRAY_GLAZED_TERRACOTTA), Material.GRAY_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SILVER_GLAZED_TERRACOTTA), Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CYAN_GLAZED_TERRACOTTA), Material.CYAN_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLUE_GLAZED_TERRACOTTA), Material.BLUE_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BROWN_GLAZED_TERRACOTTA), Material.BROWN_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GREEN_GLAZED_TERRACOTTA), Material.GREEN_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_GLAZED_TERRACOTTA), Material.RED_GLAZED_TERRACOTTA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACK_GLAZED_TERRACOTTA), Material.BLACK_GLAZED_TERRACOTTA);

		// Concrete
		nukkitToBukkit[BlockID.CONCRETE] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 0), Material.WHITE_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 1), Material.ORANGE_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 2), Material.MAGENTA_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 3), Material.LIGHT_BLUE_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 4), Material.YELLOW_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 5), Material.LIME_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 6), Material.PINK_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 7), Material.GRAY_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 8), Material.LIGHT_GRAY_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 9), Material.CYAN_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 10), Material.PURPLE_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 11), Material.BLUE_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 12), Material.BROWN_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 13), Material.GREEN_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 14), Material.RED_CONCRETE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE, 15), Material.BLACK_CONCRETE);
		
		// Concrete Powder
		nukkitToBukkit[BlockID.CONCRETE_POWDER] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 0), Material.WHITE_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 1), Material.ORANGE_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 2), Material.MAGENTA_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 3), Material.LIGHT_BLUE_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 4), Material.YELLOW_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 5), Material.LIME_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 6), Material.PINK_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 7), Material.GRAY_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 8), Material.LIGHT_GRAY_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 9), Material.CYAN_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 10), Material.PURPLE_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 11), Material.BLUE_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 12), Material.BROWN_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 13), Material.GREEN_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 14), Material.RED_CONCRETE_POWDER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER, 15), Material.BLACK_CONCRETE_POWDER);
		
		
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHORUS_PLANT), Material.CHORUS_PLANT);
		
		// Stained glass
		nukkitToBukkit[BlockID.STAINED_GLASS] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 0), Material.WHITE_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 1), Material.ORANGE_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 2), Material.MAGENTA_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 3), Material.LIGHT_BLUE_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 4), Material.YELLOW_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 5), Material.LIME_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 6), Material.PINK_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 7), Material.GRAY_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 8), Material.LIGHT_GRAY_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 9), Material.CYAN_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 10), Material.PURPLE_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 11), Material.BLUE_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 12), Material.BROWN_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 13), Material.GREEN_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 14), Material.RED_STAINED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS, 15), Material.BLACK_STAINED_GLASS);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PODZOL), Material.PODZOL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BEETROOT_BLOCK), Material.BEETROOTS);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.STONECUTTER), Material.COBBLESTONE);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.GLOWING_OBSIDIAN), Material.OBSIDIAN);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.NETHER_REACTOR), Material.RED_NETHER_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PISTON_EXTENSION), Material.MOVING_PISTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OBSERVER), Material.OBSERVER);

		// Command Blocks
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COMMAND_BLOCK), Material.COMMAND_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.REPEATING_COMMAND_BLOCK), Material.REPEATING_COMMAND_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHAIN_COMMAND_BLOCK), Material.CHAIN_COMMAND_BLOCK);

		// Structure
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRUCTURE_BLOCK), Material.STRUCTURE_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRUCTURE_VOID), Material.STRUCTURE_VOID);

		// Stripped Logs (ID 260-265)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_SPRUCE_LOG), Material.STRIPPED_SPRUCE_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_BIRCH_LOG), Material.STRIPPED_BIRCH_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_JUNGLE_LOG), Material.STRIPPED_JUNGLE_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_ACACIA_LOG), Material.STRIPPED_ACACIA_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_DARK_OAK_LOG), Material.STRIPPED_DARK_OAK_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_OAK_LOG), Material.STRIPPED_OAK_LOG);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLUE_ICE), Material.BLUE_ICE);

		// Prismarine Stairs
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PRISMARINE_STAIRS), Material.PRISMARINE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_PRISMARINE_STAIRS), Material.DARK_PRISMARINE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PRISMARINE_BRICKS_STAIRS), Material.PRISMARINE_BRICK_STAIRS);

		// Seagrass & Coral (ID 385-392)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SEAGRASS), Material.SEAGRASS);
		nukkitToBukkit[BlockID.CORAL] = new Material[16];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL, 0), Material.TUBE_CORAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL, 1), Material.BRAIN_CORAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL, 2), Material.BUBBLE_CORAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL, 3), Material.FIRE_CORAL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL, 4), Material.HORN_CORAL);
		nukkitToBukkit[BlockID.CORAL][5] = Material.TUBE_CORAL;
		nukkitToBukkit[BlockID.CORAL][6] = Material.BRAIN_CORAL;
		nukkitToBukkit[BlockID.CORAL][7] = Material.BUBBLE_CORAL;
		nukkitToBukkit[BlockID.CORAL][8] = Material.FIRE_CORAL;
		nukkitToBukkit[BlockID.CORAL][9] = Material.HORN_CORAL;

		nukkitToBukkit[BlockID.CORAL_BLOCK] = new Material[10];
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL_BLOCK, 0), Material.TUBE_CORAL_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL_BLOCK, 1), Material.BRAIN_CORAL_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL_BLOCK, 2), Material.BUBBLE_CORAL_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL_BLOCK, 3), Material.FIRE_CORAL_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CORAL_BLOCK, 4), Material.HORN_CORAL_BLOCK);
		nukkitToBukkit[BlockID.CORAL_BLOCK][5] = Material.TUBE_CORAL_BLOCK;
		nukkitToBukkit[BlockID.CORAL_BLOCK][6] = Material.BRAIN_CORAL_BLOCK;
		nukkitToBukkit[BlockID.CORAL_BLOCK][7] = Material.BUBBLE_CORAL_BLOCK;
		nukkitToBukkit[BlockID.CORAL_BLOCK][8] = Material.FIRE_CORAL_BLOCK;
		nukkitToBukkit[BlockID.CORAL_BLOCK][9] = Material.HORN_CORAL_BLOCK;

		// Kelp
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLOCK_KELP), Material.KELP);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DRIED_KELP_BLOCK), Material.DRIED_KELP_BLOCK);

		// Wood-type buttons (ID 395-399)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_BUTTON), Material.ACACIA_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_BUTTON), Material.BIRCH_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_BUTTON), Material.DARK_OAK_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_BUTTON), Material.JUNGLE_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_BUTTON), Material.SPRUCE_BUTTON);

		// Wood-type trapdoors (ID 400-404)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_TRAPDOOR), Material.ACACIA_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_TRAPDOOR), Material.BIRCH_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_TRAPDOOR), Material.DARK_OAK_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_TRAPDOOR), Material.JUNGLE_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_TRAPDOOR), Material.SPRUCE_TRAPDOOR);

		// Wood-type pressure plates (ID 405-409)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_PRESSURE_PLATE), Material.ACACIA_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_PRESSURE_PLATE), Material.BIRCH_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_PRESSURE_PLATE), Material.DARK_OAK_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_PRESSURE_PLATE), Material.JUNGLE_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_PRESSURE_PLATE), Material.SPRUCE_PRESSURE_PLATE);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARVED_PUMPKIN), Material.CARVED_PUMPKIN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SEA_PICKLE), Material.SEA_PICKLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CONDUIT), Material.CONDUIT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TURTLE_EGG), Material.TURTLE_EGG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BARRIER), Material.BARRIER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO), Material.BAMBOO);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCAFFOLDING), Material.SCAFFOLDING);

		// Stairs (ID 424-440)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRANITE_STAIRS), Material.GRANITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DIORITE_STAIRS), Material.DIORITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ANDESITE_STAIRS), Material.ANDESITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_GRANITE_STAIRS), Material.POLISHED_GRANITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_DIORITE_STAIRS), Material.POLISHED_DIORITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_ANDESITE_STAIRS), Material.POLISHED_ANDESITE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MOSSY_STONE_BRICK_STAIRS), Material.MOSSY_STONE_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOOTH_RED_SANDSTONE_STAIRS), Material.SMOOTH_RED_SANDSTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOOTH_SANDSTONE_STAIRS), Material.SMOOTH_SANDSTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.END_BRICK_STAIRS), Material.END_STONE_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MOSSY_COBBLESTONE_STAIRS), Material.MOSSY_COBBLESTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NORMAL_STONE_STAIRS), Material.STONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_NETHER_BRICK_STAIRS), Material.RED_NETHER_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOOTH_QUARTZ_STAIRS), Material.SMOOTH_QUARTZ_STAIRS);

		// Signs (ID 436-448)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_STANDING_SIGN), Material.SPRUCE_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_WALL_SIGN), Material.SPRUCE_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOOTH_STONE), Material.SMOOTH_STONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_STANDING_SIGN), Material.BIRCH_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_WALL_SIGN), Material.BIRCH_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_STANDING_SIGN), Material.JUNGLE_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_WALL_SIGN), Material.JUNGLE_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_STANDING_SIGN), Material.ACACIA_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_WALL_SIGN), Material.ACACIA_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARKOAK_STANDING_SIGN), Material.DARK_OAK_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARKOAK_WALL_SIGN), Material.DARK_OAK_WALL_SIGN);

		// Village & workstation blocks (ID 449-469)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LECTERN), Material.LECTERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRINDSTONE), Material.GRINDSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLAST_FURNACE), Material.BLAST_FURNACE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STONECUTTER_BLOCK), Material.STONECUTTER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOKER), Material.SMOKER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CARTOGRAPHY_TABLE), Material.CARTOGRAPHY_TABLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FLETCHING_TABLE), Material.FLETCHING_TABLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMITHING_TABLE), Material.SMITHING_TABLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BARREL), Material.BARREL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LOOM), Material.LOOM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BELL), Material.BELL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LANTERN), Material.LANTERN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CAMPFIRE_BLOCK), Material.CAMPFIRE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COMPOSTER), Material.COMPOSTER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WITHER_ROSE), Material.WITHER_ROSE);

		// Sticky piston head
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.PISTON_HEAD_STICKY), Material.STICKY_PISTON);

		// Bees (ID 473-476)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BEE_NEST), Material.BEE_NEST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BEEHIVE), Material.BEEHIVE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HONEY_BLOCK), Material.HONEY_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HONEYCOMB_BLOCK), Material.HONEYCOMB_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LODESTONE), Material.LODESTONE);

		// Nether blocks (ID 478-498)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_ROOTS), Material.CRIMSON_ROOTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_ROOTS), Material.WARPED_ROOTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_STEM), Material.CRIMSON_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_STEM), Material.WARPED_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_WART_BLOCK), Material.WARPED_WART_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_FUNGUS), Material.CRIMSON_FUNGUS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_FUNGUS), Material.WARPED_FUNGUS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SHROOMLIGHT), Material.SHROOMLIGHT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEEPING_VINES), Material.WEEPING_VINES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_NYLIUM), Material.CRIMSON_NYLIUM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_NYLIUM), Material.WARPED_NYLIUM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BASALT), Material.BASALT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BASALT), Material.POLISHED_BASALT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_SOIL), Material.SOUL_SOIL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_FIRE), Material.SOUL_FIRE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_SPROUTS_BLOCK), Material.NETHER_SPROUTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TARGET), Material.TARGET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_CRIMSON_STEM), Material.STRIPPED_CRIMSON_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_WARPED_STEM), Material.STRIPPED_WARPED_STEM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_PLANKS), Material.CRIMSON_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_PLANKS), Material.WARPED_PLANKS);

		// Crimson & Warped doors/trapdoors (ID 499-502)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_DOOR_BLOCK), Material.CRIMSON_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_DOOR_BLOCK), Material.WARPED_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_TRAPDOOR), Material.CRIMSON_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_TRAPDOOR), Material.WARPED_TRAPDOOR);

		// Crimson & Warped signs (ID 505-508)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_STANDING_SIGN), Material.CRIMSON_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_STANDING_SIGN), Material.WARPED_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_WALL_SIGN), Material.CRIMSON_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_WALL_SIGN), Material.WARPED_WALL_SIGN);

		// Crimson & Warped stairs (ID 509-510)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_STAIRS), Material.CRIMSON_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_STAIRS), Material.WARPED_STAIRS);

		// Crimson & Warped fences, gates, buttons, pressure plates, slabs (ID 511-522)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_FENCE), Material.CRIMSON_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_FENCE), Material.WARPED_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_FENCE_GATE), Material.CRIMSON_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_FENCE_GATE), Material.WARPED_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_BUTTON), Material.CRIMSON_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_BUTTON), Material.WARPED_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_PRESSURE_PLATE), Material.CRIMSON_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_PRESSURE_PLATE), Material.WARPED_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_SLAB), Material.CRIMSON_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_SLAB), Material.WARPED_SLAB);

		// Soul torch & lantern (ID 523-524)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_TORCH), Material.SOUL_TORCH);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_LANTERN), Material.SOUL_LANTERN);

		// Netherite, Ancient Debris, Respawn Anchor (ID 525-527)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHERITE_BLOCK), Material.NETHERITE_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ANCIENT_DEBRIS), Material.ANCIENT_DEBRIS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RESPAWN_ANCHOR), Material.RESPAWN_ANCHOR);

		// Blackstone blocks (ID 528-552)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACKSTONE), Material.BLACKSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_BRICKS), Material.POLISHED_BLACKSTONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_BRICK_STAIRS), Material.POLISHED_BLACKSTONE_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACKSTONE_STAIRS), Material.BLACKSTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACKSTONE_WALL), Material.BLACKSTONE_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_BRICK_WALL), Material.POLISHED_BLACKSTONE_BRICK_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_POLISHED_BLACKSTONE), Material.CHISELED_POLISHED_BLACKSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS), Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GILDED_BLACKSTONE), Material.GILDED_BLACKSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACKSTONE_SLAB), Material.BLACKSTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_BRICK_SLAB), Material.POLISHED_BLACKSTONE_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE), Material.POLISHED_BLACKSTONE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_STAIRS), Material.POLISHED_BLACKSTONE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_SLAB), Material.POLISHED_BLACKSTONE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_PRESSURE_PLATE), Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_BUTTON), Material.POLISHED_BLACKSTONE_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_BLACKSTONE_WALL), Material.POLISHED_BLACKSTONE_WALL);

		// Warped/Crimson Hyphae (ID 553-556)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_HYPHAE), Material.WARPED_HYPHAE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_HYPHAE), Material.CRIMSON_HYPHAE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_CRIMSON_HYPHAE), Material.STRIPPED_CRIMSON_HYPHAE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_WARPED_HYPHAE), Material.STRIPPED_WARPED_HYPHAE);

		// More nether blocks (ID 557-559)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_NETHER_BRICKS), Material.CHISELED_NETHER_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRACKED_NETHER_BRICKS), Material.CRACKED_NETHER_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.QUARTZ_BRICKS), Material.QUARTZ_BRICKS);

		// Chain, Twisting/Weeping Vines, Nether Gold, Crying Obsidian, Soul Campfire (ID 541-545)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHAIN_BLOCK), Material.CHAIN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TWISTING_VINES), Material.TWISTING_VINES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.NETHER_GOLD_ORE), Material.NETHER_GOLD_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRYING_OBSIDIAN), Material.CRYING_OBSIDIAN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SOUL_CAMPFIRE_BLOCK), Material.SOUL_CAMPFIRE);

		// Powder Snow, Sculk Sensor, Pointed Dripstone (ID 561-563)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POWDER_SNOW), Material.POWDER_SNOW);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCULK_SENSOR), Material.SCULK_SENSOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POINTED_DRIPSTONE), Material.POINTED_DRIPSTONE);

		// Copper Ore, Lightning Rod (ID 566-567)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_ORE), Material.COPPER_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIGHTNING_ROD), Material.LIGHTNING_ROD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRAFTER), Material.CRAFTER);

		// Dripstone, Rooted Dirt, Hanging Roots, Moss, etc. (ID 572-593)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DRIPSTONE_BLOCK), Material.DRIPSTONE_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ROOTED_DIRT), Material.ROOTED_DIRT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.HANGING_ROOTS), Material.HANGING_ROOTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MOSS_BLOCK), Material.MOSS_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPORE_BLOSSOM), Material.SPORE_BLOSSOM);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CAVE_VINES), Material.CAVE_VINES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIG_DRIPLEAF), Material.BIG_DRIPLEAF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AZALEA_LEAVES), Material.AZALEA_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AZALEA_LEAVES_FLOWERED), Material.FLOWERING_AZALEA_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CALCITE), Material.CALCITE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AMETHYST_BLOCK), Material.AMETHYST_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BUDDING_AMETHYST), Material.BUDDING_AMETHYST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AMETHYST_CLUSTER), Material.AMETHYST_CLUSTER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF), Material.TUFF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TINTED_GLASS), Material.TINTED_GLASS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MOSS_CARPET), Material.MOSS_CARPET);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMALL_DRIPLEAF), Material.SMALL_DRIPLEAF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.AZALEA), Material.AZALEA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.FLOWERING_AZALEA), Material.FLOWERING_AZALEA);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GLOW_FRAME), Material.GLOW_ITEM_FRAME);

		// Copper blocks (ID 595-629)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_BLOCK), Material.COPPER_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_COPPER), Material.EXPOSED_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_COPPER), Material.WEATHERED_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_COPPER), Material.OXIDIZED_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_COPPER), Material.WAXED_COPPER_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_EXPOSED_COPPER), Material.WAXED_EXPOSED_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_WEATHERED_COPPER), Material.WAXED_WEATHERED_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CUT_COPPER), Material.CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_CUT_COPPER), Material.EXPOSED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_CUT_COPPER), Material.WEATHERED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_CUT_COPPER), Material.OXIDIZED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_CUT_COPPER), Material.WAXED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER), Material.WAXED_EXPOSED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER), Material.WAXED_WEATHERED_CUT_COPPER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CUT_COPPER_STAIRS), Material.CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_CUT_COPPER_STAIRS), Material.EXPOSED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_CUT_COPPER_STAIRS), Material.WEATHERED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_CUT_COPPER_STAIRS), Material.OXIDIZED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_CUT_COPPER_STAIRS), Material.WAXED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER_STAIRS), Material.WAXED_EXPOSED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER_STAIRS), Material.WAXED_WEATHERED_CUT_COPPER_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CUT_COPPER_SLAB), Material.CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_CUT_COPPER_SLAB), Material.EXPOSED_CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_CUT_COPPER_SLAB), Material.WEATHERED_CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_CUT_COPPER_SLAB), Material.OXIDIZED_CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_CUT_COPPER_SLAB), Material.WAXED_CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER_SLAB), Material.WAXED_EXPOSED_CUT_COPPER_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER_SLAB), Material.WAXED_WEATHERED_CUT_COPPER_SLAB);

		// Cave Vines with berries
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.CAVE_VINES_BODY_WITH_BERRIES), Material.CAVE_VINES);
		registerNukkitToBukkit(cn.nukkit.block.Block.get(BlockID.CAVE_VINES_HEAD_WITH_BERRIES), Material.CAVE_VINES);

		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SMOOTH_BASALT), Material.SMOOTH_BASALT);

		// Deepslate (ID 633-665)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE), Material.DEEPSLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLED_DEEPSLATE), Material.COBBLED_DEEPSLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLED_DEEPSLATE_SLAB), Material.COBBLED_DEEPSLATE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLED_DEEPSLATE_STAIRS), Material.COBBLED_DEEPSLATE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COBBLED_DEEPSLATE_WALL), Material.COBBLED_DEEPSLATE_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_DEEPSLATE), Material.POLISHED_DEEPSLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_DEEPSLATE_SLAB), Material.POLISHED_DEEPSLATE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_DEEPSLATE_STAIRS), Material.POLISHED_DEEPSLATE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_DEEPSLATE_WALL), Material.POLISHED_DEEPSLATE_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_TILES), Material.DEEPSLATE_TILES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_TILE_SLAB), Material.DEEPSLATE_TILE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_TILE_STAIRS), Material.DEEPSLATE_TILE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_TILE_WALL), Material.DEEPSLATE_TILE_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_BRICKS), Material.DEEPSLATE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_BRICK_SLAB), Material.DEEPSLATE_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_BRICK_STAIRS), Material.DEEPSLATE_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_BRICK_WALL), Material.DEEPSLATE_BRICK_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_DEEPSLATE), Material.CHISELED_DEEPSLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_LAPIS_ORE), Material.DEEPSLATE_LAPIS_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_IRON_ORE), Material.DEEPSLATE_IRON_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_GOLD_ORE), Material.DEEPSLATE_GOLD_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_REDSTONE_ORE), Material.DEEPSLATE_REDSTONE_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_DIAMOND_ORE), Material.DEEPSLATE_DIAMOND_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_COAL_ORE), Material.DEEPSLATE_COAL_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_EMERALD_ORE), Material.DEEPSLATE_EMERALD_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DEEPSLATE_COPPER_ORE), Material.DEEPSLATE_COPPER_ORE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRACKED_DEEPSLATE_TILES), Material.CRACKED_DEEPSLATE_TILES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRACKED_DEEPSLATE_BRICKS), Material.CRACKED_DEEPSLATE_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GLOW_LICHEN), Material.GLOW_LICHEN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.INFESTED_DEEPSLATE), Material.INFESTED_DEEPSLATE);

		// Candles (ID 667-700)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CANDLE), Material.CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WHITE_CANDLE), Material.WHITE_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ORANGE_CANDLE), Material.ORANGE_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MAGENTA_CANDLE), Material.MAGENTA_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIGHT_BLUE_CANDLE), Material.LIGHT_BLUE_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.YELLOW_CANDLE), Material.YELLOW_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIME_CANDLE), Material.LIME_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PINK_CANDLE), Material.PINK_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GRAY_CANDLE), Material.GRAY_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.LIGHT_GRAY_CANDLE), Material.LIGHT_GRAY_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CYAN_CANDLE), Material.CYAN_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PURPLE_CANDLE), Material.PURPLE_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLUE_CANDLE), Material.BLUE_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BROWN_CANDLE), Material.BROWN_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.GREEN_CANDLE), Material.GREEN_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RED_CANDLE), Material.RED_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BLACK_CANDLE), Material.BLACK_CANDLE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CANDLE_CAKE), Material.CANDLE_CAKE);

		// Raw ore blocks (ID 706-708)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RAW_IRON_BLOCK), Material.RAW_IRON_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RAW_COPPER_BLOCK), Material.RAW_COPPER_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.RAW_GOLD_BLOCK), Material.RAW_GOLD_BLOCK);

		// Sculk (ID 713-716)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCULK), Material.SCULK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCULK_VEIN), Material.SCULK_VEIN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCULK_CATALYST), Material.SCULK_CATALYST);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SCULK_SHRIEKER), Material.SCULK_SHRIEKER);

		// Froglight (ID 724-726)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PEARLESCENT_FROGLIGHT), Material.PEARLESCENT_FROGLIGHT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.VERDANT_FROGLIGHT), Material.VERDANT_FROGLIGHT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OCHRE_FROGLIGHT), Material.OCHRE_FROGLIGHT);

		// Mangrove (ID 727-754)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_LEAVES), Material.MANGROVE_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUD), Material.MUD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_PROPAGULE), Material.MANGROVE_PROPAGULE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUD_BRICKS), Material.MUD_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PACKED_MUD), Material.PACKED_MUD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUD_BRICK_SLAB), Material.MUD_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUD_BRICK_STAIRS), Material.MUD_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUD_BRICK_WALL), Material.MUD_BRICK_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_ROOTS), Material.MANGROVE_ROOTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MUDDY_MANGROVE_ROOTS), Material.MUDDY_MANGROVE_ROOTS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_LOG), Material.MANGROVE_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_MANGROVE_LOG), Material.STRIPPED_MANGROVE_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_PLANKS), Material.MANGROVE_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_BUTTON), Material.MANGROVE_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_STAIRS), Material.MANGROVE_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_SLAB), Material.MANGROVE_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_PRESSURE_PLATE), Material.MANGROVE_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_FENCE), Material.MANGROVE_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_FENCE_GATE), Material.MANGROVE_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_DOOR_BLOCK), Material.MANGROVE_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_STANDING_SIGN), Material.MANGROVE_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_WALL_SIGN), Material.MANGROVE_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_TRAPDOOR), Material.MANGROVE_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_WOOD), Material.MANGROVE_WOOD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_MANGROVE_WOOD), Material.STRIPPED_MANGROVE_WOOD);

		// Hanging Signs (ID 755-763)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OAK_HANGING_SIGN), Material.OAK_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SPRUCE_HANGING_SIGN), Material.SPRUCE_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BIRCH_HANGING_SIGN), Material.BIRCH_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.JUNGLE_HANGING_SIGN), Material.JUNGLE_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.ACACIA_HANGING_SIGN), Material.ACACIA_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DARK_OAK_HANGING_SIGN), Material.DARK_OAK_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CRIMSON_HANGING_SIGN), Material.CRIMSON_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WARPED_HANGING_SIGN), Material.WARPED_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.MANGROVE_HANGING_SIGN), Material.MANGROVE_HANGING_SIGN);

		// Bamboo (ID 764-783)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_MOSAIC), Material.BAMBOO_MOSAIC);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_PLANKS), Material.BAMBOO_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_BUTTON), Material.BAMBOO_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_STAIRS), Material.BAMBOO_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_SLAB), Material.BAMBOO_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_PRESSURE_PLATE), Material.BAMBOO_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_FENCE), Material.BAMBOO_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_FENCE_GATE), Material.BAMBOO_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_DOOR), Material.BAMBOO_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_STANDING_SIGN), Material.BAMBOO_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_WALL_SIGN), Material.BAMBOO_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_TRAPDOOR), Material.BAMBOO_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_HANGING_SIGN), Material.BAMBOO_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_MOSAIC_STAIRS), Material.BAMBOO_MOSAIC_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_MOSAIC_SLAB), Material.BAMBOO_MOSAIC_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_BOOKSHELF), Material.CHISELED_BOOKSHELF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.BAMBOO_BLOCK), Material.BAMBOO_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_BAMBOO_BLOCK), Material.STRIPPED_BAMBOO_BLOCK);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SUSPICIOUS_SAND), Material.SUSPICIOUS_SAND);

		// Cherry (ID 785-804)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_BUTTON), Material.CHERRY_BUTTON);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_DOOR_BLOCK), Material.CHERRY_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_FENCE), Material.CHERRY_FENCE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_FENCE_GATE), Material.CHERRY_FENCE_GATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_HANGING_SIGN), Material.CHERRY_HANGING_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_CHERRY_LOG), Material.STRIPPED_CHERRY_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_LOG), Material.CHERRY_LOG);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_PLANKS), Material.CHERRY_PLANKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_PRESSURE_PLATE), Material.CHERRY_PRESSURE_PLATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_SLAB), Material.CHERRY_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_STAIRS), Material.CHERRY_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_STANDING_SIGN), Material.CHERRY_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_TRAPDOOR), Material.CHERRY_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_WALL_SIGN), Material.CHERRY_WALL_SIGN);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.STRIPPED_CHERRY_WOOD), Material.STRIPPED_CHERRY_WOOD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_WOOD), Material.CHERRY_WOOD);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_SAPLING), Material.CHERRY_SAPLING);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHERRY_LEAVES), Material.CHERRY_LEAVES);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PINK_PETALS), Material.PINK_PETALS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.DECORATED_POT), Material.DECORATED_POT);

		// Torchflower, Pitcher
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TORCHFLOWER), Material.TORCHFLOWER);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SUSPICIOUS_GRAVEL), Material.SUSPICIOUS_GRAVEL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PITCHER_PLANT), Material.PITCHER_PLANT);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.SNIFFER_EGG), Material.SNIFFER_EGG);

		// Tuff variants (ID 999-1014)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_SLAB), Material.TUFF_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_STAIRS), Material.TUFF_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_WALL), Material.TUFF_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_TUFF), Material.POLISHED_TUFF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_TUFF_SLAB), Material.POLISHED_TUFF_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_TUFF_STAIRS), Material.POLISHED_TUFF_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.POLISHED_TUFF_WALL), Material.POLISHED_TUFF_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_TUFF), Material.CHISELED_TUFF);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_BRICKS), Material.TUFF_BRICKS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_BRICK_SLAB), Material.TUFF_BRICK_SLAB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_BRICK_STAIRS), Material.TUFF_BRICK_STAIRS);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.TUFF_BRICK_WALL), Material.TUFF_BRICK_WALL);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.CHISELED_TUFF_BRICKS), Material.CHISELED_TUFF_BRICKS);

		// Copper Grate, Bulb, Door, Trapdoor (ID 1015-1054)
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_GRATE), Material.COPPER_GRATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_COPPER_GRATE), Material.EXPOSED_COPPER_GRATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_COPPER_GRATE), Material.WEATHERED_COPPER_GRATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_COPPER_GRATE), Material.OXIDIZED_COPPER_GRATE);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_BULB), Material.COPPER_BULB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_COPPER_BULB), Material.EXPOSED_COPPER_BULB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_COPPER_BULB), Material.WEATHERED_COPPER_BULB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_COPPER_BULB), Material.OXIDIZED_COPPER_BULB);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_DOOR), Material.COPPER_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_COPPER_DOOR), Material.EXPOSED_COPPER_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_COPPER_DOOR), Material.WEATHERED_COPPER_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_COPPER_DOOR), Material.OXIDIZED_COPPER_DOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.COPPER_TRAPDOOR), Material.COPPER_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.EXPOSED_COPPER_TRAPDOOR), Material.EXPOSED_COPPER_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.WEATHERED_COPPER_TRAPDOOR), Material.WEATHERED_COPPER_TRAPDOOR);
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.OXIDIZED_COPPER_TRAPDOOR), Material.OXIDIZED_COPPER_TRAPDOOR);

		// Piglin head
		registerTwoWay(cn.nukkit.block.Block.get(BlockID.PIGLIN_HEAD), Material.PIGLIN_HEAD);

		// Legacy materials
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.AIR), Material.LEGACY_AIR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE), Material.LEGACY_STONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GRASS), Material.LEGACY_GRASS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DIRT), Material.LEGACY_DIRT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COBBLESTONE), Material.LEGACY_COBBLESTONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOOD), Material.LEGACY_WOOD);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SAPLING), Material.LEGACY_SAPLING);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BEDROCK), Material.LEGACY_BEDROCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WATER), Material.LEGACY_WATER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STILL_WATER), Material.LEGACY_STATIONARY_WATER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LAVA), Material.LEGACY_LAVA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STILL_LAVA), Material.LEGACY_STATIONARY_LAVA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SAND), Material.LEGACY_SAND);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GRAVEL), Material.LEGACY_GRAVEL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GOLD_ORE), Material.LEGACY_GOLD_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.IRON_ORE), Material.LEGACY_IRON_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COAL_ORE), Material.LEGACY_COAL_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LOG), Material.LEGACY_LOG);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LEAVES), Material.LEGACY_LEAVES);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SPONGE), Material.LEGACY_SPONGE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GLASS), Material.LEGACY_GLASS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LAPIS_ORE), Material.LEGACY_LAPIS_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LAPIS_BLOCK), Material.LEGACY_LAPIS_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DISPENSER), Material.LEGACY_DISPENSER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SANDSTONE), Material.LEGACY_SANDSTONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NOTEBLOCK), Material.LEGACY_NOTE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BED_BLOCK), Material.LEGACY_BED_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.POWERED_RAIL), Material.LEGACY_POWERED_RAIL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DETECTOR_RAIL), Material.LEGACY_DETECTOR_RAIL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STICKY_PISTON), Material.LEGACY_PISTON_STICKY_BASE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COBWEB), Material.LEGACY_WEB);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TALL_GRASS), Material.LEGACY_LONG_GRASS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DEAD_BUSH), Material.LEGACY_DEAD_BUSH);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PISTON), Material.LEGACY_PISTON_BASE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PISTON_EXTENSION), Material.LEGACY_PISTON_EXTENSION);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOOL), Material.LEGACY_WOOL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PISTON_HEAD), Material.LEGACY_PISTON_MOVING_PIECE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FLOWER), Material.LEGACY_YELLOW_FLOWER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_FLOWER), Material.LEGACY_RED_ROSE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BROWN_MUSHROOM), Material.LEGACY_BROWN_MUSHROOM);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_MUSHROOM), Material.LEGACY_RED_MUSHROOM);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GOLD_BLOCK), Material.LEGACY_GOLD_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.IRON_BLOCK), Material.LEGACY_IRON_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_STONE_SLAB), Material.LEGACY_DOUBLE_STEP);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_SLAB), Material.LEGACY_STEP);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BRICKS), Material.LEGACY_BRICK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TNT), Material.LEGACY_TNT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BOOKSHELF), Material.LEGACY_BOOKSHELF);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MOSS_STONE), Material.LEGACY_MOSSY_COBBLESTONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.OBSIDIAN), Material.LEGACY_OBSIDIAN);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TORCH), Material.LEGACY_TORCH);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FIRE), Material.LEGACY_FIRE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MONSTER_SPAWNER), Material.LEGACY_MOB_SPAWNER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOOD_STAIRS), Material.LEGACY_WOOD_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CHEST), Material.LEGACY_CHEST);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.REDSTONE_WIRE), Material.LEGACY_REDSTONE_WIRE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DIAMOND_ORE), Material.LEGACY_DIAMOND_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DIAMOND_BLOCK), Material.LEGACY_DIAMOND_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WORKBENCH), Material.LEGACY_WORKBENCH);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WHEAT_BLOCK), Material.LEGACY_CROPS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FARMLAND), Material.LEGACY_SOIL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FURNACE), Material.LEGACY_FURNACE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BURNING_FURNACE), Material.LEGACY_BURNING_FURNACE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SIGN_POST), Material.LEGACY_SIGN_POST);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DOOR_BLOCK), Material.LEGACY_WOODEN_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LADDER), Material.LEGACY_LADDER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RAIL), Material.LEGACY_RAILS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COBBLESTONE_STAIRS), Material.LEGACY_COBBLESTONE_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WALL_SIGN), Material.LEGACY_WALL_SIGN);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LEVER), Material.LEGACY_LEVER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_PRESSURE_PLATE), Material.LEGACY_STONE_PLATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.IRON_DOOR_BLOCK), Material.LEGACY_IRON_DOOR_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOODEN_PRESSURE_PLATE), Material.LEGACY_WOOD_PLATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.REDSTONE_ORE), Material.LEGACY_REDSTONE_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GLOWING_REDSTONE_ORE), Material.LEGACY_GLOWING_REDSTONE_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.UNLIT_REDSTONE_TORCH), Material.LEGACY_REDSTONE_TORCH_OFF);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.REDSTONE_TORCH), Material.LEGACY_REDSTONE_TORCH_ON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_BUTTON), Material.LEGACY_STONE_BUTTON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SNOW), Material.LEGACY_SNOW);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ICE), Material.LEGACY_ICE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SNOW_BLOCK), Material.LEGACY_SNOW_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CACTUS), Material.LEGACY_CACTUS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CLAY_BLOCK), Material.LEGACY_CLAY);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SUGARCANE_BLOCK), Material.LEGACY_SUGAR_CANE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.JUKEBOX), Material.LEGACY_JUKEBOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PUMPKIN), Material.LEGACY_PUMPKIN);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHERRACK), Material.LEGACY_NETHERRACK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SOUL_SAND), Material.LEGACY_SOUL_SAND);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GLOWSTONE), Material.LEGACY_GLOWSTONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_PORTAL), Material.LEGACY_PORTAL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.JACK_O_LANTERN), Material.LEGACY_JACK_O_LANTERN);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CAKE_BLOCK), Material.LEGACY_CAKE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.UNPOWERED_REPEATER), Material.LEGACY_DIODE_BLOCK_OFF);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.POWERED_REPEATER), Material.LEGACY_DIODE_BLOCK_ON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS), Material.LEGACY_STAINED_GLASS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TRAPDOOR), Material.LEGACY_TRAP_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MONSTER_EGG), Material.LEGACY_MONSTER_EGGS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_BRICK), Material.LEGACY_SMOOTH_BRICK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BROWN_MUSHROOM_BLOCK), Material.LEGACY_HUGE_MUSHROOM_1);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_MUSHROOM_BLOCK), Material.LEGACY_HUGE_MUSHROOM_2);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.IRON_BAR), Material.LEGACY_IRON_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GLASS_PANE), Material.LEGACY_THIN_GLASS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MELON_BLOCK), Material.LEGACY_MELON_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PUMPKIN_STEM), Material.LEGACY_PUMPKIN_STEM);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MELON_STEM), Material.LEGACY_MELON_STEM);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.VINE), Material.LEGACY_VINE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE), Material.LEGACY_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BRICK_STAIRS), Material.LEGACY_BRICK_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_BRICK_STAIRS), Material.LEGACY_SMOOTH_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MYCELIUM), Material.LEGACY_MYCEL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WATER_LILY), Material.LEGACY_WATER_LILY);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_BRICKS), Material.LEGACY_NETHER_BRICK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_BRICK_FENCE), Material.LEGACY_NETHER_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_BRICKS_STAIRS), Material.LEGACY_NETHER_BRICK_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_WART_BLOCK), Material.LEGACY_NETHER_WARTS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ENCHANTMENT_TABLE), Material.LEGACY_ENCHANTMENT_TABLE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BREWING_STAND_BLOCK), Material.LEGACY_BREWING_STAND);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CAULDRON_BLOCK), Material.LEGACY_CAULDRON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_PORTAL), Material.LEGACY_ENDER_PORTAL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_PORTAL_FRAME), Material.LEGACY_ENDER_PORTAL_FRAME);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_STONE), Material.LEGACY_ENDER_STONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DRAGON_EGG), Material.LEGACY_DRAGON_EGG);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LIT_REDSTONE_LAMP), Material.LEGACY_REDSTONE_LAMP_OFF);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.REDSTONE_LAMP), Material.LEGACY_REDSTONE_LAMP_ON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_WOOD_SLAB), Material.LEGACY_WOOD_DOUBLE_STEP);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOOD_SLAB), Material.LEGACY_WOOD_STEP);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COCOA), Material.LEGACY_COCOA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SANDSTONE_STAIRS), Material.LEGACY_SANDSTONE_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.EMERALD_ORE), Material.LEGACY_EMERALD_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ENDER_CHEST), Material.LEGACY_ENDER_CHEST);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TRIPWIRE_HOOK), Material.LEGACY_TRIPWIRE_HOOK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TRIPWIRE), Material.LEGACY_TRIPWIRE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.EMERALD_BLOCK), Material.LEGACY_EMERALD_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SPRUCE_WOOD_STAIRS), Material.LEGACY_SPRUCE_WOOD_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BIRCH_WOOD_STAIRS), Material.LEGACY_BIRCH_WOOD_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.JUNGLE_WOOD_STAIRS), Material.LEGACY_JUNGLE_WOOD_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BEACON), Material.LEGACY_BEACON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COBBLE_WALL), Material.LEGACY_COBBLE_WALL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FLOWER_POT_BLOCK), Material.LEGACY_FLOWER_POT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CARROT_BLOCK), Material.LEGACY_CARROT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.POTATO_BLOCK), Material.LEGACY_POTATO);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WOODEN_BUTTON), Material.LEGACY_WOOD_BUTTON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SKULL_BLOCK), Material.LEGACY_SKULL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ANVIL), Material.LEGACY_ANVIL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TRAPPED_CHEST), Material.LEGACY_TRAPPED_CHEST);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LIGHT_WEIGHTED_PRESSURE_PLATE), Material.LEGACY_GOLD_PLATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.HEAVY_WEIGHTED_PRESSURE_PLATE), Material.LEGACY_IRON_PLATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.UNPOWERED_COMPARATOR), Material.LEGACY_REDSTONE_COMPARATOR_OFF);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.POWERED_COMPARATOR), Material.LEGACY_REDSTONE_COMPARATOR_ON);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DAYLIGHT_DETECTOR), Material.LEGACY_DAYLIGHT_DETECTOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.REDSTONE_BLOCK), Material.LEGACY_REDSTONE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.QUARTZ_ORE), Material.LEGACY_QUARTZ_ORE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.HOPPER_BLOCK), Material.LEGACY_HOPPER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.QUARTZ_BLOCK), Material.LEGACY_QUARTZ_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.QUARTZ_STAIRS), Material.LEGACY_QUARTZ_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ACTIVATOR_RAIL), Material.LEGACY_ACTIVATOR_RAIL);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DROPPER), Material.LEGACY_DROPPER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STAINED_HARDENED_CLAY), Material.LEGACY_STAINED_CLAY);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STAINED_GLASS_PANE), Material.LEGACY_STAINED_GLASS_PANE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LEAVES2), Material.LEGACY_LEAVES_2);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LOG2), Material.LEGACY_LOG_2);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ACACIA_WOODEN_STAIRS), Material.LEGACY_ACACIA_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DARK_OAK_WOODEN_STAIRS), Material.LEGACY_DARK_OAK_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SLIME_BLOCK), Material.LEGACY_SLIME_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.INVISIBLE_BEDROCK), Material.LEGACY_BARRIER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.IRON_TRAPDOOR), Material.LEGACY_IRON_TRAPDOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PRISMARINE), Material.LEGACY_PRISMARINE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SEA_LANTERN), Material.LEGACY_SEA_LANTERN);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.HAY_BALE), Material.LEGACY_HAY_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CARPET), Material.LEGACY_CARPET);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.TERRACOTTA), Material.LEGACY_HARD_CLAY);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.COAL_BLOCK), Material.LEGACY_COAL_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PACKED_ICE), Material.LEGACY_PACKED_ICE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_PLANT), Material.LEGACY_DOUBLE_PLANT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DAYLIGHT_DETECTOR_INVERTED), Material.LEGACY_DAYLIGHT_DETECTOR_INVERTED);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE), Material.LEGACY_RED_SANDSTONE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_SANDSTONE_STAIRS), Material.LEGACY_RED_SANDSTONE_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DOUBLE_STONE_SLAB), Material.LEGACY_DOUBLE_STONE_SLAB2);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STONE_SLAB), Material.LEGACY_STONE_SLAB2);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_SPRUCE), Material.LEGACY_SPRUCE_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_BIRCH), Material.LEGACY_BIRCH_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_JUNGLE), Material.LEGACY_JUNGLE_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_DARK_OAK), Material.LEGACY_DARK_OAK_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE_GATE_ACACIA), Material.LEGACY_ACACIA_FENCE_GATE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_SPRUCE_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_BIRCH_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_JUNGLE_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_DARK_OAK_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.FENCE), Material.LEGACY_ACACIA_FENCE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SPRUCE_DOOR_BLOCK), Material.LEGACY_SPRUCE_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BIRCH_DOOR_BLOCK), Material.LEGACY_BIRCH_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.JUNGLE_DOOR_BLOCK), Material.LEGACY_JUNGLE_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ACACIA_DOOR_BLOCK), Material.LEGACY_ACACIA_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.DARK_OAK_DOOR_BLOCK), Material.LEGACY_DARK_OAK_DOOR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_ROD), Material.LEGACY_END_ROD);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CHORUS_PLANT), Material.LEGACY_CHORUS_PLANT);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CHORUS_FLOWER), Material.LEGACY_CHORUS_FLOWER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PURPUR_BLOCK), Material.LEGACY_PURPUR_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(202), Material.LEGACY_PURPUR_PILLAR);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PURPUR_STAIRS), Material.LEGACY_PURPUR_STAIRS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_BRICKS), Material.LEGACY_END_BRICKS);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BEETROOT_BLOCK), Material.LEGACY_BEETROOT_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GRASS_PATH), Material.LEGACY_GRASS_PATH);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.END_GATEWAY), Material.LEGACY_END_GATEWAY);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ICE_FROSTED), Material.LEGACY_FROSTED_ICE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MAGMA), Material.LEGACY_MAGMA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.NETHER_WART_BLOCK), Material.LEGACY_NETHER_WART_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_NETHER_BRICK), Material.LEGACY_RED_NETHER_BRICK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BONE_BLOCK), Material.LEGACY_BONE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.AIR), Material.LEGACY_STRUCTURE_VOID);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.OBSERVER), Material.LEGACY_OBSERVER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_WHITE_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_ORANGE_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_MAGENTA_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_LIGHT_BLUE_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_YELLOW_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_LIME_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_PINK_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_GRAY_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_SILVER_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_CYAN_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_PURPLE_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_BLUE_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_BROWN_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_GREEN_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_RED_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SHULKER_BOX), Material.LEGACY_BLACK_SHULKER_BOX);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WHITE_GLAZED_TERRACOTTA), Material.LEGACY_WHITE_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.ORANGE_GLAZED_TERRACOTTA), Material.LEGACY_ORANGE_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.MAGENTA_GLAZED_TERRACOTTA), Material.LEGACY_MAGENTA_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA), Material.LEGACY_LIGHT_BLUE_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.YELLOW_GLAZED_TERRACOTTA), Material.LEGACY_YELLOW_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.LIME_GLAZED_TERRACOTTA), Material.LEGACY_LIME_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PINK_GLAZED_TERRACOTTA), Material.LEGACY_PINK_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GRAY_GLAZED_TERRACOTTA), Material.LEGACY_GRAY_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.SILVER_GLAZED_TERRACOTTA), Material.LEGACY_SILVER_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CYAN_GLAZED_TERRACOTTA), Material.LEGACY_CYAN_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.PURPLE_GLAZED_TERRACOTTA), Material.LEGACY_PURPLE_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BLUE_GLAZED_TERRACOTTA), Material.LEGACY_BLUE_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BROWN_GLAZED_TERRACOTTA), Material.LEGACY_BROWN_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.GREEN_GLAZED_TERRACOTTA), Material.LEGACY_GREEN_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.RED_GLAZED_TERRACOTTA), Material.LEGACY_RED_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.BLACK_GLAZED_TERRACOTTA), Material.LEGACY_BLACK_GLAZED_TERRACOTTA);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CONCRETE), Material.LEGACY_CONCRETE);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.CONCRETE_POWDER), Material.LEGACY_CONCRETE_POWDER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.AIR), Material.LEGACY_STRUCTURE_BLOCK);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.STANDING_BANNER), Material.LEGACY_STANDING_BANNER);
		registerBukkitToNukkit(cn.nukkit.block.Block.get(BlockID.WALL_BANNER), Material.LEGACY_WALL_BANNER);
	}

	/**
	 * Gets the Bukkit (non-legacy) material for the given Nukkit block id.
	 *
	 * @param nukkitId
	 *            The Nukkit block id.
	 * @return The material, or AIR for negative Nukkit block ids, or STONE if
	 *         unknown.
	 */
	public static Material getMaterial(int nukkitId, int blockData) {
		if (nukkitId < 0) {
			return Material.AIR;
		}
		if (nukkitId >= nukkitToBukkit.length) {
			return Material.STONE;
		}
		Material material = nukkitToBukkit[nukkitId][blockData];
		if (material == null) {
			return Material.STONE;
		}
		return material;
	}

	public static Material getMaterialOrNull(int nukkitId, int blockData) {
		if (nukkitId < 0) {
			return null;
		}
		if (nukkitId >= nukkitToBukkit.length) {
			return Material.AIR;
		}
		if (blockData >= nukkitToBukkit[nukkitId].length) {
			return null;
		}
		return nukkitToBukkit[nukkitId][blockData];
	}

	/**
	 * Gets the Nukkit block for the Bukkit material. The Bukkit material may be
	 * a legacy material.
	 *
	 * @param material
	 *            The material.
	 * @param blockData
	 *            The block data, 0 by default.
	 * @return The block.
	 */
	public static Block getNukkitBlock(Material material, int blockData) {
		Block nukkit = bukkitToNukkit[material.ordinal()];
		return cn.nukkit.block.Block.get(nukkit.getId(), blockData);
	}

	private static void registerNukkitToBukkit(Block nukkit, Material bukkit) {
		nukkitToBukkit[nukkit.getId()][nukkit.getDamage()] = bukkit;
	}

	private static void registerBukkitToNukkit(Block nukkit, Material bukkit) {
		bukkitToNukkit[bukkit.ordinal()] = nukkit;
	}

	private static void registerTwoWay(Block nukkit, Material bukkit) {
		registerBukkitToNukkit(nukkit, bukkit);
		registerNukkitToBukkit(nukkit, bukkit);
	}
}
