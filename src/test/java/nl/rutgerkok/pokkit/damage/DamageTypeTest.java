package nl.rutgerkok.pokkit.damage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.damage.DeathMessageType;
import org.junit.BeforeClass;
import org.junit.Test;

public class DamageTypeTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
	}

	@Test
	public void testRegistryIsComplete() {
		int count = 0;
		for (DamageType type : Registry.DAMAGE_TYPE) {
			assertNotNull(type.getKey());
			assertNotNull(type.getTranslationKey());
			assertNotNull(type.getDamageEffect());
			assertNotNull(type.getDeathMessageType());
			count++;
		}
		// DamageType has 50 constants; the static initialiser of DamageType
		// throws when one of them is missing.
		assertEquals(50, count);
	}

	@Test
	public void testConstantsCanBeUsed() {
		assertNotNull(DamageType.IN_FIRE);
		assertNotNull(DamageType.GENERIC_KILL);
		assertNotNull(DamageType.PLAYER_ATTACK);
		assertEquals("minecraft:in_fire", DamageType.IN_FIRE.getKey().toString());
		assertNotNull(DamageEffect.HURT);
		assertNotNull(DamageEffect.THORNS);
	}

	@Test
	public void testVanillaValues() {
		assertEquals("death.attack.inFire", DamageType.IN_FIRE.getTranslationKey());
		assertEquals(0.1f, DamageType.IN_FIRE.getExhaustion(), 0.0001f);
		assertEquals(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, DamageType.IN_FIRE.getDamageScaling());
		assertSame(PokkitDamageEffect.BURNING, DamageType.IN_FIRE.getDamageEffect());

		// Not every damage type uses the key as the message id.
		assertEquals("death.attack.explosion.player", DamageType.PLAYER_EXPLOSION.getTranslationKey());
		assertEquals("death.attack.mob", DamageType.MOB_ATTACK.getTranslationKey());
		assertEquals("death.attack.anvil", DamageType.FALLING_ANVIL.getTranslationKey());

		assertEquals(0.0f, DamageType.GENERIC_KILL.getExhaustion(), 0.0001f);
		assertEquals(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, DamageType.GENERIC_KILL.getDamageScaling());
		assertEquals(DamageScaling.ALWAYS, DamageType.EXPLOSION.getDamageScaling());
		assertEquals(DamageScaling.ALWAYS, DamageType.SONIC_BOOM.getDamageScaling());
		assertEquals(0.0f, DamageType.SONIC_BOOM.getExhaustion(), 0.0001f);

		assertEquals(DeathMessageType.FALL_VARIANTS, DamageType.FALL.getDeathMessageType());
		assertEquals(DeathMessageType.INTENTIONAL_GAME_DESIGN,
				DamageType.BAD_RESPAWN_POINT.getDeathMessageType());
		assertEquals(DeathMessageType.DEFAULT, DamageType.IN_FIRE.getDeathMessageType());

		assertSame(PokkitDamageEffect.DROWNING, DamageType.DROWN.getDamageEffect());
		assertSame(PokkitDamageEffect.FREEZING, DamageType.FREEZE.getDamageEffect());
		assertSame(PokkitDamageEffect.POKING, DamageType.CACTUS.getDamageEffect());
		assertSame(PokkitDamageEffect.THORNS, DamageType.THORNS.getDamageEffect());
	}

	@Test
	public void testDamageSourceBuilder() {
		DamageSource source = DamageSource.builder(DamageType.IN_FIRE).build();
		assertEquals(DamageType.IN_FIRE, source.getDamageType());
		assertFalse(source.isIndirect());
		assertEquals(0.1f, source.getFoodExhaustion(), 0.0001f);
		// in_fire only scales when a living non-player caused it, and no
		// entity was set here.
		assertFalse(source.scalesWithDifficulty());
		// Explosions always scale with difficulty.
		assertTrue(DamageSource.builder(DamageType.EXPLOSION).build().scalesWithDifficulty());
	}
}
