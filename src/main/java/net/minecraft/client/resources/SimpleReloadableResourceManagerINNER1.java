package net.minecraft.client.resources;

import com.google.common.base.Function;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class SimpleReloadableResourceManagerINNER1 implements Function
{
    final SimpleReloadableResourceManager theSimpleReloadableResourceManager;

    SimpleReloadableResourceManagerINNER1(final SimpleReloadableResourceManager par1SimpleReloadableResourceManager)
    {
        this.theSimpleReloadableResourceManager = par1SimpleReloadableResourceManager;
    }

    public String apply(final ResourcePack par1ResourcePack)
    {
        return par1ResourcePack.getPackName();
    }

    public Object apply(final Object par1Obj)
    {
        return this.apply((ResourcePack)par1Obj);
    }
}
