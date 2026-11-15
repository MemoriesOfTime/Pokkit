package nl.rutgerkok.pokkit.item;

import org.bukkit.inventory.meta.BookMeta;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Item meta for written books.
 */
public final class PokkitWrittenBookMeta extends PokkitBookMetaBase implements BookMeta {

	PokkitWrittenBookMeta(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	public PokkitWrittenBookMeta clone() {
		return (PokkitWrittenBookMeta) super.clone();
	}

	@Override
	public boolean hasTitle() {
		return tag.contains("title");
	}

	@Override
	public String getTitle() {
		return tag.contains("title") ? tag.getString("title") : null;
	}

	@Override
	public boolean setTitle(String title) {
		if (title == null) {
			tag.remove("title");
			return true;
		}
		if (title.length() > 32) {
			return false;
		}
		tag.putString("title", title);
		return true;
	}

	@Override
	public boolean hasAuthor() {
		return tag.contains("author");
	}

	@Override
	public String getAuthor() {
		return tag.contains("author") ? tag.getString("author") : null;
	}

	@Override
	public void setAuthor(String author) {
		if (author == null) {
			tag.remove("author");
		} else {
			tag.putString("author", author);
		}
	}

	@Override
	public boolean hasGeneration() {
		return tag.contains("generation");
	}

	@Override
	public Generation getGeneration() {
		if (!hasGeneration()) {
			// CraftBukkit reports the original generation when nothing has
			// been stored.
			return Generation.ORIGINAL;
		}
		int generation = tag.getInt("generation");
		Generation[] values = Generation.values();
		return generation >= 0 && generation < values.length ? values[generation] : null;
	}

	@Override
	public void setGeneration(Generation generation) {
		if (generation == null) {
			tag.remove("generation");
		} else {
			tag.putInt("generation", generation.ordinal());
		}
	}

	@Override
	public Spigot spigot() {
		// Component pages are not supported, but a stub is better than null:
		// its methods throw UnsupportedOperationException instead of a NPE.
		return new Spigot();
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
