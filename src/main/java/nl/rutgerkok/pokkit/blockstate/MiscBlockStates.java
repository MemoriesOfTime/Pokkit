package nl.rutgerkok.pokkit.blockstate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Banner;
import org.bukkit.block.Bed;
import org.bukkit.block.Beehive;
import org.bukkit.block.Bell;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Conduit;
import org.bukkit.block.DaylightDetector;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.Jigsaw;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.util.BoundingBox;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.blockentity.BlockEntityBell;
import cn.nukkit.blockentity.BlockEntityConduit;

final class BedBlockState extends PokkitTileState implements Bed {

	BedBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BED;
	}

	@Override
	public DyeColor getColor() {
		String name = getType().name();
		int underscore = name.indexOf("_BED");
		return underscore > 0 ? DyeColor.valueOf(name.substring(0, underscore)) : DyeColor.RED;
	}

	@Override
	public void setColor(DyeColor color) {
		// Changing the colour means replacing the block, which a block state
		// cannot do in Pokkit.
		throw Pokkit.unsupported();
	}
}

final class DaylightDetectorBlockState extends PokkitTileState implements DaylightDetector {

	DaylightDetectorBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.DAYLIGHT_DETECTOR;
	}
}

final class EnchantingTableBlockState extends PokkitTileState implements EnchantingTable {

	EnchantingTableBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.ENCHANT_TABLE;
	}

	@Override
	public String getCustomName() {
		return tileTag().contains("CustomName") ? tileTag().getString("CustomName") : null;
	}

	@Override
	public void setCustomName(String name) {
		if (name == null) {
			tileTag().remove("CustomName");
		} else {
			tileTag().putString("CustomName", name);
		}
	}
}

final class JigsawBlockState extends PokkitTileState implements Jigsaw {

	JigsawBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		// Nukkit has no jigsaw block entity.
		return null;
	}
}

final class BellBlockState extends PokkitTileState implements Bell {

	BellBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BELL;
	}

	private BlockEntityBell bell() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityBell bell ? bell : null;
	}

	@Override
	public boolean ring(Entity entity, BlockFace face) {
		BlockEntityBell bell = bell();
		if (bell == null) {
			return false;
		}
		bell.setRinging(true);
		bell.setTicks(0);
		return true;
	}

	@Override
	public boolean ring(Entity entity) {
		return ring(entity, null);
	}

	@Override
	public boolean ring(BlockFace face) {
		return ring(null, face);
	}

	@Override
	public boolean ring() {
		return ring(null, null);
	}

	@Override
	public boolean isShaking() {
		BlockEntityBell bell = bell();
		return bell != null && bell.isRinging();
	}

	@Override
	public int getShakingTicks() {
		BlockEntityBell bell = bell();
		return bell == null ? 0 : bell.getTicks();
	}

	@Override
	public boolean isResonating() {
		// Nukkit does not track resonance.
		return false;
	}

	@Override
	public int getResonatingTicks() {
		return 0;
	}
}

final class SkullBlockState extends PokkitTileState implements Skull {

	/**
	 * Rotation values of a skull, in the order used by the game (0 = south).
	 */
	private static final BlockFace[] ROTATIONS = { BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST,
			BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST,
			BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST,
			BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST,
			BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST };

	SkullBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.SKULL;
	}

	@Override
	public boolean hasOwner() {
		return tileTag().contains("SkullOwner");
	}

	@Override
	public String getOwner() {
		return hasOwner() ? tileTag().getString("SkullOwner") : null;
	}

	@Override
	public boolean setOwner(String owner) {
		if (owner == null) {
			tileTag().remove("SkullOwner");
		} else {
			tileTag().putString("SkullOwner", owner);
		}
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOwningPlayer() {
		String owner = getOwner();
		return owner == null ? null : Bukkit.getOfflinePlayer(owner);
	}

	@Override
	public void setOwningPlayer(OfflinePlayer player) {
		setOwner(player == null ? null : player.getName());
	}

	@Override
	public PlayerProfile getOwnerProfile() {
		String owner = getOwner();
		return owner == null ? null : Bukkit.createPlayerProfile(owner);
	}

	@Override
	public void setOwnerProfile(PlayerProfile profile) {
		setOwner(profile == null ? null : profile.getName());
	}

	@Override
	public NamespacedKey getNoteBlockSound() {
		String sound = tileTag().getString("NoteBlockSound");
		return sound.isEmpty() ? null : NamespacedKey.fromString(sound);
	}

	@Override
	public void setNoteBlockSound(NamespacedKey key) {
		if (key == null) {
			tileTag().remove("NoteBlockSound");
		} else {
			tileTag().putString("NoteBlockSound", key.toString());
		}
	}

	@Override
	public BlockFace getRotation() {
		int rotation = tileTag().getByte("Rot") & 0xF;
		return ROTATIONS[rotation];
	}

	@Override
	public void setRotation(BlockFace face) {
		for (int i = 0; i < ROTATIONS.length; i++) {
			if (ROTATIONS[i] == face) {
				tileTag().putByte("Rot", i);
				return;
			}
		}
		throw new IllegalArgumentException("BlockFace " + face + " is not a valid skull rotation");
	}

	@Override
	public SkullType getSkullType() {
		Material type = getType();
		return switch (type) {
		case SKELETON_SKULL, SKELETON_WALL_SKULL -> SkullType.SKELETON;
		case WITHER_SKELETON_SKULL, WITHER_SKELETON_WALL_SKULL -> SkullType.WITHER;
		case ZOMBIE_HEAD, ZOMBIE_WALL_HEAD -> SkullType.ZOMBIE;
		case CREEPER_HEAD, CREEPER_WALL_HEAD -> SkullType.CREEPER;
		case DRAGON_HEAD, DRAGON_WALL_HEAD -> SkullType.DRAGON;
		case PIGLIN_HEAD, PIGLIN_WALL_HEAD -> SkullType.PIGLIN;
		default -> SkullType.PLAYER;
		};
	}

	@Override
	public void setSkullType(SkullType type) {
		// Changing the skull type would replace the block.
		throw Pokkit.unsupported();
	}
}

final class BannerBlockState extends PokkitTileState implements Banner {

	BannerBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BANNER;
	}

	private BlockEntityBanner banner() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityBanner banner ? banner : null;
	}

	@Override
	public DyeColor getBaseColor() {
		BlockEntityBanner banner = banner();
		if (banner == null) {
			return DyeColor.WHITE;
		}
		return DyeColor.valueOf(banner.getDyeColor().name());
	}

	@Override
	public void setBaseColor(DyeColor color) {
		BlockEntityBanner banner = banner();
		if (banner != null) {
			banner.setBaseColor(cn.nukkit.utils.DyeColor.valueOf(color.name()));
		}
	}

	@Override
	public List<Pattern> getPatterns() {
		// Bedrock and Java use different pattern identifiers; translating them
		// is not implemented yet.
		return Collections.emptyList();
	}

	@Override
	public void setPatterns(List<Pattern> patterns) {
		throw Pokkit.unsupported();
	}

	@Override
	public void addPattern(Pattern pattern) {
		throw Pokkit.unsupported();
	}

	@Override
	public Pattern getPattern(int index) {
		throw Pokkit.unsupported();
	}

	@Override
	public Pattern removePattern(int index) {
		throw Pokkit.unsupported();
	}

	@Override
	public void setPattern(int index, Pattern pattern) {
		throw Pokkit.unsupported();
	}

	@Override
	public int numberOfPatterns() {
		// Patterns cannot be translated between the Java and Bedrock formats
		// yet, so this matches getPatterns() instead of the block entity.
		return 0;
	}
}

final class BeehiveBlockState extends PokkitTileState implements Beehive {

	BeehiveBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BEEHIVE;
	}

	private BlockEntityBeehive beehive() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityBeehive beehive ? beehive : null;
	}

	@Override
	public Location getFlower() {
		// Nukkit does not store the flower position of a beehive.
		return null;
	}

	@Override
	public void setFlower(Location location) {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean isSedated() {
		// Nukkit does not expose the sedation state.
		return false;
	}

	@Override
	public boolean isFull() {
		return getEntityCount() >= getMaxEntities();
	}

	@Override
	public int getEntityCount() {
		BlockEntityBeehive beehive = beehive();
		return beehive == null ? 0 : beehive.getOccupantsCount();
	}

	@Override
	public int getMaxEntities() {
		return 3;
	}

	@Override
	public void setMaxEntities(int maxEntities) {
		if (maxEntities != 3) {
			throw Pokkit.unsupported();
		}
	}

	@Override
	public List<Bee> releaseEntities() {
		return Collections.emptyList();
	}

	@Override
	public void addEntity(Bee entity) {
		throw Pokkit.unsupported();
	}
}

final class ConduitBlockState extends PokkitTileState implements Conduit {

	ConduitBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.CONDUIT;
	}

	private BlockEntityConduit conduit() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityConduit conduit ? conduit : null;
	}

	@Override
	public boolean isActive() {
		BlockEntityConduit conduit = conduit();
		return conduit != null && conduit.isActive();
	}

	@Override
	public boolean isHunting() {
		// Nukkit does not expose the hunting state.
		return false;
	}

	@Override
	public Collection<Block> getFrameBlocks() {
		// Nukkit does not expose the frame blocks of a conduit.
		return Collections.emptyList();
	}

	@Override
	public int getFrameBlockCount() {
		return 0;
	}

	@Override
	public int getRange() {
		BlockEntityConduit conduit = conduit();
		return conduit == null ? 0 : conduit.getEffectRadius();
	}

	@Override
	public boolean setTarget(LivingEntity target) {
		BlockEntityConduit conduit = conduit();
		if (conduit == null || target == null) {
			return false;
		}
		cn.nukkit.entity.Entity nukkitTarget = nl.rutgerkok.pokkit.entity.PokkitEntity.toNukkit(target);
		if (nukkitTarget == null) {
			return false;
		}
		conduit.setTarget(nukkitTarget.getId());
		return true;
	}

	@Override
	public LivingEntity getTarget() {
		BlockEntityConduit conduit = conduit();
		if (conduit == null) {
			return null;
		}
		long targetId = conduit.getTarget();
		if (targetId <= 0) {
			return null;
		}
		org.bukkit.entity.Entity target = nl.rutgerkok.pokkit.entity.PokkitEntity
				.toBukkit(conduit.getLevel().getEntity(targetId));
		return target instanceof LivingEntity living ? living : null;
	}

	@Override
	public boolean hasTarget() {
		return getTarget() != null;
	}

	@Override
	public BoundingBox getHuntingArea() {
		if (!isHunting()) {
			return null;
		}
		int range = getRange();
		return new BoundingBox(getX() - range, getY() - range, getZ() - range, getX() + range + 1,
				getY() + range + 1, getZ() + range + 1);
	}
}
