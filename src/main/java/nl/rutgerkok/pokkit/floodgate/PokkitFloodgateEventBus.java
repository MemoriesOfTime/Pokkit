package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.event.FireResult;
import org.geysermc.event.PostOrder;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.event.subscribe.Subscriber;
import org.geysermc.floodgate.api.event.FloodgateEventBus;
import org.geysermc.floodgate.api.event.FloodgateSubscriber;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Simple FloodgateEventBus implementation for Pokkit.
 * Supports basic subscribe, fire, and unregister operations.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class PokkitFloodgateEventBus implements FloodgateEventBus {

	private final Map<Class<?>, Set<FloodgateSubscriber>> subscribers = new ConcurrentHashMap<>();

	@Override
	public void register(Object listener) {
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Subscribe subscribe = method.getAnnotation(Subscribe.class);
			if (subscribe == null) {
				continue;
			}
			Class<?>[] params = method.getParameterTypes();
			if (params.length != 1) {
				continue;
			}
			Class<?> eventClass = params[0];
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			Consumer<Object> invoker = event -> {
				try {
					method.invoke(listener, event);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			};
			FloodgateSubscriber sub = new PokkitFloodgateSubscriber(
					eventClass, subscribe.postOrder(), subscribe.ignoreCancelled(), invoker);
			subscribers.computeIfAbsent(eventClass, k -> new CopyOnWriteArraySet<>()).add(sub);
		}
	}

	@Override
	public FloodgateSubscriber subscribe(Class eventClass, Consumer consumer) {
		return subscribeInternal(eventClass, consumer, PostOrder.NORMAL);
	}

	@Override
	public FloodgateSubscriber subscribe(Class eventClass, Consumer consumer, PostOrder order) {
		return subscribeInternal(eventClass, consumer, order);
	}

	private FloodgateSubscriber subscribeInternal(Class eventClass, Consumer consumer, PostOrder order) {
		FloodgateSubscriber sub = new PokkitFloodgateSubscriber<>(eventClass, order, false, consumer);
		subscribers.computeIfAbsent(eventClass, k -> new CopyOnWriteArraySet<>()).add(sub);
		return sub;
	}

	@Override
	public void unregisterAll() {
		subscribers.clear();
	}

	@Override
	public void unsubscribe(FloodgateSubscriber subscriber) {
		Set<FloodgateSubscriber> set = subscribers.get(subscriber.eventClass());
		if (set != null) {
			set.remove(subscriber);
		}
	}

	@Override
	public FireResult fire(Object event) {
		Map<Subscriber<?>, Throwable> exceptions = null;
		Set<FloodgateSubscriber> set = subscribers.get(event.getClass());
		if (set != null) {
			for (FloodgateSubscriber sub : set) {
				try {
					sub.invoke(event);
				} catch (Throwable t) {
					if (exceptions == null) {
						exceptions = new LinkedHashMap<>();
					}
					exceptions.put(sub, t);
				}
			}
		}
		return exceptions != null ? FireResult.resultFor(exceptions) : FireResult.ok();
	}

	@Override
	public FireResult fireSilently(Object event) {
		Set<FloodgateSubscriber> set = subscribers.get(event.getClass());
		if (set != null) {
			for (FloodgateSubscriber sub : set) {
				try {
					sub.invoke(event);
				} catch (Throwable ignored) {
				}
			}
		}
		return FireResult.ok();
	}

	@Override
	public Set subscribers(Class eventClass) {
		Set<FloodgateSubscriber> set = subscribers.get(eventClass);
		return set != null ? Collections.unmodifiableSet(set) : Collections.emptySet();
	}
}
