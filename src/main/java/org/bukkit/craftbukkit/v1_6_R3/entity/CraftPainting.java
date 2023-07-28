package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.Art;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.CraftArt;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;

public class CraftPainting extends CraftHanging implements Painting {

    public CraftPainting(final CraftServer server, final net.minecraft.entity.item.EntityPainting entity) {
        super(server, entity);
    }

    public Art getArt() {
        final net.minecraft.util.EnumArt art = getHandle().art;
        return CraftArt.NotchToBukkit(art);
    }

    public boolean setArt(final Art art) {
        return setArt(art, false);
    }

    public boolean setArt(final Art art, final boolean force) {
        final net.minecraft.entity.item.EntityPainting painting = this.getHandle();
        final net.minecraft.util.EnumArt oldArt = painting.art;
        painting.art = CraftArt.BukkitToNotch(art);
        painting.setDirection(painting.hangingDirection);
        if (!force && !painting.onValidSurface()) {
            // Revert painting since it doesn't fit
            painting.art = oldArt;
            painting.setDirection(painting.hangingDirection);
            return false;
        }
        this.update();
        return true;
    }

    public boolean setFacingDirection(final BlockFace face, final boolean force) {
        if (super.setFacingDirection(face, force)) {
            update();
            return true;
        }

        return false;
    }

    private void update() {
        final net.minecraft.world.WorldServer world = ((CraftWorld) getWorld()).getHandle();
        final net.minecraft.entity.item.EntityPainting painting = new net.minecraft.entity.item.EntityPainting(world);
        painting.xPosition = getHandle().xPosition;
        painting.yPosition = getHandle().yPosition;
        painting.zPosition = getHandle().zPosition;
        painting.art = getHandle().art;
        painting.setDirection(getHandle().hangingDirection);
        getHandle().setDead();
        getHandle().velocityChanged = true; // because this occurs when the painting is broken, so it might be important
        world.spawnEntityInWorld(painting);
        this.entity = painting;
    }

    @Override
    public net.minecraft.entity.item.EntityPainting getHandle() {
        return (net.minecraft.entity.item.EntityPainting) entity;
    }

    @Override
    public String toString() {
        return "CraftPainting{art=" + getArt() + "}";
    }

    public EntityType getType() {
        return EntityType.PAINTING;
    }
}
