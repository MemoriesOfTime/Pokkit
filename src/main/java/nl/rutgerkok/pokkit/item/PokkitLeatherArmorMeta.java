package nl.rutgerkok.pokkit.item;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cn.nukkit.nbt.tag.CompoundTag;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;

public class PokkitLeatherArmorMeta extends PokkitItemMeta implements LeatherArmorMeta {

	private static final String CUSTOM_COLOR = "customColor";

	/**
	 * The color CraftBukkit reports for leather armor without an explicit
	 * color.
	 */
	private static final Color DEFAULT_COLOR = Color.fromRGB(0xA06540);

	PokkitLeatherArmorMeta(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	boolean isApplicable(org.bukkit.Material material) {
		if (material == null) {
			return false;
		}
		switch (material) {
		case LEATHER_HELMET:
		case LEGACY_LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEGACY_LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEGACY_LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case LEGACY_LEATHER_BOOTS:
			return true;
		default:
			return false;
		}
	}

	@Override
	public CustomItemTagContainer getCustomTagContainer() {
		return null;
	}

	@Override
	public PokkitLeatherArmorMeta clone() {
		return (PokkitLeatherArmorMeta) super.clone();
	}

	@Override
	public Color getColor() {
		// Bedrock stores the color as a single ARGB integer.
		if (tag.contains(CUSTOM_COLOR)) {
			int argb = tag.getInt(CUSTOM_COLOR);
			return Color.fromRGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
		}
		// Older versions of this class wrote the color as three separate
		// integers, so keep reading those for backward compatibility.
		if (tag.contains("red") && tag.contains("green") && tag.contains("blue")) {
			return Color.fromRGB(tag.getInt("red") & 0xFF, tag.getInt("green") & 0xFF,
					tag.getInt("blue") & 0xFF);
		}
		// Like CraftBukkit, report the default leather color when no color
		// has been set, instead of null.
		return DEFAULT_COLOR;
	}

	@Override
	public void setColor(Color color) {
		if (color == null) {
			tag.remove(CUSTOM_COLOR);
			tag.remove("red");
			tag.remove("green");
			tag.remove("blue");
			return;
		}
		// The alpha channel must be fully opaque, otherwise the client
		// renders the armor as transparent.
		tag.putInt(CUSTOM_COLOR, 0xFF000000 | color.asRGB());
		tag.remove("red");
		tag.remove("green");
		tag.remove("blue");
	}

	/**
	 * Recreates the item meta from its serialized form. Required because
	 * Bukkit's configuration serialization looks for a method declared on the
	 * concrete class.
	 *
	 * @param args
	 *            The serialized data.
	 * @return The item meta.
	 */
	public static org.bukkit.inventory.meta.ItemMeta deserialize(java.util.Map<String, Object> args) {
		return PokkitItemMeta.deserialize(args);
	}
}
