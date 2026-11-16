package nl.rutgerkok.pokkit.potion;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cn.nukkit.potion.Effect;

public class PokkitPotionEffect {

	public static Effect toNukkit(PotionEffect bukkitEffect) {
		cn.nukkit.potion.Effect nukkitEffect = PokkitPotionEffectType.toNukkit(bukkitEffect.getType());
		nukkitEffect.setAmbient(bukkitEffect.isAmbient());
		nukkitEffect.setAmplifier(bukkitEffect.getAmplifier());
		nukkitEffect.setDuration(bukkitEffect.getDuration());
		Color color = bukkitEffect.getType().getColor();
		nukkitEffect.setColor(color.getRed(), color.getGreen(), color.getBlue());
		nukkitEffect.setVisible(bukkitEffect.hasParticles());
		return nukkitEffect;
	}

	/**
	 * Converts a Nukkit effect to a Bukkit effect.
	 *
	 * @param nukkitEffect
	 *            The Nukkit effect.
	 * @return The Bukkit effect, or null when Bukkit has no equivalent for the
	 *         Bedrock effect (fatal poison, for example).
	 */
	public static PotionEffect toBukkit(cn.nukkit.potion.Effect nukkitEffect) {
		PotionEffectType type = PokkitPotionEffectType.fromNukkitId(nukkitEffect.getId());
		if (type == null) {
			return null;
		}
		return new PotionEffect(
			type,
			nukkitEffect.getDuration(),
			nukkitEffect.getAmplifier(),
			nukkitEffect.isAmbient(),
			nukkitEffect.isVisible()
		);
	}
}
