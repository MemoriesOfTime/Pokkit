package nl.rutgerkok.pokkit.entity;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

import nl.rutgerkok.pokkit.blockdata.PokkitBlockData;
import nl.rutgerkok.pokkit.blockdata.BlockMap;

public class PokkitFallingBlock extends PokkitEntity implements FallingBlock {
    private final cn.nukkit.entity.item.EntityFallingBlock nukkitFb;

    PokkitFallingBlock(cn.nukkit.entity.item.EntityFallingBlock nukkitEntity) {
        super(nukkitEntity);
        this.nukkitFb = nukkitEntity;
    }

    @Override
    public Material getMaterial() {
        return BlockMap.getMaterial(nukkitFb.getBlock(), nukkitFb.getDamage());
    }

    @Override
    public BlockData getBlockData() {
        Material material = BlockMap.getMaterial(nukkitFb.getBlock(), nukkitFb.getDamage());
        return PokkitBlockData.createBlockData(material, nukkitFb.getDamage());
    }

    @Override
    public boolean getDropItem() {
        return nukkitFb.namedTag.contains("DropItem") ? nukkitFb.namedTag.getBoolean("DropItem") : true;
    }

    @Override
    public void setDropItem(boolean drop) {
        nukkitFb.namedTag.putBoolean("DropItem", drop);
    }

    @Override
    public boolean getCancelDrop() {
        return nukkitFb.namedTag.contains("CancelDrop") && nukkitFb.namedTag.getBoolean("CancelDrop");
    }

    @Override
    public void setCancelDrop(boolean cancelDrop) {
        nukkitFb.namedTag.putBoolean("CancelDrop", cancelDrop);
    }

    @Override
    public boolean canHurtEntities() {
        return nukkitFb.namedTag.contains("HurtEntities") && nukkitFb.namedTag.getBoolean("HurtEntities");
    }

    @Override
    public void setHurtEntities(boolean hurtEntities) {
        nukkitFb.namedTag.putBoolean("HurtEntities", hurtEntities);
    }

    @Override
    public float getDamagePerBlock() {
        return nukkitFb.namedTag.contains("FallHurtAmount") ? nukkitFb.namedTag.getFloat("FallHurtAmount") : 2f;
    }

    @Override
    public void setDamagePerBlock(float damage) {
        nukkitFb.namedTag.putFloat("FallHurtAmount", damage);
    }

    @Override
    public int getMaxDamage() {
        return nukkitFb.namedTag.contains("FallHurtMax") ? nukkitFb.namedTag.getInt("FallHurtMax") : 40;
    }

    @Override
    public void setMaxDamage(int damage) {
        nukkitFb.namedTag.putInt("FallHurtMax", damage);
    }
}
