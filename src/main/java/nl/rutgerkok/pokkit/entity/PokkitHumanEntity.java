package nl.rutgerkok.pokkit.entity;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.inventory.PokkitInventory;
import nl.rutgerkok.pokkit.inventory.PokkitPlayerInventory;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class PokkitHumanEntity extends PokkitLivingEntity implements HumanEntity {

	public static HumanEntity toBukkit(cn.nukkit.entity.EntityHuman human) {
		if (human == null) {
			return null;
		}
		if (human instanceof cn.nukkit.Player) {
			return PokkitPlayer.toBukkit((cn.nukkit.Player) human);
		}
		throw Pokkit.unsupported();
	}

	private final cn.nukkit.entity.EntityHuman nukkit;

	public PokkitHumanEntity(cn.nukkit.entity.EntityHuman nukkitEntity) {
		super(nukkitEntity);

		this.nukkit = nukkitEntity;
	}

	@Override
	public void closeInventory() {
		nukkit.close();
	}

	@Override
	public int getCooldown(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public Inventory getEnderChest() {
		return PokkitInventory.toBukkit(nukkit.getEnderChestInventory());
	}

	@Override
	public int getExpToLevel() {
		throw Pokkit.unsupported();
	}

	@Override
	public float getAttackCooldown() {
		return 0;
	}

	@Override
	public GameMode getGameMode() {
		throw Pokkit.unsupported();
	}

	@Override
	public PlayerInventory getInventory() {
		return new PokkitPlayerInventory(nukkit.getInventory());
	}

	@Override
	public ItemStack getItemInHand() {
		return PokkitItemStack.toBukkitCopy(nukkit.getInventory().getItemInHand());
	}

	@Override
	public ItemStack getItemOnCursor() {
		// In Bukkit, this gets the item in the current hand, which in Nukkit
		// only the main hand is implemented
		return getItemInHand();
	}

	@Override
	public MainHand getMainHand() {
		return MainHand.RIGHT;
	}

	@Override
	public InventoryView getOpenInventory() {
		throw Pokkit.unsupported();
	}

	@Override
	public Entity getShoulderEntityLeft() {
		return null; // Not implemented in Nukkit
	}

	@Override
	public Entity getShoulderEntityRight() {
		return null; // Not implemented in Nukkit
	}

	@Override
	public int getSleepTicks() {
		throw Pokkit.unsupported();
	}

	@Override
	public Location getBedSpawnLocation() {
		throw Pokkit.unsupported();
	}

	@Override
	public void setBedSpawnLocation(Location location) {
		Pokkit.notImplemented();
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean b) {
		Pokkit.notImplemented();
	}

	@Override
	public boolean sleep(Location location, boolean b) {
		throw Pokkit.unsupported();
	}

	@Override
	public void wakeup(boolean b) {
		Pokkit.notImplemented();
	}

	@Override
	public Location getBedLocation() {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean hasCooldown(Material material) {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean isBlocking() {
		return nukkit.isBlocking();
	}

	@Override
	public boolean isHandRaised() {
		return false;
	}

	@Override
	public boolean isSleeping() {
		throw Pokkit.unsupported();
	}

	@Override
	public void attack(Entity entity) {
		Pokkit.notImplemented();
	}

	@Override
	public void swingMainHand() {

	}

	@Override
	public void swingOffHand() {

	}

	@Override
	public InventoryView openEnchanting(Location location, boolean force) {
		throw Pokkit.unsupported();
	}

	@Override
	public InventoryView openInventory(Inventory inventory) {
		throw Pokkit.unsupported();
	}

	@Override
	public void openInventory(InventoryView inventory) {
		Pokkit.notImplemented();
	}

	@Override
	public InventoryView openMerchant(Merchant arg0, boolean arg1) {
		throw Pokkit.unsupported();
	}

	@Override
	public InventoryView openMerchant(Villager trader, boolean force) {
		throw Pokkit.unsupported();
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean force) {
		throw Pokkit.unsupported();
	}

	@Override
	public void setCooldown(Material material, int cooldown) {
		Pokkit.notImplemented();
	}

	@Override
	public void setGameMode(GameMode mode) {
		Pokkit.notImplemented();
	}

	@Override
	public void setItemInHand(ItemStack arg0) {
		nukkit.getInventory().setItemInHand(PokkitItemStack.toNukkitCopy(arg0));
	}

	@Override
	public void setItemOnCursor(ItemStack item) {
		// In Bukkit, this sets the item in the current hand, which in Nukkit
		// only the main hand is implemented
		setItemInHand(item);
	}

	@Override
	public void setShoulderEntityLeft(Entity entity) {
		Pokkit.notImplemented();
	}

	@Override
	public void setShoulderEntityRight(Entity entity) {
		Pokkit.notImplemented();
	}

	@Override
	public boolean setWindowProperty(Property prop, int value) {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean discoverRecipe(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public int discoverRecipes(Collection <NamespacedKey> key) {
		throw Pokkit.unsupported();
	}

	@Override
	public boolean undiscoverRecipe(NamespacedKey key) {
		throw Pokkit.unsupported();
	}

	@Override
	public int undiscoverRecipes(Collection <NamespacedKey> key) {
		throw Pokkit.unsupported();
	}
}
