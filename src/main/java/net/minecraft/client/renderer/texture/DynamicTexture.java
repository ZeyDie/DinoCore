package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class DynamicTexture extends AbstractTexture
{
    private final int[] dynamicTextureData;

    /** width of this icon in pixels */
    private final int width;

    /** height of this icon in pixels */
    private final int height;

    public DynamicTexture(final BufferedImage par1BufferedImage)
    {
        this(par1BufferedImage.getWidth(), par1BufferedImage.getHeight());
        par1BufferedImage.getRGB(0, 0, par1BufferedImage.getWidth(), par1BufferedImage.getHeight(), this.dynamicTextureData, 0, par1BufferedImage.getWidth());
        this.updateDynamicTexture();
    }

    public DynamicTexture(final int par1, final int par2)
    {
        this.width = par1;
        this.height = par2;
        this.dynamicTextureData = new int[par1 * par2];
        TextureUtil.allocateTexture(this.getGlTextureId(), par1, par2);
    }

    public void loadTexture(final ResourceManager par1ResourceManager) throws IOException {}

    public void updateDynamicTexture()
    {
        TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
    }

    public int[] getTextureData()
    {
        return this.dynamicTextureData;
    }
}
