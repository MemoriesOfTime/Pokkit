package nl.rutgerkok.pokkit.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class PokkitPersistentDataContainer implements PersistentDataContainer {

    private final cn.nukkit.nbt.tag.CompoundTag tag;
    private final String prefix;

    public PokkitPersistentDataContainer(cn.nukkit.nbt.tag.CompoundTag tag) {
        this(tag, "BukkitPDC:");
    }

    public PokkitPersistentDataContainer(cn.nukkit.nbt.tag.CompoundTag tag, String prefix) {
        this.tag = tag;
        this.prefix = prefix;
    }

    private String nbtKey(NamespacedKey key) {
        return prefix + key.toString();
    }

    private Set<String> collectTagKeys() {
        Set<String> keys = new java.util.HashSet<>();
        for (cn.nukkit.nbt.tag.Tag t : tag.getAllTags()) {
            keys.add(t.getName());
        }
        return keys;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P, C> void set(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        String nbtKey = nbtKey(key);
        P primitive = type.toPrimitive(value, getAdapterContext());

        if (type == PersistentDataType.BYTE) {
            tag.putByte(nbtKey, ((Number) primitive).intValue());
        } else if (type == PersistentDataType.SHORT) {
            tag.putShort(nbtKey, ((Number) primitive).intValue());
        } else if (type == PersistentDataType.INTEGER) {
            tag.putInt(nbtKey, ((Number) primitive).intValue());
        } else if (type == PersistentDataType.LONG) {
            tag.putLong(nbtKey, ((Number) primitive).longValue());
        } else if (type == PersistentDataType.FLOAT) {
            tag.putFloat(nbtKey, ((Number) primitive).floatValue());
        } else if (type == PersistentDataType.DOUBLE) {
            tag.putDouble(nbtKey, ((Number) primitive).doubleValue());
        } else if (type == PersistentDataType.STRING) {
            tag.putString(nbtKey, (String) primitive);
        } else if (type == PersistentDataType.BYTE_ARRAY) {
            tag.putByteArray(nbtKey, (byte[]) primitive);
        } else if (type == PersistentDataType.INTEGER_ARRAY) {
            tag.putIntArray(nbtKey, (int[]) primitive);
        } else if (type == PersistentDataType.BOOLEAN) {
            tag.putBoolean(nbtKey, (Boolean) primitive);
        } else if (type == PersistentDataType.TAG_CONTAINER) {
            cn.nukkit.nbt.tag.CompoundTag childTag = new cn.nukkit.nbt.tag.CompoundTag();
            tag.putCompound(nbtKey, childTag);
        }
    }

    @Override
    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        return tag.contains(nbtKey(key));
    }

    @Override
    public boolean has(NamespacedKey key) {
        return tag.contains(nbtKey(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P, C> C get(NamespacedKey key, PersistentDataType<P, C> type) {
        String nbtKey = nbtKey(key);
        if (!tag.contains(nbtKey)) return null;

        Object primitive;
        if (type == PersistentDataType.BYTE) {
            primitive = (byte) tag.getByte(nbtKey);
        } else if (type == PersistentDataType.SHORT) {
            primitive = (short) tag.getShort(nbtKey);
        } else if (type == PersistentDataType.INTEGER) {
            primitive = tag.getInt(nbtKey);
        } else if (type == PersistentDataType.LONG) {
            primitive = tag.getLong(nbtKey);
        } else if (type == PersistentDataType.FLOAT) {
            primitive = tag.getFloat(nbtKey);
        } else if (type == PersistentDataType.DOUBLE) {
            primitive = tag.getDouble(nbtKey);
        } else if (type == PersistentDataType.STRING) {
            primitive = tag.getString(nbtKey);
        } else if (type == PersistentDataType.BOOLEAN) {
            primitive = tag.getBoolean(nbtKey);
        } else {
            return null;
        }
        return type.fromPrimitive((P) primitive, getAdapterContext());
    }

    @Override
    public <P, C> C getOrDefault(NamespacedKey key, PersistentDataType<P, C> type, C defaultValue) {
        C value = get(key, type);
        return value != null ? value : defaultValue;
    }

    @Override
    public Set<NamespacedKey> getKeys() {
        Set<NamespacedKey> keys = new java.util.HashSet<>();
        for (cn.nukkit.nbt.tag.Tag t : tag.getAllTags()) {
            String nbtKey = t.getName();
            if (nbtKey.startsWith(prefix)) {
                String keyStr = nbtKey.substring(prefix.length());
                int sep = keyStr.indexOf(':');
                if (sep > 0) {
                    keys.add(new NamespacedKey(keyStr.substring(0, sep), keyStr.substring(sep + 1)));
                }
            }
        }
        return keys;
    }

    @Override
    public void remove(NamespacedKey key) {
        tag.remove(nbtKey(key));
    }

    @Override
    public boolean isEmpty() {
        if (prefix.isEmpty()) {
            return tag.getAllTags().isEmpty();
        }
        for (cn.nukkit.nbt.tag.Tag t : tag.getAllTags()) {
            if (t.getName().startsWith(prefix)) return false;
        }
        return true;
    }

    @Override
    public void copyTo(PersistentDataContainer other, boolean replace) {
        if (other instanceof PokkitPersistentDataContainer) {
            PokkitPersistentDataContainer otherContainer = (PokkitPersistentDataContainer) other;
            for (cn.nukkit.nbt.tag.Tag t : tag.getAllTags()) {
                String nbtKey = t.getName();
                if (nbtKey.startsWith(prefix)) {
                    if (replace || !otherContainer.tag.contains(nbtKey)) {
                        otherContainer.tag.put(nbtKey, t.copy());
                    }
                }
            }
        }
    }

    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return PokkitPersistentDataAdapterContext.INSTANCE;
    }
}
