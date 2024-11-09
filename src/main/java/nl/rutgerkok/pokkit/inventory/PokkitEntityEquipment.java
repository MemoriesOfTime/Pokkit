package nl.rutgerkok.pokkit.inventory;

import nl.rutgerkok.pokkit.Pokkit;
import org.bukkit.inventory.EntityEquipment;

public final class PokkitEntityEquipment extends PokkitPlayerInventory implements EntityEquipment {

	public PokkitEntityEquipment(cn.nukkit.inventory.PlayerInventory inventory) {
		super(inventory);
	}

	@Override
	public float getItemInHandDropChance() {
		return 1f;
	}

	@Override
	public void setItemInHandDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getItemInMainHandDropChance() {
		return 1f;
	}

	@Override
	public void setItemInMainHandDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getItemInOffHandDropChance() {
		return 1f;
	}

	@Override
	public void setItemInOffHandDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getHelmetDropChance() {
		return 1f;
	}

	@Override
	public void setHelmetDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getChestplateDropChance() {
		return 1f;
	}

	@Override
	public void setChestplateDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getLeggingsDropChance() {
		return 1f;
	}

	@Override
	public void setLeggingsDropChance(float v) {
		Pokkit.notImplemented();
	}

	@Override
	public float getBootsDropChance() {
		return 1f;
	}

	@Override
	public void setBootsDropChance(float v) {
		Pokkit.notImplemented();
	}
}
