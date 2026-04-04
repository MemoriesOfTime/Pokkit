package nl.rutgerkok.pokkit.pluginservice;

import cn.nukkit.event.EventHandler;
import nl.rutgerkok.pokkit.world.PokkitBlock;
import nl.rutgerkok.pokkit.world.PokkitWorld;

import org.bukkit.World;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvents extends EventTranslator {

	@EventHandler(ignoreCancelled = false)
	public void onBlockRedstone(cn.nukkit.event.block.BlockRedstoneEvent event) {
		if (canIgnore(BlockRedstoneEvent.getHandlerList())) {
			return;
		}

		BlockRedstoneEvent bukkitEvent = new BlockRedstoneEvent(
				PokkitBlock.toBukkit(event.getBlock()),
				event.getOldPower(),
				event.getNewPower());
		callUncancellable(bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onWeatherChange(cn.nukkit.event.level.WeatherChangeEvent event) {
		if (canIgnore(WeatherChangeEvent.getHandlerList())) {
			return;
		}

		World world = PokkitWorld.toBukkit(event.getLevel());
		WeatherChangeEvent bukkitEvent = new WeatherChangeEvent(world, event.toWeatherState());
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onThunderChange(cn.nukkit.event.level.ThunderChangeEvent event) {
		if (canIgnore(ThunderChangeEvent.getHandlerList())) {
			return;
		}

		World world = PokkitWorld.toBukkit(event.getLevel());
		ThunderChangeEvent bukkitEvent = new ThunderChangeEvent(world, event.toThunderState());
		callCancellable(event, bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onChunkLoad(cn.nukkit.event.level.ChunkLoadEvent event) {
		if (canIgnore(ChunkLoadEvent.getHandlerList())) {
			return;
		}

		ChunkLoadEvent bukkitEvent = new ChunkLoadEvent(
				PokkitWorld.toBukkit(event.getLevel()).getChunkAt(event.getChunk().getX(), event.getChunk().getZ()),
				event.isNewChunk());
		callUncancellable(bukkitEvent);
	}

	@EventHandler(ignoreCancelled = false)
	public void onChunkUnload(cn.nukkit.event.level.ChunkUnloadEvent event) {
		if (canIgnore(ChunkUnloadEvent.getHandlerList())) {
			return;
		}

		ChunkUnloadEvent bukkitEvent = new ChunkUnloadEvent(
				PokkitWorld.toBukkit(event.getLevel()).getChunkAt(event.getChunk().getX(), event.getChunk().getZ()));
		callUncancellable(bukkitEvent);
	}
}
