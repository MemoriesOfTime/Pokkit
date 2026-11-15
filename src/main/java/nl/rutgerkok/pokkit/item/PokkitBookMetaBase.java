package nl.rutgerkok.pokkit.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.WritableBookMeta;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

/**
 * Shared implementation for the pages of written and writable books. Bedrock
 * stores the pages as a list of compounds with a {@code text} entry; older
 * versions used a list of strings, which is still accepted when reading.
 */
public abstract class PokkitBookMetaBase extends PokkitItemMeta implements WritableBookMeta {

	private static final String PAGES = "pages";

	PokkitBookMetaBase(CompoundTag tag, int damage) {
		super(tag, damage);
	}

	@Override
	public PokkitBookMetaBase clone() {
		return (PokkitBookMetaBase) super.clone();
	}

	@Override
	public boolean hasPages() {
		return getPageCount() > 0;
	}

	@Override
	public String getPage(int page) {
		if (page < 1 || page > getPageCount()) {
			throw new IllegalArgumentException("Invalid page number " + page + " (1.." + getPageCount() + ")");
		}
		return readPages().get(page - 1);
	}

	@Override
	public void setPage(int page, String data) {
		if (page < 1) {
			throw new IllegalArgumentException("Page must be greater than 0, got " + page);
		}
		List<String> pages = readPages();
		// CraftBukkit pads the book with empty pages, so that page 1 can be
		// set on a book that has no pages yet.
		while (pages.size() < page) {
			pages.add("");
		}
		pages.set(page - 1, data == null ? "" : data);
		writePages(pages);
	}

	@Override
	public List<String> getPages() {
		return new ArrayList<>(readPages());
	}

	@Override
	public void setPages(List<String> pages) {
		writePages(pages == null ? new ArrayList<>() : pages);
	}

	@Override
	public void setPages(String... pages) {
		List<String> list = new ArrayList<>();
		if (pages != null) {
			for (String page : pages) {
				list.add(page == null ? "" : page);
			}
		}
		writePages(list);
	}

	@Override
	public void addPage(String... pages) {
		List<String> list = readPages();
		if (pages != null) {
			for (String page : pages) {
				list.add(page == null ? "" : page);
			}
		}
		writePages(list);
	}

	@Override
	public int getPageCount() {
		return readPages().size();
	}

	private List<String> readPages() {
		List<String> pages = new ArrayList<>();
		if (!tag.contains(PAGES)) {
			return pages;
		}
		Tag pageTag = tag.get(PAGES);
		if (!(pageTag instanceof ListTag<?>)) {
			return pages;
		}
		for (Tag entry : ((ListTag<?>) pageTag).getAll()) {
			if (entry instanceof CompoundTag) {
				pages.add(((CompoundTag) entry).getString("text"));
			} else if (entry instanceof StringTag) {
				pages.add(((StringTag) entry).data);
			}
		}
		return pages;
	}

	private void writePages(List<String> pages) {
		if (pages.isEmpty()) {
			tag.remove(PAGES);
			return;
		}
		ListTag<CompoundTag> pageList = new ListTag<>(PAGES);
		for (String page : pages) {
			pageList.add(new CompoundTag().putString("text", page));
		}
		tag.putList(pageList);
	}
}
