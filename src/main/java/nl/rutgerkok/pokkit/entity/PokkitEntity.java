package nl.rutgerkok.pokkit.entity;

import java.util.*;
import java.util.stream.Collectors;

import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.math.SimpleAxisAlignedBB;
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
import org.bukkit.entity.Pose;
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
        cn.nukkit.level.Location location = nukkit.getLocation();
        cn.nukkit.entity.Entity[] found = location.level.getNearbyEntities(new SimpleAxisAlignedBB(
                location.x - x, location.y - y, location.z - z,
                location.x + x, location.y + y, location.z + z));

        return Arrays.stream(found).map(PokkitEntity::toBukkit).collect(Collectors.toList());
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
            case 51: return EntityType.UNKNOWN;
            case 63: return EntityType.PLAYER;
            case EntityWitherSkeleton.NETWORK_ID: return EntityType.WITHER_SKELETON;
            case EntityHusk.NETWORK_ID: return EntityType.HUSK;
            case EntityStray.NETWORK_ID: return EntityType.STRAY;
            case EntityWitch.NETWORK_ID: return EntityType.WITCH;
            case EntityZombieVillagerV1.NETWORK_ID: return EntityType.ZOMBIE_VILLAGER;
            case EntityBlaze.NETWORK_ID: return EntityType.BLAZE;
            case EntityMagmaCube.NETWORK_ID: return EntityType.MAGMA_CUBE;
            case EntityGhast.NETWORK_ID: return EntityType.GHAST;
            case EntityCaveSpider.NETWORK_ID: return EntityType.CAVE_SPIDER;
            case EntitySilverfish.NETWORK_ID: return EntityType.SILVERFISH;
            case EntityEnderman.NETWORK_ID: return EntityType.ENDERMAN;
            case EntitySlime.NETWORK_ID: return EntityType.SLIME;
            case EntityZombiePigman.NETWORK_ID: return EntityType.PIG_ZOMBIE;
            case EntitySpider.NETWORK_ID: return EntityType.SPIDER;
            case EntitySkeleton.NETWORK_ID: return EntityType.SKELETON;
            case EntityCreeper.NETWORK_ID: return EntityType.CREEPER;
            case EntityZombie.NETWORK_ID: return EntityType.ZOMBIE;
            case EntitySkeletonHorse.NETWORK_ID: return EntityType.SKELETON_HORSE;
            case EntityMule.NETWORK_ID: return EntityType.MULE;
            case EntityDonkey.NETWORK_ID: return EntityType.DONKEY;
            case EntityDolphin.NETWORK_ID: return EntityType.DOLPHIN;
            case EntityTropicalFish.NETWORK_ID: return EntityType.TROPICAL_FISH;
            case EntityWolf.NETWORK_ID: return EntityType.WOLF;
            case EntitySquid.NETWORK_ID: return EntityType.SQUID;
            case EntityDrowned.NETWORK_ID: return EntityType.DROWNED;
            case EntitySheep.NETWORK_ID: return EntityType.SHEEP;
            case EntityMooshroom.NETWORK_ID: return EntityType.MUSHROOM_COW;
            case EntityPanda.NETWORK_ID: return EntityType.PANDA;
            case EntitySalmon.NETWORK_ID: return EntityType.SALMON;
            case EntityPig.NETWORK_ID: return EntityType.PIG;
            case EntityVillagerV1.NETWORK_ID: return EntityType.VILLAGER;
            case EntityCod.NETWORK_ID: return EntityType.COD;
            case EntityPufferfish.NETWORK_ID: return EntityType.PUFFERFISH;
            case EntityCow.NETWORK_ID: return EntityType.COD;
            case EntityChicken.NETWORK_ID: return EntityType.CHICKEN;
            case 107: return EntityType.UNKNOWN;
            case EntityLlama.NETWORK_ID: return EntityType.LLAMA;
            case EntityIronGolem.NETWORK_ID: return EntityType.IRON_GOLEM;
            case EntityRabbit.NETWORK_ID: return EntityType.RABBIT;
            case EntitySnowGolem.NETWORK_ID: return EntityType.SNOWMAN;
            case EntityBat.NETWORK_ID: return EntityType.BAT;
            case EntityOcelot.NETWORK_ID: return EntityType.OCELOT;
            case EntityHorse.NETWORK_ID: return EntityType.HORSE;
            case EntityCat.NETWORK_ID: return EntityType.CAT;
            case EntityPolarBear.NETWORK_ID: return EntityType.POLAR_BEAR;
            case EntityZombieHorse.NETWORK_ID: return EntityType.ZOMBIE_HORSE;
            case EntityTurtle.NETWORK_ID: return EntityType.TURTLE;
            case EntityParrot.NETWORK_ID: return EntityType.PARROT;
            case EntityGuardian.NETWORK_ID: return EntityType.GUARDIAN;
            case EntityElderGuardian.NETWORK_ID: return EntityType.ELDER_GUARDIAN;
            case EntityVindicator.NETWORK_ID: return EntityType.VINDICATOR;
            case EntityWither.NETWORK_ID: return EntityType.WITCH;
            case EntityEnderDragon.NETWORK_ID: return EntityType.ENDER_DRAGON;
            case EntityShulker.NETWORK_ID: return EntityType.SHULKER;
            case EntityEndermite.NETWORK_ID: return EntityType.ENDERMITE;
            case EntityMinecartEmpty.NETWORK_ID: return EntityType.MINECART;
            case EntityMinecartHopper.NETWORK_ID: return EntityType.MINECART_HOPPER;
            case EntityMinecartTNT.NETWORK_ID: return EntityType.MINECART_TNT;
            case EntityMinecartChest.NETWORK_ID: return EntityType.MINECART_CHEST;
            case 100: return EntityType.MINECART_COMMAND;
            case EntityArmorStand.NETWORK_ID: return EntityType.ARMOR_STAND;
            case EntityItem.NETWORK_ID: return EntityType.DROPPED_ITEM;
            case EntityPrimedTNT.NETWORK_ID: return EntityType.PRIMED_TNT;
            case EntityFallingBlock.NETWORK_ID: return EntityType.FALLING_BLOCK;
            case EntityExpBottle.NETWORK_ID: return EntityType.THROWN_EXP_BOTTLE;
            case EntityXPOrb.NETWORK_ID: return EntityType.EXPERIENCE_ORB;
            case 70: return EntityType.ENDER_SIGNAL;
            case EntityEndCrystal.NETWORK_ID: return EntityType.ENDER_CRYSTAL;
            case 76: return EntityType.SHULKER_BULLET;
            case EntityFishingHook.NETWORK_ID: return EntityType.FISHING_HOOK;
            case 79: return EntityType.DRAGON_FIREBALL;
            case EntityArrow.NETWORK_ID: return EntityType.ARROW;
            case EntitySnowball.NETWORK_ID: return EntityType.SNOWBALL;
            case EntityEgg.NETWORK_ID: return EntityType.EGG;
            case EntityPainting.NETWORK_ID: return EntityType.PAINTING;
            case EntityThrownTrident.NETWORK_ID: return EntityType.TRIDENT;
            case 85: return EntityType.FIREBALL;
            case EntityPotion.NETWORK_ID: return EntityType.SPLASH_POTION;
            case EntityEnderPearl.NETWORK_ID: return EntityType.ENDER_PEARL;
            case 88: return EntityType.LEASH_HITCH;
            case 89: return EntityType.WITHER_SKULL;
            case 91: return EntityType.WITHER_SKULL;
            case EntityBoat.NETWORK_ID: return EntityType.BOAT;
            case EntityLightning.NETWORK_ID: return EntityType.LIGHTNING;
            case 94: return EntityType.SMALL_FIREBALL;
            case 102: return EntityType.LLAMA_SPIT;
            case EntityAreaEffectCloud.NETWORK_ID: return EntityType.AREA_EFFECT_CLOUD;
            case EntityPotionLingering.NETWORK_ID: return EntityType.SPLASH_POTION;
            case EntityFirework.NETWORK_ID: return EntityType.FIREWORK;
            case 103: return EntityType.EVOKER_FANGS;
            case 104: return EntityType.EVOKER;
            case EntityVex.NETWORK_ID: return EntityType.VEX;
            case 56: return EntityType.UNKNOWN;
            case 106: return EntityType.UNKNOWN;
            case EntityPhantom.NETWORK_ID: return EntityType.PHANTOM;
            case 62: return EntityType.UNKNOWN;
            case EntityPillager.NETWORK_ID: return EntityType.PILLAGER;
            case EntityWanderingTrader.NETWORK_ID: return EntityType.WANDERING_TRADER;
            case EntityRavager.NETWORK_ID: return EntityType.RAVAGER;
            case EntityVillager.NETWORK_ID: return EntityType.VILLAGER;
            case EntityZombieVillager.NETWORK_ID: return EntityType.ZOMBIE_VILLAGER;
            case EntityFox.NETWORK_ID: return EntityType.FOX;
            case EntityBee.NETWORK_ID: return EntityType.BEE;
            // Spigot TODO
            case EntityPiglin.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityHoglin.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityStrider.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityZoglin.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityPiglinBrute.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityGoat.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityGlowSquid.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityAxolotl.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityWarden.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityFrog.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityTadpole.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityAllay.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityChestBoat.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityCamel.NETWORK_ID: return EntityType.UNKNOWN;
            case EntitySniffer.NETWORK_ID: return EntityType.UNKNOWN;
            case EntityBreeze.NETWORK_ID: return EntityType.UNKNOWN;
            case 141: return EntityType.UNKNOWN;
            case EntityArmadillo.NETWORK_ID: return EntityType.UNKNOWN;
            case 143: return EntityType.UNKNOWN;
            case EntityBogged.NETWORK_ID: return EntityType.UNKNOWN;
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
        return new Entity.Spigot();
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

    @Override
    public Pose getPose() {
        throw Pokkit.unsupported();
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        throw Pokkit.unsupported();
    }
}
