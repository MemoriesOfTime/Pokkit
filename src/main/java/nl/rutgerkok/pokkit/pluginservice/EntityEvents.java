package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.MovingObjectPosition;
import com.google.common.base.Function;
import nl.rutgerkok.pokkit.PokkitLocation;
import nl.rutgerkok.pokkit.entity.*;
import nl.rutgerkok.pokkit.item.PokkitItemStack;
import nl.rutgerkok.pokkit.world.PokkitBlock;
import org.bukkit.ExplosionResult;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreeperPowerEvent.PowerCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class EntityEvents extends EventTranslator {

	private static Map<DamageModifier, Double> getDamageMap(double baseDamage) {
		Map<DamageModifier, Double> map = new EnumMap<>(DamageModifier.class);
		map.put(DamageModifier.BASE, baseDamage);
		return map;
	}

	private static Map<DamageModifier, Function<? super Double, Double>> getDamageModifierMap() {
		Map<DamageModifier, Function<? super Double, Double>> map = new EnumMap<>(DamageModifier.class);
		map.put(DamageModifier.BASE, val -> 0.0);
		return map;
	}

	@EventHandler(ignoreCancelled = false)
	public void onCreeperPower(cn.nukkit.event.entity.CreeperPowerEvent event) {
		if (canIgnore(CreeperPowerEvent.getHandlerList())) {
			return;
		}

		PowerCause cause = PowerCause.valueOf(event.getCause().name()); // Dirty and fast conversion

		CreeperPowerEvent bukkitEvent = new CreeperPowerEvent((Creeper) PokkitEntity.toBukkit(event.getEntity()), cause);

		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onEntityCombust(cn.nukkit.event.entity.EntityCombustEvent event) {
		if (canIgnore(EntityCombustEvent.getHandlerList())) {
			return;
		}

		if (event instanceof cn.nukkit.event.entity.EntityCombustByEntityEvent) {
			onEntityCombustByEntity((cn.nukkit.event.entity.EntityCombustByEntityEvent) event);
			return;
		}
		if (event instanceof cn.nukkit.event.entity.EntityCombustByBlockEvent) {
			onEntityCombustByBlock((cn.nukkit.event.entity.EntityCombustByBlockEvent) event);
			return;
		}

		EntityCombustEvent bukkitEvent = new EntityCombustEvent(PokkitEntity.toBukkit(event.getEntity()), event.getDuration());

		callCancellable(event, bukkitEvent);
		event.setDuration((int) bukkitEvent.getDuration());
	}

	private void onEntityCombustByEntity(cn.nukkit.event.entity.EntityCombustByEntityEvent event) {
		if (canIgnore(EntityCombustByEntityEvent.getHandlerList())) {
			return;
		}

		EntityCombustByEntityEvent bukkitEvent = new EntityCombustByEntityEvent(
				PokkitEntity.toBukkit(event.getCombuster()),
				PokkitEntity.toBukkit(event.getEntity()),
				event.getDuration());

		callCancellable(event, bukkitEvent);
		event.setDuration((int) bukkitEvent.getDuration());
	}

	private void onEntityCombustByBlock(cn.nukkit.event.entity.EntityCombustByBlockEvent event) {
		if (canIgnore(EntityCombustByBlockEvent.getHandlerList())) {
			return;
		}

		EntityCombustByBlockEvent bukkitEvent = new EntityCombustByBlockEvent(
				PokkitBlock.toBukkit(event.getCombuster()),
				PokkitEntity.toBukkit(event.getEntity()),
				event.getDuration());

		callCancellable(event, bukkitEvent);
		event.setDuration((int) bukkitEvent.getDuration());
	}

	@EventHandler(ignoreCancelled = false)
	public void onEntityDamage(cn.nukkit.event.entity.EntityDamageEvent event) {
		if (canIgnore(EntityDamageEvent.getHandlerList())) {
			return;
		}

		// EntityDamageEvent and EntityDamageBy*Event share their handler
		// lists. So this method can also be called with the event parameter set
		// to an EntityDamageByEntityEvent or EntityDamageByBlockEvent. In that
		// case, the event translation is a bit more involved.
		if (event instanceof cn.nukkit.event.entity.EntityDamageByEntityEvent) {
			onEntityDamageByEntity((cn.nukkit.event.entity.EntityDamageByEntityEvent) event);
			return;
		}
		if (event instanceof cn.nukkit.event.entity.EntityDamageByBlockEvent) {
			onEntityDamageByBlock((cn.nukkit.event.entity.EntityDamageByBlockEvent) event);
			return;
		}

		// For now, only base damage is taken into account. Potions, armor etc
		// are ignored
		EntityDamageEvent bukkitEvent = new EntityDamageEvent(
				PokkitEntity.toBukkit(event.getEntity()),
				PokkitDamageCause.toBukkit(event.getCause()),
				getDamageMap(event.getDamage()),
				getDamageModifierMap());

		callCancellable(event, bukkitEvent);
		event.setDamage((float) bukkitEvent.getDamage());
	}

	private void onEntityDamageByBlock(cn.nukkit.event.entity.EntityDamageByBlockEvent event) {
		// Called by onEntityDamage, so don't use an @EventHandler annotation

		// For now, only base damage is taken into account. Potions, armor etc
		// are ignored
		EntityDamageByBlockEvent bukkitEvent = new EntityDamageByBlockEvent(
				PokkitBlock.toBukkit(event.getDamager()),
				PokkitEntity.toBukkit(event.getEntity()),
				PokkitDamageCause.toBukkit(event.getCause()),
				getDamageMap(event.getDamage()),
				getDamageModifierMap());

		callCancellable(event, bukkitEvent);
		event.setDamage((float) bukkitEvent.getDamage());
	}

	private void onEntityDamageByEntity(cn.nukkit.event.entity.EntityDamageByEntityEvent event) {
		// Called by onEntityDamage, so don't use an @EventHandler annotation

		// For now, only base damage is taken into account. Potions, armor etc
		// are ignored
		EntityDamageByEntityEvent bukkitEvent = new EntityDamageByEntityEvent(
				PokkitEntity.toBukkit(event.getDamager()),
				PokkitEntity.toBukkit(event.getEntity()),
				PokkitDamageCause.toBukkit(event.getCause()),
				getDamageMap(event.getDamage()),
				getDamageModifierMap());

		callCancellable(event, bukkitEvent);
		event.setDamage((float) bukkitEvent.getDamage());
	}

	@EventHandler(ignoreCancelled = false)
	public void onEntityDeath(cn.nukkit.event.entity.EntityDeathEvent event) {
		if (canIgnore(EntityDeathEvent.getHandlerList())) {
			return;
		}

		List<ItemStack> bukkitDrops = new ArrayList<>();
		for (Item item : event.getDrops()) {
			bukkitDrops.add(PokkitItemStack.toBukkitCopy(item));
		}

		EntityDeathEvent bukkitEvent = new EntityDeathEvent(
				PokkitLivingEntity.toBukkit((EntityLiving) event.getEntity()), null, bukkitDrops);

		callUncancellable(bukkitEvent);
		event.setDrops(bukkitEvent.getDrops().stream().map(PokkitItemStack::toNukkitCopy).toArray(Item[]::new));
	}

	@EventHandler(ignoreCancelled = false)
	public void onEntityExplode(cn.nukkit.event.entity.EntityExplodeEvent event) {
		if (canIgnore(EntityExplodeEvent.getHandlerList())) {
			return;
		}

		List<org.bukkit.block.Block> blocks = new ArrayList<>();

		for (cn.nukkit.block.Block nukkitBlock : event.getBlockList()) {
			blocks.add(PokkitBlock.toBukkit(nukkitBlock));
		}

		EntityExplodeEvent bukkitEvent = new EntityExplodeEvent(PokkitEntity.toBukkit(event.getEntity()), PokkitLocation.toBukkit(event.getPosition()), blocks, (float) event.getYield(), org.bukkit.ExplosionResult.DESTROY);

		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onEntityRegainHealth(cn.nukkit.event.entity.EntityRegainHealthEvent event) {
		if (canIgnore(EntityRegainHealthEvent.getHandlerList())) {
			return;
		}

		int reason = event.getRegainReason();
		RegainReason bukkitReason = RegainReason.EATING;

		switch (reason) {
		case cn.nukkit.event.entity.EntityRegainHealthEvent.CAUSE_MAGIC:
			bukkitReason = RegainReason.MAGIC;
			break;
		case cn.nukkit.event.entity.EntityRegainHealthEvent.CAUSE_CUSTOM:
			bukkitReason = RegainReason.CUSTOM;
			break;
		case cn.nukkit.event.entity.EntityRegainHealthEvent.CAUSE_EATING:
			bukkitReason = RegainReason.EATING;
			break;
		case cn.nukkit.event.entity.EntityRegainHealthEvent.CAUSE_REGEN:
			bukkitReason = RegainReason.REGEN;
			break;
		}

		EntityRegainHealthEvent bukkitEvent = new EntityRegainHealthEvent(PokkitEntity.toBukkit(event.getEntity()), event.getAmount(), bukkitReason);

		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onExplosion(cn.nukkit.event.entity.ExplosionPrimeEvent event) {
		if (canIgnore(ExplosionPrimeEvent.getHandlerList())) {
			return;
        }

		ExplosionPrimeEvent bukkitEvent = new ExplosionPrimeEvent(PokkitEntity.toBukkit(event.getEntity()), (float) event.getForce(), true);

		callCancellable(event, bukkitEvent);
		event.setForce(bukkitEvent.getRadius());
	}


	@EventHandler(ignoreCancelled = false)
    public void onItemDrop(ItemFrameDropItemEvent event) {
        // Bukkit uses EntityDamageByEntityEvent when someone hits an Item Frame
        // However Nukkit uses ItemFrameDropItemEvent, so we are going to trigger an
        // EntityDamageByEntityEvent

		if (canIgnore(EntityDamageByEntityEvent.getHandlerList())) {
			return;
		}

        EntityDamageByEntityEvent bukkitEvent = new EntityDamageByEntityEvent(
        		PokkitEntity.toBukkit(event.getPlayer()),
        		new PokkitItemFrameEntity(event.getItemFrame()),
        		EntityDamageEvent.DamageCause.ENTITY_ATTACK,
        		getDamageMap(999D),
        		getDamageModifierMap());

        callCancellable(event, bukkitEvent);
    }

	@EventHandler(ignoreCancelled = false)
	public void onSpawn(cn.nukkit.event.entity.EntitySpawnEvent event) {
		if (canIgnore(EntitySpawnEvent.getHandlerList())) {
			return;
		}
		EntitySpawnEvent bukkitEvent = new EntitySpawnEvent(PokkitEntity.toBukkit(event.getEntity()));

		callUncancellable(bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onTeleport(cn.nukkit.event.entity.EntityTeleportEvent event) {
		if (canIgnore(EntityTeleportEvent.getHandlerList())) {
			return;
		}

		EntityTeleportEvent bukkitEvent = new EntityTeleportEvent(PokkitEntity.toBukkit(event.getEntity()), PokkitLocation.toBukkit(event.getFrom()), PokkitLocation.toBukkit(event.getTo()));

		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onProjectileHit(cn.nukkit.event.entity.ProjectileHitEvent event) {
		if (canIgnore(ProjectileHitEvent.getHandlerList())) {
			return;
		}

		Entity entity = null;
		cn.nukkit.block.Block block = null;
		MovingObjectPosition movingObjectPosition = event.getMovingObjectPosition();
		if (movingObjectPosition != null) {
			if (movingObjectPosition.typeOfHit == 0) {
				// Block hit
				block = event.getEntity().getLevel().getBlock(event.getMovingObjectPosition().hitVector);
			} else {
				// Entity hit
				entity = movingObjectPosition.entityHit;
			}
		}
		EntityProjectile nukkitProjectile = (EntityProjectile) event.getEntity();
		Projectile projectile = PokkitProjectile.toBukkit(nukkitProjectile);
		ProjectileHitEvent bukkitEvent = new ProjectileHitEvent(projectile, PokkitEntity.toBukkit(entity), PokkitBlock.toBukkit(block));

		callUncancellable(bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onPortalEnter(cn.nukkit.event.entity.EntityPortalEnterEvent event) {
		if (canIgnore(org.bukkit.event.entity.EntityPortalEnterEvent.getHandlerList())) {
			return;
		}

		org.bukkit.event.entity.EntityPortalEnterEvent bukkitEvent = new org.bukkit.event.entity.EntityPortalEnterEvent(
				PokkitEntity.toBukkit(event.getEntity()),
				PokkitLocation.toBukkit(event.getEntity().getPosition()));

		callUncancellable(bukkitEvent);
	}
}
