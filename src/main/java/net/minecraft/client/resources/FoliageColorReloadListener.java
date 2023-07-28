package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class FoliageColorReloadListener implements ResourceManagerReloadListener
{
    private static final ResourceLocation field_130079_a = new ResourceLocation("textures/colormap/foliage.png");

    public void onResourceManagerReload(final ResourceManager par1ResourceManager)
    {
        try
        {
            ColorizerFoliage.setFoliageBiomeColorizer(TextureUtil.readImageData(par1ResourceManager, field_130079_a));
        }
        catch (final IOException ioexception)
        {
            ;
        }
    }
}
