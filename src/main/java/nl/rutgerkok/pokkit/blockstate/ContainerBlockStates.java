package nl.rutgerkok.pokkit.blockstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Beacon;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Hopper;
import org.bukkit.block.Lectern;
import org.bukkit.block.Lockable;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.potion.PokkitPotionEffectType;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.blockentity.BlockEntityLectern;

/**
 * Adds the item-based lock that was added to {@link Lockable} in Bukkit
 * 1.21.2. Nukkit only stores locks as plain strings, so removing a lock can be
 * represented, but an item key cannot.
 */
interface PokkitLockable extends Lockable {

	@Override
	default void setLockItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			setLock(null);
			return;
		}
		// Bedrock locks match the name of the held item; a complete item key
		// cannot be represented without silently dropping its components.
		throw Pokkit.unsupported();
	}
}

/**
 * Container block states whose Bukkit interface adds no state beyond the
 * inventory, the custom name and the lock.
 */
final class ChestBlockState extends PokkitContainerState implements Chest, PokkitLockable {

	ChestBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 27, "Chest");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.CHEST;
	}
}

final class BarrelBlockState extends PokkitContainerState implements Barrel, PokkitLockable {

	BarrelBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 27, "Barrel");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BARREL;
	}
}

final class ShulkerBoxBlockState extends PokkitContainerState implements ShulkerBox, PokkitLockable {

	ShulkerBoxBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 27, "Shulker Box");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.SHULKER_BOX;
	}

	@Override
	public DyeColor getColor() {
		String name = getType().name();
		if (name.equals("SHULKER_BOX")) {
			return DyeColor.PURPLE;
		}
		int underscore = name.indexOf("_SHULKER_BOX");
		if (underscore > 0) {
			return DyeColor.valueOf(name.substring(0, underscore));
		}
		return DyeColor.PURPLE;
	}
}

final class HopperBlockState extends PokkitContainerState implements Hopper, PokkitLockable {

	HopperBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 5, "Hopper");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.HOPPER;
	}
}

final class DropperBlockState extends PokkitContainerState implements Dropper, PokkitLockable {

	DropperBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 9, "Dropper");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.DROPPER;
	}

	@Override
	public void drop() {
		// Nukkit does not expose the dropper logic; triggering onActivate would
		// need a fake player and item.
		throw Pokkit.unsupported();
	}
}

final class DispenserBlockState extends PokkitContainerState implements Dispenser, PokkitLockable {

	DispenserBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData, 9, "Dispenser");
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.DISPENSER;
	}

	@Override
	public BlockProjectileSource getBlockProjectileSource() {
		// Nukkit has no BlockProjectileSource equivalent.
		return null;
	}

	@Override
	public boolean dispense() {
		throw Pokkit.unsupported();
	}
}

final class EnderChestBlockState extends PokkitTileState implements EnderChest {

	EnderChestBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.ENDER_CHEST;
	}

	@Override
	public void open() {
		// Nukkit has no server-side chest animation.
	}

	@Override
	public void close() {
	}
}

final class LecternBlockState extends PokkitTileState implements Lectern {

	LecternBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.LECTERN;
	}

	private BlockEntityLectern lectern() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityLectern lectern ? lectern : null;
	}

	@Override
	public int getPage() {
		BlockEntityLectern lectern = lectern();
		return lectern == null ? 0 : lectern.getLeftPage();
	}

	@Override
	public void setPage(int page) {
		BlockEntityLectern lectern = lectern();
		if (lectern != null) {
			lectern.setLeftPage(page);
			lectern.setRightPage(page + 1);
		}
	}

	@Override
	public Inventory getInventory() {
		Inventory inventory = Bukkit.createInventory(null, 9, "Lectern");
		BlockEntityLectern lectern = lectern();
		if (lectern != null && lectern.hasBook()) {
			ItemStack book = PokkitItemStack.toBukkitCopy(lectern.getBook());
			if (book != null) {
				inventory.setItem(0, book);
			}
		}
		return inventory;
	}

	@Override
	public Inventory getSnapshotInventory() {
		return getInventory();
	}
}

final class BeaconBlockState extends PokkitTileState implements Beacon, PokkitLockable {

	BeaconBlockState(Location location, PokkitBlockData blockData) {
		super(location, blockData);
	}

	@Override
	protected String blockEntityType() {
		return BlockEntity.BEACON;
	}

	private BlockEntityBeacon beacon() {
		cn.nukkit.blockentity.BlockEntity entity = tileEntity();
		return entity instanceof BlockEntityBeacon beacon ? beacon : null;
	}

	@Override
	public Collection<LivingEntity> getEntitiesInRange() {
		List<LivingEntity> result = new ArrayList<>();
		int range = 10 + getTier() * 10;
		for (Entity entity : getWorld().getNearbyEntities(getLocation(), range, range, range)) {
			if (entity instanceof LivingEntity living) {
				result.add(living);
			}
		}
		return result;
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

	@Override
	public boolean isLocked() {
		String lock = getLock();
		return lock != null && !lock.isEmpty();
	}

	@Override
	public String getLock() {
		return tileTag().contains("Lock") ? tileTag().getString("Lock") : null;
	}

	@Override
	public void setLock(String lock) {
		if (lock == null || lock.isEmpty()) {
			tileTag().remove("Lock");
		} else {
			tileTag().putString("Lock", lock);
		}
	}

	@Override
	public int getTier() {
		BlockEntityBeacon beacon = beacon();
		return beacon == null ? 0 : beacon.getPowerLevel();
	}

	@Override
	public PotionEffect getPrimaryEffect() {
		BlockEntityBeacon beacon = beacon();
		return beacon == null ? null : toEffect(beacon.getPrimaryPower());
	}

	@Override
	public void setPrimaryEffect(PotionEffectType type) {
		BlockEntityBeacon beacon = beacon();
		if (beacon == null) {
			return;
		}
		if (type == null) {
			beacon.setPrimaryPower(0);
			return;
		}
		int nukkitId = PokkitPotionEffectType.toNukkitId(type);
		if (nukkitId <= 0) {
			// Bedrock has no equivalent for this effect, so storing the Java id
			// would silently apply a different effect.
			throw Pokkit.unsupported();
		}
		beacon.setPrimaryPower(nukkitId);
	}

	@Override
	public PotionEffect getSecondaryEffect() {
		BlockEntityBeacon beacon = beacon();
		return beacon == null ? null : toEffect(beacon.getSecondaryPower());
	}

	@Override
	public void setSecondaryEffect(PotionEffectType type) {
		BlockEntityBeacon beacon = beacon();
		if (beacon == null) {
			return;
		}
		if (type == null) {
			beacon.setSecondaryPower(0);
			return;
		}
		int nukkitId = PokkitPotionEffectType.toNukkitId(type);
		if (nukkitId <= 0) {
			throw Pokkit.unsupported();
		}
		beacon.setSecondaryPower(nukkitId);
	}

	private static PotionEffect toEffect(int id) {
		PotionEffectType type = PokkitPotionEffectType.fromNukkitId(id);
		return type == null ? null : new PotionEffect(type, 0, 0);
	}
}
