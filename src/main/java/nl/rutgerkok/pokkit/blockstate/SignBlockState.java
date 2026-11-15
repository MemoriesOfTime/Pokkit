package nl.rutgerkok.pokkit.blockstate;

import java.util.Objects;

import cn.nukkit.utils.BlockColor;
import com.google.common.base.Preconditions;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import org.bukkit.persistence.PersistentDataContainer;

import nl.rutgerkok.pokkit.persistence.PokkitPersistentDataContainer;

public final class SignBlockState extends PokkitBlockState implements Sign {

	private final String[] lines;
	private String hiddenData;
	private BlockColor color;

	/**
	 * Tag the persistent data container of this sign writes to. For a placed
	 * sign this is the tag of the block entity, for a sign in an item this is
	 * the block entity tag of the item.
	 */
	private final CompoundTag persistentData;

	protected SignBlockState(Location locationOrNull, PokkitBlockData materialData, String[] lines, String hiddenData, BlockColor colorOrNull) {
		this(locationOrNull, materialData, lines, hiddenData, colorOrNull, new CompoundTag());
	}

	protected SignBlockState(Location locationOrNull, PokkitBlockData materialData, String[] lines, String hiddenData,
			BlockColor colorOrNull, CompoundTag persistentData) {
		super(locationOrNull, materialData);
		this.lines = Objects.requireNonNull(lines, "lines");
		this.hiddenData = Objects.requireNonNull(hiddenData, "hiddenData");
		this.color = colorOrNull;
		this.persistentData = Objects.requireNonNull(persistentData, "persistentData");
		Preconditions.checkArgument(lines.length == 4, "there must be four lines on a sign");
	}

	/**
	 * Gets hidden data on this sign.
	 *
	 * <p>
	 * While players cannot temper with this data, other plugins can. Always
	 * check the format of the returned string.
	 *
	 * @return Hidden data.
	 */
	public String getHiddenData() {
		return hiddenData;
	}

	@Override
	public String getLine(int index) throws IndexOutOfBoundsException {
		return lines[index];
	}

	@Override
	public String[] getLines() {
		return lines;
	}

	@Override
	public void saveToTag(CompoundTag tag) {
		tag.putString("Text1", lines[0]);
		tag.putString("Text2", lines[1]);
		tag.putString("Text3", lines[2]);
		tag.putString("Text4", lines[3]);
		if (hiddenData.isEmpty()) {
			tag.remove(Pokkit.NAME);
		} else {
			tag.putString(Pokkit.NAME, hiddenData);
		}
		if (color == null) {
			tag.remove("SignTextColor");
		} else {
			tag.putInt("SignTextColor", color.getARGB());
		}
		if (persistentData != tag) {
			// Unplaced signs keep their persistent data in a tag of their own.
			for (Tag entry : persistentData.getAllTags()) {
				tag.put(entry.getName(), entry);
			}
		}
	}

	/**
	 * Sets hidden data on this sign. Set to an empty string to clear the hidden
	 * data.
	 *
	 * @param string
	 *            The hidden data.
	 */
	public void setHiddenData(String string) {
		this.hiddenData = Objects.requireNonNull(string);
	}

	@Override
	public void setLine(int index, String line) throws IndexOutOfBoundsException {
		if (line == null) {
			line = "";
		}
		lines[index] = line;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void setEditable(boolean editable) {
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return new PokkitPersistentDataContainer(persistentData);
	}

	@Override
	public DyeColor getColor() {
		if (color == null) {
			return null;
		}
		return DyeColor.getByColor(Color.fromRGB(color.getRGB()));
	}

	@Override
	public void setColor(DyeColor dyeColor) {
		if (dyeColor == null) {
			color = null;
		} else {
			color = new BlockColor(dyeColor.getColor().asRGB());
		}
	}

	@Override
	public BlockState copy(Location location) {
		// A copy must be independent of the original: changing the copy must
		// not change the lines of this sign.
		return new SignBlockState(location, (PokkitBlockData) getBlockData().clone(), lines.clone(), hiddenData,
				color, copyPersistentData());
	}

	@Override
	public BlockState copy() {
		return copy(null);
	}

	private CompoundTag copyPersistentData() {
		CompoundTag copy = new CompoundTag();
		for (Tag entry : persistentData.getAllTags()) {
			copy.put(entry.getName(), entry);
		}
		return copy;
	}

	@Override
	public Player getAllowedEditor() {
		return null;
	}

	@Override
	public org.bukkit.block.sign.SignSide getSide(org.bukkit.block.sign.Side side) {
		return new org.bukkit.block.sign.SignSide() {
			@Override
			public String getLine(int index) throws IndexOutOfBoundsException {
				return lines[index];
			}

			@Override
			public String[] getLines() {
				return lines;
			}

			@Override
			public void setLine(int index, String line) throws IndexOutOfBoundsException {
				lines[index] = line == null ? "" : line;
			}

			@Override
			public boolean isGlowingText() {
				return SignBlockState.this.isGlowingText();
			}

			@Override
			public void setGlowingText(boolean glowing) {
				SignBlockState.this.setGlowingText(glowing);
			}

			@Override
			public DyeColor getColor() {
				return SignBlockState.this.getColor();
			}

			@Override
			public void setColor(DyeColor color) {
				SignBlockState.this.setColor(color);
			}
		};
	}

	@Override
	public org.bukkit.block.sign.SignSide getTargetSide(Player player) {
		return getSide(org.bukkit.block.sign.Side.FRONT);
	}

	@Override
	public boolean isWaxed() {
		return false;
	}

	@Override
	public void setWaxed(boolean waxed) {
	}

	@Override
	public boolean isGlowingText() {
		return false;
	}

	@Override
	public void setGlowingText(boolean glowing) {
	}
}
