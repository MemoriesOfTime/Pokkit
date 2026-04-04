package nl.rutgerkok.pokkit;

import org.bukkit.BanEntry;

import java.util.Date;

public class PokkitBanEntry implements BanEntry {

    private cn.nukkit.permission.BanEntry nukkit;

    PokkitBanEntry(cn.nukkit.permission.BanEntry banEntry) {
        this.nukkit = banEntry;
    }

    public static BanEntry toBukkit(cn.nukkit.permission.BanEntry banEntry) {
        return new PokkitBanEntry(banEntry);
    }

    public static cn.nukkit.permission.BanEntry toNukkit(BanEntry banEntry) {
        return ((PokkitBanEntry) banEntry).nukkit;
    }

    @Override
    public String getTarget() {
        return nukkit.getName();
    }

    @Override
    public Date getCreated() {
        return nukkit.getCreationDate();
    }

    @Override
    public void setCreated(Date date) {
        nukkit.setCreationDate(date);
    }

    @Override
    public String getSource() {
        return nukkit.getSource();
    }

    @Override
    public void setSource(String source) {
        nukkit.setSource(source);
    }

    @Override
    public Date getExpiration() {
        return nukkit.getExpirationDate();
    }

    @Override
    public void setExpiration(Date date) {
        this.setExpiration(date);
    }

    @Override
    public String getReason() {
        return nukkit.getReason();
    }

    @Override
    public void setReason(String reason) {
        nukkit.setReason(reason);
    }

    @Override
    public void save() {
        // silently unsupported
    }

    @Override
    public void remove() {
    }

    @Override
    public Object getBanTarget() {
        return getTarget();
    }
}
