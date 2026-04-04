package nl.rutgerkok.pokkit.command;

import java.util.UUID;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;

/**
 * Implementation of {@link ConsoleCommandSender} on top of Nukkit.
 *
 */
final class PokkitConsoleCommandSender extends PokkitCommandSender implements ConsoleCommandSender {

	protected PokkitConsoleCommandSender(cn.nukkit.command.ConsoleCommandSender nukkit) {
		super(nukkit);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
	}

	@Override
	public void acceptConversationInput(String input) {
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return false;
	}

	@Override
	public boolean isConversing() {
		return false;
	}

	@Override
	public void sendRawMessage(String message) {
		this.sendMessage(message);
	}

	@Override
	public void sendRawMessage(UUID uuid, String message) {
		this.sendRawMessage(message);
	}

}
