package nl.rutgerkok.pokkit.item;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import cn.nukkit.nbt.tag.CompoundTag;

final class PokkitSkullMeta extends PokkitBlockStateMeta implements SkullMeta {

	PokkitSkullMeta(CompoundTag tag, Material material, int damage) {
		super(tag, material, damage);
	}

	@Override
	public PokkitSkullMeta clone() {
		return (PokkitSkullMeta) super.clone();
	}

	@Override
	public String getOwner() {
		if (!tag.contains("SkullOwner")) return null;
		CompoundTag skullOwner = tag.getCompound("SkullOwner");
		return skullOwner.contains("Name") ? skullOwner.getString("Name") : null;
	}

	@Override
	public boolean hasOwner() {
		return tag.contains("SkullOwner") && tag.getCompound("SkullOwner").contains("Name");
	}

	@Override
	public boolean setOwner(String owner) {
		if (owner == null || owner.isEmpty()) {
			tag.remove("SkullOwner");
			return true;
		}
		CompoundTag skullOwner = tag.contains("SkullOwner")
				? tag.getCompound("SkullOwner")
				: new CompoundTag();
		skullOwner.putString("Name", owner);
		tag.putCompound("SkullOwner", skullOwner);
		return true;
	}

	@Override
	public OfflinePlayer getOwningPlayer() {
		String owner = getOwner();
		if (owner != null) {
			return Bukkit.getOfflinePlayer(owner);
		}
		if (tag.contains("SkullOwner")) {
			CompoundTag skullOwner = tag.getCompound("SkullOwner");
			if (skullOwner.contains("Id")) {
				try {
					UUID uuid = UUID.fromString(skullOwner.getString("Id"));
					return Bukkit.getOfflinePlayer(uuid);
				} catch (IllegalArgumentException ignored) {
				}
			}
		}
		return null;
	}

	@Override
	public boolean setOwningPlayer(OfflinePlayer player) {
		if (player == null) {
			tag.remove("SkullOwner");
			return true;
		}
		CompoundTag skullOwner = tag.contains("SkullOwner")
				? tag.getCompound("SkullOwner")
				: new CompoundTag();
		skullOwner.putString("Name", player.getName());
		if (player.getUniqueId() != null) {
			skullOwner.putString("Id", player.getUniqueId().toString());
		}
		tag.putCompound("SkullOwner", skullOwner);
		return true;
	}

	@Override
	public PlayerProfile getOwnerProfile() {
		String owner = getOwner();
		if (owner == null) return null;
		UUID uuid = null;
		if (tag.contains("SkullOwner")) {
			CompoundTag skullOwner = tag.getCompound("SkullOwner");
			if (skullOwner.contains("Id")) {
				try {
					uuid = UUID.fromString(skullOwner.getString("Id"));
				} catch (IllegalArgumentException ignored) {
				}
			}
		}
		if (uuid != null) {
			return Bukkit.createPlayerProfile(uuid, owner);
		}
		return Bukkit.createPlayerProfile(owner);
	}

	@Override
	public void setOwnerProfile(PlayerProfile profile) {
		if (profile == null) {
			tag.remove("SkullOwner");
			return;
		}
		CompoundTag skullOwner = new CompoundTag();
		if (profile.getName() != null) {
			skullOwner.putString("Name", profile.getName());
		}
		if (profile.getUniqueId() != null) {
			skullOwner.putString("Id", profile.getUniqueId().toString());
		}
		tag.putCompound("SkullOwner", skullOwner);
	}

	@Override
	public void setNoteBlockSound(org.bukkit.NamespacedKey sound) {
		if (sound == null) {
			tag.remove("note_block_sound");
		} else {
			tag.putString("note_block_sound", sound.toString());
		}
	}

	@Override
	public org.bukkit.NamespacedKey getNoteBlockSound() {
		if (!tag.contains("note_block_sound")) return null;
		String sound = tag.getString("note_block_sound");
		return org.bukkit.NamespacedKey.fromString(sound);
	}
}
