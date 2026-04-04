package nl.rutgerkok.pokkit.player;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.*;
import nl.rutgerkok.pokkit.inventory.PokkitEntityEquipment;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.world.PokkitWorld;
import java.util.Objects;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_99_R9.CraftServer;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.PokkitGameMode;
import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.PokkitSound;
import nl.rutgerkok.pokkit.PokkitVector;
import nl.rutgerkok.pokkit.UniqueIdConversion;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.entity.PokkitHumanEntity;
import nl.rutgerkok.pokkit.inventory.PokkitInventory;
import nl.rutgerkok.pokkit.inventory.PokkitInventoryView;
import nl.rutgerkok.pokkit.metadata.PlayerMetadataStore;
import nl.rutgerkok.pokkit.particle.PokkitParticle;
import nl.rutgerkok.pokkit.permission.PokkitPermission;
import nl.rutgerkok.pokkit.permission.PokkitPermissionAttachment;
import nl.rutgerkok.pokkit.permission.PokkitPermissionAttachmentInfo;
import nl.rutgerkok.pokkit.plugin.PokkitPlugin;
import nl.rutgerkok.pokkit.potion.PokkitPotionEffect;

import cn.nukkit.AdventureSettings;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

@DelegateDeserialization(PokkitOfflinePlayer.class)
public class PokkitPlayer extends PokkitHumanEntity implements Player {

	public static final int ITEM_SLOT_NOT_INITIALIZED = -999;
	public static PokkitPlayer toBukkit(cn.nukkit.Player nukkit) {
		if (nukkit == null) {
			return null;
		}
		return ((CraftServer) Bukkit.getServer()).getOnlinePlayerData().getPlayer(nukkit);
	}

	public static cn.nukkit.Player toNukkit(Player player) {
		if (player == null) {
			return null;
		}
		return ((PokkitPlayer) player).nukkit;
	}

	private final Player.Spigot spigot;
	private final cn.nukkit.Player nukkit;
	private Scoreboard scoreboard;
	private InetSocketAddress address;
	private boolean sleepingIgnored;
	public int lastItemSlot = ITEM_SLOT_NOT_INITIALIZED;
	private double healthScale = 20;
	private boolean healthScaled = false;
	private GameMode previousGameMode = null;
	private int expCooldown = 0;
	private String playerListHeader = null;
	private String playerListFooter = null;
	private long playerTimeOffset = 0;
	private boolean playerTimeRelative = true;
	private Entity spectatorTarget = null;

	/**
	 * All plugin classes that currently request the player to be hidden. If
	 * this set is empty, the player can be shown again.
	 *
	 * @see #hidePlayer(Plugin, Player)
	 * @see #showPlayer(Plugin, Player)
	 */
	private final Set<Class<?>> hidingRequests = new HashSet<>();

	public PokkitPlayer(cn.nukkit.Player player) {
		super(player);
		this.nukkit = Objects.requireNonNull(player);
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		PokkitPlayer instance = this;
		this.spigot = new Player.Spigot() {
			@Override
			public Set<Player> getHiddenPlayers() {
				Set<Player> hiddenPlayers = new HashSet<>();
				for (Player player : getServer().getOnlinePlayers()) {
					if (!canSee(player)) {
						hiddenPlayers.add(player);
					}
				}
				return hiddenPlayers;
			}

			@Override
			public InetSocketAddress getRawAddress() {
				return InetSocketAddress.createUnresolved(nukkit.getAddress(), nukkit.getPort());
			}

            @Override
            public void respawn() {
                if (!nukkit.isAlive()) {
                    nukkit.setHealth(nukkit.getMaxHealth());
                }
            }

			@Override
			public void sendMessage(BaseComponent component) {
				instance.sendMessage(component.toLegacyText());
			}

			@Override
			public void sendMessage(BaseComponent... components) {
				StringBuilder text = new StringBuilder();
				for (BaseComponent component : components) {
					text.append(component.toLegacyText());
				}
				instance.sendMessage(text.toString());
			}

			@Override
			public void sendMessage(ChatMessageType position, BaseComponent component) {
				switch (position) {
					case CHAT:
					case SYSTEM:
						sendMessage(component);
						break;
					case ACTION_BAR:
					default:
						nukkit.sendActionBar(component.toLegacyText());
						break;
				}
			}

			@Override
			public void sendMessage(ChatMessageType position, BaseComponent... components) {
				switch (position) {
					case CHAT:
					case SYSTEM:
						sendMessage(components);
						break;
					case ACTION_BAR:
						StringBuilder text = new StringBuilder();
						for (BaseComponent component : components) {
							text.append(component.toLegacyText());
						}
						nukkit.sendActionBar(text.toString());
						break;
				}
			}
		};
	}

	@Override
	public void abandonConversation(Conversation arg0) {
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
	}

	@Override
	public void acceptConversationInput(String arg0) {
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return PokkitPermissionAttachment.toBukkit(nukkit.addAttachment(PokkitPlugin.toNukkit(plugin)));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		cn.nukkit.plugin.Plugin nukkitPlugin = PokkitPlugin.toNukkit(plugin);
		cn.nukkit.permission.PermissionAttachment nukkitAttachment = nukkit.addAttachment(nukkitPlugin);
		nukkit.getServer().getScheduler().scheduleDelayedTask(nukkitPlugin, () -> {
			nukkit.removeAttachment(nukkitAttachment);
		}, ticks);

		return PokkitPermissionAttachment.toBukkit(nukkitAttachment);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return PokkitPermissionAttachment.toBukkit(nukkit.addAttachment(PokkitPlugin.toNukkit(plugin), name, value));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return PokkitPermissionAttachment.toBukkit(nukkit.addAttachment(PokkitPlugin.toNukkit(plugin), name, value));
	}

	@Override
	public boolean addPotionEffect(PotionEffect bukkitEffect) {
		return addPotionEffect(bukkitEffect, false);
	}

	@Override
	public boolean addPotionEffect(PotionEffect bukkitEffect, boolean force) {
		nukkit.addEffect(PokkitPotionEffect.toNukkit(bukkitEffect));
		return true;
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> bukkitEffects) {
		for (PotionEffect bukkitEffect : bukkitEffects) {
			nukkit.addEffect(PokkitPotionEffect.toNukkit(bukkitEffect));
		}
		return true;
	}

	/*public void removePotionEffect(PotionEffect bukkitEffect) {
		nukkit.removeEffect(PokkitPotionEffect.toNukkit(bukkitEffect).getId());
	}*/

	@Override
	public boolean beginConversation(Conversation conversation) {
		return true;
	}

	@Override
	public boolean canSee(Player other) {
		return nukkit.canSee(PokkitPlayer.toNukkit(other));
	}

	@Override
	public void chat(String message) {
		 String msg = nukkit.getRemoveFormat() ? TextFormat.clean(message) : message;
		 PlayerChatEvent chatEvent = new PlayerChatEvent(toNukkit(this), msg);
		nukkit.getServer().getPluginManager().callEvent(chatEvent);
		if (!chatEvent.isCancelled()) {
			nukkit.getServer().broadcastMessage(
					nukkit.getServer().getLanguage().translateString(chatEvent.getFormat(),
							new String[] { chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage() }),
					chatEvent.getRecipients());
		}
	}

	@Override
	public void closeInventory() {
		nukkit.removeAllWindows();
	}

    @Override
    public void decrementStatistic(Statistic stat) throws IllegalArgumentException {
        setStatistic(stat, getStatistic(stat) - 1);
    }

    @Override
    public void decrementStatistic(Statistic stat, EntityType entityType) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        nukkit.namedTag.putInt(key, nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) - 1 : -1);
    }

    @Override
    public void decrementStatistic(Statistic stat, EntityType entityType, int amount) {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) - amount);
    }

    @Override
    public void decrementStatistic(Statistic stat, int amount) throws IllegalArgumentException {
        setStatistic(stat, getStatistic(stat) - amount);
    }

    @Override
    public void decrementStatistic(Statistic stat, Material material) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        nukkit.namedTag.putInt(key, nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) - 1 : -1);
    }

    @Override
    public void decrementStatistic(Statistic stat, Material material, int amount) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) - amount);
    }

	@Override
	public boolean eject() {
		return super.eject();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokkitPlayer other = (PokkitPlayer) obj;
		if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public InetSocketAddress getAddress() {
		InetSocketAddress address = this.address;
		if (address == null || address.getAddress() == null) {
			address = new InetSocketAddress(nukkit.getAddress(), nukkit.getPort());
			this.address = address;
		}
		return address;
	}

	@Override
	public AdvancementProgress getAdvancementProgress(Advancement advancement) {
		return null;
	}

	@Override
	public int getClientViewDistance() {
		return nukkit.getViewDistance();
	}

	@Override
	public boolean getAllowFlight() {
		return nukkit.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT);
	}

	@Override
	public AttributeInstance getAttribute(Attribute arg0) {
		return super.getAttribute(arg0);
	}

	@Override
	public Location getBedSpawnLocation() {
		return PokkitLocation.toBukkit(nukkit.getSpawn());
	}

	@Override
	public boolean getCanPickupItems() {
		if (nukkit.namedTag.contains("CanPickupItems")) {
			return nukkit.namedTag.getBoolean("CanPickupItems");
		}
		return isValid();
	}

	@Override
	public Location getCompassTarget() {
		// Revise if Nukkit adds custom compass locations
		return PokkitLocation.toBukkit(nukkit.getLevel().getSpawnLocation());
	}

	@Override
	public String getCustomName() {
		return nukkit.getNameTag();
	}

	@Override
	public String getDisplayName() {
		return nukkit.getDisplayName();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return nukkit.getEffectivePermissions().values().stream().map(PokkitPermissionAttachmentInfo::toBukkit)
				.collect(Collectors.toSet());
	}

	@Override
	public int getEntityId() {
		return (int) nukkit.getId();
	}

	@Override
	public EntityEquipment getEquipment() {
		return new PokkitEntityEquipment(nukkit.getInventory());
	}

    @Override
    public float getExhaustion() {
        return nukkit.namedTag.contains("BukkitExhaustion") ? nukkit.namedTag.getFloat("BukkitExhaustion") : 0f;
    }

	@Override
	public float getExp() {
		return nukkit.getExperience();
	}

	@Override
	public double getEyeHeight() {
		return this.getEyeHeight(false);
	}

	@Override
	public double getEyeHeight(boolean ignoreSneaking) {
		if (ignoreSneaking) {
			return 1.62;
		}
		if (this.isSneaking()) {
			return 1.54;
		}
		return 1.62;
	}

	@Override
	public Location getEyeLocation() {
		final Location loc = this.getLocation();
		loc.setY(loc.getY() + this.getEyeHeight());
		return loc;
	}

	@Override
	public long getFirstPlayed() {
		Long firstPlayed = nukkit.getFirstPlayed();
		if (firstPlayed == null) {
			return 0;
		}
		return firstPlayed;
	}

    @Override
    public float getFlySpeed() {
        return nukkit.getFlySpeed();
    }

	@Override
	public int getFoodLevel() {
		return nukkit.getFoodData().getLevel();

	}

	@Override
	public GameMode getGameMode() {
		return PokkitGameMode.toBukkit(nukkit.getGamemode());
	}

	@Override
	public double getHealth() {
		return nukkit.getHealth();
	}

	@Override
	public double getHealthScale() {
		return healthScale;
	}

	@Override
	public long getLastPlayed() {
		Long lastPlayed = nukkit.getLastPlayed();
		if (lastPlayed == null) {
			return 0;
		}
		return lastPlayed;
	}

	@Override
	public int getLevel() {
		return nukkit.getExperienceLevel();
	}

    @Override
    public Set<String> getListeningPluginChannels() {
        return Bukkit.getMessenger().getIncomingChannels();
    }

	@Override
	public String getLocale() {
		return nukkit.getLoginChainData().getLanguageCode();
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return getMetadataStore().getMetadata(this, metadataKey);
	}

	private PlayerMetadataStore getMetadataStore() {
		return ((CraftServer) Bukkit.getServer()).getMetadata().getPlayerMetadata();
	}

	@Override
	public Player getPlayer() {
		return this;
	}

	@Override
	public String getPlayerListFooter() {
		return playerListFooter;
	}

	@Override
	public String getPlayerListHeader() {
		return playerListHeader;
	}

	@Override
	public String getPlayerListName() {
		return nukkit.getDisplayName(); // In Nukkit, if you change the player's display name, it also changes in the player list, so...
	}

	@Override
	public long getPlayerTime() {
		if (playerTimeRelative) {
			return nukkit.getLevel().getTime() + playerTimeOffset;
		}
		return playerTimeOffset;
	}

	@Override
	public long getPlayerTimeOffset() {
		return playerTimeOffset;
	}

	@Override
	public WeatherType getPlayerWeather() {
		return nukkit.getLevel().isRaining() ? WeatherType.DOWNFALL : WeatherType.CLEAR; // Always same as on the server
	}

	@Override
	public int getPortalCooldown() {
		return nukkit.namedTag.contains("PortalCooldown") ? nukkit.namedTag.getInt("PortalCooldown") : 80;
	}

	@Override
	public float getSaturation() {
		return nukkit.getFoodData().getFoodSaturationLevel();
	}

	@Override
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

    @Override
    public int getSleepTicks() {
        if (!nukkit.isSleeping()) return 0;
        if (nukkit.namedTag.contains("SleepStartTick")) {
            int elapsed = nukkit.getServer().getTick() - nukkit.namedTag.getInt("SleepStartTick");
            return Math.min(elapsed, 100);
        }
        return 100;
    }

	@Override
	public Entity getSpectatorTarget() {
		if (!nukkit.isSpectator()) return null;
		return spectatorTarget;
	}

    @Override
    public int getStatistic(Statistic stat) throws IllegalArgumentException {
        switch (stat) {
        case PLAY_ONE_MINUTE:
            if (nukkit.namedTag.contains("Stat_PLAY_ONE_MINUTE")) {
                return nukkit.namedTag.getInt("Stat_PLAY_ONE_MINUTE");
            }
            long first = nukkit.getFirstPlayed();
            long diffMillis = System.currentTimeMillis() - first;
            return (int) (diffMillis / 1000) * 20;
        default:
            String key = "Stat_" + stat.name();
            return nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0;
        }
    }

    @Override
    public int getStatistic(Statistic stat, EntityType entityType) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        return nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0;
    }

    @Override
    public int getStatistic(Statistic stat, Material material) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        return nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0;
    }

	@Override
	public int getTotalExperience() {
		int level = nukkit.getExperienceLevel();
		if (level <= 16) {
			return nukkit.getExperience() + (int) (Math.pow(level, 2) + 6 * level);
		} else if (level <= 31) {
			return nukkit.getExperience() + (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
		} else {
			return nukkit.getExperience() + (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public UUID getUniqueId() {
		return UniqueIdConversion.playerNameToId(getName());
	}

	@Override
	public float getWalkSpeed() {
		return nukkit.getMovementSpeed();
	}

	@Override
	public void giveExp(int exp) {
		nukkit.addExperience(exp);
	}

	@Override
	public void giveExpLevels(int levels) {
		nukkit.setExperience(nukkit.getExperience(), nukkit.getExperienceLevel() + levels);
	}

	@Override
	public boolean hasAI() {
		return true; // I think it is a bit difficult a player to not have AI...
	}

	@Override
	public boolean hasGravity() {
		return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_GRAVITY);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return getMetadataStore().hasMetadata(this, metadataKey);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return nukkit.hasPermission(PokkitPermission.toNukkit(permission));
	}

	@Override
	public boolean hasPermission(String permission) {
		return nukkit.hasPermission(permission);
	}

	@Override
	public boolean hasPlayedBefore() {
		return nukkit.hasPlayedBefore();
	}

	private void hidePlayer(Class<?> responsible, Player player) {
		this.hidingRequests.add(responsible);
		nukkit.hidePlayer(PokkitPlayer.toNukkit(player));
	}

	@Override
	@Deprecated
	public void hidePlayer(Player player) {
		// No information available on which plugin requested the hide, so just use our own class as a stand-in
		hidePlayer(PokkitPlayer.class, player);
	}

	@Override
	public void hidePlayer(Plugin plugin, Player player) {
		hidePlayer(plugin.getClass(), player);
	}

    @Override
    public void incrementStatistic(Statistic stat) throws IllegalArgumentException {
        setStatistic(stat, getStatistic(stat) + 1);
    }

    @Override
    public void incrementStatistic(Statistic stat, EntityType entityType) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) + 1);
    }

    @Override
    public void incrementStatistic(Statistic stat, EntityType entityType, int amount) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) + amount);
    }

    @Override
    public void incrementStatistic(Statistic stat, int amount) throws IllegalArgumentException {
        setStatistic(stat, getStatistic(stat) + amount);
    }

    @Override
    public void incrementStatistic(Statistic stat, Material material) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) + 1);
    }

    @Override
    public void incrementStatistic(Statistic stat, Material material, int amount) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        nukkit.namedTag.putInt(key, (nukkit.namedTag.contains(key) ? nukkit.namedTag.getInt(key) : 0) + amount);
    }

	@Override
	public boolean isBanned() {
		return nukkit.isBanned();
	}

	@Override
	public boolean isConversing() {
		return false;
	}

	@Override
	public boolean isFlying() {
		return nukkit.getAdventureSettings().get(AdventureSettings.Type.FLYING);
	}

	@Override
	public boolean isHandRaised() {
		return nukkit.isUsingItem();
	}

	@Override
	public boolean isHealthScaled() {
		return healthScaled;
	}

	@Override
	public boolean isOnline() {
		return nukkit.isOnline();
	}

	@Override
	public boolean isOp() {
		return nukkit.isOp();
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return nukkit.isPermissionSet(PokkitPermission.toNukkit(permission));
	}

	@Override
	public boolean isPermissionSet(String permission) {
		try {
			return nukkit.isPermissionSet(permission);
		} catch (NullPointerException e) { // WorldGuard workaround
			return false;
		}
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return playerTimeRelative;
	}

	@Override
	public boolean isSleeping() {
		return nukkit.isSleeping();
	}

	@Override
	public boolean isSleepingIgnored() {
		return sleepingIgnored;
	}

	@Override
	public boolean isSneaking() {
		return nukkit.isSneaking();
	}

	@Override
	public boolean isSprinting() {
		return nukkit.isSprinting();
	}

	@Override
	public boolean isValid() {
		return isOnline() && !isDead();
	}

	@Override
	public boolean isWhitelisted() {
		return nukkit.isWhitelisted();
	}

	@Override
	public boolean isAllowingServerListings() {
		return false;
	}

	@Override
	public void kickPlayer(String reason) {
		nukkit.kick(reason);
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean force) {
		cn.nukkit.level.Position pos = location != null
				? PokkitLocation.toNukkit(location)
				: nukkit.getPosition();
		nukkit.craftingType = cn.nukkit.Player.CRAFTING_ENCHANT;
		nukkit.getUIInventory().getCraftingGrid().sendContents(nukkit);
		return new PokkitInventoryView(getInventory(), this);
	}

	@Override
	public InventoryView openInventory(Inventory inventory) {
		nukkit.addWindow(PokkitInventory.toNukkit(inventory));
		return new PokkitInventoryView(inventory, this);
	}

	@Override
	public void openInventory(InventoryView inventoryView) {
		openInventory(inventoryView.getTopInventory());
	}

	@Override
	public InventoryView openMerchant(Villager trader, boolean force) {
		nukkit.craftingType = cn.nukkit.Player.TRADE_WINDOW_ID;
		return null;
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean force) {
		nukkit.craftingType = cn.nukkit.Player.CRAFTING_BIG;
		nukkit.getUIInventory().getCraftingGrid().sendContents(nukkit);
		return new PokkitInventoryView(getInventory(), this);
	}

	@Override
	public boolean performCommand(String arg0) {
		return nukkit.getServer().dispatchCommand(nukkit, arg0);
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		getWorld().playEffect(arg0, arg1, arg2);
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
		getWorld().playEffect(arg0, arg1, arg2);
	}

	@Override
	public void playNote(Location arg0, byte arg1, byte arg2) {
		playNote(arg0, Instrument.getByType(arg1), new Note(arg2));
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		cn.nukkit.level.Sound sound;
		switch (arg1.getType()) {
			case 0:
				sound = cn.nukkit.level.Sound.NOTE_HARP;
				break;
			case 1:
				sound = cn.nukkit.level.Sound.NOTE_BD;
				break;
			case 2:
				sound = cn.nukkit.level.Sound.NOTE_SNARE;
				break;
			case 3:
				sound = cn.nukkit.level.Sound.NOTE_HAT;
				break;
			case 4:
				sound = cn.nukkit.level.Sound.NOTE_BASS;
				break;
			case 5:
				sound = cn.nukkit.level.Sound.NOTE_FLUTE;
				break;
			case 6:
				sound = cn.nukkit.level.Sound.NOTE_BELL;
				break;
			case 7:
				sound = cn.nukkit.level.Sound.NOTE_GUITAR;
				break;
			case 8:
				sound = cn.nukkit.level.Sound.NOTE_CHIME;
				break;
			case 9:
				sound = cn.nukkit.level.Sound.NOTE_XYLOPHONE;
				break;
			default:
				sound = null;
		}
		float note;
		switch (arg2.getId()) {
			case 0: note = 0.5f; break;
			case 1: note = 0.529732f; break;
			case 2: note = 0.561231f; break;
			case 3: note = 0.594604f; break;
			case 4: note = 0.629961f; break;
			case 5: note = 0.667420f; break;
			case 6: note = 0.707107f; break;
			case 7: note = 0.749154f; break;
			case 8: note = 0.793701f; break;
			case 9: note = 0.840896f; break;
			case 10: note = 0.890899f; break;
			case 11: note = 0.943874f; break;
			case 12: note = 1.0f; break;
			case 13: note = 1.059463f; break;
			case 14: note = 1.122462f; break;
			case 15: note = 1.189207f; break;
			case 16: note = 1.259921f; break;
			case 17: note = 1.334840f; break;
			case 18: note = 1.414214f; break;
			case 19: note = 1.498307f; break;
			case 20: note = 1.587401f; break;
			case 21: note = 1.681793f; break;
			case 22: note = 1.781797f; break;
			case 23: note = 1.887749f; break;
			case 24: note = 2.0f; break;
			default: note = 0f;
		}
		PlaySoundPacket soundPk = new PlaySoundPacket();
		soundPk.name = sound.getSound();
		soundPk.volume = 1f;
		soundPk.pitch = note;
		soundPk.x = arg0.getBlockX();
		soundPk.y = arg0.getBlockY();
		soundPk.z = arg0.getBlockZ();
		nukkit.dataPacket(soundPk);
	}

	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch) {
		playSound(location, sound, SoundCategory.NEUTRAL, volume, pitch);
	}

	@Override
	public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
		if (location == null || sound == null) {
			return;
		}

		cn.nukkit.level.Sound nukkitSound = PokkitSound.toNukkit(location, sound, pitch);
		if (nukkitSound == null) {
			return;
		}

		if (pitch < 0) pitch = 0;
		if (volume < 0) volume = 0;
		if (volume > 1) volume = 1;

		Vector3 pos = PokkitVector.toNukkit(location.toVector());
		nukkit.level.addSound(pos, nukkitSound, volume, pitch);
	}

	@Override
	public void playSound(Location location, String soundString, float volume, float pitch) {
		playSound(location, soundString, SoundCategory.NEUTRAL, volume, pitch);
	}

	@Override
	public void playSound(Location location, String soundString, SoundCategory category, float volume, float pitch) {
		if (location == null || soundString == null) {
			return;
		}
		try {
			Sound sound = Sound.valueOf(soundString.replace("minecraft:", "").toUpperCase());
			playSound(location, sound, volume, pitch);
		} catch (IllegalArgumentException e) {
			// Ignore non-existing sound
		}
	}

	@Override
	public void recalculatePermissions() {
		nukkit.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		nukkit.removeAttachment(PokkitPermissionAttachment.toNukkit(attachment));
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		getMetadataStore().removeMetadata(this, metadataKey, owningPlugin);
	}

	@Override
	public void resetPlayerTime() {
		playerTimeOffset = 0;
		playerTimeRelative = true;
		nukkit.level.sendTime(nukkit);
	}

	@Override
	public void resetPlayerWeather() {
		nukkit.level.sendWeather(nukkit);
	}

	@Override
	public void resetTitle() {
		nukkit.clearTitle();
	}

	@Override
	public void saveData() {
		nukkit.save();
	}

	@Override
	public void loadData() {
	}

	@Override
	public void sendBlockChange(Location location, BlockData block) {
		PokkitBlockData materialData = (PokkitBlockData) block;

		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		int nukkitBlockId = materialData.getNukkitId();
		int nukkitBlockData = materialData.getNukkitData();
		int flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;

		UpdateBlockPacket packet = new UpdateBlockPacket();
		packet.x = x;
		packet.y = y;
		packet.z = z;
		packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(nukkit.getGameVersion(), nukkitBlockId, nukkitBlockData);
		packet.flags = flags;
		nukkit.dataPacket(packet, false);
	}

	@Override
	public void sendBlockChange(Location location, Material material, byte blockData) {
		this.sendBlockChange(location, PokkitBlockData.createBlockData(material, blockData));
	}

	@Override
	public void sendMap(MapView arg0) {
	}

	@Override
	public void sendMessage(String message) {
		nukkit.sendMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		for (String message : messages) {
			nukkit.sendMessage(message);
		}
	}

	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		if (!(nukkit instanceof cn.nukkit.Player)) return;
		cn.nukkit.network.protocol.ScriptCustomEventPacket pk = new cn.nukkit.network.protocol.ScriptCustomEventPacket();
		pk.eventName = channel;
		pk.eventData = message;
		((cn.nukkit.Player) nukkit).dataPacket(pk);
	}

	@Override
	public void sendRawMessage(String message) {
		nukkit.sendMessage(message);
	}

    @Override
    public void sendSignChange(Location arg0, String[] arg1) throws IllegalArgumentException {
        sendSignChange(arg0, arg1, DyeColor.BLACK);
    }

    @Override
    public void sendSignChange(Location location, String[] lines, DyeColor dyeColor) throws IllegalArgumentException {
        sendSignChange(location, lines, dyeColor, false);
    }

	@Override
	public void sendTitle(String title, String subtitle) {
		sendTitle(title, subtitle, 10, 70, 20); // Copied from CraftBukkit
	}

	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		if (title != null) {
			nukkit.sendTitle(title);
		}
		if (subtitle != null) {
			nukkit.setSubtitle(subtitle);
		}
		nukkit.setTitleAnimationTimes(fadeIn, stay, fadeOut);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("name", getName());
		data.put("uuid", getUniqueId().toString());
		return data;
	}

	@Override
	public void setAllowFlight(boolean value) {
		nukkit.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, value);
	}

	@Override
	public void setBedSpawnLocation(Location arg0) {
		this.nukkit.setSpawn(new Vector3(arg0.getX(), arg0.getY(), arg0.getZ()));
	}

	@Override
	public void setBedSpawnLocation(Location arg0, boolean arg1) {
		this.nukkit.setSpawn(new Vector3(arg0.getX(), arg0.getY(), arg0.getZ()));
	}

	@Override
	public boolean sleep(Location location, boolean b) {
		nukkit.namedTag.putInt("SleepStartTick", nukkit.getServer().getTick());
		return nukkit.sleepOn(PokkitLocation.toNukkit(location));
	}

	@Override
	public void wakeup(boolean b) {
		nukkit.namedTag.remove("SleepStartTick");
		nukkit.stopSleep();

		if (b) {
			nukkit.setSpawn(nukkit);
		}
	}

	@Override
	public Location getBedLocation() {
		if (!nukkit.isSleeping()) {
			throw new IllegalStateException("Cannot use getBedLocation() while player is not sleeping");
		} else {
			return PokkitLocation.toBukkit(nukkit);
		}
	}

	@Override
	public void setCompassTarget(Location arg0) {
		// this may not be the best idea
		SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
		pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
		Position spawn = PokkitWorld.toNukkit(arg0.getWorld()).getSpawnLocation();
		pk.x = spawn.getFloorX();
		pk.y = spawn.getFloorY();
		pk.z = spawn.getFloorZ();
		nukkit.dataPacket(pk);
	}

	@Override
	public void setCustomName(String customName) {
		nukkit.setNameTag(customName);
	}

	@Override
	public void setCustomNameVisible(boolean customNameVisible) {
		nukkit.setNameTagVisible(customNameVisible);
	}

	@Override
	public void setDisplayName(String displayName) {
		nukkit.setDisplayName(displayName);
	}

    @Override
    public void setExhaustion(float arg0) {
        nukkit.namedTag.putFloat("BukkitExhaustion", arg0);
    }

	@Override
	public void setExp(float exp) {
		if (nukkit.spawned) {
			nukkit.setAttribute(cn.nukkit.entity.Attribute.getAttribute(cn.nukkit.entity.Attribute.EXPERIENCE).setValue(exp));
		}
	}

	@Override
	public void setFallDistance(float fallDistance) {
		nukkit.fallDistance = fallDistance;
	}

	@Override
	public void setFireTicks(int fireTicks) {
		nukkit.fireTicks = fireTicks;
	}

	@Override
	public void setFlying(boolean flying) {
		nukkit.getAdventureSettings().set(AdventureSettings.Type.FLYING, flying);
		nukkit.getAdventureSettings().update();
	}

    @Override
    public void setFlySpeed(float arg0) throws IllegalArgumentException {
        nukkit.setFlySpeed(arg0);
    }

	@Override
	public void setFoodLevel(int arg0) {
		nukkit.getFoodData().setLevel(arg0);
	}

	@Override
	public void setGameMode(GameMode gamemode) {
		this.previousGameMode = getGameMode();
		nukkit.setGamemode(PokkitGameMode.toNukkit(gamemode));
	}

	@Override
	public void setHealthScale(double arg0) throws IllegalArgumentException {
		this.healthScale = arg0;
		this.healthScaled = true;
	}

	@Override
	public void setHealthScaled(boolean arg0) {
		this.healthScaled = arg0;
	}

	@Override
	public void setItemOnCursor(ItemStack arg0) {
		nukkit.getCursorInventory().setItem(0, PokkitItemStack.toNukkitCopy(arg0));
	}

	@Override
	public void setLevel(int level) {
		nukkit.setExperience(nukkit.getExperience(), level);
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		getMetadataStore().setMetadata(this, metadataKey, newMetadataValue);
	}

	@Override
	public void setNoDamageTicks(int arg0) {
		nukkit.noDamageTicks = arg0;
	}

	@Override
	public void setOp(boolean value) {
		nukkit.setOp(value);
	}

	@Override
	public void setPlayerListFooter(String footer) {
		this.playerListFooter = footer;
		sendPlayerListHeaderFooterPacket();
	}

	@Override
	public void setPlayerListHeader(String header) {
		this.playerListHeader = header;
		sendPlayerListHeaderFooterPacket();
	}

	@Override
	public void setPlayerListHeaderFooter(String header, String footer) {
		this.playerListHeader = header;
		this.playerListFooter = footer;
		sendPlayerListHeaderFooterPacket();
	}

	private void sendPlayerListHeaderFooterPacket() {
		if (playerListHeader != null) {
			SetTitlePacket pk = new SetTitlePacket();
			pk.type = SetTitlePacket.TYPE_SUBTITLE;
			pk.text = playerListHeader;
			pk.fadeInTime = 0;
			pk.stayTime = 0x7fffffff;
			pk.fadeOutTime = 0;
			nukkit.dataPacket(pk);
		}
		if (playerListFooter != null) {
			SetTitlePacket pk = new SetTitlePacket();
			pk.type = SetTitlePacket.TYPE_ACTION_BAR;
			pk.text = playerListFooter;
			pk.fadeInTime = 0;
			pk.stayTime = 0x7fffffff;
			pk.fadeOutTime = 0;
			nukkit.dataPacket(pk);
		}
	}

    @Override
    public void setPlayerListName(String arg0) {
        nukkit.setDisplayName(arg0 != null ? arg0 : nukkit.getName());
    }

	@Override
	public void setPlayerTime(long time, boolean relative) {
		playerTimeOffset = time;
		playerTimeRelative = relative;
		SetTimePacket pk = new SetTimePacket();
		pk.time = (int) getPlayerTime();
		nukkit.dataPacket(pk);
	}

	@Override
	public void setPlayerWeather(WeatherType type) {
		LevelEventPacket pk = new LevelEventPacket();
		if (type == WeatherType.DOWNFALL) {
			pk.evid = LevelEventPacket.EVENT_START_RAIN;
		} else {
			pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
		}
		nukkit.dataPacket(pk);
	}

    @Override
    public void setResourcePack(String url) {
        setResourcePack(url, null);
    }

    @Override
    public void setResourcePack(String url, byte[] hash) {
        cn.nukkit.resourcepacks.ResourcePack[] packs = nukkit.getServer().getResourcePackManager().getResourceStack();
        ResourcePackStackPacket pk = new ResourcePackStackPacket();
        pk.mustAccept = nukkit.getServer().forceResources;
        pk.resourcePackStack = packs;
        pk.behaviourPackStack = new cn.nukkit.resourcepacks.ResourcePack[0];
        nukkit.dataPacket(pk);
    }

	@Override
	public void setSaturation(float arg0) {
		nukkit.getFoodData().setFoodSaturationLevel(arg0);
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
		Objects.requireNonNull(scoreboard);
		this.scoreboard = scoreboard;
	}

	@Override
	public void setSleepingIgnored(boolean sleepingIgnored) {
		this.sleepingIgnored = sleepingIgnored;
	}

	@Override
	public void setSneaking(boolean value) {
		nukkit.setSneaking(value);
	}

	@Override
	public void setSpectatorTarget(Entity entity) {
		if (entity == null) {
			spectatorTarget = null;
			return;
		}
		if (!nukkit.isSpectator()) return;
		spectatorTarget = entity;
		cn.nukkit.entity.Entity nukkitEntity = nl.rutgerkok.pokkit.entity.PokkitEntity.toNukkit(entity);
		CameraPacket pk = new CameraPacket();
		pk.cameraUniqueId = nukkitEntity.getId();
		pk.playerUniqueId = nukkit.getId();
		nukkit.dataPacket(pk);
	}

	@Override
	public void setSprinting(boolean value) {
		nukkit.setSprinting(value);
	}

    @Override
    public void setStatistic(Statistic stat, EntityType entityType, int value) {
        String key = "Stat_" + stat.name() + "_" + entityType.name();
        nukkit.namedTag.putInt(key, value);
    }

    @Override
    public void setStatistic(Statistic stat, int value) throws IllegalArgumentException {
        nukkit.namedTag.putInt("Stat_" + stat.name(), value);
    }

    @Override
    public void setStatistic(Statistic stat, Material material, int value) throws IllegalArgumentException {
        String key = "Stat_" + stat.name() + "_" + material.name();
        nukkit.namedTag.putInt(key, value);
    }

	@Override
	public void setTexturePack(String arg0) {
	}

	@Override
	public void setTotalExperience(int arg0) {
		nukkit.setExperience(0, 0);
		nukkit.addExperience(arg0);
	}

	@Override
	public void sendExperienceChange(float progress) {
		nukkit.sendExperience((int) (progress * nukkit.calculateRequireExperience(nukkit.getExperienceLevel())));
	}

	@Override
	public void sendExperienceChange(float progress, int level) {
		//nukkit.sendExperience(progress);
		nukkit.sendExperienceLevel(level);
	}

	@Override
	public void setWalkSpeed(float arg0) throws IllegalArgumentException {
		nukkit.setMovementSpeed(arg0);
	}

	@Override
	public void setWhitelisted(boolean value) {
		nukkit.setWhitelisted(value);
	}

	@Override
	public void showDemoScreen() {
	}

	private void showPlayer(Class<?> clazz, Player player) {
		this.hidingRequests.remove(clazz);
		if (this.hidingRequests.isEmpty()) {
			nukkit.showPlayer(toNukkit(player));
		}
	}

	@Override
	@Deprecated
	public void showPlayer(Player player) {
		// No information available on which plugin was responsible, so just use our own class as a stand-in
		showPlayer(PokkitPlayer.class, player);
	}

	@Override
	public void showPlayer(Plugin plugin, Player player) {
		showPlayer(plugin.getClass(), player);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count) {
		spawnParticle(particle, x, y, z, count, 0, 0 , 0);

	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY,
			double offsetZ) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 0);

	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY,
			double offsetZ, double extra) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data) {
		int id = PokkitParticle.toNukkit(particle);

		SplittableRandom random = new SplittableRandom();

		int index = 0;
		while (count > index) {
			double realOffsetX;
			double realOffsetY;
			double realOffsetZ;
			if (offsetX != 0) {
				realOffsetX = offsetX / 2;
				x = x + random.nextDouble(-realOffsetX, realOffsetX);
			}
			if (offsetY != 0) {
				realOffsetY = offsetY / 2;
				y = y + random.nextDouble(-realOffsetY, realOffsetY);
			}
			if (offsetZ != 0) {
				realOffsetZ = offsetZ / 2;
				z = z + random.nextDouble(-realOffsetZ, realOffsetZ);
			}

			GenericParticle nukkitParticle = new GenericParticle(new Vector3(x, y, z), id);

			nukkit.getLevel().addParticle(nukkitParticle, nukkit);
			index++;
		}
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, T data) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 0, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
		spawnParticle(particle, x, y, z, count, 0, 0, 0, 0, data);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ,
			double extra) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ,
			double extra, T data) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ,
			T data) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
	}

	@Override
	public Player.Spigot spigot() {
		return spigot;
	}

	@Override
	public void stopSound(Sound sound) {
		StopSoundPacket pk = new StopSoundPacket();
		pk.name = sound.name().toLowerCase();
		pk.stopAll = false;
		nukkit.dataPacket(pk);
	}

	@Override
	public void stopSound(Sound sound, SoundCategory category) {
		stopSound(sound);
	}

	@Override
	public void stopSound(String sound) {
		StopSoundPacket pk = new StopSoundPacket();
		pk.name = sound;
		pk.stopAll = false;
		nukkit.dataPacket(pk);
	}

	@Override
	public void stopSound(String sound, SoundCategory category) {
		stopSound(sound);
	}

	@Override
	public void updateCommands() {
		nukkit.sendCommandData();
	}

	@Override
	public void openBook(ItemStack itemStack) {
	}

	@Override
	public void openSign(org.bukkit.block.Sign sign, org.bukkit.block.sign.Side side) {
		openSign(sign);
	}

	@Override
	public void openSign(org.bukkit.block.Sign sign) {
		org.bukkit.Location loc = sign.getLocation();
		cn.nukkit.level.Position pos = PokkitLocation.toNukkit(loc);
		cn.nukkit.blockentity.BlockEntity blockEntity = pos.getLevel().getBlockEntity(
				new cn.nukkit.math.BlockVector3(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()));
		if (blockEntity instanceof cn.nukkit.blockentity.BlockEntitySign) {
			cn.nukkit.blockentity.BlockEntitySign nukkitSign = (cn.nukkit.blockentity.BlockEntitySign) blockEntity;
			nukkitSign.setEditorEntityRuntimeId(nukkit.getId());
			cn.nukkit.network.protocol.BlockEntityDataPacket packet = new cn.nukkit.network.protocol.BlockEntityDataPacket();
			packet.x = pos.getFloorX();
			packet.y = pos.getFloorY();
			packet.z = pos.getFloorZ();
			try {
				packet.namedTag = cn.nukkit.nbt.NBTIO.writeNetwork(nukkitSign.getSpawnCompound());
			} catch (java.io.IOException e) {
				return;
			}
			nukkit.dataPacket(packet);
		}
	}

	@Override
	public int getPing() {
		return nukkit.getPing();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data, boolean force) {
		spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX,
			double offsetY, double offsetZ, double extra, T data, boolean force) {
		spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
	}

	@Override
	public void sendHealthUpdate() {
		SetHealthPacket pk = new SetHealthPacket();
		pk.health = (int) nukkit.getHealth();
		nukkit.dataPacket(pk);
	}

	@Override
	public void sendHealthUpdate(double health, int food, float saturation) {
		SetHealthPacket pk = new SetHealthPacket();
		pk.health = (int) health;
		nukkit.dataPacket(pk);
	}

	@Override
	public void updateInventory() {
		nukkit.getInventory().sendContents(nukkit);
	}

	@Override
	public InventoryView getOpenInventory() {
		Optional<cn.nukkit.inventory.Inventory> inventory = nukkit.getTopWindow();
		if (inventory.isPresent()) {
			return new PokkitInventoryView(PokkitInventory.toBukkit(inventory.get()), this);
		}
		return new PokkitInventoryView(PokkitInventory.toBukkit(nukkit.getCraftingGrid()), this);
	}

    @Override
    public boolean canSee(org.bukkit.entity.Entity entity) {
        if (entity instanceof Player) {
            return canSee((Player) entity);
        }
        return true;
    }

    @Override
    public boolean isOnGround() { return nukkit.isOnGround(); }

    @Override
    public boolean isTransferred() { return false; }

    @Override
    public org.bukkit.GameMode getPreviousGameMode() { return previousGameMode; }

    @Override
    public void setWorldBorder(org.bukkit.WorldBorder border) { }

    @Override
    public org.bukkit.WorldBorder getWorldBorder() { return null; }

    @Override
    public int getExpCooldown() { return expCooldown; }

    @Override
    public void setExpCooldown(int ticks) { this.expCooldown = ticks; }

    @Override
    public org.bukkit.Location getRespawnLocation() { return getBedSpawnLocation(); }

    @Override
    public void setRespawnLocation(org.bukkit.Location location) {
        setRespawnLocation(location, false);
    }

    @Override
    public void setRespawnLocation(org.bukkit.Location location, boolean force) {
        if (location != null) {
            nukkit.setSpawn(PokkitLocation.toNukkit(location));
        }
    }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, org.bukkit.Sound sound, float volume, float pitch) { playSound(entity.getLocation(), sound, volume, pitch); }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, java.lang.String sound, float volume, float pitch) { playSound(entity.getLocation(), sound, volume, pitch); }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, org.bukkit.Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) { playSound(entity.getLocation(), sound, category, volume, pitch); }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, java.lang.String sound, org.bukkit.SoundCategory category, float volume, float pitch) { playSound(entity.getLocation(), sound, category, volume, pitch); }

    @Override
    public void playSound(org.bukkit.Location location, org.bukkit.Sound sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) { playSound(location, sound, category, volume, pitch); }

    @Override
    public void playSound(org.bukkit.Location location, java.lang.String sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) { playSound(location, sound, category, volume, pitch); }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, org.bukkit.Sound sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) { playSound(entity, sound, category, volume, pitch); }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, java.lang.String sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) { playSound(entity, sound, category, volume, pitch); }

    @Override
    public void stopAllSounds() {
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = "";
        pk.stopAll = true;
        nukkit.dataPacket(pk);
    }

    @Override
    public void stopSound(org.bukkit.SoundCategory category) {
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = "";
        pk.stopAll = true;
        nukkit.dataPacket(pk);
    }

    @Override
    public boolean breakBlock(org.bukkit.block.Block block) {
        org.bukkit.Location loc = block.getLocation();
        cn.nukkit.level.Position pos = PokkitLocation.toNukkit(loc);
        cn.nukkit.block.Block nukkitBlock = pos.getLevel().getBlock(
                pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        cn.nukkit.event.block.BlockBreakEvent event = new cn.nukkit.event.block.BlockBreakEvent(
                nukkit, nukkitBlock, nukkit.getInventory().getItemInHand(), new cn.nukkit.item.Item[0], true, true);
        nukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        nukkitBlock.getLevel().useBreakOn(pos, null, nukkit, true);
        return true;
    }

    @Override
    public void sendBlockDamage(org.bukkit.Location location, float progress) {
        sendBlockDamagePacket(location, progress);
    }

    @Override
    public void sendBlockDamage(org.bukkit.Location location, float progress, org.bukkit.entity.Entity source) {
        sendBlockDamagePacket(location, progress);
    }

    @Override
    public void sendBlockDamage(org.bukkit.Location location, float progress, int id) {
        sendBlockDamagePacket(location, progress);
    }

    private void sendBlockDamagePacket(org.bukkit.Location location, float progress) {
        LevelEventPacket pk = new LevelEventPacket();
        if (progress <= 0) {
            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
            pk.x = (float) location.getBlockX();
            pk.y = (float) location.getBlockY();
            pk.z = (float) location.getBlockZ();
            pk.data = 0;
        } else {
            pk.evid = LevelEventPacket.EVENT_BLOCK_UPDATE_BREAK;
            pk.x = (float) location.getBlockX();
            pk.y = (float) location.getBlockY();
            pk.z = (float) location.getBlockZ();
            pk.data = (int) (65535 * Math.min(1.0f, Math.max(0.0f, progress)));
        }
        nukkit.dataPacket(pk);
    }

    @Override
    public void sendBlockChanges(java.util.Collection<org.bukkit.block.BlockState> blockStates) {
        sendBlockChanges(blockStates, false);
    }

    @Override
    public void sendBlockChanges(java.util.Collection<org.bukkit.block.BlockState> blockStates, boolean suppressLightUpdates) {
        for (org.bukkit.block.BlockState state : blockStates) {
            org.bukkit.Location loc = state.getLocation();
            org.bukkit.Material mat = state.getType();
            cn.nukkit.block.Block nukkitBlock = nl.rutgerkok.pokkit.blockdata.PokkitBlockData.toNukkit(state.getBlockData());
            UpdateBlockPacket pk = new UpdateBlockPacket();
            pk.x = loc.getBlockX();
            pk.y = loc.getBlockY();
            pk.z = loc.getBlockZ();
            pk.blockRuntimeId = cn.nukkit.level.GlobalBlockPalette.getOrCreateRuntimeId(nukkit.getGameVersion(), nukkitBlock.getId(), nukkitBlock.getDamage());
            pk.flags = UpdateBlockPacket.FLAG_ALL;
            pk.dataLayer = 0;
            nukkit.dataPacket(pk);
        }
    }

    @Override
    public void sendBlockUpdate(org.bukkit.Location location, org.bukkit.block.TileState tileState) {
        try {
            cn.nukkit.nbt.tag.CompoundTag nbt = new cn.nukkit.nbt.tag.CompoundTag();
            nbt.putString("id", tileState.getBlock().getType().name().toLowerCase());
            BlockEntityDataPacket pk = new BlockEntityDataPacket();
            pk.x = location.getBlockX();
            pk.y = location.getBlockY();
            pk.z = location.getBlockZ();
            pk.namedTag = cn.nukkit.nbt.NBTIO.writeNetwork(nbt);
            nukkit.dataPacket(pk);
        } catch (java.io.IOException e) {
        }
    }

    @Override
    public void sendEquipmentChange(org.bukkit.entity.LivingEntity entity, org.bukkit.inventory.EquipmentSlot slot, org.bukkit.inventory.ItemStack item) {
        long eid = nl.rutgerkok.pokkit.entity.PokkitLivingEntity.toNukkit(entity).getId();
        cn.nukkit.item.Item nukkitItem = PokkitItemStack.toNukkitCopy(item);
        if (slot == org.bukkit.inventory.EquipmentSlot.HAND || slot == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
            MobEquipmentPacket pk = new MobEquipmentPacket();
            pk.eid = eid;
            pk.item = nukkitItem != null ? nukkitItem : cn.nukkit.item.Item.get(0);
            pk.inventorySlot = slot == org.bukkit.inventory.EquipmentSlot.HAND ? 0 : 1;
            pk.hotbarSlot = pk.inventorySlot;
            pk.windowId = 0;
            nukkit.dataPacket(pk);
        } else {
            MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
            pk.eid = eid;
            cn.nukkit.item.Item air = cn.nukkit.item.Item.get(0);
            pk.slots = new cn.nukkit.item.Item[4];
            pk.slots[0] = air;
            pk.slots[1] = air;
            pk.slots[2] = air;
            pk.slots[3] = air;
            pk.body = air;
            int idx = slot == org.bukkit.inventory.EquipmentSlot.FEET ? 0 :
                      slot == org.bukkit.inventory.EquipmentSlot.LEGS ? 1 :
                      slot == org.bukkit.inventory.EquipmentSlot.CHEST ? 2 : 3;
            pk.slots[idx] = nukkitItem != null ? nukkitItem : air;
            nukkit.dataPacket(pk);
        }
    }

    @Override
    public void sendEquipmentChange(org.bukkit.entity.LivingEntity entity, java.util.Map<org.bukkit.inventory.EquipmentSlot, org.bukkit.inventory.ItemStack> items) {
        for (java.util.Map.Entry<org.bukkit.inventory.EquipmentSlot, org.bukkit.inventory.ItemStack> entry : items.entrySet()) {
            sendEquipmentChange(entity, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void sendHurtAnimation(float yaw) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = nukkit.getId();
        pk.event = EntityEventPacket.HURT_ANIMATION;
        cn.nukkit.Server.broadcastPacket(nukkit.hasSpawned.values(), pk);
    }

    @Override
    public void sendPotionEffectChange(org.bukkit.entity.LivingEntity entity, org.bukkit.potion.PotionEffect effect) {
        MobEffectPacket pk = new MobEffectPacket();
        pk.eid = nl.rutgerkok.pokkit.entity.PokkitLivingEntity.toNukkit(entity).getId();
        pk.eventId = MobEffectPacket.EVENT_ADD;
        pk.effectId = effect.getType().getId();
        pk.amplifier = effect.getAmplifier();
        pk.particles = effect.hasParticles();
        pk.duration = effect.getDuration();
        pk.ambient = effect.isAmbient();
        nukkit.dataPacket(pk);
    }

    @Override
    public void sendPotionEffectChangeRemove(org.bukkit.entity.LivingEntity entity, org.bukkit.potion.PotionEffectType type) {
        MobEffectPacket pk = new MobEffectPacket();
        pk.eid = nl.rutgerkok.pokkit.entity.PokkitLivingEntity.toNukkit(entity).getId();
        pk.eventId = MobEffectPacket.EVENT_REMOVE;
        pk.effectId = type.getId();
        pk.amplifier = 0;
        pk.particles = true;
        pk.duration = 0;
        pk.ambient = false;
        nukkit.dataPacket(pk);
    }

    @Override
    public void addCustomChatCompletions(java.util.Collection<java.lang.String> completions) { }

    @Override
    public void removeCustomChatCompletions(java.util.Collection<java.lang.String> completions) { }

    @Override
    public void setCustomChatCompletions(java.util.Collection<java.lang.String> completions) { }

    @Override
    public void showEntity(org.bukkit.plugin.Plugin plugin, org.bukkit.entity.Entity entity) {
        if (entity instanceof Player) {
            showPlayer(plugin, (Player) entity);
            return;
        }
        cn.nukkit.entity.Entity nukkitEntity = nl.rutgerkok.pokkit.entity.PokkitEntity.toNukkit(entity);
        nukkitEntity.spawnTo(nukkit);
    }

    @Override
    public void hideEntity(org.bukkit.plugin.Plugin plugin, org.bukkit.entity.Entity entity) {
        if (entity instanceof Player) {
            hidePlayer(plugin, (Player) entity);
            return;
        }
        cn.nukkit.entity.Entity nukkitEntity = nl.rutgerkok.pokkit.entity.PokkitEntity.toNukkit(entity);
        nukkitEntity.despawnFrom(nukkit);
    }

    @Override
    public void addResourcePack(java.util.UUID id, java.lang.String url, byte[] hash, java.lang.String prompt, boolean force) {
        setResourcePack(url, hash);
    }

    @Override
    public void removeResourcePacks() {
        ResourcePackStackPacket pk = new ResourcePackStackPacket();
        pk.mustAccept = false;
        pk.resourcePackStack = new cn.nukkit.resourcepacks.ResourcePack[0];
        pk.behaviourPackStack = new cn.nukkit.resourcepacks.ResourcePack[0];
        nukkit.dataPacket(pk);
    }

    @Override
    public void removeResourcePack(java.util.UUID id) {
        removeResourcePacks();
    }

    @Override
    public void storeCookie(org.bukkit.NamespacedKey key, byte[] value) { }

    @Override
    public java.util.concurrent.CompletableFuture<byte[]> retrieveCookie(org.bukkit.NamespacedKey key) {
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    @Override
    public org.bukkit.profile.PlayerProfile getPlayerProfile() {
        return Bukkit.createPlayerProfile(nukkit.getUniqueId(), nukkit.getName());
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.util.Date expires, java.lang.String source) {
        org.bukkit.BanEntry entry = org.bukkit.Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(getName(), reason, expires, source);
        if (isOnline()) kickPlayer(reason);
        return entry;
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.time.Instant expires, java.lang.String source) {
        java.util.Date date = expires != null ? java.util.Date.from(expires) : null;
        return ban(reason, date, source);
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.time.Duration duration, java.lang.String source) {
        java.util.Date date = duration != null ? new java.util.Date(System.currentTimeMillis() + duration.toMillis()) : null;
        return ban(reason, date, source);
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.util.Date expires, java.lang.String source, boolean kickIfOnline) {
        org.bukkit.BanEntry entry = org.bukkit.Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(getName(), reason, expires, source);
        if (kickIfOnline && isOnline()) kickPlayer(reason);
        return entry;
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.time.Instant expires, java.lang.String source, boolean kickIfOnline) {
        java.util.Date date = expires != null ? java.util.Date.from(expires) : null;
        return ban(reason, date, source, kickIfOnline);
    }

    @Override
    public org.bukkit.BanEntry ban(java.lang.String reason, java.time.Duration duration, java.lang.String source, boolean kickIfOnline) {
        java.util.Date date = duration != null ? new java.util.Date(System.currentTimeMillis() + duration.toMillis()) : null;
        return ban(reason, date, source, kickIfOnline);
    }

    @Override
    public org.bukkit.BanEntry banIp(java.lang.String address, java.util.Date expires, java.lang.String source, boolean kickIfOnline) {
        org.bukkit.BanEntry entry = org.bukkit.Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(address, null, expires, source);
        if (kickIfOnline && isOnline()) kickPlayer("Banned");
        return entry;
    }

    @Override
    public org.bukkit.BanEntry banIp(java.lang.String address, java.time.Instant expires, java.lang.String source, boolean kickIfOnline) {
        java.util.Date date = expires != null ? java.util.Date.from(expires) : null;
        return banIp(address, date, source, kickIfOnline);
    }

    @Override
    public org.bukkit.BanEntry banIp(java.lang.String address, java.time.Duration duration, java.lang.String source, boolean kickIfOnline) {
        java.util.Date date = duration != null ? new java.util.Date(System.currentTimeMillis() + duration.toMillis()) : null;
        return banIp(address, date, source, kickIfOnline);
    }

    @Override
    public void sendSignChange(Location location, String[] lines, DyeColor dyeColor, boolean hasGlowingText) throws IllegalArgumentException {
        if (lines == null) {
            lines = new String[4];
        }
        if (lines.length < 4) {
            throw new IllegalArgumentException("Lines must have at least 4 entries");
        }
        try {
            cn.nukkit.nbt.tag.CompoundTag nbt = new cn.nukkit.nbt.tag.CompoundTag()
                    .putString("id", "Sign")
                    .putString("Text1", lines[0] != null ? lines[0] : "")
                    .putString("Text2", lines[1] != null ? lines[1] : "")
                    .putString("Text3", lines[2] != null ? lines[2] : "")
                    .putString("Text4", lines[3] != null ? lines[3] : "")
                    .putInt("x", location.getBlockX())
                    .putInt("y", location.getBlockY())
                    .putInt("z", location.getBlockZ());
            cn.nukkit.network.protocol.BlockEntityDataPacket pk = new cn.nukkit.network.protocol.BlockEntityDataPacket();
            pk.x = location.getBlockX();
            pk.y = location.getBlockY();
            pk.z = location.getBlockZ();
            pk.namedTag = cn.nukkit.nbt.NBTIO.writeNetwork(nbt);
            nukkit.dataPacket(pk);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setResourcePack(String url, byte[] hash, String prompt) {
        setResourcePack(url, hash);
    }

    @Override
    public void setResourcePack(String url, byte[] hash, boolean force) {
        setResourcePack(url, hash);
    }

    @Override
    public void setResourcePack(String url, byte[] hash, String prompt, boolean force) {
        setResourcePack(url, hash);
    }

    @Override
    public void setResourcePack(java.util.UUID id, String url, byte[] hash, String prompt, boolean force) {
        setResourcePack(url, hash);
    }

    @Override
    public void sendRawMessage(java.util.UUID uuid, java.lang.String s) { sendRawMessage(s); }

    @Override
    public void transfer(java.lang.String address, int port) { nukkit.transfer(address, port); }

    @Override
    public void sendLinks(org.bukkit.ServerLinks links) {
        throw Pokkit.unsupported();
    }
}
