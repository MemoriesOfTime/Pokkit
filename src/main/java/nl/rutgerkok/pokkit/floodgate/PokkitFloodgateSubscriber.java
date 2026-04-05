package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.event.PostOrder;
import org.geysermc.floodgate.api.event.FloodgateSubscriber;

import java.util.function.Consumer;

/**
 * Simple FloodgateSubscriber implementation.
 * Used by PokkitFloodgateEventBus for both annotated and programmatic subscriptions.
 */
final class PokkitFloodgateSubscriber<T> implements FloodgateSubscriber<T> {

	private final Class<T> eventClass;
	private final PostOrder order;
	private final boolean ignoreCancelled;
	private final Consumer<T> invoker;

	PokkitFloodgateSubscriber(Class<T> eventClass, PostOrder order, boolean ignoreCancelled, Consumer<T> invoker) {
		this.eventClass = eventClass;
		this.order = order;
		this.ignoreCancelled = ignoreCancelled;
		this.invoker = invoker;
	}

	@Override
	public Class<T> eventClass() {
		return eventClass;
	}

	@Override
	public PostOrder order() {
		return order;
	}

	@Override
	public boolean ignoreCancelled() {
		return ignoreCancelled;
	}

	@Override
	public void invoke(T event) {
		invoker.accept(event);
	}
}
