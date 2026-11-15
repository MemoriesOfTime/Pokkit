package nl.rutgerkok.pokkit.entity;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

import cn.nukkit.level.Location;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;

/**
 * In Bukkit, dropped items are always entities. This is not the case in Nukkit (???),
 * so here we have a "fake entity" implementation.
 *
 */
public class PokkitItemEntity extends PokkitFakeEntity implements Item {

	public cn.nukkit.item.Item item;
	public final cn.nukkit.level.Location location;

	public PokkitItemEntity(cn.nukkit.item.Item item, Location location) {
		this.item = Objects.requireNonNull(item, "item");
		this.location = Objects.requireNonNull(location, "location");
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = PokkitItemStack.toBukkitCopy(item);
		// Bukkit never returns null here, even for an item that this server
		// cannot map.
		return stack != null ? stack : new ItemStack(org.bukkit.Material.AIR);
	}

	@Override
	cn.nukkit.level.Location getNukkitLocation() {
		return this.location;
	}

	@Override
	public int getPickupDelay() {
		return 0;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return super.getBoundingBox();
	}

	@Override
	public void setRotation(float v, float v1) {
		super.setRotation(v, v1);
	}

	@Override
	public EntityType getType() {
        return EntityType.ITEM;
	}

	@Override
	public void setItemStack(ItemStack stack) {
		this.item = PokkitItemStack.toNukkitCopy(stack);
	}

	@Override
	public void setPickupDelay(int delay) {
	}

	@Override
	public BlockFace getFacing() {
		return BlockFace.SOUTH;
	}

	@Override
	public Pose getPose() {
		return Pose.STANDING;
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return null;
	}

	@Override
	public UUID getThrower() {
		return null;
	}

	@Override
	public void setThrower(UUID uuid) {
	}

	@Override
	public UUID getOwner() {
		return null;
	}

	@Override
	public void setOwner(UUID uuid) {
	}

	@Override
	public boolean isUnlimitedLifetime() {
		return false;
	}

	@Override
	public void setUnlimitedLifetime(boolean unlimited) {
	}
}
