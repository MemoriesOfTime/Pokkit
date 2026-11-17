package nl.rutgerkok.pokkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoundAndAttributeRegistryTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
	}

	@Test
	public void testSoundConstantsAreBackedByRegistry() {
		// Sound is an interface since 1.21.11 and fills its constants from
		// Registry.SOUNDS. Touch a constant to prove the registry works and
		// does not deadlock during interface initialisation.
		assertNotNull(Sound.ENTITY_PLAYER_HURT);
		assertEquals("entity.player.hurt", Sound.ENTITY_PLAYER_HURT.getKey().getKey());
		assertSame(Sound.ENTITY_PLAYER_HURT, Registry.SOUNDS.get(Sound.ENTITY_PLAYER_HURT.getKey()));
	}

	@Test
	public void testSoundConversionStillWorks() {
		assertNotNull(PokkitSound.toNukkit(null, Sound.BLOCK_ANVIL_BREAK, 1.0f));
	}

	@Test
	public void testAttributeConstantsAreBackedByRegistry() {
		assertNotNull(Attribute.MAX_HEALTH);
		assertEquals("max_health", Attribute.MAX_HEALTH.getKey().getKey());
		assertSame(Attribute.MAX_HEALTH, Registry.ATTRIBUTE.get(Attribute.MAX_HEALTH.getKey()));
	}

	@Test
	public void testBiomeConstantsAreBackedByRegistry() {
		assertNotNull(org.bukkit.block.Biome.OCEAN);
		assertEquals("ocean", org.bukkit.block.Biome.OCEAN.getKey().getKey());
		assertSame(org.bukkit.block.Biome.OCEAN, Registry.BIOME.get(org.bukkit.block.Biome.OCEAN.getKey()));
		assertSame(org.bukkit.block.Biome.OCEAN, nl.rutgerkok.pokkit.world.biome.PokkitBiome.toBukkit(0));
	}

	@Test
	public void testPotionEffectConstantsAreBackedByRegistry() {
		assertNotNull(org.bukkit.potion.PotionEffectType.SPEED);
		assertEquals("speed", org.bukkit.potion.PotionEffectType.SPEED.getKey().getKey());
		assertSame(org.bukkit.potion.PotionEffectType.SPEED,
				Registry.EFFECT.get(org.bukkit.potion.PotionEffectType.SPEED.getKey()));
		// Added in 1.21.11; PotionEffectType fails to initialise without it.
		assertNotNull(org.bukkit.potion.PotionEffectType.BREATH_OF_THE_NAUTILUS);
	}
}
