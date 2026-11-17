package nl.rutgerkok.pokkit.floodgate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.geysermc.event.Cancellable;
import org.geysermc.event.FireResult;
import org.geysermc.event.PostOrder;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.InstanceHolder;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.junit.Test;

/**
 * Tests the parts of the Floodgate (GeyserMC) integration that do not need a
 * running server. Everything that touches a live {@code cn.nukkit.Player} is
 * covered by the Docker integration test instead.
 */
public class FloodgateApiTest {

	/** A plain event, used to check the event bus. */
	private static final class SimpleEvent {
	}

	/** An event that implements an interface, like {@code SkinApplyEvent}. */
	private interface SkinLikeEvent {
	}

	private static final class SkinLikeEventImpl implements SkinLikeEvent {
	}

	/** A cancellable event, like {@code SkinApplyEvent}. */
	private static final class CancellableEvent implements Cancellable {
		private boolean cancelled;

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}
	}

	@Test
	public void testCreateJavaPlayerId() {
		PokkitFloodgateApi api = new PokkitFloodgateApi();

		UUID id = api.createJavaPlayerId(2535412345678901L);
		assertEquals(new UUID(0, 2535412345678901L), id);
		assertTrue(api.isFloodgateId(id));
		assertFalse(api.isFloodgateId(UUID.randomUUID()));
		assertFalse(api.isFloodgateId(null));
	}

	@Test
	public void testPlayerLookupWithoutPlayers() {
		PokkitFloodgateApi api = new PokkitFloodgateApi();

		assertEquals(0, api.getPlayerCount());
		assertTrue(api.getPlayers().isEmpty());
		assertFalse(api.isFloodgatePlayer(UUID.randomUUID()));
		assertFalse(api.isFloodgatePlayer(null));
		assertNull(api.getPlayer(UUID.randomUUID()));
		assertNull(api.getPlayer(null));
		assertEquals(".", api.getPlayerPrefix());
	}

	@Test
	public void testFormsAndTransfersForUnknownPlayerReturnFalse() {
		PokkitFloodgateApi api = new PokkitFloodgateApi();
		UUID unknown = UUID.randomUUID();

		assertFalse(api.closeForm(unknown));
		assertFalse(api.transferPlayer(unknown, "127.0.0.1", 19132));
		assertFalse(api.sendForm(unknown, org.geysermc.cumulus.form.SimpleForm.builder().title("t")));
		assertFalse(api.closeForm(null));
	}

	@Test
	public void testPlayerLinkIsDisabled() {
		PokkitPlayerLink link = new PokkitPlayerLink();

		assertFalse(link.isEnabled());
		assertFalse(link.isAllowLinking());
		link.load();
		link.stop();
		assertEquals("PokkitNoLink", link.getName());
		assertFalse(link.isLinkedPlayer(UUID.randomUUID()).join());
		assertNull(link.getLinkedPlayer(UUID.randomUUID()).join());
	}

	@Test
	public void testNoOpHandlersDoNotThrow() {
		PokkitUnsafe unsafe = new PokkitUnsafe();
		unsafe.sendPacket(UUID.randomUUID(), 1, new byte[0]);
		unsafe.sendPacket((FloodgatePlayer) null, 1, new byte[0]);

		PokkitPacketHandlers packetHandlers = new PokkitPacketHandlers();
		packetHandlers.registerAll(null);
		packetHandlers.deregister(null);

		PokkitHandshakeHandlers handshakeHandlers = new PokkitHandshakeHandlers();
		assertEquals(-1, handshakeHandlers.addHandshakeHandler(null));
		handshakeHandlers.removeHandshakeHandler(1);
		handshakeHandlers.removeHandshakeHandler(null);
	}

	@Test
	public void testEventBusProgrammaticSubscription() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		AtomicInteger calls = new AtomicInteger();

		var subscriber = bus.subscribe(SimpleEvent.class, event -> calls.incrementAndGet());
		bus.fire(new SimpleEvent());
		assertEquals(1, calls.get());

		bus.unsubscribe(subscriber);
		bus.fire(new SimpleEvent());
		assertEquals(1, calls.get());
	}

	@Test
	public void testEventBusAnnotatedListener() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		AtomicInteger calls = new AtomicInteger();

		class Listener {
			@Subscribe(postOrder = PostOrder.LATE)
			public void onEvent(SimpleEvent event) {
				calls.incrementAndGet();
			}
		}

		bus.register(new Listener());
		bus.fire(new SimpleEvent());
		assertEquals(1, calls.get());
		assertEquals(1, bus.subscribers(SimpleEvent.class).size());
	}

	@Test
	public void testEventBusDoesNotInvokeOverriddenListenerTwice() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		AtomicInteger calls = new AtomicInteger();

		class Base {
			@Subscribe
			public void onEvent(SimpleEvent event) {
				calls.incrementAndGet();
			}
		}
		class Child extends Base {
			@Override
			@Subscribe
			public void onEvent(SimpleEvent event) {
				calls.incrementAndGet();
			}
		}

		bus.register(new Child());
		bus.fire(new SimpleEvent());
		assertEquals(1, calls.get());
	}

	@Test
	public void testEventBusDispatchesToInterfaceSubscribers() {
		// SkinApplyEvent is an interface; Floodgate fires implementation
		// objects, so subscribers of the interface must be called.
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		AtomicInteger calls = new AtomicInteger();

		bus.subscribe(SkinLikeEvent.class, event -> calls.incrementAndGet());
		bus.fire(new SkinLikeEventImpl());
		assertEquals(1, calls.get());
	}

	@Test
	public void testEventBusCallsSubscribersInPostOrder() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		java.util.List<String> calls = new java.util.ArrayList<>();

		// Registered out of order on purpose: Floodgate calls FIRST before
		// NORMAL before LAST, regardless of registration order.
		bus.subscribe(SimpleEvent.class, event -> calls.add("late"), PostOrder.LATE);
		bus.subscribe(SimpleEvent.class, event -> calls.add("first"), PostOrder.FIRST);
		bus.subscribe(SimpleEvent.class, event -> calls.add("normal"), PostOrder.NORMAL);

		bus.fire(new SimpleEvent());
		assertEquals(java.util.Arrays.asList("first", "normal", "late"), calls);
	}

	@Test
	public void testEventBusHonoursIgnoreCancelled() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		AtomicInteger normal = new AtomicInteger();
		AtomicInteger ignoring = new AtomicInteger();

		class Listener {
			@Subscribe(ignoreCancelled = false)
			public void onEveryEvent(CancellableEvent event) {
				normal.incrementAndGet();
			}

			@Subscribe(ignoreCancelled = true)
			public void onUncancelledEvent(CancellableEvent event) {
				ignoring.incrementAndGet();
			}
		}

		bus.register(new Listener());

		CancellableEvent cancelled = new CancellableEvent();
		cancelled.setCancelled(true);
		bus.fire(cancelled);
		assertEquals(1, normal.get());
		assertEquals(0, ignoring.get());

		bus.fire(new CancellableEvent());
		assertEquals(2, normal.get());
		assertEquals(1, ignoring.get());
	}

	@Test
	public void testEventBusReportsSubscriberExceptions() {
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();
		bus.subscribe(SimpleEvent.class, event -> {
			throw new IllegalStateException("boom");
		});

		FireResult result = bus.fire(new SimpleEvent());
		assertFalse(result.success());
		assertEquals(1, result.exceptions().size());

		// The silently variant must swallow the exception.
		assertTrue(bus.fireSilently(new SimpleEvent()).success());
	}

	@Test
	public void testInstanceHolderExposesPokkitImplementations() {
		PokkitFloodgateApi api = new PokkitFloodgateApi();
		PokkitPlayerLink link = new PokkitPlayerLink();
		PokkitFloodgateEventBus bus = new PokkitFloodgateEventBus();

		assertTrue(InstanceHolder.set(api, link, bus, null, new PokkitPacketHandlers(),
				new PokkitHandshakeHandlers(), new UUID(0, 0)));

		assertSame(api, FloodgateApi.getInstance());
		assertSame(bus, FloodgateApi.getInstance().getEventBus());
		assertSame(link, FloodgateApi.getInstance().getPlayerLink());
		assertSame(api.unsafe(), FloodgateApi.getInstance().unsafe());
	}

	@Test
	public void testCumulusFormWindowPassesJsonAndResponse() {
		AtomicInteger responses = new AtomicInteger();
		CumulusFormWindow window = new CumulusFormWindow("{\"type\":\"form\"}", response -> {
			assertEquals("null", response);
			responses.incrementAndGet();
		});

		assertEquals("{\"type\":\"form\"}", window.getJSONData(cn.nukkit.GameVersion.V1_21_0));

		// A closed form is reported as "null" by the client.
		window.setResponse(0, "null");
		assertEquals(1, responses.get());
		assertNull(window.getResponse());
	}
}
