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
		return BlockFace.SOUTH;
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
		int rot = nukkit.getItemRotation();
		switch (rot % 4) {
		case 0:
			return Rotation.NONE;
		case 1:
			return Rotation.CLOCKWISE;
		case 2:
			return Rotation.FLIPPED;
		case 3:
			return Rotation.COUNTER_CLOCKWISE;
		default:
			return Rotation.NONE;
		}
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
	}

	@Override
	public void remove() {
		nukkit.close();
	}

	@Override
	public void setFacingDirection(BlockFace face) {
	}

	@Override
	public boolean setFacingDirection(BlockFace face, boolean force) {
		return false;
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
		int rot;
		switch (rotation) {
		case NONE:
			rot = 0;
			break;
		case CLOCKWISE:
			rot = 1;
			break;
		case FLIPPED:
			rot = 2;
			break;
		case COUNTER_CLOCKWISE:
			rot = 3;
			break;
		default:
			rot = 0;
			break;
		}
		nukkit.setItemRotation(rot);
	}

	@Override
	public PersistentDataContainer getPersistentDataContainer() {
		return null;
	}

	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public void setFixed(boolean fixed) {
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
	}

	@Override
	public float getItemDropChance() {
		return 1.0f;
	}

	@Override
	public void setItemDropChance(float chance) {
		nukkit.setItemDropChance(chance);
	}
}
