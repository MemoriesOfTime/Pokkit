package nl.rutgerkok.pokkit.enchantment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl.rutgerkok.pokkit.item.PokkitItemFactory;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.craftbukkit.v1_99_R9.CraftServers;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnchantmentMetaTest {

	@BeforeClass
	public static void setup() {
		new CraftServers();
		cn.nukkit.item.enchantment.Enchantment.init();
		PokkitEnchantment.registerNukkitEnchantmentsInBukkit();
	}

	private final PokkitItemFactory factory = new PokkitItemFactory();

	@Test
	public void testItemMeta() {
		ItemMeta itemMeta = factory.getItemMeta(Material.DIAMOND_PICKAXE);

		// Test initial state
		assertFalse(itemMeta.hasEnchants());
		assertFalse(itemMeta.hasEnchant(Enchantment.UNBREAKING));

		// Add an enchantment, test state
		assertTrue(itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true));
		assertTrue(itemMeta.hasEnchants());
		assertTrue(itemMeta.hasEnchant(Enchantment.UNBREAKING));
		assertFalse(itemMeta.hasEnchant(Enchantment.FIRE_ASPECT));

		// Remove it again, test state
		assertTrue(itemMeta.removeEnchant(Enchantment.UNBREAKING));
		assertFalse("Removing twice", itemMeta.removeEnchant(Enchantment.UNBREAKING));
		assertFalse(itemMeta.hasEnchants());
		assertFalse(itemMeta.hasEnchant(Enchantment.UNBREAKING));
	}
}
