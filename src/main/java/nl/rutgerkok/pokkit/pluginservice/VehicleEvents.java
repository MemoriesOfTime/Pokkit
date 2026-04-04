package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import nl.rutgerkok.pokkit.entity.PokkitEntity;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;

public final class VehicleEvents extends EventTranslator {

	@EventHandler(ignoreCancelled = false)
	public void onVehicleEnter(cn.nukkit.event.entity.EntityVehicleEnterEvent event) {
		if (canIgnore(EntityMountEvent.getHandlerList())) {
			return;
		}

		Entity entity = PokkitEntity.toBukkit(event.getEntity());
		Entity vehicle = PokkitEntity.toBukkit(event.getVehicle());

		EntityMountEvent bukkitEvent = new EntityMountEvent(entity, vehicle);
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onVehicleExit(cn.nukkit.event.entity.EntityVehicleExitEvent event) {
		if (canIgnore(EntityDismountEvent.getHandlerList())) {
			return;
		}

		Entity entity = PokkitEntity.toBukkit(event.getEntity());
		Entity vehicle = PokkitEntity.toBukkit(event.getVehicle());

		EntityDismountEvent bukkitEvent = new EntityDismountEvent(entity, vehicle);
		callCancellable(event, bukkitEvent);
	}
}
