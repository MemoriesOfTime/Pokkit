package nl.rutgerkok.pokkit.particle;

import org.bukkit.Particle;

public class PokkitParticle {
	public static int toNukkit(Particle particle) {

		switch(particle) {
		case BARRIER:
			break;
		case BLOCK_CRACK:
			break;
		case BLOCK_DUST:
			return cn.nukkit.level.particle.Particle.TYPE_DUST;
		case CLOUD:
			return cn.nukkit.level.particle.Particle.TYPE_SMOKE;
		case CRIT:
			return cn.nukkit.level.particle.Particle.TYPE_CRITICAL;
		case CRIT_MAGIC:
			return cn.nukkit.level.particle.Particle.TYPE_CRITICAL;
		case DAMAGE_INDICATOR:
			break;
		case DRAGON_BREATH:
			return cn.nukkit.level.particle.Particle.TYPE_DRAGON_BREATH;
		case DRIP_LAVA:
			return cn.nukkit.level.particle.Particle.TYPE_DRIP_LAVA;
		case DRIP_WATER:
			return cn.nukkit.level.particle.Particle.TYPE_DRIP_WATER;
		case ENCHANTMENT_TABLE:
			return cn.nukkit.level.particle.Particle.TYPE_ENCHANTMENT_TABLE;
		case END_ROD:
			return cn.nukkit.level.particle.Particle.TYPE_END_ROD;
		case EXPLOSION_HUGE:
			return cn.nukkit.level.particle.Particle.TYPE_HUGE_EXPLODE;
		case EXPLOSION_LARGE:
			return cn.nukkit.level.particle.Particle.TYPE_HUGE_EXPLODE;
		case EXPLOSION_NORMAL:
			return cn.nukkit.level.particle.Particle.TYPE_EXPLODE;
		case FALLING_DUST:
			return cn.nukkit.level.particle.Particle.TYPE_DUST;
		case FIREWORKS_SPARK:
			return cn.nukkit.level.particle.Particle.TYPE_FIREWORKS_SPARK;
		case FLAME:
			return cn.nukkit.level.particle.Particle.TYPE_FLAME;
		case HEART:
			return cn.nukkit.level.particle.Particle.TYPE_HEART;
		case ITEM_CRACK:
			return cn.nukkit.level.particle.Particle.TYPE_ITEM_BREAK;
		case LAVA:
			return cn.nukkit.level.particle.Particle.TYPE_LAVA;
		case MOB_APPEARANCE:
			break;
		case NOTE:
			return cn.nukkit.level.particle.Particle.TYPE_NOTE;
		case PORTAL:
			return cn.nukkit.level.particle.Particle.TYPE_PORTAL;
		case REDSTONE:
			return cn.nukkit.level.particle.Particle.TYPE_REDSTONE;
		case SLIME:
			return cn.nukkit.level.particle.Particle.TYPE_SLIME;
		case SMOKE_LARGE:
			return cn.nukkit.level.particle.Particle.TYPE_LARGE_SMOKE;
		case SMOKE_NORMAL:
			return cn.nukkit.level.particle.Particle.TYPE_SMOKE;
		case SNOWBALL:
			return cn.nukkit.level.particle.Particle.TYPE_SNOWBALL_POOF;
		case SNOW_SHOVEL:
			return cn.nukkit.level.particle.Particle.TYPE_SNOWBALL_POOF;
		case SPELL:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL;
		case SPELL_INSTANT:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL_INSTANTANEOUS;
		case SPELL_MOB:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL;
		case SPELL_MOB_AMBIENT:
			return cn.nukkit.level.particle.Particle.TYPE_MOB_SPELL_AMBIENT;
		case SPELL_WITCH:
			return cn.nukkit.level.particle.Particle.TYPE_WITCH_SPELL;
		case SUSPENDED:
			break;
		case SUSPENDED_DEPTH:
			break;
		case SWEEP_ATTACK:
			break;
		case TOWN_AURA:
			return cn.nukkit.level.particle.Particle.TYPE_TOWN_AURA;
		case VILLAGER_ANGRY:
			return cn.nukkit.level.particle.Particle.TYPE_VILLAGER_ANGRY;
		case VILLAGER_HAPPY:
			return cn.nukkit.level.particle.Particle.TYPE_VILLAGER_HAPPY;
		case WATER_BUBBLE:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE;
		case WATER_DROP:
			return cn.nukkit.level.particle.Particle.TYPE_RAIN_SPLASH;
		case WATER_SPLASH:
			return cn.nukkit.level.particle.Particle.TYPE_WATER_SPLASH;
		case WATER_WAKE:
			return cn.nukkit.level.particle.Particle.TYPE_WATER_WAKE;
		case BUBBLE_COLUMN_UP:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE_COLUMN_UP;
		case BUBBLE_POP:
			return cn.nukkit.level.particle.Particle.TYPE_BUBBLE;
		case CURRENT_DOWN:
			break;
		case DOLPHIN:
			break;
		case LEGACY_BLOCK_CRACK:
			break;
		case LEGACY_BLOCK_DUST:
			return cn.nukkit.level.particle.Particle.TYPE_DUST;
		case LEGACY_FALLING_DUST:
			return cn.nukkit.level.particle.Particle.TYPE_FALLING_DUST;
		case NAUTILUS:
			break;
		case SPIT:
			return cn.nukkit.level.particle.Particle.TYPE_SPIT;
		case SQUID_INK:
			return cn.nukkit.level.particle.Particle.TYPE_INK;
		case TOTEM:
			return cn.nukkit.level.particle.Particle.TYPE_TOTEM;
		default:
			break;
		}

		return 0;
	}
}
