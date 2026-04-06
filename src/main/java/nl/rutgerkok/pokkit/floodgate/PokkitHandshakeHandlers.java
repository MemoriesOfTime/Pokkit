package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.floodgate.api.handshake.HandshakeHandler;
import org.geysermc.floodgate.api.handshake.HandshakeHandlers;

/**
 * No-op HandshakeHandlers implementation for Pokkit.
 * Handshake injection is not applicable on Nukkit-MOT.
 */
public final class PokkitHandshakeHandlers implements HandshakeHandlers {

	@Override
	public int addHandshakeHandler(HandshakeHandler handler) {
		return -1;
	}

	@Override
	public void removeHandshakeHandler(int id) {
	}

	@Override
	public void removeHandshakeHandler(Class<? extends HandshakeHandler> handlerClass) {
	}
}
