package nl.rutgerkok.pokkit.inventory;

import com.google.common.base.Preconditions;

import cn.nukkit.Player;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import nl.rutgerkok.pokkit.entity.PokkitHumanEntity;
import nl.rutgerkok.pokkit.item.PokkitItemStack;

import cn.nukkit.item.Item;

public class PokkitPlayerInventory extends PokkitLiveInventory implements PlayerInventory {

	/**
	 * Number of Bukkit slots after the storage slots: four armour slots and one
	 * off-hand slot.
	 */
	private static final int EXTRA_SLOTS = 5;

	public PokkitPlayerInventory(cn.nukkit.inventory.PlayerInventory inventory) {
		super(inventory);
	}

	/**
	 * Gets the number of storage slots of the underlying Nukkit inventory.
	 * Nukkit does not count the armour slots in the inventory size, Bukkit
	 * does.
	 *
	 * @return The number of storage slots (36 for players).
	 */
	private int storageSize() {
		return nukkit().getSize();
	}

	@Override
	public int getSize() {
		// Bukkit counts the four armour slots and the off-hand slot.
		return storageSize() + EXTRA_SLOTS;
	}

	@Override
	protected int getStorageSize() {
		return storageSize();
	}

	@Override
	protected Item getNukkitItem(int index) {
		int storage = storageSize();
		if (index < storage) {
			return super.getNukkitItem(index);
		}
		switch (index - storage) {
		case 0:
			return nukkit().getBoots();
		case 1:
			return nukkit().getLeggings();
		case 2:
			return nukkit().getChestplate();
		case 3:
			return nukkit().getHelmet();
		case 4:
			cn.nukkit.inventory.Inventory offhand = offhandInventoryOrNull();
			return offhand == null ? Item.get(Item.AIR) : offhand.getItem(0);
		default:
			return Item.get(Item.AIR);
		}
	}

	@Override
	public ItemStack getItem(int index) {
		if (index < 0 || index >= getSize()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return PokkitItemStack.toBukkitCopy(getNukkitItem(index));
	}

	@Override
	public void setItem(int index, ItemStack item) {
		if (index < 0 || index >= getSize()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		int storage = storageSize();
		if (index < storage) {
			super.setItem(index, item);
			return;
		}
		Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
		switch (index - storage) {
		case 0:
			nukkit().setBoots(nukkitItem);
			break;
		case 1:
			nukkit().setLeggings(nukkitItem);
			break;
		case 2:
			nukkit().setChestplate(nukkitItem);
			break;
		case 3:
			nukkit().setHelmet(nukkitItem);
			break;
		case 4:
			cn.nukkit.inventory.Inventory offhand = offhandInventoryOrNull();
			if (offhand != null) {
				offhand.setItem(0, nukkitItem);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void clear(int index) {
		setItem(index, null);
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[getSize()];
		int storage = storageSize();
		for (int i = 0; i < storage; i++) {
			contents[i] = PokkitItemStack.toBukkitCopy(nukkit().getItem(i));
		}
		// Bukkit slot order: boots, leggings, chestplate, helmet, off-hand.
		contents[storage] = getBoots();
		contents[storage + 1] = getLeggings();
		contents[storage + 2] = getChestplate();
		contents[storage + 3] = getHelmet();
		contents[storage + 4] = getItemInOffHand();
		return contents;
	}

	@Override
	public ItemStack[] getStorageContents() {
		ItemStack[] contents = new ItemStack[storageSize()];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = PokkitItemStack.toBukkitCopy(nukkit().getItem(i));
		}
		return contents;
	}

	@Override
	public void setStorageContents(ItemStack[] items) {
		Preconditions.checkArgument(items.length <= storageSize(),
				"Storage contents must have at most %s items", storageSize());
		for (int i = 0; i < storageSize(); i++) {
			setItem(i, i < items.length ? items[i] : null);
		}
	}

	@Override
	public ItemStack[] getArmorContents() {
		Item[] nukkitArmor = nukkit().getArmorContents();
		ItemStack[] bukkitArmor = new ItemStack[nukkitArmor.length];

		// Nukkit stores the armour from helmet to boots, Bukkit the other way
		// around ("starting from the boots and going up to the helmet").
		for (int i = 0; i < nukkitArmor.length; i++) {
			bukkitArmor[i] = PokkitItemStack.toBukkitCopy(nukkitArmor[nukkitArmor.length - 1 - i]);
		}

		return bukkitArmor;
	}

	@Override
	public void setArmorContents(ItemStack[] bukkitArmor) {
		if (bukkitArmor == null) {
			bukkitArmor = new ItemStack[0];
		}
		Preconditions.checkArgument(bukkitArmor.length <= 4,
				"Armor contents must have at most 4 items, got %s", bukkitArmor.length);

		nukkit().setArmorContents(toNukkitArmor(bukkitArmor));
	}

	/**
	 * Converts Bukkit armour contents to the array Nukkit expects.
	 *
	 * <p>
	 * Bukkit orders the array from the boots up to the helmet, Nukkit the other
	 * way around. The array is always exactly four items long: slots that are
	 * not present in the Bukkit array are cleared, and the items that are
	 * present keep the position they have in Bukkit (so a single item is the
	 * boots, never the helmet).
	 *
	 * @param bukkitArmor
	 *            The Bukkit armour contents, at most four items.
	 * @return Four Nukkit items, ordered from helmet to boots.
	 */
	static Item[] toNukkitArmor(ItemStack[] bukkitArmor) {
		// Nukkit always uses four armour slots, independent of how many items
		// the caller supplies.
		Item[] nukkitArmor = new Item[4];
		for (int i = 0; i < nukkitArmor.length; i++) {
			nukkitArmor[i] = Item.get(Item.AIR);
		}
		for (int i = 0; i < bukkitArmor.length; i++) {
			// Bukkit index 0 is the boots, Nukkit index 3 is. The Nukkit array
			// length must be used here: using the length of the Bukkit array
			// would shift every item down when fewer than four items are set.
			nukkitArmor[nukkitArmor.length - 1 - i] = PokkitItemStack.toNukkitCopy(bukkitArmor[i]);
		}
		return nukkitArmor;
	}

	@Override
	public ItemStack[] getExtraContents() {
		return new ItemStack[] { getItemInOffHand() };
	}

	@Override
	public void setExtraContents(ItemStack[] items) {
		if (items == null || items.length == 0) {
			setItemInOffHand(null);
			return;
		}
		setItemInOffHand(items[0]);
	}

	@Override
	public ItemStack getBoots() {
		return PokkitItemStack.toBukkitCopy(nukkit().getBoots());
	}

	@Override
	public void setItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		switch (equipmentSlot) {
			case HAND:
				setItemInHand(itemStack);
				return;
			case OFF_HAND:
				setItemInOffHand(itemStack);
				return;
			case FEET:
				setBoots(itemStack);
				return;
			case LEGS:
				setLeggings(itemStack);
				return;
			case CHEST:
				setChestplate(itemStack);
				return;
			case HEAD:
				setHelmet(itemStack);
				return;
		}
	}

	@Override
	public ItemStack getItem(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot) {
			case HAND:
				return getItemInHand();
			case OFF_HAND:
				return getItemInOffHand();
			case FEET:
				return getBoots();
			case LEGS:
				return getLeggings();
			case CHEST:
				return getChestplate();
			case HEAD:
				return getHelmet();
		}
		return null;
	}

	@Override
	public ItemStack getChestplate() {
		return PokkitItemStack.toBukkitCopy(nukkit().getChestplate());
	}

	@Override
	public int getHeldItemSlot() {
		return nukkit().getHeldItemIndex();
	}

	@Override
	public ItemStack getHelmet() {
		return PokkitItemStack.toBukkitCopy(nukkit().getHelmet());
	}

	@Override
	public HumanEntity getHolder() {
		return PokkitHumanEntity.toBukkit(nukkit().getHolder());
	}

	@Override
	public ItemStack getItemInHand() {
		return PokkitItemStack.toBukkitCopy(nukkit().getItemInHand());
	}

	@Override
	public ItemStack getItemInMainHand() {
		return PokkitItemStack.toBukkitCopy(nukkit().getItemInHand());
	}

	@Override
	public ItemStack getItemInOffHand() {
		cn.nukkit.inventory.Inventory offhand = offhandInventoryOrNull();
		if (offhand == null) {
			return new ItemStack(Material.AIR);
		}
		ItemStack item = PokkitItemStack.toBukkitCopy(offhand.getItem(0));
		if (item == null) return new ItemStack(Material.AIR);
		return item;
	}

	@Override
	public ItemStack getLeggings() {
		return PokkitItemStack.toBukkitCopy(nukkit().getLeggings());
	}

	private cn.nukkit.inventory.PlayerInventory nukkit() {
		return (cn.nukkit.inventory.PlayerInventory) super.nukkit;
	}

	/**
	 * Gets the off-hand inventory, or null if the holder is not a player (for
	 * example a villager, which also has a player inventory).
	 *
	 * @return The off-hand inventory, or null.
	 */
	private cn.nukkit.inventory.Inventory offhandInventoryOrNull() {
		return nukkit.getHolder() instanceof Player player ? player.getOffhandInventory() : null;
	}

	@Override
	public void setBoots(ItemStack boots) {
		nukkit().setBoots(PokkitItemStack.toNukkitCopy(boots));
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		nukkit().setChestplate(PokkitItemStack.toNukkitCopy(chestplate));
	}

	@Override
	public void setHeldItemSlot(int slot) {
		nukkit().setHeldItemSlot(slot);
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		nukkit().setHelmet(PokkitItemStack.toNukkitCopy(helmet));
	}

	@Override
	public void setItemInHand(ItemStack stack) {
		nukkit().setItemInHand(PokkitItemStack.toNukkitCopy(stack));
	}

	@Override
	public void setItemInMainHand(ItemStack item) {
		nukkit().setItemInHand(PokkitItemStack.toNukkitCopy(item));
	}

	@Override
	public void setItemInOffHand(ItemStack item) {
		cn.nukkit.inventory.Inventory offhand = offhandInventoryOrNull();
		if (offhand != null) {
			offhand.setItem(0, PokkitItemStack.toNukkitCopy(item));
		}
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		nukkit().setLeggings(PokkitItemStack.toNukkitCopy(leggings));
	}

}
