package nl.rutgerkok.pokkit.blockdata;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;

/**
 * A coarse {@link SoundGroup} for a block.
 *
 * <p>
 * Nukkit does not expose the sound category of a block, so the category is
 * derived from the material name. The result is good enough for plugins that
 * play block sounds, but it is not guaranteed to match the vanilla sound of
 * every block.
 */
final class PokkitSoundGroup implements SoundGroup {

	private static final String DEFAULT_CATEGORY = "STONE";

	static final SoundGroup STONE = new PokkitSoundGroup(DEFAULT_CATEGORY);

	static SoundGroup of(Material material) {
		if (material == null || !material.isBlock()) {
			return STONE;
		}
		return new PokkitSoundGroup(categoryOf(material.name()));
	}

	private static String categoryOf(String name) {
		if (name.contains("LADDER")) {
			return "LADDER";
		}
		if (name.contains("ANVIL")) {
			return "ANVIL";
		}
		if (name.contains("SLIME")) {
			return "SLIME_BLOCK";
		}
		if (name.contains("HONEY")) {
			return "HONEY_BLOCK";
		}
		if (name.startsWith("BAMBOO") || name.contains("_BAMBOO")) {
			return "BAMBOO";
		}
		if (name.contains("GLASS")) {
			return "GLASS";
		}
		if (name.contains("WOOL") || name.contains("CARPET")) {
			return "WOOL";
		}
		if (name.contains("GRAVEL")) {
			return "GRAVEL";
		}
		if (name.contains("SAND")) {
			return "SAND";
		}
		if (name.contains("SNOW")) {
			return "SNOW";
		}
		if (name.contains("IRON") || name.contains("GOLD") || name.contains("COPPER")
				|| name.contains("NETHERITE")) {
			return "METAL";
		}
		if (name.contains("GRASS") || name.contains("DIRT") || name.contains("MOSS")
				|| name.contains("FARMLAND") || name.contains("PODZOL")) {
			return "GRASS";
		}
		if (name.contains("PLANKS") || name.contains("LOG") || name.contains("WOOD")
				|| name.contains("FENCE") || name.contains("DOOR") || name.contains("SIGN")
				|| name.contains("CHEST") || name.contains("BARREL") || name.contains("TABLE")
				|| name.contains("BOOKSHELF")) {
			return "WOOD";
		}
		return DEFAULT_CATEGORY;
	}

	private final String category;

	private PokkitSoundGroup(String category) {
		this.category = category;
	}

	@Override
	public float getVolume() {
		return 1.0F;
	}

	@Override
	public float getPitch() {
		return 1.0F;
	}

	@Override
	public Sound getBreakSound() {
		return sound("BREAK");
	}

	@Override
	public Sound getStepSound() {
		return sound("STEP");
	}

	@Override
	public Sound getPlaceSound() {
		return sound("PLACE");
	}

	@Override
	public Sound getHitSound() {
		return sound("HIT");
	}

	@Override
	public Sound getFallSound() {
		return sound("FALL");
	}

	private Sound sound(String action) {
		return Sound.valueOf("BLOCK_" + category.toUpperCase(Locale.ROOT) + "_" + action);
	}

	@Override
	public String toString() {
		return "PokkitSoundGroup[" + category + "]";
	}
}
