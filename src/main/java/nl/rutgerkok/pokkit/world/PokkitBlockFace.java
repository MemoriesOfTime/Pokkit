package nl.rutgerkok.pokkit.world;

import java.util.List;

import org.bukkit.block.BlockFace;

public final class PokkitBlockFace {

	/**
	 * The six faces that exist in Nukkit. Bukkit additionally has diagonal
	 * faces and {@link BlockFace#SELF}, which have no Nukkit equivalent.
	 */
	public static final List<BlockFace> CARDINAL_FACES = List.of(BlockFace.NORTH, BlockFace.EAST,
			BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

	public static BlockFace toBukkit(cn.nukkit.math.BlockFace nukkit) {
		if (nukkit == null) {
			return null;
		}
		switch (nukkit) {
		case DOWN:
			return BlockFace.DOWN;
		case UP:
			return BlockFace.UP;
		case NORTH:
			return BlockFace.NORTH;
		case SOUTH:
			return BlockFace.SOUTH;
		case WEST:
			return BlockFace.WEST;
		case EAST:
			return BlockFace.EAST;
		default:
			throw new IllegalArgumentException("Unknown Nukkit block face: " + nukkit);
		}
	}

	public static cn.nukkit.math.BlockFace toNukkit(BlockFace bukkit) {
		cn.nukkit.math.BlockFace nukkit = toNukkitOrNull(bukkit);
		if (nukkit == null) {
			throw new IllegalArgumentException(
					"BlockFace " + bukkit + " has no Nukkit equivalent: Nukkit only knows the six cardinal faces");
		}
		return nukkit;
	}

	/**
	 * Gets the Nukkit equivalent of the given Bukkit face.
	 *
	 * @param bukkit
	 *            The Bukkit face, may be null.
	 * @return The Nukkit face, or null for {@link BlockFace#SELF} and the
	 *         diagonal faces.
	 */
	public static cn.nukkit.math.BlockFace toNukkitOrNull(BlockFace bukkit) {
		if (bukkit == null) {
			return null;
		}
		switch (bukkit) {
		case DOWN:
			return cn.nukkit.math.BlockFace.DOWN;
		case UP:
			return cn.nukkit.math.BlockFace.UP;
		case NORTH:
			return cn.nukkit.math.BlockFace.NORTH;
		case SOUTH:
			return cn.nukkit.math.BlockFace.SOUTH;
		case WEST:
			return cn.nukkit.math.BlockFace.WEST;
		case EAST:
			return cn.nukkit.math.BlockFace.EAST;
		default:
			return null;
		}
	}

	private PokkitBlockFace() {
	}
}
