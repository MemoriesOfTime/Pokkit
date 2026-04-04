package nl.rutgerkok.pokkit;

import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class PokkitBanList implements BanList {

    private cn.nukkit.permission.BanList nukkit;

    PokkitBanList(cn.nukkit.permission.BanList banList) {
        this.nukkit = banList;
    }

    public static BanList toBukkit(cn.nukkit.permission.BanList banList) {
        return new PokkitBanList(banList);
    }

    public static cn.nukkit.permission.BanList toNukkit(BanList banList) {
        return ((PokkitBanList) banList).nukkit;
    }

    @Override
    public BanEntry getBanEntry(String banEntry) {
        return new PokkitBanEntry(nukkit.getEntires().get(banEntry));
    }

    @Override
    public BanEntry addBan(String target, String reason, Date date, String source) {
        return new PokkitBanEntry(nukkit.addBan(target, reason, date, source));
    }

    @Override
    public Set<BanEntry> getBanEntries() {
        return nukkit.getEntires().values().stream().map(PokkitBanEntry::new).collect(Collectors.toSet());
    }

    @Override
    public boolean isBanned(String target) {
        return nukkit.isBanned(target);
    }

    @Override
    public void pardon(String target) {
        nukkit.remove(target);
    }

    @Override
    public void pardon(Object target) {
        pardon(String.valueOf(target));
    }

    @Override
    public boolean isBanned(Object target) {
        return isBanned(String.valueOf(target));
    }

    @Override
    public Set getEntries() {
        return nukkit.getEntires().values().stream().map(PokkitBanEntry::new).collect(Collectors.toSet());
    }

    @Override
    public BanEntry getBanEntry(Object target) {
        return getBanEntry(String.valueOf(target));
    }

    @Override
    public BanEntry addBan(Object target, String reason, Date date, String source) {
        return addBan(String.valueOf(target), reason, date, source);
    }

    @Override
    public BanEntry addBan(Object target, String reason, java.time.Instant instant, String source) {
        Date date = instant != null ? Date.from(instant) : null;
        return addBan(String.valueOf(target), reason, date, source);
    }

    @Override
    public BanEntry addBan(Object target, String reason, java.time.Duration duration, String source) {
        Date date = duration != null ? new Date(System.currentTimeMillis() + duration.toMillis()) : null;
        return addBan(String.valueOf(target), reason, date, source);
    }
}
