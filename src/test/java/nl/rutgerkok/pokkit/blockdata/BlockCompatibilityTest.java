package nl.rutgerkok.pokkit.blockdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * Tests that the block API behaves like the Bukkit API, not just like
 * something that happens to compile.
 */
public class BlockCompatibilityTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
		Block.init();
	}

	@Test
	public void testMaterialBlockAndItemFlags() {
		// Registry.BLOCK and Registry.ITEM must contain the vanilla blocks and
		// items, otherwise every material reports that it is both.
		assertFalse(Material.DIAMOND_SWORD.isBlock());
		assertFalse(Material.STICK.isBlock());
		assertTrue(Material.STONE.isBlock());
		assertTrue(Material.WATER.isBlock());

		assertTrue(Material.DIAMOND_SWORD.isItem());
		assertFalse(Material.WATER.isItem());

		assertNull(Material.DIAMOND_SWORD.asBlockType());
		assertSame(BlockType.STONE, Material.STONE.asBlockType());
	}

	@Test
	public void testRegistriesContainAllVanillaKeys() {
		// BlockType and ItemType throw in their static initialiser when a key
		// is missing, so touching the constants proves the registries are
		// complete.
		assertEquals(1058, count(Registry.BLOCK));
		assertEquals(1333, count(Registry.ITEM));
		assertTrue(BlockType.WHITE_BED != null);
		assertTrue(BlockType.WAXED_OXIDIZED_COPPER != null);
	}

	private static int count(Registry<?> registry) {
		int size = 0;
		for (Object ignored : registry) {
			size++;
		}
		return size;
	}

	@Test
	public void testBlockTypeProperties() {
		assertEquals("minecraft:stone", BlockType.STONE.createBlockData().getAsString());
		assertTrue(BlockType.STONE.isSolid());
		assertFalse(BlockType.AIR.isSolid());
		assertEquals(1.5f, BlockType.STONE.getHardness(), 0.001f);
		assertEquals(0.6f, BlockType.STONE.getSlipperiness(), 0.001f);
		assertSame(Material.STONE, BlockType.STONE.asMaterial());
		assertSame(BlockType.STONE, BlockType.STONE.typed());
		assertFalse(BlockType.WATER.hasItemType());
		assertTrue(BlockType.STONE.hasItemType());

		assertTrue(BlockType.CHEST.isInteractable());
		assertTrue(BlockType.OAK_DOOR.isInteractable());
		assertFalse(BlockType.STONE.isInteractable());
	}

	@Test
	public void testBlockDataUsesValueEquality() {
		BlockData stone = Bukkit.createBlockData(Material.STONE);
		BlockData stoneAgain = Bukkit.createBlockData(Material.STONE);
		BlockData dirt = Bukkit.createBlockData(Material.DIRT);

		assertEquals(stone, stoneAgain);
		assertEquals(stone.hashCode(), stoneAgain.hashCode());
		assertNotEquals(stone, dirt);
		assertTrue(stone.matches(stoneAgain));
		assertFalse(stone.matches(dirt));
		assertFalse(stone.matches(null));
	}

	@Test
	public void testBlockDataStringFormat() {
		assertEquals("minecraft:stone", Bukkit.createBlockData(Material.STONE).getAsString());
		// The data value of the material must not leak into the string.
		assertEquals("minecraft:granite", Bukkit.createBlockData(Material.GRANITE).getAsString());
		assertEquals("minecraft:red_wool", Bukkit.createBlockData(Material.RED_WOOL).getAsString());
		assertEquals("minecraft:granite",
				Bukkit.createBlockData(Material.GRANITE).getAsString(true));

		assertEquals(Material.STONE, Bukkit.createBlockData("minecraft:stone").getMaterial());
		assertEquals(Material.OAK_STAIRS, Bukkit.createBlockData("minecraft:oak_stairs").getMaterial());
		assertEquals(Material.STONE, Bukkit.createBlockData("1:0").getMaterial());
		assertEquals(Material.GRANITE, Bukkit.createBlockData("minecraft:stone[data=1]").getMaterial());

		// Round trip
		for (Material material : new Material[] { Material.STONE, Material.GRANITE, Material.RED_WOOL,
				Material.OAK_STAIRS }) {
			BlockData data = Bukkit.createBlockData(material);
			assertEquals(material, Bukkit.createBlockData(data.getAsString()).getMaterial());
		}
	}

	@Test
	public void testBlockDataRejectsUnsupportedInput() {
		try {
			Bukkit.createBlockData(Material.DIAMOND_SWORD);
			fail("A non-block material must be rejected");
		} catch (IllegalArgumentException expected) {
		}

		try {
			// Block states cannot be represented by Nukkit; silently ignoring
			// them would place a different block.
			Bukkit.createBlockData("minecraft:oak_stairs[facing=east]");
			fail("Unsupported block states must be rejected");
		} catch (IllegalArgumentException expected) {
		}

		try {
			Bukkit.createBlockData("not_a_block");
			fail("Unknown materials must be rejected");
		} catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void testCreateBlockDataKeepsMaterialVariant() {
		// The data value that belongs to the material must be used; using the
		// data value of the caller turned granite into plain stone.
		assertEquals(Material.GRANITE, Bukkit.createBlockData(Material.GRANITE).getMaterial());
		assertEquals(Material.DIORITE, Bukkit.createBlockData(Material.DIORITE).getMaterial());
		assertEquals(Material.RED_WOOL, Bukkit.createBlockData(Material.RED_WOOL).getMaterial());
		assertEquals(Material.BIRCH_PLANKS, Bukkit.createBlockData(Material.BIRCH_PLANKS).getMaterial());
		assertEquals(Material.SHORT_GRASS, Bukkit.createBlockData(Material.SHORT_GRASS).getMaterial());
		assertEquals(Material.FERN, Bukkit.createBlockData(Material.FERN).getMaterial());
		assertEquals(Material.TALL_GRASS, Bukkit.createBlockData(Material.TALL_GRASS).getMaterial());
	}

	@Test
	public void testBlockMapDoesNotThrowOnUnknownData() {
		// Block data read from a world can contain values that are no longer
		// registered; that must not throw an array index exception.
		assertEquals(Material.STONE, BlockMap.getMaterial(BlockID.STONE, 7));
		assertEquals(Material.STONE, BlockMap.getMaterial(BlockID.STONE, 15));
		assertEquals(Material.WHITE_WOOL, BlockMap.getMaterial(BlockID.WOOL, 20));
		assertEquals(Material.STONE, BlockMap.getMaterial(9999, 0));
		assertNull(BlockMap.getMaterialOrNull(BlockID.STONE, 7));
		assertNull(BlockMap.getMaterialOrNull(9999, 0));
	}

	@Test
	public void testBlockDataProperties() {
		assertEquals(15, Bukkit.createBlockData(Material.GLOWSTONE).getLightEmission());
		assertEquals(0, Bukkit.createBlockData(Material.STONE).getLightEmission());

		assertTrue(Bukkit.createBlockData(Material.STONE).isOccluding());
		assertFalse(Bukkit.createBlockData(Material.GLASS).isOccluding());

		assertEquals(PistonMoveReaction.BLOCK, Bukkit.createBlockData(Material.OBSIDIAN).getPistonMoveReaction());
		assertEquals(PistonMoveReaction.MOVE, Bukkit.createBlockData(Material.STONE).getPistonMoveReaction());

		assertEquals(0x707070, Bukkit.createBlockData(Material.STONE).getMapColor().asRGB());
	}

	@Test
	public void testBlockStateCopyIsIndependent() {
		BlockState state = Bukkit.createBlockData(Material.STONE).createBlockState();
		assertEquals(Material.STONE, state.getType());

		BlockState copy = state.copy();
		copy.setType(Material.DIAMOND_BLOCK);
		assertEquals(Material.STONE, state.getType());
		assertEquals(Material.DIAMOND_BLOCK, copy.getType());

		BlockState placedCopy = state.copy(new org.bukkit.Location(null, 1, 2, 3));
		assertEquals(1, placedCopy.getX());
		assertEquals(2, placedCopy.getY());
		assertEquals(3, placedCopy.getZ());
	}

	@Test
	public void testBlockDataCopyTo() {
		BlockData stone = Bukkit.createBlockData(Material.STONE);
		BlockData dirt = Bukkit.createBlockData(Material.DIRT);
		stone.copyTo(dirt);
		assertEquals(Material.STONE, dirt.getMaterial());
		assertEquals(stone, dirt);
	}

	@Test
	public void testMergeRequiresSameMaterial() {
		BlockData stone = Bukkit.createBlockData(Material.STONE);
		assertEquals(stone, stone.merge(Bukkit.createBlockData(Material.STONE)));
		try {
			stone.merge(Bukkit.createBlockData(Material.GRANITE));
			fail("Merging different materials must be rejected");
		} catch (IllegalArgumentException expected) {
		}
	}
}
