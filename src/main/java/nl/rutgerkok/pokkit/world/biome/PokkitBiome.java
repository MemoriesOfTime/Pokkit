package nl.rutgerkok.pokkit.world.biome;

import java.util.Objects;

import org.bukkit.block.Biome;

public class PokkitBiome {

    public static Biome toBukkit(int id) {
        switch (id) {
            case 0:
                return Biome.OCEAN;
            case 1:
                return Biome.PLAINS;
            case 2:
                return Biome.DESERT;
            case 3:
                return Biome.WINDSWEPT_HILLS;
            case 4:
                return Biome.FOREST;
            case 5:
                return Biome.TAIGA;
            case 6:
                return Biome.SWAMP;
            case 7:
                return Biome.RIVER;
            case 8:
                return Biome.NETHER_WASTES;
            case 9:
                return Biome.THE_END;
            case 10:
                return Biome.FROZEN_OCEAN;
            case 11:
                return Biome.FROZEN_RIVER;
            case 12:
                return Biome.SNOWY_PLAINS;
            case 13:
                return Biome.MUSHROOM_FIELDS;
            case 14:
                return Biome.MUSHROOM_FIELDS;
            case 15:
                return Biome.BEACH;
            case 16:
                return Biome.BEACH;
            case 17:
                return Biome.DESERT;
            case 18:
                return Biome.FOREST;
            case 19:
                return Biome.TAIGA;
            case 20:
                return Biome.WINDSWEPT_HILLS;
            case 21:
                return Biome.JUNGLE;
            case 22:
                return Biome.JUNGLE;
            case 23:
                return Biome.SPARSE_JUNGLE;
            case 24:
                return Biome.DEEP_OCEAN;
            case 25:
                return Biome.STONY_SHORE;
            case 26:
                return Biome.SNOWY_BEACH;
            case 27:
                return Biome.BIRCH_FOREST;
            case 28:
                return Biome.BIRCH_FOREST;
            case 29:
                return Biome.DARK_FOREST;
            case 30:
                return Biome.SNOWY_TAIGA;
            case 31:
                return Biome.SNOWY_TAIGA;
            case 32:
                return Biome.OLD_GROWTH_PINE_TAIGA;
            case 33:
                return Biome.OLD_GROWTH_PINE_TAIGA;
            case 34:
                return Biome.WINDSWEPT_FOREST;
            case 35:
                return Biome.SAVANNA;
            case 36:
                return Biome.SAVANNA;
            case 37:
                return Biome.BADLANDS;
            case 38:
                return Biome.WOODED_BADLANDS;
            case 39:
                return Biome.BADLANDS;
            case 40:
                return Biome.MEADOW;
            case 42:
                return Biome.LUSH_CAVES;
            case 44:
                return Biome.JAGGED_PEAKS;
            case 45:
                return Biome.STONY_PEAKS;
            case 46:
                return Biome.DRIPSTONE_CAVES;
            case 48:
                return Biome.SNOWY_SLOPES;
            case 49:
                return Biome.FROZEN_PEAKS;
            case 50:
                return Biome.GROVE;
            case 127:
                return Biome.THE_VOID;
            case 129:
                return Biome.SUNFLOWER_PLAINS;
            case 130:
                return Biome.DESERT;
            case 131:
                return Biome.WINDSWEPT_HILLS;
            case 132:
                return Biome.FLOWER_FOREST;
            case 133:
                return Biome.TAIGA;
            case 134:
                return Biome.SWAMP;
            case 140:
                return Biome.ICE_SPIKES;
            case 149:
                return Biome.JUNGLE;
            case 151:
                return Biome.SPARSE_JUNGLE;
            case 155:
                return Biome.OLD_GROWTH_BIRCH_FOREST;
            case 156:
                return Biome.OLD_GROWTH_BIRCH_FOREST;
            case 157:
                return Biome.DARK_FOREST;
            case 158:
                return Biome.SNOWY_TAIGA;
            case 160:
                return Biome.OLD_GROWTH_SPRUCE_TAIGA;
            case 161:
                return Biome.OLD_GROWTH_SPRUCE_TAIGA;
            case 162:
                return Biome.WINDSWEPT_HILLS;
            case 163:
                return Biome.WINDSWEPT_SAVANNA;
            case 164:
                return Biome.SAVANNA;
            case 165:
                return Biome.ERODED_BADLANDS;
            case 166:
                return Biome.WOODED_BADLANDS;
            case 167:
                return Biome.BADLANDS;
            case 168:
                return Biome.BAMBOO_JUNGLE;
            case 169:
                return Biome.BAMBOO_JUNGLE;
            default:
                return Biome.OCEAN;
        }
    }

    public static int toNukkit(Biome biome) {
        Objects.requireNonNull(biome, "biome");
        if (biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.WARM_OCEAN
                || biome == Biome.LUKEWARM_OCEAN || biome == Biome.COLD_OCEAN
                || biome == Biome.DEEP_LUKEWARM_OCEAN || biome == Biome.DEEP_COLD_OCEAN
                || biome == Biome.DEEP_FROZEN_OCEAN) {
            return 0;
        } else if (biome == Biome.PLAINS || biome == Biome.SUNFLOWER_PLAINS) {
            return 1;
        } else if (biome == Biome.DESERT) {
            return 2;
        } else if (biome == Biome.WINDSWEPT_HILLS || biome == Biome.WINDSWEPT_GRAVELLY_HILLS) {
            return 3;
        } else if (biome == Biome.FOREST || biome == Biome.FLOWER_FOREST) {
            return 4;
        } else if (biome == Biome.TAIGA) {
            return 5;
        } else if (biome == Biome.SWAMP) {
            return 6;
        } else if (biome == Biome.RIVER) {
            return 7;
        } else if (biome == Biome.NETHER_WASTES) {
            return 8;
        } else if (biome == Biome.THE_END || biome == Biome.END_BARRENS || biome == Biome.END_HIGHLANDS
                || biome == Biome.END_MIDLANDS || biome == Biome.SMALL_END_ISLANDS) {
            return 9;
        } else if (biome == Biome.FROZEN_OCEAN) {
            return 10;
        } else if (biome == Biome.FROZEN_RIVER) {
            return 11;
        } else if (biome == Biome.SNOWY_PLAINS || biome == Biome.ICE_SPIKES) {
            return 12;
        } else if (biome == Biome.MUSHROOM_FIELDS) {
            return 14;
        } else if (biome == Biome.BEACH || biome == Biome.STONY_SHORE) {
            return 16;
        } else if (biome == Biome.JUNGLE || biome == Biome.BAMBOO_JUNGLE) {
            return 21;
        } else if (biome == Biome.SPARSE_JUNGLE) {
            return 23;
        } else if (biome == Biome.SNOWY_BEACH) {
            return 26;
        } else if (biome == Biome.BIRCH_FOREST || biome == Biome.OLD_GROWTH_BIRCH_FOREST) {
            return 27;
        } else if (biome == Biome.DARK_FOREST) {
            return 29;
        } else if (biome == Biome.SNOWY_TAIGA) {
            return 30;
        } else if (biome == Biome.OLD_GROWTH_PINE_TAIGA) {
            return 32;
        } else if (biome == Biome.OLD_GROWTH_SPRUCE_TAIGA) {
            return 160;
        } else if (biome == Biome.WINDSWEPT_FOREST) {
            return 34;
        } else if (biome == Biome.SAVANNA) {
            return 35;
        } else if (biome == Biome.WINDSWEPT_SAVANNA) {
            return 163;
        } else if (biome == Biome.BADLANDS || biome == Biome.ERODED_BADLANDS) {
            return 37;
        } else if (biome == Biome.WOODED_BADLANDS) {
            return 38;
        } else if (biome == Biome.MEADOW) {
            return 40;
        } else if (biome == Biome.LUSH_CAVES) {
            return 42;
        } else if (biome == Biome.JAGGED_PEAKS || biome == Biome.FROZEN_PEAKS || biome == Biome.STONY_PEAKS) {
            return 44;
        } else if (biome == Biome.DRIPSTONE_CAVES) {
            return 46;
        } else if (biome == Biome.GROVE || biome == Biome.SNOWY_SLOPES) {
            return 48;
        } else if (biome == Biome.CHERRY_GROVE) {
            return 50;
        } else if (biome == Biome.MANGROVE_SWAMP) {
            return 6;
        } else if (biome == Biome.DEEP_DARK) {
            return 46;
        } else if (biome == Biome.THE_VOID) {
            return 127;
        }
        return 0;
    }
}
