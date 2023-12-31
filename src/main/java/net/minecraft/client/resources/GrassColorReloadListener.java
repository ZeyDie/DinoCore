package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GrassColorReloadListener implements ResourceManagerReloadListener
{
    private static final ResourceLocation field_130078_a = new ResourceLocation("textures/colormap/grass.png");

    public void onResourceManagerReload(final ResourceManager par1ResourceManager)
    {
        try
        {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(par1ResourceManager, field_130078_a));
        }
        catch (final IOException ioexception)
        {
            ;
        }
    }
}
