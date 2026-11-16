package nl.rutgerkok.pokkit.blockstate;

import org.bukkit.Location;
import org.bukkit.block.HangingSign;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

/**
 * Hanging sign state. Bedrock hanging signs use the same block entity as
 * normal signs, so all behaviour is inherited.
 */
final class HangingSignBlockState extends SignBlockState implements HangingSign {

	HangingSignBlockState(Location locationOrNull, PokkitBlockData blockData, String[] lines, String[] backLines,
			String hiddenData, BlockColor color, CompoundTag persistentData) {
		super(locationOrNull, blockData, lines, backLines, hiddenData, color, persistentData);
	}
}
