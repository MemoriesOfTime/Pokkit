package nl.rutgerkok.pokkit.entity;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.PokkitGameMode;
import nl.rutgerkok.pokkit.inventory.PokkitInventory;
import nl.rutgerkok.pokkit.inventory.PokkitInventoryView;
import nl.rutgerkok.pokkit.inventory.PokkitPlayerInventory;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PokkitHumanEntity extends PokkitLivingEntity implements HumanEntity {

	private final Map<Material, Long> cooldowns = new HashMap<>();

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
		Long endTime = cooldowns.get(material);
		if (endTime == null || endTime <= System.currentTimeMillis()) {
			cooldowns.remove(material);
			return 0;
		}
		return (int) ((endTime - System.currentTimeMillis()) / 50);
	}

	@Override
	public Inventory getEnderChest() {
		return PokkitInventory.toBukkit(nukkit.getEnderChestInventory());
	}

    @Override
    public int getExpToLevel() {
        if (nukkit instanceof cn.nukkit.Player) {
            return cn.nukkit.Player.calculateRequireExperience(((cn.nukkit.Player) nukkit).getExperienceLevel());
        }
        return 7;
    }

    @Override
    public float getAttackCooldown() {
        return nukkit.namedTag.contains("AttackCooldownProgress")
                ? nukkit.namedTag.getFloat("AttackCooldownProgress") : 0f;
    }

	@Override
	public GameMode getGameMode() {
		if (nukkit instanceof cn.nukkit.Player) {
			return PokkitGameMode.toBukkit(((cn.nukkit.Player) nukkit).getGamemode());
		}
		return GameMode.SURVIVAL;
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
		if (nukkit instanceof cn.nukkit.Player) {
			java.util.Optional<cn.nukkit.inventory.Inventory> inventory = ((cn.nukkit.Player) nukkit).getTopWindow();
			if (inventory.isPresent()) {
				return new PokkitInventoryView(PokkitInventory.toBukkit(inventory.get()), this);
			}
		}
		return new PokkitInventoryView(getInventory(), this);
	}

	@Override
	public Entity getShoulderEntityLeft() {
		return null; // Not implemented in Nukkit
	}

	@Override
	public Entity getShoulderEntityRight() {
		return null; // Not implemented in Nukkit
	}

    public Location getBedSpawnLocation() {
        if (nukkit instanceof cn.nukkit.Player) {
            cn.nukkit.level.Position spawn = ((cn.nukkit.Player) nukkit).getSpawn();
            if (spawn != null) {
                return nl.rutgerkok.pokkit.PokkitLocation.toBukkit(spawn);
            }
        }
        return null;
    }

    public void setBedSpawnLocation(Location location) {
        setBedSpawnLocation(location, false);
    }

    public void setBedSpawnLocation(Location location, boolean force) {
        if (nukkit instanceof cn.nukkit.Player && location != null) {
            ((cn.nukkit.Player) nukkit).setSpawn(nl.rutgerkok.pokkit.PokkitLocation.toNukkit(location));
        }
    }

    public boolean sleep(Location location, boolean force) {
        if (nukkit instanceof cn.nukkit.Player) {
            nukkit.namedTag.putInt("SleepStartTick", nukkit.getServer().getTick());
            return ((cn.nukkit.Player) nukkit).sleepOn(nl.rutgerkok.pokkit.PokkitLocation.toNukkit(location));
        }
        return false;
    }

    public void wakeup(boolean setSpawnLocation) {
        if (nukkit instanceof cn.nukkit.Player) {
            if (setSpawnLocation) {
                cn.nukkit.math.Vector3 sleepingPos = ((cn.nukkit.Player) nukkit).getSleepingPos();
                if (sleepingPos != null) {
                    ((cn.nukkit.Player) nukkit).setSpawn(sleepingPos);
                }
            }
            nukkit.namedTag.remove("SleepStartTick");
            ((cn.nukkit.Player) nukkit).stopSleep();
        }
    }

    public Location getBedLocation() {
        if (nukkit instanceof cn.nukkit.Player) {
            cn.nukkit.math.Vector3 sleepingPos = ((cn.nukkit.Player) nukkit).getSleepingPos();
            if (sleepingPos != null) {
                return nl.rutgerkok.pokkit.PokkitLocation.toBukkit(
                        cn.nukkit.level.Position.fromObject(sleepingPos, nukkit.getLevel()));
            }
        }
        return null;
    }

	@Override
	public boolean hasCooldown(Material material) {
		Long endTime = cooldowns.get(material);
		if (endTime == null || endTime <= System.currentTimeMillis()) {
			cooldowns.remove(material);
			return false;
		}
		return true;
	}

	@Override
	public boolean isBlocking() {
		return nukkit.isBlocking();
	}

	@Override
	public boolean isHandRaised() {
		if (nukkit instanceof cn.nukkit.Player) {
			return ((cn.nukkit.Player) nukkit).isUsingItem();
		}
		return nukkit.isBlocking();
	}

	@Override
	public boolean isSleeping() {
		return nukkit.getDataFlag(cn.nukkit.entity.EntityHuman.DATA_PLAYER_FLAGS, cn.nukkit.entity.EntityHuman.DATA_PLAYER_FLAG_SLEEP);
	}

	@Override
	public int getSleepTicks() {
		if (!isSleeping()) return 0;
		if (nukkit.namedTag.contains("SleepStartTick")) {
			int elapsed = nukkit.getServer().getTick() - nukkit.namedTag.getInt("SleepStartTick");
			return Math.min(elapsed, 100);
		}
		return 100;
	}

    @Override
    public void attack(Entity entity) {
        cn.nukkit.entity.Entity nukkitTarget = PokkitEntity.toNukkit(entity);
        cn.nukkit.event.entity.EntityDamageByEntityEvent event = new cn.nukkit.event.entity.EntityDamageByEntityEvent(
                nukkit, nukkitTarget, cn.nukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1f);
        nukkitTarget.attack(event);
    }

	@Override
	public void swingMainHand() {
		super.swingMainHand();
	}

	@Override
	public void swingOffHand() {
		super.swingOffHand();
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean force) {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			cn.nukkit.level.Position pos = location != null
					? nl.rutgerkok.pokkit.PokkitLocation.toNukkit(location)
					: player.getPosition();
			player.craftingType = cn.nukkit.Player.CRAFTING_ENCHANT;
			player.getUIInventory().getCraftingGrid().sendContents(player);
			return new PokkitInventoryView(getInventory(), this);
		}
		return null;
	}

	@Override
	public InventoryView openInventory(Inventory inventory) {
		if (nukkit instanceof cn.nukkit.Player) {
			((cn.nukkit.Player) nukkit).addWindow(PokkitInventory.toNukkit(inventory));
		}
		return new PokkitInventoryView(inventory, this);
	}

    @Override
    public void openInventory(InventoryView inventory) {
        if (nukkit instanceof cn.nukkit.Player) {
            ((cn.nukkit.Player) nukkit).addWindow(PokkitInventory.toNukkit(inventory.getTopInventory()));
        }
    }

	@Override
	public InventoryView openMerchant(Merchant merchant, boolean force) {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			player.craftingType = cn.nukkit.Player.CRAFTING_SMALL;
		}
		return null;
	}

	@Override
	public InventoryView openMerchant(Villager trader, boolean force) {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			player.craftingType = cn.nukkit.Player.CRAFTING_SMALL;
		}
		return null;
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean force) {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			player.craftingType = cn.nukkit.Player.CRAFTING_BIG;
			player.getUIInventory().getCraftingGrid().sendContents(player);
			return new PokkitInventoryView(getInventory(), this);
		}
		return null;
	}

	@Override
	public void setCooldown(Material material, int cooldown) {
		if (cooldown <= 0) {
			cooldowns.remove(material);
		} else {
			cooldowns.put(material, System.currentTimeMillis() + cooldown * 50L);
		}
	}

	@Override
	public void setGameMode(GameMode mode) {
		if (nukkit instanceof cn.nukkit.Player) {
			((cn.nukkit.Player) nukkit).setGamemode(PokkitGameMode.toNukkit(mode));
		}
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
	}

	@Override
	public void setShoulderEntityRight(Entity entity) {
	}

	@Override
	public boolean setWindowProperty(Property prop, int value) {
		if (nukkit instanceof cn.nukkit.Player) {
			java.util.Optional<cn.nukkit.inventory.Inventory> topWindow = ((cn.nukkit.Player) nukkit).getTopWindow();
			if (topWindow.isPresent()) {
				cn.nukkit.level.Position holder = topWindow.get().getHolder() instanceof cn.nukkit.level.Position
						? (cn.nukkit.level.Position) topWindow.get().getHolder()
						: null;
				if (holder != null) {
					cn.nukkit.network.protocol.BlockEventPacket packet = new cn.nukkit.network.protocol.BlockEventPacket();
					packet.x = (int) holder.x;
					packet.y = (int) holder.y;
					packet.z = (int) holder.z;
					packet.case1 = prop.getId();
					packet.case2 = value;
					((cn.nukkit.Player) nukkit).dataPacket(packet);
					return true;
				}
			}
		}
		return false;
	}

	private Set<NamespacedKey> getDiscoveredRecipeSet() {
		Set<NamespacedKey> recipes = new HashSet<>();
		if (nukkit.namedTag.contains("DiscoveredRecipes")) {
			cn.nukkit.nbt.tag.ListTag<cn.nukkit.nbt.tag.StringTag> list = nukkit.namedTag.getList("DiscoveredRecipes", cn.nukkit.nbt.tag.StringTag.class);
			for (cn.nukkit.nbt.tag.Tag tag : list.getAll()) {
				String data = ((cn.nukkit.nbt.tag.StringTag) tag).data;
				String[] parts = data.split(":", 2);
				if (parts.length == 2) {
					recipes.add(new NamespacedKey(parts[0], parts[1]));
				}
			}
		}
		return recipes;
	}

	private void saveDiscoveredRecipeSet(Set<NamespacedKey> recipes) {
		cn.nukkit.nbt.tag.ListTag<cn.nukkit.nbt.tag.StringTag> list = new cn.nukkit.nbt.tag.ListTag<>();
		list.setAll(recipes.stream()
				.map(key -> new cn.nukkit.nbt.tag.StringTag(key.getNamespace() + ":" + key.getKey()))
				.collect(Collectors.toList()));
		nukkit.namedTag.putList("DiscoveredRecipes", list);
	}

	@Override
	public boolean discoverRecipe(NamespacedKey key) {
		Set<NamespacedKey> recipes = getDiscoveredRecipeSet();
		if (recipes.contains(key)) return false;
		recipes.add(key);
		saveDiscoveredRecipeSet(recipes);
		return true;
	}

	@Override
	public int discoverRecipes(Collection<NamespacedKey> keys) {
		Set<NamespacedKey> recipes = getDiscoveredRecipeSet();
		int count = 0;
		for (NamespacedKey key : keys) {
			if (recipes.add(key)) count++;
		}
		saveDiscoveredRecipeSet(recipes);
		return count;
	}

	@Override
	public boolean undiscoverRecipe(NamespacedKey key) {
		Set<NamespacedKey> recipes = getDiscoveredRecipeSet();
		if (!recipes.contains(key)) return false;
		recipes.remove(key);
		saveDiscoveredRecipeSet(recipes);
		return true;
	}

	@Override
	public int undiscoverRecipes(Collection<NamespacedKey> keys) {
		Set<NamespacedKey> recipes = getDiscoveredRecipeSet();
		int count = 0;
		for (NamespacedKey key : keys) {
			if (recipes.remove(key)) count++;
		}
		saveDiscoveredRecipeSet(recipes);
		return count;
	}

	@Override
	public void setLastDeathLocation(Location location) {
		if (location == null) {
			nukkit.namedTag.remove("LastDeathX");
			nukkit.namedTag.remove("LastDeathY");
			nukkit.namedTag.remove("LastDeathZ");
			nukkit.namedTag.remove("LastDeathLevel");
		} else {
			nukkit.namedTag.putInt("LastDeathX", location.getBlockX());
			nukkit.namedTag.putInt("LastDeathY", location.getBlockY());
			nukkit.namedTag.putInt("LastDeathZ", location.getBlockZ());
			nukkit.namedTag.putString("LastDeathLevel", location.getWorld().getName());
		}
	}

	@Override
	public Location getLastDeathLocation() {
		if (!nukkit.namedTag.contains("LastDeathX")) return null;
		org.bukkit.World world = org.bukkit.Bukkit.getWorld(nukkit.namedTag.getString("LastDeathLevel"));
		if (world == null) return null;
		return new Location(world,
				nukkit.namedTag.getInt("LastDeathX"),
				nukkit.namedTag.getInt("LastDeathY"),
				nukkit.namedTag.getInt("LastDeathZ"));
	}

	@Override
	public Firework fireworkBoost(org.bukkit.inventory.ItemStack firework) {
		return null;
	}

	@Override
	public void setStarvationRate(int rate) {
		nukkit.namedTag.putInt("StarvationRate", rate);
	}

	@Override
	public int getStarvationRate() {
		return nukkit.namedTag.contains("StarvationRate") ? nukkit.namedTag.getInt("StarvationRate") : 0;
	}

	@Override
	public int getEnchantmentSeed() {
		return nukkit.namedTag.contains("EnchantmentSeed") ? nukkit.namedTag.getInt("EnchantmentSeed") : 0;
	}

	@Override
	public void setEnchantmentSeed(int seed) {
		nukkit.namedTag.putInt("EnchantmentSeed", seed);
	}

    @Override
    public float getExhaustion() {
        if (nukkit instanceof cn.nukkit.Player) {
            return nukkit.namedTag.contains("BukkitExhaustion") ? nukkit.namedTag.getFloat("BukkitExhaustion") : 0f;
        }
        return 0;
    }

    @Override
    public void setExhaustion(float value) {
        if (nukkit instanceof cn.nukkit.Player) {
            nukkit.namedTag.putFloat("BukkitExhaustion", value);
        }
    }

	@Override
	public int getFoodLevel() {
		if (nukkit instanceof cn.nukkit.Player) {
			return ((cn.nukkit.Player) nukkit).getFoodData().getLevel();
		}
		return 20;
	}

	@Override
	public void setFoodLevel(int value) {
		if (nukkit instanceof cn.nukkit.Player) {
			((cn.nukkit.Player) nukkit).getFoodData().setLevel(value);
		}
	}

	@Override
	public float getSaturation() {
		if (nukkit instanceof cn.nukkit.Player) {
			return ((cn.nukkit.Player) nukkit).getFoodData().getFoodSaturationLevel();
		}
		return 0;
	}

	@Override
	public void setSaturation(float value) {
		if (nukkit instanceof cn.nukkit.Player) {
			((cn.nukkit.Player) nukkit).getFoodData().setFoodSaturationLevel(value);
		}
	}

	@Override
	public boolean hasDiscoveredRecipe(NamespacedKey key) {
		return getDiscoveredRecipeSet().contains(key);
	}

	@Override
	public Set<NamespacedKey> getDiscoveredRecipes() {
		return getDiscoveredRecipeSet();
	}

    @Override
    public boolean dropItem(boolean dropAll) {
        if (nukkit instanceof cn.nukkit.Player) {
            cn.nukkit.item.Item item = nukkit.getInventory().getItemInHand();
            if (item == null || item.isNull()) return false;
            int count = dropAll ? item.getCount() : 1;
            cn.nukkit.item.Item drop = item.clone();
            drop.setCount(count);
            if (((cn.nukkit.Player) nukkit).dropItem(drop)) {
                item.setCount(item.getCount() - count);
                if (item.getCount() <= 0) {
                    nukkit.getInventory().setItemInHand(cn.nukkit.item.Item.get(cn.nukkit.item.Item.AIR));
                }
                return true;
            }
        }
        return false;
    }

	@Override
	public int getSaturatedRegenRate() {
		return nukkit.namedTag.contains("SaturatedRegenRate") ? nukkit.namedTag.getInt("SaturatedRegenRate") : 0;
	}

	@Override
	public void setSaturatedRegenRate(int rate) {
		nukkit.namedTag.putInt("SaturatedRegenRate", rate);
	}

	@Override
	public int getUnsaturatedRegenRate() {
		return nukkit.namedTag.contains("UnsaturatedRegenRate") ? nukkit.namedTag.getInt("UnsaturatedRegenRate") : 0;
	}

	@Override
	public void setUnsaturatedRegenRate(int rate) {
		nukkit.namedTag.putInt("UnsaturatedRegenRate", rate);
	}
}
