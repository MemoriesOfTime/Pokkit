package nl.rutgerkok.pokkit.inventory;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class PokkitEntityEquipment extends PokkitPlayerInventory implements EntityEquipment {

    private final Map<EquipmentSlot, Float> dropChances = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, ItemStack> silentItems = new EnumMap<>(EquipmentSlot.class);

    public PokkitEntityEquipment(cn.nukkit.inventory.PlayerInventory inventory) {
        super(inventory);
    }

    @Override
    public float getItemInHandDropChance() {
        return getItemInMainHandDropChance();
    }

    @Override
    public void setItemInHandDropChance(float v) {
        setItemInMainHandDropChance(v);
    }

    @Override
    public float getItemInMainHandDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.HAND, 1f);
    }

    @Override
    public void setItemInMainHandDropChance(float v) {
        dropChances.put(EquipmentSlot.HAND, v);
    }

    @Override
    public float getItemInOffHandDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.OFF_HAND, 1f);
    }

    @Override
    public void setItemInOffHandDropChance(float v) {
        dropChances.put(EquipmentSlot.OFF_HAND, v);
    }

    @Override
    public float getHelmetDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.HEAD, 1f);
    }

    @Override
    public void setHelmetDropChance(float v) {
        dropChances.put(EquipmentSlot.HEAD, v);
    }

    @Override
    public float getChestplateDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.CHEST, 1f);
    }

    @Override
    public void setChestplateDropChance(float v) {
        dropChances.put(EquipmentSlot.CHEST, v);
    }

    @Override
    public float getLeggingsDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.LEGS, 1f);
    }

    @Override
    public void setLeggingsDropChance(float v) {
        dropChances.put(EquipmentSlot.LEGS, v);
    }

    @Override
    public float getBootsDropChance() {
        return dropChances.getOrDefault(EquipmentSlot.FEET, 1f);
    }

    @Override
    public void setBootsDropChance(float v) {
        dropChances.put(EquipmentSlot.FEET, v);
    }

    @Override
    public void setBoots(ItemStack item, boolean silent) {
        setBoots(item);
    }

    @Override
    public void setLeggings(ItemStack item, boolean silent) {
        setLeggings(item);
    }

    @Override
    public void setChestplate(ItemStack item, boolean silent) {
        setChestplate(item);
    }

    @Override
    public void setHelmet(ItemStack item, boolean silent) {
        setHelmet(item);
    }

    @Override
    public void setItemInMainHand(ItemStack item, boolean silent) {
        setItemInMainHand(item);
    }

    @Override
    public void setItemInOffHand(ItemStack item, boolean silent) {
        setItemInOffHand(item);
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item, boolean silent) {
        setItem(slot, item);
    }
}
