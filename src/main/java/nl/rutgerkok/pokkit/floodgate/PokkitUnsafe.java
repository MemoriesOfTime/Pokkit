package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.api.unsafe.Unsafe;

import java.util.UUID;

/**
 * No-op Unsafe implementation for Pokkit.
 * Raw packet sending is not applicable on Nukkit-MOT.
 */
public final class PokkitUnsafe implements Unsafe {

	@Override
	public void sendPacket(UUID uuid, int packetId, byte[] data) {
	}

	@Override
	public void sendPacket(FloodgatePlayer player, int packetId, byte[] data) {
	}
}
