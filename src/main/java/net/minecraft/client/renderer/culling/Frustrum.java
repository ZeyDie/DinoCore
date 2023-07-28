package net.minecraft.client.renderer.culling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;

@SideOnly(Side.CLIENT)
public class Frustrum implements ICamera
{
    private ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public void setPosition(final double par1, final double par3, final double par5)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.zPosition = par5;
    }

    /**
     * Calls the clipping helper. Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11)
    {
        return this.clippingHelper.isBoxInFrustum(par1 - this.xPosition, par3 - this.yPosition, par5 - this.zPosition, par7 - this.xPosition, par9 - this.yPosition, par11 - this.zPosition);
    }

    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoundingBoxInFrustum(final AxisAlignedBB par1AxisAlignedBB)
    {
        return this.isBoxInFrustum(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ, par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
    }
}
