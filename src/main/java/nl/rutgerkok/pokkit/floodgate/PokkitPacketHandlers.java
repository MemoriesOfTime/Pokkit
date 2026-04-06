package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.floodgate.api.packet.PacketHandler;
import org.geysermc.floodgate.api.packet.PacketHandlers;
import org.geysermc.floodgate.api.util.TriFunction;

import io.netty.channel.ChannelHandlerContext;

/**
 * No-op PacketHandlers implementation for Pokkit.
 * Packet injection is not applicable on Nukkit-MOT.
 */
public final class PokkitPacketHandlers implements PacketHandlers {

	@Override
	public void register(PacketHandler handler, Class<?> packetClass,
			TriFunction<ChannelHandlerContext, Object, Boolean, Object> transformer) {
	}

	@Override
	public void register(PacketHandler handler, Class<?> packetClass) {
	}

	@Override
	public void registerAll(PacketHandler handler) {
	}

	@Override
	public void deregister(PacketHandler handler) {
	}
}
