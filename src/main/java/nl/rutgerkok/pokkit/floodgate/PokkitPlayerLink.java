package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.floodgate.api.link.LinkRequestResult;
import org.geysermc.floodgate.api.link.PlayerLink;
import org.geysermc.floodgate.util.LinkedPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Disabled PlayerLink implementation for Pokkit.
 * On a pure Bedrock server, account linking is not applicable.
 */
public final class PokkitPlayerLink implements PlayerLink {

	@Override
	public void load() {
	}

	@Override
	public CompletableFuture<LinkedPlayer> getLinkedPlayer(UUID uuid) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Boolean> isLinkedPlayer(UUID uuid) {
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<Void> linkPlayer(UUID bedrockUuid, UUID javaUuid, String javaUsername) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> unlinkPlayer(UUID uuid) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<?> createLinkRequest(UUID bedrockUuid, String javaUsername, String bedrockUsername) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<LinkRequestResult> verifyLinkRequest(UUID javaUuid, String javaUsername, String bedrockUsername, String code) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public String getName() {
		return "PokkitNoLink";
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public long getVerifyLinkTimeout() {
		return 0;
	}

	@Override
	public boolean isAllowLinking() {
		return false;
	}

	@Override
	public void stop() {
	}
}
