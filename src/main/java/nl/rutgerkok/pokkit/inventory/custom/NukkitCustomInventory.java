package nl.rutgerkok.pokkit.inventory.custom;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nl.rutgerkok.pokkit.plugin.PokkitPlugin;
import org.jetbrains.annotations.NotNull;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

/**
 * A custom inventory that creates fake blocks for Bedrock clients.
 * Based on <a href="https://gist.github.com/Creeperface01/2340f5ebf8fc48705086bdfc26d49606">this
 * class</a> by Creeperface01, and the <a href="https://github.com/NukkitX/FakeInventories">FakeInventories</a>
 * project. Uses the same fake-block approach as Geyser's BlockInventoryHolder.
 */
public class NukkitCustomInventory extends BaseInventory {

	/** Block info needed to place a fake block for a container type */
	private static final class FakeBlockInfo {
		final int blockId;
		final String blockEntityId;

		FakeBlockInfo(int blockId, String blockEntityId) {
			this.blockId = blockId;
			this.blockEntityId = blockEntityId;
		}
	}

	/** Mapping from Nukkit InventoryType to the fake block that represents it */
	private static final Map<InventoryType, FakeBlockInfo> BLOCK_MAPPINGS = new EnumMap<>(InventoryType.class);

	static {
		// Chest types
		BLOCK_MAPPINGS.put(InventoryType.CHEST, new FakeBlockInfo(BlockID.CHEST, BlockEntity.CHEST));
		BLOCK_MAPPINGS.put(InventoryType.ENDER_CHEST, new FakeBlockInfo(BlockID.CHEST, BlockEntity.CHEST));
		// Furnace types
		BLOCK_MAPPINGS.put(InventoryType.FURNACE, new FakeBlockInfo(BlockID.FURNACE, BlockEntity.FURNACE));
		BLOCK_MAPPINGS.put(InventoryType.BLAST_FURNACE, new FakeBlockInfo(BlockID.BLAST_FURNACE, BlockEntity.BLAST_FURNACE));
		BLOCK_MAPPINGS.put(InventoryType.SMOKER, new FakeBlockInfo(BlockID.SMOKER, BlockEntity.SMOKER));
		// Special containers
		BLOCK_MAPPINGS.put(InventoryType.ANVIL, new FakeBlockInfo(BlockID.ANVIL, "Anvil"));
		BLOCK_MAPPINGS.put(InventoryType.BREWING_STAND, new FakeBlockInfo(BlockID.BREWING_STAND_BLOCK, BlockEntity.BREWING_STAND));
		BLOCK_MAPPINGS.put(InventoryType.ENCHANT_TABLE, new FakeBlockInfo(BlockID.ENCHANTING_TABLE, BlockEntity.ENCHANT_TABLE));
		BLOCK_MAPPINGS.put(InventoryType.HOPPER, new FakeBlockInfo(BlockID.HOPPER_BLOCK, BlockEntity.HOPPER));
		BLOCK_MAPPINGS.put(InventoryType.BEACON, new FakeBlockInfo(BlockID.BEACON, BlockEntity.BEACON));
		BLOCK_MAPPINGS.put(InventoryType.LOOM, new FakeBlockInfo(BlockID.LOOM, "Loom"));
		BLOCK_MAPPINGS.put(InventoryType.GRINDSTONE, new FakeBlockInfo(BlockID.GRINDSTONE, "Grindstone"));
		BLOCK_MAPPINGS.put(InventoryType.STONECUTTER, new FakeBlockInfo(BlockID.STONECUTTER_BLOCK, "Stonecutter"));
		BLOCK_MAPPINGS.put(InventoryType.SMITHING_TABLE, new FakeBlockInfo(BlockID.SMITHING_TABLE, "SmithingTable"));
		BLOCK_MAPPINGS.put(InventoryType.DISPENSER, new FakeBlockInfo(BlockID.DISPENSER, BlockEntity.DISPENSER));
		BLOCK_MAPPINGS.put(InventoryType.DROPPER, new FakeBlockInfo(BlockID.DROPPER, BlockEntity.DROPPER));
		BLOCK_MAPPINGS.put(InventoryType.SHULKER_BOX, new FakeBlockInfo(BlockID.SHULKER_BOX, BlockEntity.SHULKER_BOX));
		BLOCK_MAPPINGS.put(InventoryType.BARREL, new FakeBlockInfo(BlockID.BARREL, BlockEntity.BARREL));
	}

	/** Check if a Nukkit InventoryType is supported for custom inventories */
	public static boolean isSupported(@NotNull InventoryType type) {
		return BLOCK_MAPPINGS.containsKey(type);
	}

	// Tracks fake block positions per player (1 for single block, 2 for double chest)
	private final HashMap<String, Vector3[]> spawnedFakeBlocks = new HashMap<>();
	private final FakeBlockInfo blockInfo;

	public final String customName;

	public NukkitCustomInventory(@NotNull String customName, org.bukkit.inventory.InventoryHolder holder, @NotNull InventoryType nukkitType) {
		super(new BukkitToNukkitInventoryHolder(holder), nukkitType);
		getHolder().setInventory(this);
		this.customName = Objects.requireNonNull(customName, "customName");
		this.blockInfo = BLOCK_MAPPINGS.get(nukkitType);
		if (this.blockInfo == null) {
			throw new IllegalArgumentException("Unsupported inventory type for fake block: " + nukkitType);
		}
	}

	@Override
	public BukkitToNukkitInventoryHolder getHolder() {
		return (BukkitToNukkitInventoryHolder) this.holder;
	}

	@Override
	public void onOpen(@NotNull Player who) {
		super.onOpen(who);

		boolean isDoubleChest = this.getType() == InventoryType.CHEST && this.getSize() > 27;

		// Calculate fake block position
		Vector3 v1 = new Vector3(who.getFloorX(), who.getFloorY() + 5, who.getFloorZ());

		if (isDoubleChest) {
			Vector3 v2 = new Vector3(who.getFloorX() + 1, who.getFloorY() + 5, who.getFloorZ());

			// Place two chest blocks
			placeBlockUpdate(who, v1);
			placeBlockUpdate(who, v2);

			// Send paired block entity data (includes pairx/pairz + CustomName)
			sendChestPairData(who, v1, v2);
			sendChestPairData(who, v2, v1);

			spawnedFakeBlocks.put(who.getName().toLowerCase(), new Vector3[] { v1, v2 });
		} else {
			// Place single fake block with entity data
			sendFakeBlock(who, v1);
			spawnedFakeBlocks.put(who.getName().toLowerCase(), new Vector3[] { v1 });
		}

		// Delay ContainerOpenPacket to wait for:
		// 1. Client to finish processing the UpdateBlockPacket (fake block placement)
		// 2. Client to close any active UI (e.g. chat/command input after running a command)
		// Without this delay, the client may reject the container open.
		int delayTicks = 20;
		Server.getInstance().getScheduler().scheduleDelayedTask(PokkitPlugin.getInstance(), () -> {
			ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
			containerOpenPacket.windowId = who.getWindowId(this);
			containerOpenPacket.type = this.getType().getNetworkType();
			containerOpenPacket.x = (int) v1.x;
			containerOpenPacket.y = (int) v1.y;
			containerOpenPacket.z = (int) v1.z;

			who.dataPacket(containerOpenPacket);

			this.sendContents(who);
		}, delayTicks, false);
	}

	@Override
	public void onClose(@NotNull Player who) {
		// Remove the fake block(s)
		if (who.getClosingWindowId() != Integer.MAX_VALUE) {
			ContainerClosePacket closePacket = new ContainerClosePacket();
			closePacket.windowId = who.getWindowId(this);
			closePacket.wasServerInitiated = who.getClosingWindowId() != closePacket.windowId;
			closePacket.type = cn.nukkit.network.protocol.types.inventory.ContainerType.from(this.getType().getNetworkType());
			who.dataPacket(closePacket);
		}

		Vector3[] positions = spawnedFakeBlocks.remove(who.getName().toLowerCase());

		if (positions != null) {
			who.getLevel().sendBlocks(new Player[] { who }, positions);
		}

		super.onClose(who);
	}

	/** Places a fake block with block entity data (CustomName) */
	private void sendFakeBlock(@NotNull Player who, @NotNull Vector3 v) {
		placeBlockUpdate(who, v);

		BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
		blockEntityDataPacket.x = (int) v.x;
		blockEntityDataPacket.y = (int) v.y;
		blockEntityDataPacket.z = (int) v.z;

		try {
			blockEntityDataPacket.namedTag = NBTIO.writeNetwork(getSpawnCompound(v));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		who.dataPacket(blockEntityDataPacket);
	}

	/** Sends only the UpdateBlockPacket (no block entity data) */
	private void placeBlockUpdate(@NotNull Player who, @NotNull Vector3 v) {
		UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
		updateBlockPacket.x = (int) v.x;
		updateBlockPacket.y = (int) v.y;
		updateBlockPacket.z = (int) v.z;
		updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(who.getGameVersion(), blockInfo.blockId, 0);
		updateBlockPacket.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
		who.dataPacket(updateBlockPacket);
	}

	/** Sends paired chest block entity data (for double chests only) */
	private void sendChestPairData(@NotNull Player who, @NotNull Vector3 pos, @NotNull Vector3 pairPos) {
		CompoundTag tag = new CompoundTag()
				.putString("id", BlockEntity.CHEST)
				.putInt("x", (int) pos.x)
				.putInt("y", (int) pos.y)
				.putInt("z", (int) pos.z)
				.putInt("pairx", (int) pairPos.x)
				.putInt("pairz", (int) pairPos.z)
				.putString("CustomName", this.customName);

		BlockEntityDataPacket packet = new BlockEntityDataPacket();
		packet.x = (int) pos.x;
		packet.y = (int) pos.y;
		packet.z = (int) pos.z;
		try {
			packet.namedTag = NBTIO.writeNetwork(tag);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		who.dataPacket(packet);
	}

	private @NotNull CompoundTag getSpawnCompound(@NotNull Vector3 position) {
		CompoundTag tag = new CompoundTag()
				.putString("id", blockInfo.blockEntityId)
				.putInt("x", (int) position.x)
				.putInt("y", (int) position.y)
				.putInt("z", (int) position.z)
				.putString("CustomName", this.customName);
		return tag;
	}
}
