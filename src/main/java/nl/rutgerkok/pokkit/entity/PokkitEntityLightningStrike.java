package nl.rutgerkok.pokkit.entity;

import nl.rutgerkok.pokkit.Pokkit;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightningStrike;
import org.bukkit.util.BoundingBox;

public class PokkitEntityLightningStrike extends PokkitEntity implements LightningStrike{

	private final cn.nukkit.entity.Entity nukkit;
	
	PokkitEntityLightningStrike(Entity nukkitEntity) {
		super(nukkitEntity);
		this.nukkit = nukkitEntity;
	}
	
	public static LightningStrike toBukkit(cn.nukkit.entity.Entity nukkit) {
		if (nukkit == null) {
			return null;
		}
		if (!(nukkit instanceof EntityLightningStrike)) {
			return null;
		}
		
		return new PokkitEntityLightningStrike(nukkit);
	}

	public static cn.nukkit.entity.weather.EntityLightningStrike toNukkit(LightningStrike entity) {
		if (entity == null) {
			return null;
		}
		return (EntityLightningStrike) ((PokkitEntityLightningStrike) entity).nukkit;
	}


	@Override
	public boolean isEffect() {
		return ((EntityLightningStrike)nukkit).isEffect();
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
	public org.bukkit.entity.LightningStrike.Spigot spigot() {
		return new org.bukkit.entity.LightningStrike.Spigot();
	}

	@Override
	public Player getCausingPlayer() {
		return null;
	}

	@Override
	public void setCausingPlayer(Player player) {
	}

	@Override
	public int getLifeTicks() {
		return -1;
	}

	@Override
	public void setLifeTicks(int ticks) {
	}

	@Override
	public int getFlashes() {
		return -1;
	}

	@Override
	public void setFlashes(int flashes) {
	}

}
