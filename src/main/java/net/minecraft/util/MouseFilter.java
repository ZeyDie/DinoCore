package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MouseFilter
{
    private float field_76336_a;
    private float field_76334_b;
    private float field_76335_c;

    /**
     * Smooths mouse input
     */
    public float smooth(float par1, final float par2)
    {
        float par11 = par1;
        this.field_76336_a += par11;
        par11 = (this.field_76336_a - this.field_76334_b) * par2;
        this.field_76335_c += (par11 - this.field_76335_c) * 0.5F;

        if (par11 > 0.0F && par11 > this.field_76335_c || par11 < 0.0F && par11 < this.field_76335_c)
        {
            par11 = this.field_76335_c;
        }

        this.field_76334_b += par11;
        return par11;
    }
}
