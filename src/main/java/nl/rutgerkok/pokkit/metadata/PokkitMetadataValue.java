package nl.rutgerkok.pokkit.metadata;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import nl.rutgerkok.pokkit.plugin.PokkitPlugin;

public class PokkitMetadataValue implements MetadataValue {
    cn.nukkit.metadata.MetadataValue nukkit;
    
    public PokkitMetadataValue(cn.nukkit.metadata.MetadataValue nukkit) {
        this.nukkit = nukkit;
    }
    
    public static MetadataValue toBukkit(cn.nukkit.metadata.MetadataValue nukkit) {
        return new PokkitMetadataValue(nukkit);
    }
    
    public static cn.nukkit.metadata.MetadataValue toNukkit(MetadataValue bukkit) {
        cn.nukkit.plugin.Plugin nukkitPlugin = nl.rutgerkok.pokkit.plugin.PokkitPlugin.toNukkit(bukkit.getOwningPlugin());
        return new cn.nukkit.metadata.MetadataValue(nukkitPlugin) {
            @Override
            public Object value() {
                return bukkit.value();
            }
            @Override
            public void invalidate() {
                bukkit.invalidate();
            }
        };
    }
    
    @Override
    public Object value() {
        return nukkit.value();
    }

    @Override
    public int asInt() {
        return (Integer) nukkit.value();
    }

    @Override
    public float asFloat() {
        return (Float) nukkit.value();
    }

    @Override
    public double asDouble() {
        return (Double) nukkit.value();
    }

    @Override
    public long asLong() {
        return (Long) nukkit.value();
    }

    @Override
    public short asShort() {
        return (Short) nukkit.value();
    }

    @Override
    public byte asByte() {
        return (Byte) nukkit.value();
    }

    @Override
    public boolean asBoolean() {
        return (Boolean) nukkit.value();
    }

    @Override
    public String asString() {
        return (String) nukkit.value();
    }

    @Override
    public Plugin getOwningPlugin() {
        return PokkitPlugin.toBukkit(nukkit.getOwningPlugin());
    }

    @Override
    public void invalidate() {
        nukkit.invalidate();
    }

}
