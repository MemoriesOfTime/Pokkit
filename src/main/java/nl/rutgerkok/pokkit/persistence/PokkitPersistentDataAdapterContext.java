package nl.rutgerkok.pokkit.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;

public class PokkitPersistentDataAdapterContext implements PersistentDataAdapterContext {
    public static final PokkitPersistentDataAdapterContext INSTANCE = new PokkitPersistentDataAdapterContext();

    @Override
    public PokkitPersistentDataContainer newPersistentDataContainer() {
        return new PokkitPersistentDataContainer(new cn.nukkit.nbt.tag.CompoundTag());
    }
}
