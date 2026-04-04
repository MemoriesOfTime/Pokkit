package nl.rutgerkok.pokkit.enchantment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.bukkit.enchantments.Enchantment;
import org.junit.BeforeClass;
import org.junit.Test;

public class PokkitEnchantmentTest {

	// Bukkit enchantments that have no Nukkit equivalent
	private static final Set<NamespacedKey> SKIP_BUKKIT_TO_NUKKIT = Set.of(
			NamespacedKey.minecraft("sweeping_edge")
	);

	@BeforeClass
	public static void setup() {
		new CraftServers();
		cn.nukkit.item.enchantment.Enchantment.init();
		PokkitEnchantment.registerNukkitEnchantmentsInBukkit();
	}

	@Test
	public void testBukkitToNukkit() {
		int tested = 0;

		for (Enchantment enchantment : Enchantment.values()) {
			if (SKIP_BUKKIT_TO_NUKKIT.contains(enchantment.getKey())) {
				continue;
			}
			assertTrue("Type " + enchantment + " must have a mapping", PokkitEnchantment.toNukkit(enchantment) != -1);
			tested++;
		}

		assertTrue("Didn't test any enchantments", tested > 0);
	}

	@Test
	public void testNukkitToBukkit() {
		for (cn.nukkit.item.enchantment.Enchantment enchantment : cn.nukkit.item.enchantment.Enchantment
				.getEnchantments()) {
			assertNotNull("Type " + enchantment + " must have a mapping", PokkitEnchantment.toBukkit(enchantment.id));
		}
	}
}
