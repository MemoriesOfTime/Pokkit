package nl.rutgerkok.pokkit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightning;
import com.google.common.base.Strings;

import nl.rutgerkok.pokkit.world.PokkitBlockFace;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.metadata.PokkitMetadataValue;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import nl.rutgerkok.pokkit.player.PokkitTeleportCause;
import nl.rutgerkok.pokkit.world.PokkitWorld;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.RemoveEntityPacket;

public class PokkitEntity implements Entity {
    public static Entity toBukkit(cn.nukkit.entity.Entity nukkit) {
        if (nukkit == null) {
            return null;
        }

        if (nukkit instanceof cn.nukkit.entity.EntityLiving) {
            // Return more specific type
            return PokkitLivingEntity.toBukkit((cn.nukkit.entity.EntityLiving) nukkit);
        }
        return new PokkitEntity(nukkit);
    }

    public static cn.nukkit.entity.Entity toNukkit(Entity entity) {
        if (entity == null) {
            return null;
        }
        return ((PokkitEntity) entity).nukkit;
    }

    private final cn.nukkit.entity.Entity nukkit;

    PokkitEntity(cn.nukkit.entity.Entity nukkitEntity) {
        this.nukkit = nukkitEntity;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw Pokkit.unsupported();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        throw Pokkit.unsupported();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        throw Pokkit.unsupported();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        throw Pokkit.unsupported();
    }

    @Override
	public boolean addPassenger(Entity passenger) {
        return nukkit.mountEntity(PokkitEntity.toNukkit(passenger));
	}

    @Override
    public boolean addScoreboardTag(String tag) {
        throw Pokkit.unsupported();
    }

    @Override
    public boolean eject() {
        boolean hadPassengers = false;
        ArrayList<cn.nukkit.entity.Entity> passengers = new ArrayList<>(nukkit.passengers);
        for (cn.nukkit.entity.Entity entity : passengers) {
            if (nukkit.dismountEntity(entity)) {
                hadPassengers = true;
            }
        }
        return hadPassengers;
    }

    @Override
    public String getCustomName() {
        return Strings.emptyToNull(nukkit.getNameTag());
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<>();
    }

    @Override
    public int getEntityId() {
        return (int) nukkit.getId();
    }

    @Override
    public float getFallDistance() {
        return nukkit.fallDistance;
    }

    @Override
    public int getFireTicks() {
        return nukkit.fireTicks;
    }

    @Override
	public double getHeight() {
		return nukkit.getHeight();
	}

    @Override
    public EntityDamageEvent getLastDamageCause() {
        cn.nukkit.event.entity.EntityDamageEvent nukkitEvent = nukkit.getLastDamageCause();
        EntityDamageEvent bukkitEvent = new EntityDamageEvent(PokkitEntity.toBukkit(nukkitEvent.getEntity()),
                PokkitDamageCause.toBukkit(nukkitEvent.getCause()), nukkitEvent.getDamage());
        return bukkitEvent;
    }

    @Override
    public Location getLocation() {
        return PokkitLocation.toBukkit(nukkit.getLocation());
    }

    @Override
    public Location getLocation(Location toOverwrite) {
        return PokkitLocation.toBukkit(nukkit.getLocation(), toOverwrite);
    }

    @Override
    public int getMaxFireTicks() {
        return 32767;
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        List<MetadataValue> bukkitList = new ArrayList<>();
        if (nukkit.getMetadata(metadataKey) != null) {
            nukkit.getMetadata(metadataKey).forEach((value) -> bukkitList.add(PokkitMetadataValue.toBukkit(value)));
        }
        return bukkitList;
    }

    @Override
    public String getName() {
        return nukkit.getName();
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        throw Pokkit.unsupported();
    }

    @Override
    public Entity getPassenger() {
        return toBukkit(nukkit.riding);
    }

    @Override
	public List<Entity> getPassengers() {
        return nukkit.passengers.stream().map(PokkitEntity::toBukkit).collect(Collectors.toList());
	}

    @Override
	public PistonMoveReaction getPistonMoveReaction() {
		return PistonMoveReaction.MOVE;
	}

    @Override
    public int getPortalCooldown() {
        return 80;
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
        return nukkit.ticksLived;
    }

    @Override
    public EntityType getType() {
        switch (nukkit.getNetworkId()) {
            case EntityBat.NETWORK_ID:
                return EntityType.BAT;
            case EntityChicken.NETWORK_ID:
                return EntityType.CHICKEN;
            case EntityCod.NETWORK_ID:
                return EntityType.COD;
            case EntityCow.NETWORK_ID:
                return EntityType.COW;
            case EntityDolphin.NETWORK_ID:
                return EntityType.DOLPHIN;
            case EntityDonkey.NETWORK_ID:
                return EntityType.DONKEY;
            case EntityHorse.NETWORK_ID:
                return EntityType.HORSE;
            case EntityLlama.NETWORK_ID:
                return EntityType.LLAMA;
            case EntityMooshroom.NETWORK_ID:
                return EntityType.MUSHROOM_COW;
            case EntityMule.NETWORK_ID:
                return EntityType.MULE;
            case EntityOcelot.NETWORK_ID:
                return EntityType.OCELOT;
            case EntityParrot.NETWORK_ID:
                return EntityType.PARROT;
            case EntityPig.NETWORK_ID:
                return EntityType.PIG;
            case EntityPolarBear.NETWORK_ID:
                return EntityType.POLAR_BEAR;
            case EntityPufferfish.NETWORK_ID:
                return EntityType.PUFFERFISH;
            case EntityRabbit.NETWORK_ID:
                return EntityType.RABBIT;
            case EntitySalmon.NETWORK_ID:
                return EntityType.SALMON;
            case EntitySheep.NETWORK_ID:
                return EntityType.SHEEP;
            case EntitySkeletonHorse.NETWORK_ID:
                return EntityType.SKELETON_HORSE;
            case EntitySquid.NETWORK_ID:
                return EntityType.SQUID;
            case EntityTropicalFish.NETWORK_ID:
                return EntityType.TROPICAL_FISH;
            case EntityTurtle.NETWORK_ID:
                return EntityType.TURTLE;
            case EntityVillager.NETWORK_ID:
            case EntityVillagerV1.NETWORK_ID:
                return EntityType.VILLAGER;
            case EntityWolf.NETWORK_ID:
                return EntityType.WOLF;
            case EntityBlaze.NETWORK_ID:
                return EntityType.BLAZE;
            case EntityCaveSpider.NETWORK_ID:
                return EntityType.CAVE_SPIDER;
            case EntityCreeper.NETWORK_ID:
                return EntityType.CREEPER;
            case EntityDrowned.NETWORK_ID:
                return EntityType.DROWNED;
            case EntityElderGuardian.NETWORK_ID:
                return EntityType.ELDER_GUARDIAN;
            case EntityEnderDragon.NETWORK_ID:
                return EntityType.ENDER_DRAGON;
            case EntityEnderman.NETWORK_ID:
                return EntityType.ENDERMAN;
            case EntityEndermite.NETWORK_ID:
                return EntityType.ENDERMITE;
            case EntityEvoker.NETWORK_ID:
                return EntityType.EVOKER;
            case EntityGhast.NETWORK_ID:
                return EntityType.GHAST;
            case EntityGuardian.NETWORK_ID:
                return EntityType.GUARDIAN;
            case EntityHusk.NETWORK_ID:
                return EntityType.HUSK;
            case EntityMagmaCube.NETWORK_ID:
                return EntityType.MAGMA_CUBE;
            case EntityPhantom.NETWORK_ID:
                return EntityType.PHANTOM;
            case EntityShulker.NETWORK_ID:
                return EntityType.SHULKER;
            case EntitySilverfish.NETWORK_ID:
                return EntityType.SILVERFISH;
            case EntitySkeleton.NETWORK_ID:
                return EntityType.SKELETON;
            case EntitySlime.NETWORK_ID:
                return EntityType.SLIME;
            case EntitySnowGolem.NETWORK_ID:
                return EntityType.SNOWMAN;
            case EntitySpider.NETWORK_ID:
                return EntityType.SPIDER;
            case EntityStray.NETWORK_ID:
                return EntityType.STRAY;
            case EntityVex.NETWORK_ID:
                return EntityType.VEX;
            case EntityVindicator.NETWORK_ID:
                return EntityType.VINDICATOR;
            case EntityWitch.NETWORK_ID:
                return EntityType.WITCH;
            case EntityWither.NETWORK_ID:
                return EntityType.WITHER;
            case EntityWitherSkeleton.NETWORK_ID:
                return EntityType.WITHER_SKELETON;
            case EntityZombie.NETWORK_ID:
                return EntityType.ZOMBIE;
            case EntityZombiePigman.NETWORK_ID:
                return EntityType.PIG_ZOMBIE;
            case EntityZombieVillager.NETWORK_ID:
            case EntityZombieVillagerV1.NETWORK_ID:
                return EntityType.ZOMBIE_VILLAGER;
            case 20:
                return EntityType.IRON_GOLEM;
            case 61:
                return EntityType.ARMOR_STAND;
            case 76:
                return EntityType.SHULKER_BULLET;
            case 79:
                return EntityType.DRAGON_FIREBALL;
            case 85:
                return EntityType.FIREBALL;
            case 88:
                return EntityType.LEASH_HITCH;
            case 89:
            case 91:
                return EntityType.WITHER_SKULL;
            case 94:
                return EntityType.SMALL_FIREBALL;
            case 100:
                return EntityType.MINECART_COMMAND;
            case 101:
                return EntityType.LINGERING_POTION;
            case 102:
                return EntityType.LLAMA_SPIT;
            case EntityLightning.NETWORK_ID:
                return EntityType.LIGHTNING;
            case EntityArrow.NETWORK_ID:
                return EntityType.ARROW;
            case EntityEgg.NETWORK_ID:
                return EntityType.EGG;
            case EntityEnderPearl.NETWORK_ID:
                return EntityType.ENDER_PEARL;
            case EntitySnowball.NETWORK_ID:
                return EntityType.SNOWBALL;
            case EntityThrownTrident.NETWORK_ID:
                return EntityType.TRIDENT;
            case EntityBoat.NETWORK_ID:
                return EntityType.BOAT;
            case EntityEndCrystal.NETWORK_ID:
                return EntityType.ENDER_CRYSTAL;
            case EntityFallingBlock.NETWORK_ID:
                return EntityType.FALLING_BLOCK;
            case EntityFirework.NETWORK_ID:
                return EntityType.FIREWORK;
            case EntityFishingHook.NETWORK_ID:
                return EntityType.FISHING_HOOK;
            case EntityItem.NETWORK_ID:
                return EntityType.DROPPED_ITEM;
            case EntityMinecartChest.NETWORK_ID:
                return EntityType.MINECART_CHEST;
            case EntityMinecartHopper.NETWORK_ID:
                return EntityType.MINECART_HOPPER;
            case EntityMinecartTNT.NETWORK_ID:
                return EntityType.MINECART_TNT;
            case EntityPainting.NETWORK_ID:
                return EntityType.PAINTING;
            case EntityPotion.NETWORK_ID:
                return EntityType.SPLASH_POTION;
            case EntityPrimedTNT.NETWORK_ID:
                return EntityType.PRIMED_TNT;
            case EntityXPOrb.NETWORK_ID:
                return EntityType.EXPERIENCE_ORB;
            default:
                return EntityType.UNKNOWN;
        }
    }

    @Override
    public UUID getUniqueId() {
        throw Pokkit.unsupported();
    }

    @Override
    public Entity getVehicle() {
        return toBukkit(nukkit.riding);
    }

    @Override
    public Vector getVelocity() {
        return new Vector(nukkit.motionX, nukkit.motionY, nukkit.motionZ);
    }

    @Override
	public double getWidth() {
		return nukkit.getWidth();
	}

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(nukkit.getBoundingBox().getMinX(), nukkit.getBoundingBox().getMinY(), nukkit.getBoundingBox().getMinZ(), nukkit.getBoundingBox().getMaxX(), nukkit.getBoundingBox().getMaxY(), nukkit.getBoundingBox().getMaxZ());
    }

    @Override
    public World getWorld() {
        return PokkitWorld.toBukkit(nukkit.getLevel());
    }

    @Override
    public void setRotation(float v, float v1) {
        nukkit.setRotation(v, v1);
    }

    @Override
    public boolean hasGravity() {
        return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_GRAVITY);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return nukkit.hasMetadata(metadataKey);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        switch (perm.getDefault()) {
        case FALSE:
            return false;
        case NOT_OP:
            return !isOp();
        case OP:
            return isOp();
        case TRUE:
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean hasPermission(String name) {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return nukkit.isNameTagVisible();
    }

    @Override
    public boolean isDead() {
        return !nukkit.isAlive();
    }

    @Override
    public boolean isEmpty() {
        return false; // No vehicle support yet
    }

    @Override
    public boolean isGlowing() {
        return false; // No glow support yet
    }

    @Override
    public boolean isInsideVehicle() {
        return nukkit.riding instanceof EntityVehicle;
    }

    @Override
    public boolean isInvulnerable() {
        return nukkit.invulnerable;
    }

    @Override
    public boolean isOnGround() {
        return nukkit.isOnGround();
    }

    @Override
    public boolean isOp() {
        return false; // Entities cannot be OP in Nukkit
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return false; // Permissions cannot be set for entities in Nukkit
    }

    @Override
    public boolean isPermissionSet(String name) {
        return false; // Permissions cannot be set for entities in Nukkit
    }

    @Override
	public boolean isPersistent() {
		return true; // By default all entities are persistent
	}

    @Override
    public boolean isSilent() {
        return false; // No silence support yet
    }

    @Override
    public boolean isValid() {
        return nukkit.isAlive() && nukkit.isValid();
    }

    @Override
    public boolean leaveVehicle() {
        if (nukkit.riding != null) {
            return nukkit.riding.dismountEntity(nukkit);
        }
        return false;
    }

    @Override
    public void playEffect(EntityEffect type) {
        // Not supported yet. As effects are usually unimportant, it's not worth
        // to crash a plugin over this, so let this method fail silently
    }

    @Override
    public void recalculatePermissions() {
        // No permission support for entities in Nukkit, so nothing to
        // recalculate.
    }

    @Override
    public void remove() {
    	RemoveEntityPacket pk = new RemoveEntityPacket();
    	pk.eid = nukkit.getId();
    	for (Player p : getServer().getOnlinePlayers())
    		PokkitPlayer.toNukkit(p).dataPacket(pk);
    	nukkit.chunk.removeEntity(nukkit);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        Pokkit.notImplemented();
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        Pokkit.notImplemented();
    }

    @Override
	public boolean removePassenger(Entity passenger) {
        return nukkit.dismountEntity(toNukkit(passenger));
	}

    @Override
    public boolean removeScoreboardTag(String tag) {
        throw Pokkit.unsupported();
    }

    @Override
    public void sendMessage(String message) {
        // Ignore, entities don't record messages in Nukkit
    }

    @Override
    public void sendMessage(String[] messages) {
        // Ignore, entities don't record messages in Nukkit
    }

    @Override
    public void setCustomName(String name) {
        nukkit.setNameTag(name);
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        nukkit.setNameTagVisible(visible);
    }

    @Override
    public void setFallDistance(float distance) {
        nukkit.fallDistance = distance;
    }

    @Override
    public void setFireTicks(int ticks) {
        nukkit.fireTicks = ticks;
    }

    @Override
    public void setGlowing(boolean flag) {
        // not supported in bedrock edition
    }

    @Override
    public void setGravity(boolean gravity) {
        nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_GRAVITY, gravity);
    }

    @Override
    public void setInvulnerable(boolean flag) {
        nukkit.invulnerable = flag;
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        nukkit.setLastDamageCause(new cn.nukkit.event.entity.EntityDamageEvent(nukkit, PokkitDamageCause.toNukkit(event.getCause()), (float) event.getDamage()));
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        Pokkit.notImplemented();
    }

    @Override
    public void setOp(boolean value) {
        //entities can't be op
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        return nukkit.mountEntity(toNukkit(passenger));
    }

    @Override
	public void setPersistent(boolean persistent) {
        Pokkit.notImplemented();
	}

    @Override
    public void setPortalCooldown(int cooldown) {
        //silently unsupported
    }

	@Override
    public void setSilent(boolean flag) {
        nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_SILENT, flag);
    }

	@Override
    public void setTicksLived(int value) {
        nukkit.ticksLived = value;
    }

	@Override
    public void setVelocity(Vector velocity) {
        nukkit.setMotion(new Vector3(velocity.getX(), velocity.getY(), velocity.getZ()));
    }

	@Override
    public Entity.Spigot spigot() {
        return new Entity.Spigot() {
            @Override
            public boolean isInvulnerable() {
                return PokkitEntity.this.isInvulnerable();
            }
        };
    }

	@Override
    public boolean teleport(Entity entity) {
        return teleport(entity.getLocation());
    }

	@Override
	public boolean teleport(Entity entity, TeleportCause cause) {
		return teleport(entity.getLocation(), cause);
	}

	@Override
	public boolean teleport(Location location) {
		return nukkit.teleport(PokkitLocation.toNukkit(location));
	}

	@Override
    public boolean teleport(Location location, TeleportCause cause) {
        return nukkit.teleport(PokkitLocation.toNukkit(location), PokkitTeleportCause.toNukkit(cause));
    }

    @Override
	public BlockFace getFacing() {
        return PokkitBlockFace.toBukkit(nukkit.getDirection());
	}
}
