package nl.rutgerkok.pokkit.entity;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.world.PokkitWorld;

import cn.nukkit.math.SimpleAxisAlignedBB;

abstract class PokkitFakeEntity implements Entity {

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return null;
	}

	@Override
	public boolean addPassenger(Entity passenger) {
		return false;
	}

	@Override
	public boolean addScoreboardTag(String tag) {
		return false;
	}

	@Override
	public boolean eject() {
		return false;
	}

	@Override
	public String getCustomName() {
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return Collections.emptySet();
	}

	@Override
	public int getEntityId() {
		return -1;
	}

	@Override
	public float getFallDistance() {
		return 0;
	}

	@Override
	public int getFireTicks() {
		return 0;
	}

	@Override
	public double getHeight() {
		return 0;
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return null;
	}

	@Override
	public Location getLocation() {
		return PokkitLocation.toBukkit(getNukkitLocation());
	}

	@Override
	public Location getLocation(Location loc) {
		return PokkitLocation.toBukkit(getNukkitLocation(), loc);
	}

	@Override
	public int getMaxFireTicks() {
		return 0;
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		String customName = this.getCustomName();
		if (customName != null) {
			return customName;
		}
		return this.getType().toString();
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		cn.nukkit.level.Location location = this.getNukkitLocation();
		cn.nukkit.entity.Entity[] found = location.level.getNearbyEntities(new SimpleAxisAlignedBB(
				location.x - x, location.y - y, location.z - z,
				location.x + x, location.y + y, location.z + z));

		return Arrays.stream(found).map(PokkitEntity::toBukkit).collect(Collectors.toList());
	}

	abstract cn.nukkit.level.Location getNukkitLocation();

	@Override
	public Entity getPassenger() {
		return null;
	}

	@Override
	public List<Entity> getPassengers() {
		return Collections.emptyList();
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		return PistonMoveReaction.MOVE;
	}

	@Override
	public int getPortalCooldown() {
		return 0;
	}

	@Override
	public Set<String> getScoreboardTags() {
		return Collections.emptySet();
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public int getTicksLived() {
		return 0;
	}

	@Override
	public UUID getUniqueId() {
		return UUID.randomUUID();
	}

	@Override
	public Entity getVehicle() {
		return null;
	}

	@Override
	public Vector getVelocity() {
		return new Vector(0, 0, 0);
	}

	@Override
	public double getWidth() {
		return 0;
	}

	@Override
	public World getWorld() {
		return PokkitWorld.toBukkit(getNukkitLocation().level);
	}

	@Override
	public boolean hasGravity() {
		return false;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return false;
	}

	@Override
	public boolean hasPermission(String name) {
		return false;
	}

	@Override
	public boolean isCustomNameVisible() {
		return false;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isGlowing() {
		return false;
	}

	@Override
	public boolean isInsideVehicle() {
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		return false;
	}

	@Override
	public boolean isOnGround() {
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return false;
	}

	@Override
	public boolean isPermissionSet(String name) {
		return false;
	}

	@Override
	public boolean isPersistent() {
		return false;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

	@Override
	public boolean isValid() {
		return false; // Fake entities are not valid for most purposes
	}

	@Override
	public boolean leaveVehicle() {
		return false;
	}

	@Override
	public void playEffect(EntityEffect type) {
	}

	@Override
	public void recalculatePermissions() {
	}

	@Override
	public void remove() {
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
	}

	@Override
	public boolean removePassenger(Entity passenger) {
		return false;
	}

	@Override
	public boolean removeScoreboardTag(String tag) {
		return false;
	}

	@Override
	public void sendMessage(String message) {
	}

	@Override
	public void sendMessage(String[] messages) {
	}

	@Override
	public void setCustomName(String name) {
	}

	@Override
	public void setCustomNameVisible(boolean flag) {
	}

	@Override
	public void setFallDistance(float distance) {
	}

	@Override
	public void setFireTicks(int ticks) {
	}

	@Override
	public void setGlowing(boolean flag) {
	}

	@Override
	public void setGravity(boolean gravity) {
	}

	@Override
	public void setInvulnerable(boolean flag) {
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
	}

	@Override
	public void setOp(boolean value) {
		//entities can't be op
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		return false;
	}

	@Override
	public void setPersistent(boolean persistent) {
	}

	@Override
	public void setPortalCooldown(int cooldown) {
	}

	@Override
	public void setSilent(boolean flag) {
	}

	@Override
	public void setTicksLived(int value) {
	}

	@Override
	public void setVelocity(Vector velocity) {
	}

	@Override
	public Spigot spigot() {
		return new Spigot();
	}

	@Override
	public boolean teleport(Entity destination) {
		return false;
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return false;
	}

	@Override
	public boolean teleport(Location location) {
		return false;
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return false;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void setRotation(float v, float v1) {
	}

	@Override
	public org.bukkit.block.BlockFace getFacing() {
		return org.bukkit.block.BlockFace.SOUTH;
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
	public Entity copy() {
		return null;
	}

	@Override
	public Entity copy(Location location) {
		return null;
	}

	@Override
	public EntitySnapshot createSnapshot() {
		return null;
	}

	@Override
	public String getAsString() {
		return "PokkitFakeEntity{type=" + getType() + "}";
	}

	@Override
	public SpawnCategory getSpawnCategory() {
		return SpawnCategory.MISC;
	}

	@Override
	public int getFreezeTicks() {
		return 0;
	}

	@Override
	public int getMaxFreezeTicks() {
		return 140;
	}

	@Override
	public boolean isFrozen() {
		return false;
	}

	@Override
	public void setFreezeTicks(int ticks) {
	}

	@Override
	public Sound getSwimSound() {
		return Sound.ENTITY_GENERIC_SWIM;
	}

	@Override
	public Sound getSwimSplashSound() {
		return Sound.ENTITY_GENERIC_SPLASH;
	}

	@Override
	public Sound getSwimHighSpeedSplashSound() {
		return Sound.ENTITY_GENERIC_SPLASH;
	}

	@Override
	public boolean isVisualFire() {
		return false;
	}

	@Override
	public void setVisualFire(boolean fire) {
	}

	@Override
	public boolean isInWorld() {
		return false;
	}

	@Override
	public boolean isInWater() {
		return false;
	}

	@Override
	public boolean isVisibleByDefault() {
		return true;
	}

	@Override
	public void setVisibleByDefault(boolean visible) {
	}

	@Override
	public Set<Player> getTrackedBy() {
		return Collections.emptySet();
	}

	@Override
	public void sendMessage(UUID sender, String message) {
		sendMessage(message);
	}

	@Override
	public void sendMessage(UUID sender, String... messages) {
		sendMessage(messages);
	}
}
