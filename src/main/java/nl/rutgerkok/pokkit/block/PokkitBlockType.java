package nl.rutgerkok.pokkit.block;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemType;

public final class PokkitBlockType implements BlockType.Typed<BlockData> {

	private final NamespacedKey key;

	private PokkitBlockType(NamespacedKey key) {
		this.key = key;
	}

	public static PokkitBlockType of(String materialName) {
		return new PokkitBlockType(NamespacedKey.minecraft(materialName.toLowerCase()));
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public String getTranslationKey() {
		return "block." + key.getNamespace() + "." + key.getKey();
	}

	@Override
	public Typed<BlockData> typed() {
		return this;
	}

	@Override
	public <B extends BlockData> Typed<B> typed(Class<B> blockDataType) {
		return null;
	}

	@Override
	public boolean hasItemType() {
		return true;
	}

	@Override
	public ItemType getItemType() {
		return null;
	}

	@Override
	public Class<BlockData> getBlockDataClass() {
		return BlockData.class;
	}

	@Override
	public BlockData createBlockData() {
		return null;
	}

	@Override
	public BlockData createBlockData(Consumer<? super BlockData> consumer) {
		return null;
	}

	@Override
	public BlockData createBlockData(String data) {
		return null;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isFlammable() {
		return false;
	}

	@Override
	public boolean isBurnable() {
		return false;
	}

	@Override
	public boolean isOccluding() {
		return false;
	}

	@Override
	public boolean hasGravity() {
		return false;
	}

	@Override
	public boolean isInteractable() {
		return false;
	}

	@Override
	public float getHardness() {
		return 0;
	}

	@Override
	public float getBlastResistance() {
		return 0;
	}

	@Override
	public float getSlipperiness() {
		return 0.6f;
	}

	@Override
	public boolean isAir() {
		String name = key.getKey();
		return name.equals("air") || name.equals("cave_air") || name.equals("void_air");
	}

	@Override
	public boolean isEnabledByFeature(World world) {
		return true;
	}

	@Override
	public Material asMaterial() {
		return Material.matchMaterial(key.getKey());
	}
}
