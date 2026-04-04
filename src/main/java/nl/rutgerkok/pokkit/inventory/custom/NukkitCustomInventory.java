package nl.rutgerkok.pokkit.inventory.custom;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Objects;

import cn.nukkit.Player;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

/**
 * A class that opens a custom inventory for a player. Based on <a href=
 * "https://gist.github.com/Creeperface01/2340f5ebf8fc48705086bdfc26d49606">this
 * class</a> by Creeperface01. Only supports chests for now, no other
 * inventories.
 *
 */
public class NukkitCustomInventory extends BaseInventory {

	// Tracks fake chest positions per player (1 for single chest, 2 for double chest)
	private HashMap<String, Vector3[]> spawnedFakeChestBlocks = new HashMap<>();

	public final String customName;

	public NukkitCustomInventory(String customName, org.bukkit.inventory.InventoryHolder holder) {
		super(new BukkitToNukkitInventoryHolder(holder), InventoryType.CHEST);
		getHolder().setInventory(this);
		this.customName = Objects.requireNonNull(customName, "customName");
	}

	@Override
	public BukkitToNukkitInventoryHolder getHolder() {
		return (BukkitToNukkitInventoryHolder) this.holder;
	}

	private CompoundTag getSpawnCompound(Vector3 position) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", "Chest");
		tag.putInt("x", (int) position.x);
		tag.putInt("y", (int) position.y);
		tag.putInt("z", (int) position.z);
		tag.putString("CustomName", this.customName);

		return tag;
	}

	@Override
	public void onClose(Player player) {
		// Remove the fake chest block
		if (player.getClosingWindowId() != Integer.MAX_VALUE) {
			ContainerClosePacket closePacket = new ContainerClosePacket();
			closePacket.windowId = player.getWindowId(this);
			closePacket.wasServerInitiated = player.getClosingWindowId() != closePacket.windowId;
			closePacket.type = cn.nukkit.network.protocol.types.inventory.ContainerType.from(this.getType().getNetworkType());
			player.dataPacket(closePacket);
		}

		Vector3[] positions = spawnedFakeChestBlocks.get(player.getName().toLowerCase());

		if (positions != null) {
			player.getLevel().sendBlocks(new Player[] { player }, positions);
		}

		spawnedFakeChestBlocks.remove(player.getName().toLowerCase());

		super.onClose(player);
	}

	@Override
    public void onOpen(Player who) {
        super.onOpen(who);

        boolean isDoubleChest = this.getSize() > 27;

        // Place fake chest block(s)
        Vector3 v1 = new Vector3(who.getFloorX(), who.getFloorY() + 5, who.getFloorZ());
        sendFakeChest(who, v1);

        Vector3 v2 = null;
        if (isDoubleChest) {
            v2 = new Vector3(who.getFloorX() + 1, who.getFloorY() + 5, who.getFloorZ());
            sendFakeChest(who, v2);
        }

        spawnedFakeChestBlocks.put(who.getName().toLowerCase(),
                isDoubleChest ? new Vector3[] { v1, v2 } : new Vector3[] { v1 });

		// Open the chest
        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.windowId = who.getWindowId(this);
        containerOpenPacket.type = this.getType().getNetworkType();

        containerOpenPacket.x = (int) v1.x;
        containerOpenPacket.y = (int) v1.y;
        containerOpenPacket.z = (int) v1.z;

        who.dataPacket(containerOpenPacket);

        this.sendContents(who);
    }

    private void sendFakeChest(Player who, Vector3 v) {
        UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
        updateBlockPacket.x = (int) v.x;
        updateBlockPacket.y = (int) v.y;
        updateBlockPacket.z = (int) v.z;
        updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(who.getGameVersion(), BlockID.CHEST, 0);
        updateBlockPacket.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        who.dataPacket(updateBlockPacket);

        BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
        blockEntityDataPacket.x = (int) v.x;
        blockEntityDataPacket.y = (int) v.y;
        blockEntityDataPacket.z = (int) v.z;

        try {
            blockEntityDataPacket.namedTag = NBTIO.write(getSpawnCompound(v), ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        who.dataPacket(blockEntityDataPacket);
    }
}