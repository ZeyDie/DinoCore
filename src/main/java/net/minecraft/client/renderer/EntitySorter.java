package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

import java.util.Comparator;

@SideOnly(Side.CLIENT)
public class EntitySorter implements Comparator
{
    /** Entity position X */
    private double entityPosX;

    /** Entity position Y */
    private double entityPosY;

    /** Entity position Z */
    private double entityPosZ;

    public EntitySorter(final Entity par1Entity)
    {
        this.entityPosX = -par1Entity.posX;
        this.entityPosY = -par1Entity.posY;
        this.entityPosZ = -par1Entity.posZ;
    }

    /**
     * Sorts the two world renderers according to their distance to a given entity.
     */
    public int sortByDistanceToEntity(final WorldRenderer par1WorldRenderer, final WorldRenderer par2WorldRenderer)
    {
        final double d0 = (double)par1WorldRenderer.posXPlus + this.entityPosX;
        final double d1 = (double)par1WorldRenderer.posYPlus + this.entityPosY;
        final double d2 = (double)par1WorldRenderer.posZPlus + this.entityPosZ;
        final double d3 = (double)par2WorldRenderer.posXPlus + this.entityPosX;
        final double d4 = (double)par2WorldRenderer.posYPlus + this.entityPosY;
        final double d5 = (double)par2WorldRenderer.posZPlus + this.entityPosZ;
        return (int)((d0 * d0 + d1 * d1 + d2 * d2 - (d3 * d3 + d4 * d4 + d5 * d5)) * 1024.0D);
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.sortByDistanceToEntity((WorldRenderer)par1Obj, (WorldRenderer)par2Obj);
    }
}
