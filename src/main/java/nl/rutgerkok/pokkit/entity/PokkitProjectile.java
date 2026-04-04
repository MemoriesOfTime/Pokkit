package nl.rutgerkok.pokkit.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BoundingBox;

public class PokkitProjectile extends PokkitEntity implements Projectile {

    public static Projectile toBukkit(cn.nukkit.entity.projectile.EntityProjectile nukkit) {
        if (nukkit == null) {
            return null;
        }

        return new PokkitProjectile(nukkit);
    }

    private final cn.nukkit.entity.projectile.EntityProjectile nukkit;

    PokkitProjectile(cn.nukkit.entity.projectile.EntityProjectile nukkit) {
        super(nukkit);
        this.nukkit = nukkit;
    }

    /**
     * Retrieve the shooter of this projectile.
     *
     * @return the {@link ProjectileSource} that shot this projectile
     */
    public ProjectileSource getShooter(){
        return PokkitProjectileShooter.toBukkit(this.nukkit.shootingEntity);
    };

    /**
     * Set the shooter of this projectile.
     *
     * @param source the {@link ProjectileSource} that shot this projectile
     */
    public void setShooter(ProjectileSource source) {
        if (source instanceof Entity) {
            this.nukkit.shootingEntity = PokkitEntity.toNukkit((Entity) source);
        }
    }

    /**
     * Determine if this projectile should bounce or not when it hits.
     * <p>
     * If a small fireball does not bounce it will set the target on fire.
     *
     * @return true if it should bounce.
     */
    public boolean doesBounce() {
        return false; // not implemented
    }

    /**
     * Set whether this projectile should bounce or not when it hits
     * something.
     *
     * @param doesBounce whether or not it should bounce.
     */
    public void setBounce(boolean doesBounce) {
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(nukkit.getBoundingBox().getMinX(), nukkit.getBoundingBox().getMinY(), nukkit.getBoundingBox().getMinZ(), nukkit.getBoundingBox().getMaxX(), nukkit.getBoundingBox().getMaxY(), nukkit.getBoundingBox().getMaxZ());
    }

    @Override
    public void setRotation(float v, float v1) {
        nukkit.setRotation(v, v1);
    }

    @Override
    public Entity copy(org.bukkit.Location location) {
        return null;
    }
}


