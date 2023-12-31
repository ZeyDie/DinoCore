package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;

public class CraftHanging extends CraftEntity implements Hanging {
    public CraftHanging(final CraftServer server, final net.minecraft.entity.EntityHanging entity) {
        super(server, entity);
    }

    public BlockFace getAttachedFace() {
        return getFacing().getOppositeFace();
    }

    public void setFacingDirection(final BlockFace face) {
        setFacingDirection(face, false);
    }

    public boolean setFacingDirection(final BlockFace face, final boolean force) {
        final Block block = getLocation().getBlock().getRelative(getAttachedFace()).getRelative(face.getOppositeFace()).getRelative(getFacing());
        final net.minecraft.entity.EntityHanging hanging = getHandle();
        final int x = hanging.xPosition;
        int y = hanging.yPosition;
        int z = hanging.zPosition;
        final int dir = hanging.hangingDirection;
        hanging.xPosition = block.getX();
        hanging.yPosition = block.getY();
        hanging.zPosition = block.getZ();
        switch (face) {
            case SOUTH:
            default:
                getHandle().setDirection(0);
                break;
            case WEST:
                getHandle().setDirection(1);
                break;
            case NORTH:
                getHandle().setDirection(2);
                break;
            case EAST:
                getHandle().setDirection(3);
                break;
        }
        if (!force && !hanging.onValidSurface()) {
            // Revert since it doesn't fit
            hanging.xPosition = x;
            hanging.yPosition = y;
            hanging.zPosition = z;
            hanging.setDirection(dir);
            return false;
        }
        return true;
    }

    public BlockFace getFacing() {
        switch (this.getHandle().hangingDirection) {
            case 0:
            default:
                return BlockFace.SOUTH;
            case 1:
                return BlockFace.WEST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.EAST;
        }
    }

    @Override
    public net.minecraft.entity.EntityHanging getHandle() {
        return (net.minecraft.entity.EntityHanging) entity;
    }

    @Override
    public String toString() {
        return "CraftHanging";
    }

    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
