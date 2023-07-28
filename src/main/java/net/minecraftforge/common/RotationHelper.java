package net.minecraftforge.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.*;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.common.ForgeDirection.*;

public class RotationHelper {
    /**
     * Some blocks have the same rotation.
     * The first of these blocks (sorted by itemID) should be listed as a type.
     * Some of the types aren't actual blocks (helper types).
     */
    private static enum BlockType {
        LOG,
        DISPENSER,
        BED,
        RAIL,
        RAIL_POWERED,
        RAIL_ASCENDING,
        RAIL_CORNER,
        TORCH,
        STAIR,
        CHEST,
        SIGNPOST,
        DOOR,
        LEVER,
        BUTTON,
        REDSTONE_REPEATER,
        TRAPDOOR,
        MUSHROOM_CAP,
        MUSHROOM_CAP_CORNER,
        MUSHROOM_CAP_SIDE,
        VINE,
        SKULL,
        ANVIL
    }

    private static final ForgeDirection[] UP_DOWN_AXES = { UP, DOWN };
    private static final Map<BlockType, BiMap<Integer, ForgeDirection>> MAPPINGS = new HashMap<BlockType, BiMap<Integer, ForgeDirection>>();

    public static ForgeDirection[] getValidVanillaBlockRotations(final Block block)
    {
        return (block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame || block instanceof BlockTripWireSource || block instanceof BlockCocoa || block instanceof BlockRailPowered || block instanceof BlockDetectorRail || block instanceof BlockStairs || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockFurnace || block instanceof BlockLadder || block.blockID == Block.signWall.blockID || block.blockID == Block.signPost.blockID || block instanceof BlockDoor || block instanceof BlockRail || block instanceof BlockButton || block instanceof BlockRedstoneRepeater || block instanceof BlockComparator || block instanceof BlockTrapDoor || block instanceof BlockMushroomCap || block instanceof BlockVine || block instanceof BlockSkull || block instanceof BlockAnvil) ? UP_DOWN_AXES : VALID_DIRECTIONS;
    }

    public static boolean rotateVanillaBlock(final Block block, final World worldObj, final int x, final int y, final int z, final ForgeDirection axis)
    {
        if (worldObj.isRemote)
        {
            return false;
        }

        if (axis == UP || axis == DOWN)
        {
            if (block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame || block instanceof BlockTripWireSource || block instanceof BlockCocoa)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.BED);
            }
            if (block instanceof BlockRail)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.RAIL);
            }
            if (block instanceof BlockRailPowered || block instanceof BlockDetectorRail)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.RAIL_POWERED);
            }
            if (block instanceof BlockStairs)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.STAIR);
            }
            if (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockFurnace || block instanceof BlockLadder || block.blockID == Block.signWall.blockID)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.CHEST);
            }
            if (block.blockID == Block.signPost.blockID)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.SIGNPOST);
            }
            if (block instanceof BlockDoor)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.DOOR);
            }
            if (block instanceof BlockButton)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.BUTTON);
            }
            if (block instanceof BlockRedstoneRepeater || block instanceof BlockComparator)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.REDSTONE_REPEATER);
            }
            if (block instanceof BlockTrapDoor)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x3, BlockType.TRAPDOOR);
            }
            if (block instanceof BlockMushroomCap)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.MUSHROOM_CAP);
            }
            if (block instanceof BlockVine)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.VINE);
            }
            if (block instanceof BlockSkull)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.SKULL);
            }
            if (block instanceof BlockAnvil)
            {
                return rotateBlock(worldObj, x, y, z, axis, 0x1, BlockType.ANVIL);
            }
        }

        if (block instanceof BlockLog)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0xC, BlockType.LOG);
        }
        if (block instanceof BlockDispenser || block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockHopper)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.DISPENSER);
        }
        if (block instanceof BlockTorch)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0xF, BlockType.TORCH);
        }
        if (block instanceof BlockLever)
        {
            return rotateBlock(worldObj, x, y, z, axis, 0x7, BlockType.LEVER);
        }

        return false;
    }

    private static boolean rotateBlock(final World worldObj, final int x, final int y, final int z, final ForgeDirection axis, final int mask, final BlockType blockType)
    {
        final int rotMeta = worldObj.getBlockMetadata(x, y, z);
        if (blockType == BlockType.DOOR && (rotMeta & 0x8) == 0x8)
        {
            return false;
        }
        final int masked = rotMeta & ~mask;
        final int meta = rotateMetadata(axis, blockType, rotMeta & mask);
        if (meta == -1)
        {
            return false;
        }
        worldObj.setBlockMetadataWithNotify(x, y, z, meta & mask | masked, 3);
        return true;
    }

    private static int rotateMetadata(final ForgeDirection axis, BlockType blockType, final int meta)
    {
        BlockType blockType1 = blockType;
        if (blockType1 == BlockType.RAIL || blockType1 == BlockType.RAIL_POWERED)
        {
            if (meta == 0x0 || meta == 0x1)
            {
                return ~meta & 0x1;
            }
            if (meta >= 0x2 && meta <= 0x5)
            {
                blockType1 = BlockType.RAIL_ASCENDING;
            }
            if (meta >= 0x6 && meta <= 0x9 && blockType1 == BlockType.RAIL)
            {
                blockType1 = BlockType.RAIL_CORNER;
            }
        }
        if (blockType1 == BlockType.SIGNPOST)
        {
            return (axis == UP) ? (meta + 0x4) % 0x10 : (meta + 0xC) % 0x10;
        }
        if (blockType1 == BlockType.LEVER && (axis == UP || axis == DOWN))
        {
            switch (meta)
            {
            case 0x5:
                return 0x6;
            case 0x6:
                return 0x5;
            case 0x7:
                return 0x0;
            case 0x0:
                return 0x7;
            }
        }
        if (blockType1 == BlockType.MUSHROOM_CAP)
        {
            if (meta % 0x2 == 0)
            {
                blockType1 = BlockType.MUSHROOM_CAP_SIDE;
            }
            else
            {
                blockType1 = BlockType.MUSHROOM_CAP_CORNER;
            }
        }
        if (blockType1 == BlockType.VINE)
        {
            return ((meta << 1) | ((meta & 0x8) >> 3));
        }

        final ForgeDirection orientation = metadataToDirection(blockType1, meta);
        final ForgeDirection rotated = orientation.getRotation(axis);
        return directionToMetadata(blockType1, rotated);
    }

    private static ForgeDirection metadataToDirection(final BlockType blockType, int meta)
    {
        int meta1 = meta;
        if (blockType == BlockType.LEVER)
        {
            if (meta1 == 0x6)
            {
                meta1 = 0x5;
            }
            else if (meta1 == 0x0)
            {
                meta1 = 0x7;
            }
        }

        if (MAPPINGS.containsKey(blockType))
        {
            final BiMap<Integer, ForgeDirection> biMap = MAPPINGS.get(blockType);
            if (biMap.containsKey(meta1))
            {
                return biMap.get(meta1);
            }
        }

        if (blockType == BlockType.TORCH)
        {
            return ForgeDirection.getOrientation(6 - meta1);
        }
        if (blockType == BlockType.STAIR)
        {
            return ForgeDirection.getOrientation(5 - meta1);
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL)
        {
            return ForgeDirection.getOrientation(meta1);
        }
        if (blockType == BlockType.BUTTON)
        {
            return ForgeDirection.getOrientation(6 - meta1);
        }
        if (blockType == BlockType.TRAPDOOR)
        {
            return ForgeDirection.getOrientation(meta1 + 2).getOpposite();
        }

        return ForgeDirection.UNKNOWN;
    }

    private static int directionToMetadata(final BlockType blockType, ForgeDirection direction)
    {
        ForgeDirection direction1 = direction;
        if ((blockType == BlockType.LOG || blockType == BlockType.ANVIL) && (direction1.offsetX + direction1.offsetY + direction1.offsetZ) < 0)
        {
            direction1 = direction1.getOpposite();
        }

        if (MAPPINGS.containsKey(blockType))
        {
            final BiMap<ForgeDirection, Integer> biMap = MAPPINGS.get(blockType).inverse();
            if (biMap.containsKey(direction1))
            {
                return biMap.get(direction1);
            }
        }

        if (blockType == BlockType.TORCH)
        {
            if (direction1.ordinal() >= 1)
            {
                return 6 - direction1.ordinal();
            }
        }
        if (blockType == BlockType.STAIR)
        {
            return 5 - direction1.ordinal();
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL)
        {
            return direction1.ordinal();
        }
        if (blockType == BlockType.BUTTON)
        {
            if (direction1.ordinal() >= 2)
            {
                return 6 - direction1.ordinal();
            }
        }
        if (blockType == BlockType.TRAPDOOR)
        {
            return direction1.getOpposite().ordinal() - 2;
        }

        return -1;
    }

    static
    {
        BiMap<Integer, ForgeDirection> biMap;

        biMap = HashBiMap.create(3);
        biMap.put(0x0, UP);
        biMap.put(0x4, EAST);
        biMap.put(0x8, SOUTH);
        MAPPINGS.put(BlockType.LOG, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, SOUTH);
        biMap.put(0x1, WEST);
        biMap.put(0x2, NORTH);
        biMap.put(0x3, EAST);
        MAPPINGS.put(BlockType.BED, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x2, EAST);
        biMap.put(0x3, WEST);
        biMap.put(0x4, NORTH);
        biMap.put(0x5, SOUTH);
        MAPPINGS.put(BlockType.RAIL_ASCENDING, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x6, WEST);
        biMap.put(0x7, NORTH);
        biMap.put(0x8, EAST);
        biMap.put(0x9, SOUTH);
        MAPPINGS.put(BlockType.RAIL_CORNER, biMap);

        biMap = HashBiMap.create(6);
        biMap.put(0x1, EAST);
        biMap.put(0x2, WEST);
        biMap.put(0x3, SOUTH);
        biMap.put(0x4, NORTH);
        biMap.put(0x5, UP);
        biMap.put(0x7, DOWN);
        MAPPINGS.put(BlockType.LEVER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, WEST);
        biMap.put(0x1, NORTH);
        biMap.put(0x2, EAST);
        biMap.put(0x3, SOUTH);
        MAPPINGS.put(BlockType.DOOR, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x0, NORTH);
        biMap.put(0x1, EAST);
        biMap.put(0x2, SOUTH);
        biMap.put(0x3, WEST);
        MAPPINGS.put(BlockType.REDSTONE_REPEATER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x1, EAST);
        biMap.put(0x3, SOUTH);
        biMap.put(0x7, NORTH);
        biMap.put(0x9, WEST);
        MAPPINGS.put(BlockType.MUSHROOM_CAP_CORNER, biMap);

        biMap = HashBiMap.create(4);
        biMap.put(0x2, NORTH);
        biMap.put(0x4, WEST);
        biMap.put(0x6, EAST);
        biMap.put(0x8, SOUTH);
        MAPPINGS.put(BlockType.MUSHROOM_CAP_SIDE, biMap);

        biMap = HashBiMap.create(2);
        biMap.put(0x0, SOUTH);
        biMap.put(0x1, EAST);
        MAPPINGS.put(BlockType.ANVIL, biMap);
    }
}
