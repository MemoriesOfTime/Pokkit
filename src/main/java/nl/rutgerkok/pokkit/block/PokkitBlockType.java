package nl.rutgerkok.pokkit.block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemType;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.BlockMap;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.block.Block;

public final class PokkitBlockType implements BlockType.Typed<BlockData> {

	private static final Map<Class<?>, Boolean> INTERACTABLE_CACHE = new ConcurrentHashMap<>();

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
		if (!blockDataType.isInstance(createBlockData())) {
			throw new IllegalArgumentException(blockDataType.getName() + " is not a valid BlockData type for " + key);
		}
		@SuppressWarnings("unchecked")
		Typed<B> typed = (Typed<B>) this;
		return typed;
	}

	@Override
	public boolean hasItemType() {
		return Registry.ITEM.get(key) != null;
	}

	@Override
	public ItemType getItemType() {
		return Registry.ITEM.get(key);
	}

	@Override
	public Class<BlockData> getBlockDataClass() {
		return BlockData.class;
	}

	@Override
	public BlockData createBlockData() {
		return PokkitBlockData.createBlockData(asMaterial(), 0);
	}

	@Override
	public BlockData createBlockData(Consumer<? super BlockData> consumer) {
		BlockData blockData = createBlockData();
		consumer.accept(blockData);
		return blockData;
	}

	@Override
	public BlockData createBlockData(String data) {
		return PokkitBlockData.createBlockData(asMaterial(), data);
	}

	/**
	 * Gets the Nukkit block that belongs to this block type.
	 *
	 * @return The Nukkit block.
	 * @throws UnsupportedOperationException
	 *             If Nukkit has no equivalent for this block type.
	 */
	private Block nukkitBlock() {
		Block block = BlockMap.getNukkitBlockOrNull(asMaterial());
		if (block == null) {
			throw Pokkit.unsupported();
		}
		return block;
	}

	@Override
	public boolean isSolid() {
		return nukkitBlock().isSolid();
	}

	@Override
	public boolean isFlammable() {
		return nukkitBlock().getBurnChance() > 0;
	}

	@Override
	public boolean isBurnable() {
		return nukkitBlock().getBurnAbility() > 0;
	}

	@Override
	public boolean isOccluding() {
		return !nukkitBlock().isTransparent();
	}

	@Override
	public boolean hasGravity() {
		// Nukkit does not expose whether a block is affected by gravity: the
		// falling behaviour is implemented in the level, not in the block.
		throw Pokkit.unsupported();
	}

	@Override
	public boolean isInteractable() {
		return INTERACTABLE_CACHE.computeIfAbsent(nukkitBlock().getClass(), PokkitBlockType::overridesOnActivate);
	}

	private static boolean overridesOnActivate(Class<?> blockClass) {
		// Most blocks only override the two argument version, so both
		// signatures have to be checked.
		return declaresOwnOnActivate(blockClass, cn.nukkit.item.Item.class, cn.nukkit.Player.class)
				|| declaresOwnOnActivate(blockClass, cn.nukkit.item.Item.class);
	}

	private static boolean declaresOwnOnActivate(Class<?> blockClass, Class<?>... parameterTypes) {
		try {
			return blockClass.getMethod("onActivate", parameterTypes).getDeclaringClass() != Block.class;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	@Override
	public float getHardness() {
		return (float) nukkitBlock().getHardness();
	}

	@Override
	public float getBlastResistance() {
		return (float) nukkitBlock().getResistance();
	}

	@Override
	public float getSlipperiness() {
		return (float) nukkitBlock().getFrictionFactor();
	}

	@Override
	public boolean isAir() {
		return nukkitBlock().isAir();
	}

	@Override
	public boolean isEnabledByFeature(World world) {
		// All vanilla blocks are available in every Nukkit world; experimental
		// feature flags do not exist.
		return true;
	}

	@Override
	public Material asMaterial() {
		return Material.matchMaterial(key.getNamespace() + ":" + key.getKey());
	}
}
