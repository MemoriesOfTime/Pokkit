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

	@SuppressWarnings("deprecation")
	public static PotionEffect toBukkit(cn.nukkit.potion.Effect nukkitEffect) {
		return new PotionEffect(
			PotionEffectType.getById(nukkitEffect.getId()),
			nukkitEffect.getDuration(),
			nukkitEffect.getAmplifier(),
			nukkitEffect.isAmbient(),
			nukkitEffect.isVisible()
		);
	}
}
