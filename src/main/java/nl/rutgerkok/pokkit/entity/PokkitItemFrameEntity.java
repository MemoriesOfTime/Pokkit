package nl.rutgerkok.pokkit.entity;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;

import cn.nukkit.blockentity.BlockEntityItemFrame;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;

public class PokkitItemFrameEntity extends PokkitFakeEntity implements ItemFrame {
	BlockEntityItemFrame nukkit;

	public PokkitItemFrameEntity(BlockEntityItemFrame nukkit) {
		this.nukkit = nukkit;
	}

	@Override
	public BlockFace getAttachedFace() {
		throw Pokkit.unsupported();
	}

	@Override
	public BlockFace getFacing() {
		throw Pokkit.unsupported();
	}

	@Override
	public Pose getPose() {
		throw Pokkit.unsupported();
	}

	@Override
	public ItemStack getItem() {
		return PokkitItemStack.toBukkitCopy(nukkit.getItem());
	}

	@Override
	cn.nukkit.level.Location getNukkitLocation() {
		return nukkit.getLocation();
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		return PistonMoveReaction.BREAK;
	}

	@Override
	public Rotation getRotation() {
		// TODO: Implement
		return Rotation.CLOCKWISE;
	}

	@Override
	public EntityType getType() {
		return EntityType.ITEM_FRAME;
	}

	@Override
	public boolean isValid() {
		return nukkit.isBlockEntityValid();
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void setRotation(float v, float v1) {
		Pokkit.notImplemented();
	}

	@Override
	public void remove() {
		nukkit.close();
	}

	@Override
	public void setFacingDirection(BlockFace face) {
		Pokkit.notImplemented();
	}

	@Override
	public boolean setFacingDirection(BlockFace face, boolean force) {
		throw Pokkit.unsupported();
	}

	@Override
	public void setItem(ItemStack item) {
		nukkit.setItem(PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public void setItem(ItemStack item, boolean b) {
		nukkit.setItem(PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public void setRotation(Rotation rotation) throws IllegalArgumentException {
		Pokkit.notImplemented();
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		throw Pokkit.unsupported();
	}
}
