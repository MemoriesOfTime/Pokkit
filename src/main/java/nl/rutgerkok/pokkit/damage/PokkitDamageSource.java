package nl.rutgerkok.pokkit.damage;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

/**
 * Immutable damage source, as created by
 * {@link DamageSource#builder(DamageType)}.
 */
public final class PokkitDamageSource implements DamageSource {

	/**
	 * Creates a new builder for a damage source.
	 *
	 * @param damageType
	 *            The damage type.
	 * @return The builder.
	 */
	public static Builder builder(DamageType damageType) {
		return new BuilderImpl(damageType);
	}

	private final DamageType damageType;
	private final Entity causingEntity;
	private final Entity directEntity;
	private final Location damageLocation;

	private PokkitDamageSource(DamageType damageType, Entity causingEntity, Entity directEntity,
			Location damageLocation) {
		this.damageType = damageType;
		this.causingEntity = causingEntity;
		this.directEntity = directEntity;
		this.damageLocation = damageLocation;
	}

	@Override
	public DamageType getDamageType() {
		return damageType;
	}

	@Override
	public Entity getCausingEntity() {
		return causingEntity;
	}

	@Override
	public Entity getDirectEntity() {
		return directEntity;
	}

	@Override
	public Location getDamageLocation() {
		return damageLocation;
	}

	@Override
	public Location getSourceLocation() {
		// The source location is the position of the entity that caused the
		// damage; the damage location is only used when there is no such
		// entity.
		if (causingEntity != null) {
			return causingEntity.getLocation();
		}
		if (directEntity != null) {
			return directEntity.getLocation();
		}
		return damageLocation;
	}

	@Override
	public boolean isIndirect() {
		return causingEntity != directEntity;
	}

	@Override
	public float getFoodExhaustion() {
		return damageType.getExhaustion();
	}

	@Override
	public boolean scalesWithDifficulty() {
		switch (damageType.getDamageScaling()) {
		case ALWAYS:
			return true;
		case WHEN_CAUSED_BY_LIVING_NON_PLAYER:
			// Only mobs scale with difficulty, players and non-living
			// entities do not.
			return causingEntity instanceof org.bukkit.entity.LivingEntity
					&& !(causingEntity instanceof org.bukkit.entity.Player);
		case NEVER:
		default:
			return false;
		}
	}

	private static final class BuilderImpl implements Builder {

		private final DamageType damageType;
		private Entity causingEntity;
		private Entity directEntity;
		private Location damageLocation;

		BuilderImpl(DamageType damageType) {
			this.damageType = Objects.requireNonNull(damageType, "damageType");
		}

		@Override
		public Builder withCausingEntity(Entity entity) {
			this.causingEntity = entity;
			return this;
		}

		@Override
		public Builder withDirectEntity(Entity entity) {
			this.directEntity = entity;
			return this;
		}

		@Override
		public Builder withDamageLocation(Location location) {
			this.damageLocation = location;
			return this;
		}

		@Override
		public DamageSource build() {
			return new PokkitDamageSource(damageType, causingEntity, directEntity, damageLocation);
		}
	}
}
