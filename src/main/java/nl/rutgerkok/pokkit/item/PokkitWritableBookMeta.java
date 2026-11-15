package nl.rutgerkok.pokkit.item;

import org.bukkit.inventory.meta.WritableBookMeta;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Item meta for book and quills.
 */
public final class PokkitWritableBookMeta extends PokkitBookMetaBase implements WritableBookMeta {

	PokkitWritableBookMeta(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	public PokkitWritableBookMeta clone() {
		return (PokkitWritableBookMeta) super.clone();
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
