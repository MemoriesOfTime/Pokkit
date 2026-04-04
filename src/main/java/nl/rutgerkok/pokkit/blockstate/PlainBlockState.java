package nl.rutgerkok.pokkit.blockstate;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.nbt.tag.CompoundTag;

final class PlainBlockState extends PokkitBlockState {

	PlainBlockState(Location locationOrNull, PokkitBlockData materialData) {
		super(locationOrNull, materialData);
	}

	@Override
	public void saveToTag(CompoundTag tag) {
		// Empty!
	}

	@Override
	public BlockState copy(Location location) {
		return this;
	}

	@Override
	public BlockState copy() {
		return this;
	}

}
