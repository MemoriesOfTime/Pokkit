package nl.rutgerkok.pokkit.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import nl.rutgerkok.pokkit.Pokkit;
import nl.rutgerkok.pokkit.attribute.PokkitAttributeInstance;
import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.inventory.PokkitEntityEquipment;
import nl.rutgerkok.pokkit.player.PokkitPlayer;
import nl.rutgerkok.pokkit.potion.PokkitPotionEffect;
import nl.rutgerkok.pokkit.potion.PokkitPotionEffectType;
import nl.rutgerkok.pokkit.world.PokkitBlock;

import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.potion.Effect;

public class PokkitLivingEntity extends PokkitEntity implements LivingEntity {
	private static final Map<UUID, Map<MemoryKey<?>, Object>> memoryStore = new ConcurrentHashMap<>();
	private static final Map<UUID, Set<UUID>> collidableExemptions = new ConcurrentHashMap<>();

	public static LivingEntity toBukkit(cn.nukkit.entity.EntityLiving nukkit) {
		if (nukkit == null) {
			return null;
		}

		if (nukkit instanceof cn.nukkit.Player) {
			return PokkitPlayer.toBukkit((cn.nukkit.Player) nukkit);
		}

		return new PokkitLivingEntity(nukkit);
	}

	public static cn.nukkit.entity.Entity toNukkit(LivingEntity entity) {
		if (entity == null) {
			return null;
		}
		return ((PokkitLivingEntity) entity).nukkit;
	}

	private final cn.nukkit.entity.EntityLiving nukkit;

	PokkitLivingEntity(cn.nukkit.entity.EntityLiving nukkitEntity) {
		super(nukkitEntity);
		this.nukkit = nukkitEntity;
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

	@Override
	public void damage(double arg0) {
		cn.nukkit.event.entity.EntityDamageEvent e = new cn.nukkit.event.entity.EntityDamageEvent(nukkit, cn.nukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, (float) arg0);
		nukkit.attack(e);
	}

	@Override
	public void damage(double arg0, Entity arg1) {
		cn.nukkit.event.entity.EntityDamageEvent e = new cn.nukkit.event.entity.EntityDamageByEntityEvent(nukkit, PokkitEntity.toNukkit(arg1), cn.nukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, (float) arg0);
		nukkit.attack(e);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		List<PotionEffect> effects = new ArrayList<>();
		for (cn.nukkit.potion.Effect eff : nukkit.getEffects().values()) {
			effects.add(PokkitPotionEffect.toBukkit(eff));
		}
		return effects;
	}

	@Override
	public AttributeInstance getAttribute(Attribute attribute) {
		cn.nukkit.entity.Attribute.init();
		int nukkitAttributeType = PokkitAttributeInstance.fromBukkit(attribute);
		cn.nukkit.entity.Attribute nukkitAttribute = cn.nukkit.entity.Attribute.getAttribute(nukkitAttributeType);
		return new PokkitAttributeInstance(nukkitAttribute);
	}

	@Override
	public boolean getCanPickupItems() {
		if (nukkit.namedTag.contains("CanPickupItems")) {
			return nukkit.namedTag.getBoolean("CanPickupItems");
		}
		return nukkit instanceof cn.nukkit.Player;
	}

	@Override
	public EntityEquipment getEquipment() {
		if (nukkit instanceof cn.nukkit.entity.EntityHumanType) {
			return new PokkitEntityEquipment(((cn.nukkit.entity.EntityHumanType) nukkit).getInventory());
		}
		return null;
	}

	@Override
	public double getEyeHeight() {
		return nukkit.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean ignoreSneaking) {
		return nukkit.getEyeHeight();
	}

	@Override
	public Location getEyeLocation() {
		return getLocation().add(0, getEyeHeight(), 0);
	}

	@Override
	public double getHealth() {
		return nukkit.getHealth();
	}

	@Override
	public Player getKiller() {
		cn.nukkit.event.entity.EntityDamageEvent lastDamage = nukkit.getLastDamageCause();
		if (lastDamage instanceof cn.nukkit.event.entity.EntityDamageByEntityEvent) {
			cn.nukkit.entity.Entity damager = ((cn.nukkit.event.entity.EntityDamageByEntityEvent) lastDamage).getDamager();
			if (damager instanceof cn.nukkit.Player) {
				return PokkitPlayer.toBukkit((cn.nukkit.Player) damager);
			}
		}
		return null;
	}

	@Override
	public double getLastDamage() {
		return nukkit.getLastDamageCause().getFinalDamage();
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
		cn.nukkit.block.Block[] nukkitBlocks = nukkit.getLineOfSight(maxDistance, 2,
				toNukkitBlockIds(transparent));
		List<Block> bukkitBlocks = new ArrayList<>(nukkitBlocks.length);
		for (cn.nukkit.block.Block nukkitBlock : nukkitBlocks) {
			bukkitBlocks.add(PokkitBlock.toBukkit(nukkitBlock));
		}
		return bukkitBlocks;
	}

	@Override
	public Block getTargetBlockExact(int maxDistance) {
		return getTargetBlockExact(maxDistance, FluidCollisionMode.NEVER);
	}

	@Override
	public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
		cn.nukkit.block.Block target = nukkit.getTargetBlock(maxDistance, new Integer[0]);
		if (target == null) {
			return null;
		}
		return PokkitBlock.toBukkit(target);
	}

	@Override
	public RayTraceResult rayTraceBlocks(double maxDistance) {
		return rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
	}

	@Override
	public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
		cn.nukkit.block.Block target = nukkit.getTargetBlock((int) maxDistance, new Integer[0]);
		if (target == null) {
			return null;
		}
		Block bukkitBlock = PokkitBlock.toBukkit(target);
		org.bukkit.util.Vector hitPos = bukkitBlock.getLocation().toVector();
		return new RayTraceResult(hitPos, bukkitBlock, null);
	}

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        if (!isLeashed()) {
            throw new IllegalStateException("Not leashed");
        }
        long holderEid = nukkit.getDataPropertyLong(cn.nukkit.entity.Entity.DATA_LEAD_HOLDER_EID);
        for (cn.nukkit.Player p : nukkit.getLevel().getPlayers().values()) {
            if (p.getId() == holderEid) {
                return PokkitPlayer.toBukkit(p);
            }
        }
        for (cn.nukkit.entity.Entity e : nukkit.getLevel().getEntities()) {
            if (e.getId() == holderEid) {
                return PokkitEntity.toBukkit(e);
            }
        }
        return null;
    }

	@Override
	public List<Block> getLineOfSight(Set<Material> bukkitTransparent, int maxDistance) {
		cn.nukkit.block.Block[] nukkitBlocks = nukkit.getLineOfSight(maxDistance, 0,
				toNukkitBlockIds(bukkitTransparent));

		List<Block> bukkitBlocks = new ArrayList<>(nukkitBlocks.length);
		for (cn.nukkit.block.Block nukkitBlock : nukkitBlocks) {
			bukkitBlocks.add(PokkitBlock.toBukkit(nukkitBlock));
		}

		return bukkitBlocks;
	}

	@Override
	public double getMaxHealth() {
		return nukkit.getMaxHealth();
	}

	@Override
	public int getMaximumAir() {
		return nukkit.namedTag.contains("MaxAir") ? nukkit.namedTag.getInt("MaxAir") : 400;
	}

    @Override
    public int getMaximumNoDamageTicks() {
        return nukkit.namedTag.contains("MaxNoDamageTicks") ? nukkit.namedTag.getInt("MaxNoDamageTicks") : 20;
    }

	@Override
	public int getNoDamageTicks() {
		return nukkit.noDamageTicks;
	}

	@Override
	public PotionEffect getPotionEffect(PotionEffectType type) {
		cn.nukkit.potion.Effect nukkitEffect = nukkit.getEffect(type.getId());
		if (nukkitEffect == null) {
			return null;
		}
		return PokkitPotionEffect.toBukkit(nukkitEffect);
	}

	@Override
	public int getRemainingAir() {
		return nukkit.getAirTicks();
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return !nukkit.canBeSavedWithChunk();
	}

	@Override
	public Block getTargetBlock(Set<Material> bukkitTransparent, int maxDistance) {
		cn.nukkit.block.Block nukkitBlock = nukkit.getTargetBlock(maxDistance, toNukkitBlockIds(bukkitTransparent));

		return PokkitBlock.toBukkit(nukkitBlock);
	}

	@Override
	public boolean hasAI() {
		return !nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_NO_AI);
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
		EntityEventPacket pk = new EntityEventPacket();
		pk.eid = nukkit.getId();
		pk.event = EntityEventPacket.ARM_SWING;
		cn.nukkit.Server.broadcastPacket(nukkit.hasSpawned.values(), pk);
	}

	@Override
	public void swingOffHand() {
		EntityEventPacket pk = new EntityEventPacket();
		pk.eid = nukkit.getId();
		pk.event = EntityEventPacket.ARM_SWING;
		cn.nukkit.Server.broadcastPacket(nukkit.hasSpawned.values(), pk);
	}

	@Override
	public boolean hasLineOfSight(Entity other) {
		return nukkit.hasLineOfSight(PokkitEntity.toNukkit(other));
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType potionEffect) {
		Effect nukkitEffect = PokkitPotionEffectType.toNukkit(potionEffect);
		for (cn.nukkit.potion.Effect eff : nukkit.getEffects().values()) {
			if (eff.getId() == nukkitEffect.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Sound getEatingSound(ItemStack itemStack) {
		return Sound.ENTITY_GENERIC_EAT;
	}

	@Override
	public Sound getDrinkingSound(ItemStack itemStack) {
		return Sound.ENTITY_GENERIC_DRINK;
	}

	@Override
	public Sound getFallDamageSoundBig() {
		return Sound.ENTITY_GENERIC_BIG_FALL;
	}

	@Override
	public Sound getFallDamageSoundSmall() {
		return Sound.ENTITY_GENERIC_SMALL_FALL;
	}

	@Override
	public Sound getDeathSound() {
		return Sound.ENTITY_GENERIC_DEATH;
	}

	@Override
	public Sound getHurtSound() {
		return Sound.ENTITY_GENERIC_HURT;
	}

	@Override
	public Sound getFallDamageSound(int distance) {
		return distance > 4 ? getFallDamageSoundBig() : getFallDamageSoundSmall();
	}

	@Override
	public boolean isClimbing() {
		return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_WALLCLIMBING);
	}

	@Override
	public java.util.Set<java.util.UUID> getCollidableExemptions() {
		Set<UUID> exemptions = collidableExemptions.get(nukkit.getUniqueId());
		return exemptions != null ? Collections.unmodifiableSet(exemptions) : Collections.emptySet();
	}

	@Override
	public int getArrowCooldown() {
		return nukkit.namedTag.contains("ArrowCooldown") ? nukkit.namedTag.getInt("ArrowCooldown") : 0;
	}

	@Override
	public void setArrowCooldown(int cooldown) {
		nukkit.namedTag.putInt("ArrowCooldown", cooldown);
	}

	@Override
	public int getArrowsInBody() {
		return nukkit.namedTag.contains("ArrowsInBody") ? nukkit.namedTag.getInt("ArrowsInBody") : 0;
	}

	@Override
	public void setArrowsInBody(int count) {
		nukkit.namedTag.putInt("ArrowsInBody", count);
	}

	@Override
	public org.bukkit.inventory.ItemStack getItemInUse() {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			if (player.isUsingItem()) {
				cn.nukkit.item.Item item = player.getInventory().getItemInHand();
				if (item != null && !item.isNull()) {
					return nl.rutgerkok.pokkit.item.PokkitItemStack.toBukkitCopy(item);
				}
			}
		}
		return null;
	}

	@Override
	public int getItemInUseTicks() {
		if (nukkit instanceof cn.nukkit.Player) {
			cn.nukkit.Player player = (cn.nukkit.Player) nukkit;
			if (player.isUsingItem()) {
				return nukkit.namedTag.contains("ItemInUseTicks") ? nukkit.namedTag.getInt("ItemInUseTicks") : 0;
			}
		}
		return 0;
	}

	@Override
	public void setItemInUseTicks(int ticks) {
		nukkit.namedTag.putInt("ItemInUseTicks", ticks);
	}

	@Override
	public int getNoActionTicks() {
		return nukkit.namedTag.contains("NoActionTicks") ? nukkit.namedTag.getInt("NoActionTicks") : 0;
	}

	@Override
	public void setNoActionTicks(int ticks) {
		nukkit.namedTag.putInt("NoActionTicks", ticks);
	}

	@Override
	public void playHurtAnimation(float yaw) {
		EntityEventPacket pk = new EntityEventPacket();
		pk.eid = nukkit.getId();
		pk.event = EntityEventPacket.HURT_ANIMATION;
		cn.nukkit.Server.broadcastPacket(nukkit.hasSpawned.values(), pk);
	}

	@Override
	public void damage(double amount, org.bukkit.damage.DamageSource source) {
		damage(amount);
	}

	@Override
	public boolean isCollidable() {
		return nukkit.canCollide();
	}

	@Override
	public <T> T getMemory(MemoryKey<T> memoryKey) {
		Map<MemoryKey<?>, Object> memories = memoryStore.get(nukkit.getUniqueId());
		if (memories == null) return null;
		@SuppressWarnings("unchecked")
		T value = (T) memories.get(memoryKey);
		return value;
	}

	@Override
	public <T> void setMemory(MemoryKey<T> memoryKey, T t) {
		memoryStore.computeIfAbsent(nukkit.getUniqueId(), k -> new ConcurrentHashMap<>()).put(memoryKey, t);
	}

	@Override
	public boolean isGliding() {
		return nukkit.isGliding();
	}

    @Override
    public boolean isLeashed() {
        return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_LEASHED);
    }

	@Override
	public boolean isRiptiding() {
		return nukkit.isSpinAttack();
	}

	@Override
	public boolean isSleeping() {
		return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_SLEEPING);
	}

	@Override
	public boolean isSwimming() {
		return nukkit.isSwimming();
	}

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return launchProjectile(projectile, getEyeLocation().getDirection());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        String entityName = null;
        if (org.bukkit.entity.Arrow.class.isAssignableFrom(projectile)) entityName = "Arrow";
        else if (org.bukkit.entity.Snowball.class.isAssignableFrom(projectile)) entityName = "Snowball";
        else if (org.bukkit.entity.Egg.class.isAssignableFrom(projectile)) entityName = "Egg";
        else if (org.bukkit.entity.EnderPearl.class.isAssignableFrom(projectile)) entityName = "EnderPearl";
        else if (org.bukkit.entity.ThrownExpBottle.class.isAssignableFrom(projectile)) entityName = "ExpBottle";
        else if (org.bukkit.entity.ThrownPotion.class.isAssignableFrom(projectile)) entityName = "Potion";
        if (entityName == null) {
            return null;
        }
        cn.nukkit.level.Position pos = cn.nukkit.level.Position.fromObject(
                new cn.nukkit.math.Vector3(getEyeLocation().getX(), getEyeLocation().getY(), getEyeLocation().getZ()),
                nukkit.getLevel());
        cn.nukkit.entity.Entity nukkitEntity = cn.nukkit.entity.Entity.createEntity(entityName, pos);
        if (nukkitEntity == null) {
            return null;
        }
        cn.nukkit.math.Vector3 motion = new cn.nukkit.math.Vector3(velocity.getX(), velocity.getY(), velocity.getZ());
        nukkitEntity.setMotion(motion);
        nukkitEntity.spawnToAll();
        return (T) PokkitEntity.toBukkit(nukkitEntity);
    }

	@Override
	public void removePotionEffect(PotionEffectType type) {
		nukkit.removeEffect(type.getId());
	}

    @Override
    public void resetMaxHealth() {
        nukkit.setMaxHealth(20);
    }

	@Override
	public void setAI(boolean ai) {
		nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_NO_AI, !ai);
	}

    @Override
    public void setCanPickupItems(boolean pickup) {
        nukkit.namedTag.putBoolean("CanPickupItems", pickup);
    }

	@Override
	public void setCollidable(boolean collidable) {
		nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_HAS_COLLISION, collidable);
	}

	@Override
	public void setGliding(boolean gliding) {
		nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_GLIDING, gliding);
	}

	@Override
	public void setHealth(double health) {
		nukkit.setHealth((float) health);
	}

	@Override
	public double getAbsorptionAmount() {
		return nukkit.getAbsorption();
	}

	@Override
	public void setAbsorptionAmount(double v) {
		nukkit.setAbsorption((float) v);
	}

	@Override
	public void setLastDamage(double damage) {
		nukkit.getLastDamageCause().setDamage((float) damage);
	}

    @Override
    public boolean setLeashHolder(Entity holder) {
        if (holder == null) {
            nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_LEASHED, false);
            return true;
        }
        nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_LEASHED, true);
        nukkit.setDataProperty(new cn.nukkit.entity.data.LongEntityData(cn.nukkit.entity.Entity.DATA_LEAD_HOLDER_EID, PokkitEntity.toNukkit(holder).getId()));
        return true;
    }

	@Override
	public void setMaxHealth(double health) {
		nukkit.setMaxHealth((int) Math.ceil(health));
	}

    @Override
    public void setMaximumAir(int ticks) {
        nukkit.namedTag.putInt("MaxAir", ticks);
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        nukkit.namedTag.putInt("MaxNoDamageTicks", ticks);
    }

	@Override
	public void setNoDamageTicks(int ticks) {
		nukkit.noDamageTicks = ticks;
	}

	@Override
	public void setRemainingAir(int ticks) {
		nukkit.setAirTicks(ticks);
	}

    @Override
    public void setRemoveWhenFarAway(boolean remove) {
        nukkit.setCanBeSavedWithChunk(!remove);
    }

	@Override
	public void setSwimming(boolean swimming) {
		nukkit.setSwimming(swimming);
	}

	private Integer[] toNukkitBlockIds(Set<Material> bukkitMaterials) {
		if (bukkitMaterials == null) {
			return new Integer[cn.nukkit.block.Block.AIR];
		}
		return bukkitMaterials.stream()
				.map(material -> PokkitBlockData.createBlockData(material, 0).getNukkitId())
				.toArray(Integer[]::new);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(nukkit.getBoundingBox().getMinX(), nukkit.getBoundingBox().getMinY(), nukkit.getBoundingBox().getMinZ(), nukkit.getBoundingBox().getMaxX(), nukkit.getBoundingBox().getMaxY(), nukkit.getBoundingBox().getMaxZ());
	}

	@Override
	public void setRotation(float v, float v1) {
		nukkit.setRotation(v, v1);
	}

	@Override
	public EntityCategory getCategory() {
		if (nukkit instanceof cn.nukkit.entity.mob.EntitySkeleton
				|| nukkit instanceof cn.nukkit.entity.mob.EntityZombie
				|| nukkit instanceof cn.nukkit.entity.mob.EntityHusk
				|| nukkit instanceof cn.nukkit.entity.mob.EntityStray
				|| nukkit instanceof cn.nukkit.entity.mob.EntityWitherSkeleton
				|| nukkit instanceof cn.nukkit.entity.mob.EntityZombieVillager
				|| nukkit instanceof cn.nukkit.entity.mob.EntityZombieVillagerV2
				|| nukkit instanceof cn.nukkit.entity.passive.EntitySkeletonHorse
				|| nukkit instanceof cn.nukkit.entity.passive.EntityZombieHorse
				|| nukkit instanceof cn.nukkit.entity.mob.EntityPhantom) {
			return EntityCategory.UNDEAD;
		}
		if (nukkit instanceof cn.nukkit.entity.mob.EntitySpider
				|| nukkit instanceof cn.nukkit.entity.mob.EntityCaveSpider
				|| nukkit instanceof cn.nukkit.entity.mob.EntitySilverfish
				|| nukkit instanceof cn.nukkit.entity.mob.EntityEndermite) {
			return EntityCategory.ARTHROPOD;
		}
		if (nukkit instanceof cn.nukkit.entity.mob.EntityWitch
				|| nukkit instanceof cn.nukkit.entity.mob.EntityPillager
				|| nukkit instanceof cn.nukkit.entity.mob.EntityEvoker
				|| nukkit instanceof cn.nukkit.entity.mob.EntityVindicator
				|| nukkit instanceof cn.nukkit.entity.mob.EntityRavager
				|| nukkit instanceof cn.nukkit.entity.passive.EntityVillagerV2
				|| nukkit instanceof cn.nukkit.entity.passive.EntityWanderingTrader) {
			return EntityCategory.ILLAGER;
		}
		if (nukkit instanceof cn.nukkit.entity.passive.EntityWaterAnimal) {
			return EntityCategory.WATER;
		}
		return EntityCategory.NONE;
	}

    @Override
    public boolean isInvisible() {
        return nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_INVISIBLE);
    }

	@Override
	public boolean canBreatheUnderwater() {
		return !nukkit.getDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_BREATHING);
	}

    @Override
    public void setInvisible(boolean invisible) {
        nukkit.setDataFlag(cn.nukkit.entity.Entity.DATA_FLAGS, cn.nukkit.entity.Entity.DATA_FLAG_INVISIBLE, invisible);
    }
}
