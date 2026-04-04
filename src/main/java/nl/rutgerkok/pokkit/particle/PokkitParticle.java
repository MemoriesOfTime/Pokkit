package nl.rutgerkok.pokkit.particle;

import org.bukkit.Particle;

public class PokkitParticle {
	public static int toNukkit(Particle particle) {

		switch(particle) {
		case BLOCK_MARKER:
			break;
		case BLOCK:
			break;
		case CLOUD:
			return cn.nukkit.level.particle.Particle.TYPE_SMOKE;
		case CRIT:
			return cn.nukkit.level.particle.Particle.TYPE_CRITICAL;
		case ENCHANTED_HIT:
			return cn.nukkit.level.particle.Particle.TYPE_CRITICAL;
		case DAMAGE_INDICATOR:
			break;
		case DRAGON_BREATH:
			return cn.nukkit.level.particle.Particle.TYPE_DRAGONS_BREATH;
		case DRIPPING_LAVA:
			return cn.nukkit.level.particle.Particle.TYPE_DRIP_LAVA;
		case DRIPPING_WATER:
			return cn.nukkit.level.particle.Particle.TYPE_DRIP_WATER;
		case ENCHANT:
			return cn.nukkit.level.particle.Particle.TYPE_ENCHANTMENT_TABLE;
		case END_ROD:
			return cn.nukkit.level.particle.Particle.TYPE_END_ROD;
		case EXPLOSION_EMITTER:
			return cn.nukkit.level.particle.Particle.TYPE_HUGE_EXPLODE;
		case EXPLOSION:
			return cn.nukkit.level.particle.Particle.TYPE_HUGE_EXPLODE;
		case POOF:
			return cn.nukkit.level.particle.Particle.TYPE_EXPLODE;
		case FALLING_DUST:
			return cn.nukkit.level.particle.Particle.TYPE_DUST;
		case FIREWORK:
			return cn.nukkit.level.particle.Particle.TYPE_FIREWORKS_SPARK;
		case FLAME:
			return cn.nukkit.level.particle.Particle.TYPE_FLAME;
		case HEART:
			return cn.nukkit.level.particle.Particle.TYPE_HEART;
		case ITEM:
			return cn.nukkit.level.particle.Particle.TYPE_ITEM_BREAK;
		case LAVA:
			return cn.nukkit.level.particle.Particle.TYPE_LAVA;
		case ELDER_GUARDIAN:
			break;
		case NOTE:
			return cn.nukkit.level.particle.Particle.TYPE_NOTE;
		case PORTAL:
			return cn.nukkit.level.particle.Particle.TYPE_PORTAL;
		case DUST:
			return cn.nukkit.level.particle.Particle.TYPE_REDSTONE;
		case ITEM_SLIME:
			return cn.nukkit.level.particle.Particle.TYPE_SLIME;
		case LARGE_SMOKE:
			return cn.nukkit.level.particle.Particle.TYPE_LARGE_SMOKE;
		case SMOKE:
			return cn.nukkit.level.particle.Particle.TYPE_SMOKE;
		case ITEM_SNOWBALL:
			return cn.nukkit.level.particle.Particle.TYPE_SNOWBALL_POOF;
		case EFFECT:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL;
		case INSTANT_EFFECT:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL_INSTANTANEOUS;
		case ENTITY_EFFECT:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL_AMBIENT;
		case WITCH:
			return cn.nukkit.level.particle.Particle.TYPE_WITCH_SPELL;
		case UNDERWATER:
			break;
		case SWEEP_ATTACK:
			break;
		case MYCELIUM:
			return cn.nukkit.level.particle.Particle.TYPE_TOWN_AURA;
		case ANGRY_VILLAGER:
			return cn.nukkit.level.particle.Particle.TYPE_VILLAGER_ANGRY;
		case HAPPY_VILLAGER:
			return cn.nukkit.level.particle.Particle.TYPE_VILLAGER_HAPPY;
		case BUBBLE:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE;
		case RAIN:
			return cn.nukkit.level.particle.Particle.TYPE_RAIN_SPLASH;
		case SPLASH:
			return cn.nukkit.level.particle.Particle.TYPE_WATER_SPLASH;
		case FISHING:
			return cn.nukkit.level.particle.Particle.TYPE_WATER_WAKE;
		case BUBBLE_COLUMN_UP:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE_COLUMN_UP;
		case BUBBLE_POP:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE;
		case CURRENT_DOWN:
			break;
		case DOLPHIN:
			break;
		case NAUTILUS:
			break;
		case SPIT:
			return cn.nukkit.level.particle.Particle.TYPE_SPIT;
		case SQUID_INK:
			return cn.nukkit.level.particle.Particle.TYPE_INK;
		case TOTEM_OF_UNDYING:
			return cn.nukkit.level.particle.Particle.TYPE_TOTEM;
		default:
			break;
		}

		return 0;
	}
}
