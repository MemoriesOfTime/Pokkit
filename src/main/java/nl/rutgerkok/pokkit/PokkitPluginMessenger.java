package nl.rutgerkok.pokkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;

public final class PokkitPluginMessenger implements Messenger {

	private final Set<String> incomingChannels = ConcurrentHashMap.newKeySet();
	private final Set<String> outgoingChannels = ConcurrentHashMap.newKeySet();
	private final Map<String, Set<PluginMessageListenerRegistration>> incomingRegistrations = new ConcurrentHashMap<>();
	private final Map<Plugin, Set<String>> outgoingByPlugin = new ConcurrentHashMap<>();
	private final Map<Plugin, Set<String>> incomingByPlugin = new ConcurrentHashMap<>();

	@Override
	public void dispatchIncomingMessage(Player source, String channel, byte[] message) {
		Set<PluginMessageListenerRegistration> registrations = incomingRegistrations.get(channel);
		if (registrations == null) return;
		for (PluginMessageListenerRegistration reg : registrations) {
			if (reg.isValid()) {
				reg.getListener().onPluginMessageReceived(channel, source, message);
			}
		}
	}

	@Override
	public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin) {
		Set<String> channels = incomingByPlugin.get(plugin);
		if (channels == null) return Collections.emptySet();
		Set<PluginMessageListenerRegistration> result = new HashSet<>();
		for (String channel : channels) {
			Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
			if (regs != null) result.addAll(regs);
		}
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin, String channel) {
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
		if (regs == null) return Collections.emptySet();
		Set<PluginMessageListenerRegistration> result = new HashSet<>();
		for (PluginMessageListenerRegistration reg : regs) {
			if (reg.getPlugin().equals(plugin)) result.add(reg);
		}
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(String channel) {
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
		return regs != null ? Collections.unmodifiableSet(regs) : Collections.emptySet();
	}

	@Override
	public Set<String> getIncomingChannels() {
		return Collections.unmodifiableSet(incomingChannels);
	}

	@Override
	public Set<String> getIncomingChannels(Plugin plugin) {
		Set<String> channels = incomingByPlugin.get(plugin);
		return channels != null ? Collections.unmodifiableSet(channels) : Collections.emptySet();
	}

	@Override
	public Set<String> getOutgoingChannels() {
		return Collections.unmodifiableSet(outgoingChannels);
	}

	@Override
	public Set<String> getOutgoingChannels(Plugin plugin) {
		Set<String> channels = outgoingByPlugin.get(plugin);
		return channels != null ? Collections.unmodifiableSet(channels) : Collections.emptySet();
	}

	@Override
	public boolean isIncomingChannelRegistered(Plugin plugin, String channel) {
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
		if (regs == null) return false;
		for (PluginMessageListenerRegistration reg : regs) {
			if (reg.getPlugin().equals(plugin)) return true;
		}
		return false;
	}

	@Override
	public boolean isOutgoingChannelRegistered(Plugin plugin, String channel) {
		Set<String> channels = outgoingByPlugin.get(plugin);
		return channels != null && channels.contains(channel);
	}

	@Override
	public boolean isRegistrationValid(PluginMessageListenerRegistration registration) {
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(registration.getChannel());
		return regs != null && regs.contains(registration);
	}

	@Override
	public boolean isReservedChannel(String channel) {
		return channel.equals("REGISTER") || channel.equals("UNREGISTER");
	}

	@Override
	public PluginMessageListenerRegistration registerIncomingPluginChannel(Plugin plugin, String channel,
			PluginMessageListener listener) {
		incomingChannels.add(channel);
		incomingByPlugin.computeIfAbsent(plugin, k -> ConcurrentHashMap.newKeySet()).add(channel);
		PluginMessageListenerRegistration reg = new PluginMessageListenerRegistration(this, plugin, channel, listener);
		incomingRegistrations.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(reg);
		return reg;
	}

	@Override
	public void registerOutgoingPluginChannel(Plugin plugin, String channel) {
		outgoingChannels.add(channel);
		outgoingByPlugin.computeIfAbsent(plugin, k -> ConcurrentHashMap.newKeySet()).add(channel);
	}

	@Override
	public void unregisterIncomingPluginChannel(Plugin plugin) {
		Set<String> channels = incomingByPlugin.remove(plugin);
		if (channels != null) {
			for (String channel : channels) {
				Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
				if (regs != null) {
					regs.removeIf(r -> r.getPlugin().equals(plugin));
					if (regs.isEmpty()) {
						incomingRegistrations.remove(channel);
						incomingChannels.remove(channel);
					}
				}
			}
		}
	}

	@Override
	public void unregisterIncomingPluginChannel(Plugin plugin, String channel) {
		Set<String> channels = incomingByPlugin.get(plugin);
		if (channels != null) {
			channels.remove(channel);
			if (channels.isEmpty()) incomingByPlugin.remove(plugin);
		}
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
		if (regs != null) {
			regs.removeIf(r -> r.getPlugin().equals(plugin));
			if (regs.isEmpty()) {
				incomingRegistrations.remove(channel);
				incomingChannels.remove(channel);
			}
		}
	}

	@Override
	public void unregisterIncomingPluginChannel(Plugin plugin, String channel, PluginMessageListener listener) {
		Set<PluginMessageListenerRegistration> regs = incomingRegistrations.get(channel);
		if (regs != null) {
			regs.removeIf(r -> r.getPlugin().equals(plugin) && r.getListener().equals(listener));
			if (regs.isEmpty()) {
				incomingRegistrations.remove(channel);
				incomingChannels.remove(channel);
			}
		}
	}

	@Override
	public void unregisterOutgoingPluginChannel(Plugin plugin) {
		Set<String> channels = outgoingByPlugin.remove(plugin);
		if (channels != null) {
			outgoingChannels.removeAll(channels);
		}
	}

	@Override
	public void unregisterOutgoingPluginChannel(Plugin plugin, String channel) {
		Set<String> channels = outgoingByPlugin.get(plugin);
		if (channels != null) {
			channels.remove(channel);
			if (channels.isEmpty()) outgoingByPlugin.remove(plugin);
		}
		outgoingChannels.remove(channel);
	}
}
