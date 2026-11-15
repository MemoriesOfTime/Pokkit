package nl.rutgerkok.pokkit.damage;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageType;
import org.bukkit.damage.DeathMessageType;

/**
 * Damage type as exposed by {@link org.bukkit.Registry#DAMAGE_TYPE}. The values
 * are the vanilla 1.21 values, taken from the damage type registry of the
 * vanilla data pack.
 */
public final class PokkitDamageType implements DamageType {

	private static final Map<String, PokkitDamageType> BY_KEY = new LinkedHashMap<>();

	static {
		// key, scales with difficulty, exhaustion, message id, effect
		add("in_fire", false, 0.1f, "inFire", PokkitDamageEffect.BURNING);
		add("campfire", false, 0.1f, "inFire", PokkitDamageEffect.BURNING);
		add("lightning_bolt", false, 0.1f, "lightningBolt", PokkitDamageEffect.HURT);
		add("on_fire", false, 0.0f, "onFire", PokkitDamageEffect.BURNING);
		add("lava", false, 0.1f, "lava", PokkitDamageEffect.BURNING);
		add("hot_floor", false, 0.1f, "hotFloor", PokkitDamageEffect.BURNING);
		add("in_wall", false, 0.0f, "inWall", PokkitDamageEffect.HURT);
		add("cramming", false, 0.0f, "cramming", PokkitDamageEffect.HURT);
		add("drown", false, 0.0f, "drown", PokkitDamageEffect.DROWNING);
		add("starve", false, 0.0f, "starve", PokkitDamageEffect.HURT);
		add("cactus", false, 0.1f, "cactus", PokkitDamageEffect.POKING);
		add("fall", false, 0.0f, "fall", DeathMessageType.FALL_VARIANTS, PokkitDamageEffect.HURT);
		add("fly_into_wall", false, 0.0f, "flyIntoWall", PokkitDamageEffect.HURT);
		add("out_of_world", false, 0.0f, "outOfWorld", PokkitDamageEffect.HURT);
		add("generic", false, 0.0f, "generic", PokkitDamageEffect.HURT);
		add("magic", false, 0.0f, "magic", PokkitDamageEffect.HURT);
		add("wither", false, 0.0f, "wither", PokkitDamageEffect.HURT);
		add("dragon_breath", false, 0.0f, "dragonBreath", PokkitDamageEffect.HURT);
		add("dry_out", false, 0.1f, "dryout", PokkitDamageEffect.HURT);
		add("sweet_berry_bush", false, 0.1f, "sweetBerryBush", PokkitDamageEffect.POKING);
		add("freeze", false, 0.0f, "freeze", PokkitDamageEffect.FREEZING);
		add("stalagmite", false, 0.0f, "stalagmite", PokkitDamageEffect.POKING);
		add("falling_block", false, 0.1f, "fallingBlock", PokkitDamageEffect.HURT);
		add("falling_anvil", false, 0.1f, "anvil", PokkitDamageEffect.HURT);
		add("falling_stalactite", false, 0.1f, "fallingStalactite", PokkitDamageEffect.HURT);
		add("sting", false, 0.1f, "sting", PokkitDamageEffect.HURT);
		add("mob_attack", false, 0.1f, "mob", PokkitDamageEffect.HURT);
		add("mob_attack_no_aggro", false, 0.1f, "mob", PokkitDamageEffect.HURT);
		add("player_attack", false, 0.1f, "player", PokkitDamageEffect.HURT);
		add("arrow", false, 0.1f, "arrow", PokkitDamageEffect.HURT);
		add("trident", false, 0.1f, "trident", PokkitDamageEffect.HURT);
		add("mob_projectile", false, 0.1f, "mob", PokkitDamageEffect.HURT);
		add("spit", false, 0.1f, "mob", PokkitDamageEffect.HURT);
		add("fireworks", false, 0.1f, "fireworks", PokkitDamageEffect.HURT);
		add("fireball", false, 0.1f, "fireball", PokkitDamageEffect.BURNING);
		add("unattributed_fireball", false, 0.1f, "onFire", PokkitDamageEffect.BURNING);
		add("wither_skull", false, 0.1f, "witherSkull", PokkitDamageEffect.HURT);
		add("thrown", false, 0.1f, "thrown", PokkitDamageEffect.HURT);
		add("indirect_magic", false, 0.0f, "indirectMagic", PokkitDamageEffect.HURT);
		add("thorns", false, 0.1f, "thorns", PokkitDamageEffect.THORNS);
		add("explosion", true, 0.1f, "explosion", PokkitDamageEffect.HURT);
		add("player_explosion", true, 0.1f, "explosion.player", PokkitDamageEffect.HURT);
		add("sonic_boom", true, 0.0f, "sonic_boom", PokkitDamageEffect.HURT);
		add("bad_respawn_point", true, 0.1f, "badRespawnPoint", DeathMessageType.INTENTIONAL_GAME_DESIGN,
				PokkitDamageEffect.HURT);
		add("outside_border", false, 0.0f, "outsideBorder", PokkitDamageEffect.HURT);
		add("generic_kill", false, 0.0f, "genericKill", PokkitDamageEffect.HURT);
	}

	private static void add(String key, boolean always, float exhaustion, String messageId, DamageEffect effect) {
		add(key, always, exhaustion, messageId, DeathMessageType.DEFAULT, effect);
	}

	private static void add(String key, boolean always, float exhaustion, String messageId,
			DeathMessageType deathMessageType, DamageEffect effect) {
		PokkitDamageType type = new PokkitDamageType(key,
				always ? DamageScaling.ALWAYS : DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
				exhaustion, messageId, deathMessageType, effect);
		BY_KEY.put(key, type);
	}

	/**
	 * Gets all vanilla damage types, in the order of the vanilla registry.
	 *
	 * @return All damage types, never null.
	 */
	public static Collection<PokkitDamageType> all() {
		return Collections.unmodifiableCollection(BY_KEY.values());
	}

	private final NamespacedKey key;
	private final DamageScaling scaling;
	private final float exhaustion;
	private final String messageId;
	private final DeathMessageType deathMessageType;
	private final DamageEffect damageEffect;

	private PokkitDamageType(String key, DamageScaling scaling, float exhaustion, String messageId,
			DeathMessageType deathMessageType, DamageEffect damageEffect) {
		this.key = NamespacedKey.minecraft(key);
		this.scaling = scaling;
		this.exhaustion = exhaustion;
		this.messageId = messageId;
		this.deathMessageType = deathMessageType;
		this.damageEffect = damageEffect;
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public String getTranslationKey() {
		// The vanilla death message key is "death.attack." + message_id, which
		// is not always the same as the damage type key.
		return "death.attack." + messageId;
	}

	@Override
	public DamageScaling getDamageScaling() {
		return scaling;
	}

	@Override
	public DamageEffect getDamageEffect() {
		return damageEffect;
	}

	@Override
	public DeathMessageType getDeathMessageType() {
		return deathMessageType;
	}

	@Override
	public float getExhaustion() {
		return exhaustion;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PokkitDamageType other)) {
			return false;
		}
		return key.equals(other.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "PokkitDamageType{" + key + "}";
	}
}
