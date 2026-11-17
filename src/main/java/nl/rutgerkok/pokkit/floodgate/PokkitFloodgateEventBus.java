package nl.rutgerkok.pokkit.floodgate;

import org.geysermc.event.Cancellable;
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
 * <p>
 * Events are delivered to subscribers of the event's own class as well as to
 * subscribers of its superclasses and interfaces, so subscribing to
 * {@code SkinApplyEvent.class} (an interface) works like it does in Floodgate.
 * Subscribers are called in {@link PostOrder} order, like in Floodgate.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class PokkitFloodgateEventBus implements FloodgateEventBus {

	private final Map<Class<?>, Set<FloodgateSubscriber>> subscribers = new ConcurrentHashMap<>();

	@Override
	public void register(Object listener) {
		Set<String> overridden = new HashSet<>();
		for (Class<?> type = listener.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
			for (Method method : type.getDeclaredMethods()) {
				// A method that is overridden lower in the hierarchy has
				// already been handled: registering the superclass method too
				// would invoke the listener twice.
				if (!overridden.add(method.getName() + Arrays.toString(method.getParameterTypes()))) {
					continue;
				}
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
		for (FloodgateSubscriber sub : matchingSubscribers(event.getClass())) {
			if (skipsCancelled(sub, event)) {
				continue;
			}
			try {
				sub.invoke(event);
			} catch (Throwable t) {
				if (exceptions == null) {
					exceptions = new LinkedHashMap<>();
				}
				exceptions.put(sub, t);
			}
		}
		return exceptions != null ? FireResult.resultFor(exceptions) : FireResult.ok();
	}

	@Override
	public FireResult fireSilently(Object event) {
		for (FloodgateSubscriber sub : matchingSubscribers(event.getClass())) {
			if (skipsCancelled(sub, event)) {
				continue;
			}
			try {
				sub.invoke(event);
			} catch (Throwable ignored) {
			}
		}
		return FireResult.ok();
	}

	@Override
	public Set subscribers(Class eventClass) {
		return Collections.unmodifiableSet(new LinkedHashSet<>(matchingSubscribers(eventClass)));
	}

	private static boolean skipsCancelled(FloodgateSubscriber sub, Object event) {
		return sub.ignoreCancelled() && event instanceof Cancellable cancellable && cancellable.isCancelled();
	}

	/**
	 * Collects the subscribers of the event's class, its superclasses and all
	 * interfaces it implements.
	 */
	private List<FloodgateSubscriber> matchingSubscribers(Class<?> eventClass) {
		List<FloodgateSubscriber> result = new ArrayList<>();
		Set<Class<?>> seen = new LinkedHashSet<>();
		Deque<Class<?>> queue = new ArrayDeque<>();
		queue.add(eventClass);
		while (!queue.isEmpty()) {
			Class<?> type = queue.poll();
			if (type == null || type == Object.class || !seen.add(type)) {
				continue;
			}
			Set<FloodgateSubscriber> set = subscribers.get(type);
			if (set != null) {
				result.addAll(set);
			}
			if (type.getSuperclass() != null) {
				queue.add(type.getSuperclass());
			}
			queue.addAll(Arrays.asList(type.getInterfaces()));
		}
		// Floodgate calls the FIRST subscribers before the LAST ones. List.sort
		// is stable, so subscribers with the same order keep the order in which
		// they were registered.
		result.sort(Comparator.comparingInt(sub -> sub.order().ordinal()));
		return result;
	}
}
