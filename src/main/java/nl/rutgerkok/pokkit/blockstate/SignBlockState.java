package nl.rutgerkok.pokkit.blockstate;

import java.lang.reflect.Field;
import java.util.Objects;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.utils.BlockColor;
import com.google.common.base.Preconditions;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import org.bukkit.persistence.PersistentDataContainer;

import nl.rutgerkok.pokkit.persistence.PokkitPersistentDataContainer;

public class SignBlockState extends PokkitBlockState implements Sign {

	private final String[] lines;
	private final String[] backLines;
	private String hiddenData;
	private BlockColor color;

	/**
	 * Tag the persistent data container of this sign writes to. For a placed
	 * sign this is the tag of the block entity, for a sign in an item this is
	 * the block entity tag of the item.
	 */
	private final CompoundTag persistentData;

	/**
	 * Nukkit has no public setter for the back side of a sign, so the backing
	 * field is updated directly. The field is looked up once; when a future
	 * Nukkit version renames it, writing the back side becomes a no-op instead
	 * of failing every sign update.
	 */
	private static final Field BACK_TEXT_FIELD = findBackTextField();

	private static Field findBackTextField() {
		try {
			Field field = BlockEntitySign.class.getDeclaredField("backText");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException | RuntimeException e) {
			return null;
		}
	}

	protected SignBlockState(Location locationOrNull, PokkitBlockData materialData, String[] lines,
			String hiddenData, BlockColor colorOrNull) {
		this(locationOrNull, materialData, lines, emptyLines(), hiddenData, colorOrNull, new CompoundTag());
	}

	protected SignBlockState(Location locationOrNull, PokkitBlockData materialData, String[] lines, String hiddenData,
			BlockColor colorOrNull, CompoundTag persistentData) {
		this(locationOrNull, materialData, lines, emptyLines(), hiddenData, colorOrNull, persistentData);
	}

	protected SignBlockState(Location locationOrNull, PokkitBlockData materialData, String[] lines,
			String[] backLines, String hiddenData, BlockColor colorOrNull, CompoundTag persistentData) {
		super(locationOrNull, materialData);
		this.lines = Objects.requireNonNull(lines, "lines");
		this.backLines = Objects.requireNonNull(backLines, "backLines");
		this.hiddenData = Objects.requireNonNull(hiddenData, "hiddenData");
		this.color = colorOrNull;
		this.persistentData = Objects.requireNonNull(persistentData, "persistentData");
		Preconditions.checkArgument(lines.length == 4, "there must be four lines on a sign");
		Preconditions.checkArgument(backLines.length == 4, "there must be four lines on a sign");
	}

	private static String[] emptyLines() {
		return new String[] { "", "", "", "" };
	}

	/**
	 * Reads the four back lines from the given tag.
	 *
	 * <p>
	 * Pokkit stores the Bukkit back side in {@code BackText1} to
	 * {@code BackText4} for signs that are saved through Pokkit. Signs edited
	 * in-game by a Bedrock player only have Nukkit's {@code BackText} compound,
	 * which is used as a fallback.
	 * </p>
	 *
	 * @param tag
	 *            The block entity tag.
	 * @return The four back lines.
	 */
	public static String[] readBackLines(CompoundTag tag) {
		// Nukkit's own compound is authoritative: it also contains text that a
		// player typed in-game. The custom keys are only a fallback for signs
		// that were never written in the 1.20 format.
		if (tag.containsCompound(BlockEntitySign.TAG_BACK_TEXT)) {
			String text = tag.getCompound(BlockEntitySign.TAG_BACK_TEXT)
					.getString(BlockEntitySign.TAG_TEXT_BLOB);
			if (!text.isEmpty()) {
				String[] split = text.split("\n", 4);
				String[] lines = new String[4];
				for (int i = 0; i < lines.length; i++) {
					lines[i] = i < split.length ? split[i] : "";
				}
				return lines;
			}
		}
		return new String[] { tag.getString("BackText1"), tag.getString("BackText2"), tag.getString("BackText3"),
				tag.getString("BackText4") };
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
		return lines.clone();
	}

	@Override
	public void saveToTag(CompoundTag tag) {
		// Legacy keys, used when the sign is stored inside an item.
		tag.putString("Text1", lines[0]);
		tag.putString("Text2", lines[1]);
		tag.putString("Text3", lines[2]);
		tag.putString("Text4", lines[3]);
		saveMetadata(tag);
	}

	@Override
	public void saveToBlockEntity(cn.nukkit.blockentity.BlockEntity blockEntity) {
		if (blockEntity instanceof BlockEntitySign sign) {
			// Nukkit-MOT uses the 1.20 sign format, so the text has to go
			// through the block entity instead of the legacy Text1..Text4
			// keys.
			sign.setText(lines);
			setBackText(sign, backLines);
			sign.setGlowing(isGlowingText(Side.FRONT));
			sign.setGlowing(false, isGlowingText(Side.BACK));
			if (color != null) {
				sign.setColor(color);
			}
		}
		saveMetadata(blockEntity.namedTag);
	}

	private static void setBackText(BlockEntitySign sign, String[] lines) {
		if (BACK_TEXT_FIELD == null) {
			return;
		}
		try {
			BACK_TEXT_FIELD.set(sign, lines.clone());
		} catch (IllegalAccessException e) {
			// The field is not accessible; the custom BackText1..4 keys are
			// still written, so Pokkit itself keeps the text.
		}
	}

	private void saveMetadata(CompoundTag tag) {
		tag.putString("BackText1", backLines[0]);
		tag.putString("BackText2", backLines[1]);
		tag.putString("BackText3", backLines[2]);
		tag.putString("BackText4", backLines[3]);
		tag.putBoolean(cn.nukkit.blockentity.BlockEntitySign.TAG_WAXED, isWaxed());
		tag.putBoolean("PokkitEditable", isEditable());
		if (color == null) {
			tag.remove("SignTextColor");
		} else {
			tag.putInt("SignTextColor", color.getARGB());
		}
		if (hiddenData.isEmpty()) {
			tag.remove(Pokkit.NAME);
		} else {
			tag.putString(Pokkit.NAME, hiddenData);
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
		return !isWaxed() && persistentData.getBoolean("PokkitEditable", true);
	}

	@Override
	public void setEditable(boolean editable) {
		persistentData.putBoolean("PokkitEditable", editable);
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
		return new SignBlockState(location, (PokkitBlockData) getBlockData().clone(), lines.clone(),
				backLines.clone(), hiddenData, color, copyPersistentData());
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
	public org.bukkit.block.sign.SignSide getSide(Side side) {
		boolean back = side == Side.BACK;
		String[] target = back ? backLines : lines;
		return new org.bukkit.block.sign.SignSide() {
			@Override
			public String getLine(int index) throws IndexOutOfBoundsException {
				return target[index];
			}

			@Override
			public String[] getLines() {
				return target.clone();
			}

			@Override
			public void setLine(int index, String line) throws IndexOutOfBoundsException {
				target[index] = line == null ? "" : line;
			}

			@Override
			public boolean isGlowingText() {
				return SignBlockState.this.isGlowingText(side);
			}

			@Override
			public void setGlowingText(boolean glowing) {
				SignBlockState.this.setGlowingText(side, glowing);
			}

			@Override
			public DyeColor getColor() {
				if (back) {
					BlockEntitySign sign = signEntity();
					if (sign != null) {
						return DyeColor.getByColor(Color.fromRGB(sign.getColor(false).getRGB()));
					}
				}
				return SignBlockState.this.getColor();
			}

			@Override
			public void setColor(DyeColor color) {
				if (back) {
					BlockEntitySign sign = signEntity();
					if (sign != null && color != null) {
						sign.setColor(false, new BlockColor(color.getColor().asRGB()));
						return;
					}
				}
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
		return persistentData.getBoolean("IsWaxed", false);
	}

	@Override
	public void setWaxed(boolean waxed) {
		if (waxed) {
			persistentData.putBoolean("IsWaxed", true);
		} else {
			persistentData.remove("IsWaxed");
		}
	}

	@Override
	public boolean isGlowingText() {
		return isGlowingText(Side.FRONT);
	}

	@Override
	public void setGlowingText(boolean glowing) {
		setGlowingText(Side.FRONT, glowing);
	}

	private boolean isGlowingText(Side side) {
		BlockEntitySign sign = signEntity();
		if (sign != null) {
			// Nukkit stores the glow in FrontText/BackText.IgnoreLighting,
			// which players can also change in-game.
			return sign.isGlowing(side == Side.FRONT);
		}
		String key = glowKey(side);
		if (persistentData.contains(key)) {
			return persistentData.getBoolean(key);
		}
		String textTag = side == Side.FRONT ? BlockEntitySign.TAG_FRONT_TEXT : BlockEntitySign.TAG_BACK_TEXT;
		if (persistentData.containsCompound(textTag)) {
			return persistentData.getCompound(textTag).getBoolean(BlockEntitySign.TAG_GLOWING_TEXT);
		}
		return false;
	}

	private void setGlowingText(Side side, boolean glowing) {
		String key = glowKey(side);
		if (glowing) {
			persistentData.putBoolean(key, true);
		} else {
			persistentData.remove(key);
		}
		BlockEntitySign sign = signEntity();
		if (sign != null) {
			sign.setGlowing(side == Side.FRONT, glowing);
		}
	}

	private static String glowKey(Side side) {
		return side == Side.FRONT ? "GlowingText" : "BackGlowingText";
	}

	/**
	 * Gets the Nukkit sign backing this state, or null when the sign is not
	 * placed or has no block entity.
	 *
	 * @return The block entity, or null.
	 */
	private BlockEntitySign signEntity() {
		if (!isPlaced()) {
			return null;
		}
		BlockEntity entity = getBlockEntity();
		return entity instanceof BlockEntitySign sign ? sign : null;
	}
}
