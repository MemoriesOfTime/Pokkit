package nl.rutgerkok.pokkit.blockdata;

import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockstate.PokkitBlockState;
import nl.rutgerkok.pokkit.world.PokkitBlock;
import nl.rutgerkok.pokkit.world.PokkitBlockFace;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.utils.BlockColor;

/**
 * Implementation of {@link BlockData} on top of a Nukkit block.
 *
 * <p>
 * Nukkit does not expose the block state system of the Bedrock edition, so a
 * block is identified by its numeric id and data value. That means that the
 * properties of a block state (facing, half, waterlogged, ...) cannot be
 * represented: those are all encoded in the data value. The string form
 * therefore uses the Bukkit material key, with the data value as an explicit
 * property when it differs from the default state of the material.
 */
public class PokkitBlockData implements BlockData {

	/**
	 * Creates block data for the given material and Nukkit data value.
	 *
	 * @param material
	 *            The material.
	 * @param blockData
	 *            The Nukkit data value, 0 for the default state.
	 * @return The block data.
	 * @throws IllegalArgumentException
	 *             If the material is not a block.
	 */
	public static PokkitBlockData createBlockData(Material material, int blockData) {
		return new PokkitBlockData(BlockMap.getNukkitBlock(material, blockData));
	}

	/**
	 * Creates block data for the given material, with the given state string,
	 * for example {@code "[data=1]"}.
	 *
	 * @param material
	 *            The material.
	 * @param data
	 *            The state string, may be empty or null.
	 * @return The block data.
	 * @throws IllegalArgumentException
	 *             If the state string is invalid or unsupported.
	 */
	public static BlockData createBlockData(Material material, String data) {
		Objects.requireNonNull(material, "material");
		if (material.isLegacy()) {
			throw new IllegalArgumentException("Material " + material + " is a legacy material");
		}
		int blockData = parseStates(material, data);
		return createBlockData(material, blockData);
	}

	/**
	 * Parses a block data string. Both the Bukkit format
	 * ({@code minecraft:stone}, {@code minecraft:oak_stairs[data=2]}) and the
	 * old Pokkit format ({@code 1:0}) are accepted.
	 *
	 * @param inputString
	 *            The input string.
	 * @return The parsed block data.
	 * @throws IllegalArgumentException
	 *             If no such material exists, or the string is in an invalid
	 *             format.
	 */
	public static BlockData createBlockData(String inputString) {
		Objects.requireNonNull(inputString, "inputString");

		int bracket = inputString.indexOf('[');
		String materialPart = bracket < 0 ? inputString : inputString.substring(0, bracket);
		String statePart = bracket < 0 ? "" : inputString.substring(bracket);

		Material material = Material.matchMaterial(materialPart);
		if (material != null) {
			return createBlockData(material, statePart);
		}

		// Old Pokkit format: "blockId:blockData".
		String[] parts = inputString.split(":", 2);
		if (parts.length == 2) {
			try {
				int blockId = Integer.parseUnsignedInt(parts[0]);
				int blockData = Integer.parseUnsignedInt(parts[1]);
				if (blockData >= 16) {
					throw new IllegalArgumentException("Invalid block data: " + blockData);
				}
				return new PokkitBlockData(Block.get(blockId, blockData));
			} catch (NumberFormatException e) {
				// Fall through to the error below.
			}
		}
		throw new IllegalArgumentException(
				"Unknown block data \"" + inputString + "\": expected a material name, optionally followed by"
						+ " a state string, for example \"minecraft:oak_stairs[data=2]\".");
	}

	/**
	 * Parses a Bukkit state string into a Nukkit data value.
	 *
	 * <p>
	 * Bedrock does not expose block states, so only the {@code data} property is
	 * supported. That property is a Pokkit extension that makes the string form
	 * round-trip; all other properties are rejected instead of being silently
	 * ignored.
	 *
	 * @param material
	 *            The material the states belong to.
	 * @param data
	 *            The state string, may be empty or null.
	 * @return The Nukkit data value.
	 */
	private static int parseStates(Material material, String data) {
		if (data == null || data.isEmpty() || data.equals("[]")) {
			return 0;
		}
		if (!data.startsWith("[") || !data.endsWith("]")) {
			throw new IllegalArgumentException("Invalid block state string: " + data);
		}

		int blockData = 0;
		String body = data.substring(1, data.length() - 1).trim();
		if (body.isEmpty()) {
			return 0;
		}
		for (String pair : body.split(",")) {
			String[] parts = pair.split("=", 2);
			if (parts.length != 2) {
				throw new IllegalArgumentException("Invalid block state \"" + pair + "\" in " + data);
			}
			String key = parts[0].trim();
			String value = parts[1].trim();
			if (!key.equals("data")) {
				// Bedrock has no block states, so we cannot apply this
				// property. Silently ignoring it would change the block.
				throw new IllegalArgumentException("Block state \"" + key + "\" is not supported by " + Pokkit.NAME
						+ ": Bedrock has no block states, only a numeric data value.");
			}
			try {
				blockData = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid data value \"" + value + "\" in " + data);
			}
		}
		if (blockData < 0 || blockData > 15) {
			throw new IllegalArgumentException("Invalid block data: " + blockData);
		}
		return blockData;
	}

	public static PokkitBlockData toBukkit(cn.nukkit.block.Block block) {
		return new PokkitBlockData(block);
	}

	public static cn.nukkit.block.Block toNukkit(BlockData blockData) {
		return ((PokkitBlockData) blockData).block;
	}

	private cn.nukkit.block.Block block;

	protected PokkitBlockData(Block block) {
		this.block = Objects.requireNonNull(block);
	}

	@Override
	public BlockData clone() {
		return new PokkitBlockData(block.clone());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PokkitBlockData other)) {
			return false;
		}
		return block.getId() == other.block.getId() && block.getDamage() == other.block.getDamage();
	}

	@Override
	public int hashCode() {
		return 31 * block.getId() + block.getDamage();
	}

	@Override
	public String getAsString() {
		return getAsString(false);
	}

	@Override
	public String getAsString(boolean hideStates) {
		NamespacedKey key = getMaterial().getKey();
		String materialKey = key.getNamespace() + ":" + key.getKey();
		if (hideStates) {
			return materialKey;
		}
		// Omit the data value when this is the default state of the material,
		// so that granite is written as "minecraft:granite" and not as
		// "minecraft:granite[data=1]".
		Block defaultState = BlockMap.getNukkitBlockOrNull(getMaterial());
		if (defaultState != null && defaultState.getDamage() == block.getDamage()) {
			return materialKey;
		}
		return materialKey + "[data=" + block.getDamage() + "]";
	}

	@Override
	public Material getMaterial() {
		return BlockMap.getMaterial(block.getId(), block.getDamage());
	}

	public byte getNukkitData() {
		return (byte) block.getDamage();
	}

	/**
	 * Gets the block id according to the MCPE system.
	 * @return The block id.
	 */
	public int getNukkitId() {
		return block.getId();
	}

	@Override
	public boolean matches(BlockData data) {
		if (!(data instanceof PokkitBlockData other)) {
			return false;
		}
		return block.getId() == other.block.getId() && block.getDamage() == other.block.getDamage();
	}

	@Override
	public org.bukkit.block.BlockState createBlockState() {
		return PokkitBlockState.fromBlockData(this);
	}

	@Override
	public void copyTo(BlockData other) {
		if (!(other instanceof PokkitBlockData target)) {
			throw new IllegalArgumentException("Cannot copy to " + other.getClass().getName());
		}
		if (target == this) {
			return;
		}
		target.block = Block.get(block.getId(), block.getDamage());
	}

	@Override
	public void mirror(Mirror mirror) {
		Objects.requireNonNull(mirror, "mirror");
		if (mirror == Mirror.NONE || !(block instanceof cn.nukkit.block.BlockStairs)) {
			// Bedrock only stores a facing for stairs; other blocks would need
			// a full block state implementation.
			return;
		}
		int data = block.getDamage();
		int top = data & 0x4;
		int facing = data & 0x3;
		int mirrored = switch (mirror) {
		case LEFT_RIGHT -> switch (facing) {
			case 1 -> 3;
			case 3 -> 1;
			default -> facing;
		};
		case FRONT_BACK -> switch (facing) {
			case 0 -> 2;
			case 2 -> 0;
			default -> facing;
		};
		default -> facing;
		};
		block.setDamage(top | mirrored);
	}

	@Override
	public void rotate(StructureRotation rotation) {
		Objects.requireNonNull(rotation, "rotation");
		if (rotation == StructureRotation.NONE || !(block instanceof cn.nukkit.block.BlockStairs)) {
			// Bedrock only stores a facing for stairs; other blocks would need
			// a full block state implementation.
			return;
		}
		// Bedrock stairs store their facing as 0 = south, 1 = west, 2 = north,
		// 3 = east, with bit 2 marking an upside-down stair. A clockwise
		// quarter turn therefore adds one to the facing.
		int turns = switch (rotation) {
		case CLOCKWISE_90 -> 1;
		case CLOCKWISE_180 -> 2;
		case COUNTERCLOCKWISE_90 -> 3;
		default -> 0;
		};
		int data = block.getDamage();
		int top = data & 0x4;
		int facing = (data + turns) & 0x3;
		block.setDamage(top | facing);
	}

	@Override
	public org.bukkit.Material getPlacementMaterial() {
		return getMaterial();
	}

	@Override
	public org.bukkit.SoundGroup getSoundGroup() {
		// Nukkit does not expose the sound category of a block, so a coarse
		// group is derived from the material name.
		return PokkitSoundGroup.of(getMaterial());
	}

	@Override
	public int getLightEmission() {
		return block.getLightLevel();
	}

	@Override
	public boolean isOccluding() {
		return !block.isTransparent();
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return block.getToolType() != 0 && block.getToolTier() > 0;
	}

	@Override
	public boolean isPreferredTool(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return false;
		}
		cn.nukkit.item.Item nukkitItem = nl.rutgerkok.pokkit.item.PokkitItemStack.toNukkitCopy(itemStack);
		double withTool = block.calculateBreakTime(nukkitItem);
		double bareHand = block.calculateBreakTime(cn.nukkit.item.Item.get(cn.nukkit.item.Item.AIR));
		return withTool < bareHand;
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		if (block.breaksWhenMoved()) {
			return PistonMoveReaction.BREAK;
		}
		return block.canBePushed() ? PistonMoveReaction.MOVE : PistonMoveReaction.BLOCK;
	}

	@Override
	public boolean isSupported(org.bukkit.block.Block bukkitBlock) {
		Objects.requireNonNull(bukkitBlock, "block");
		return isSupported(bukkitBlock.getLocation());
	}

	@Override
	public boolean isSupported(Location location) {
		Objects.requireNonNull(location, "location");
		org.bukkit.block.Block bukkitBlock = location.getBlock();
		if (!(bukkitBlock instanceof PokkitBlock)) {
			return false;
		}
		cn.nukkit.block.Block nukkitBlock = PokkitBlock.toNukkit(bukkitBlock);
		cn.nukkit.level.Level level = nukkitBlock.getLevel();
		if (level == null || !nukkitBlock.canBeReplaced()) {
			return false;
		}
		cn.nukkit.block.Block support = nukkitBlock.getSide(cn.nukkit.math.BlockFace.DOWN);
		return block.canPlaceOn(support, Position.fromObject(nukkitBlock, level));
	}

	@Override
	public boolean isFaceSturdy(BlockFace face, BlockSupport support) {
		Objects.requireNonNull(face, "face");
		Objects.requireNonNull(support, "support");
		cn.nukkit.math.BlockFace nukkitFace = PokkitBlockFace.toNukkitOrNull(face);
		if (nukkitFace == null) {
			return false;
		}
		// Nukkit has no notion of center/rigid side support, so the full-face
		// check is used for every support type.
		return block.isSolid(nukkitFace);
	}

	@Override
	public org.bukkit.Color getMapColor() {
		BlockColor color = block.getColor();
		return Color.fromRGB(color.getRGB());
	}

	@Override
	public BlockData merge(BlockData data) {
		Objects.requireNonNull(data, "data");
		if (getMaterial() != data.getMaterial()) {
			throw new IllegalArgumentException("Cannot merge " + data.getMaterial() + " into " + getMaterial());
		}
		return data.clone();
	}

	/**
	 * Bridge to old API.
	 *
	 * @return Equivalent object in the old API.
	 */
	@Deprecated
	public org.bukkit.material.MaterialData toMaterialData() {
		Material material = BlockMap.getMaterial(block.getId(), block.getDamage());
		return material.getNewData((byte) block.getDamage());
	}
}
