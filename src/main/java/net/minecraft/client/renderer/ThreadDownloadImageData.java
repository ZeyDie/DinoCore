package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class ThreadDownloadImageData extends AbstractTexture
{
    private final String imageUrl;
    private final IImageBuffer imageBuffer;
    private BufferedImage bufferedImage;
    private Thread imageThread;
    private SimpleTexture imageLocation;
    private boolean textureUploaded;

    public ThreadDownloadImageData(final String par1Str, final ResourceLocation par2ResourceLocation, final IImageBuffer par3IImageBuffer)
    {
        this.imageUrl = par1Str;
        this.imageBuffer = par3IImageBuffer;
        this.imageLocation = par2ResourceLocation != null ? new SimpleTexture(par2ResourceLocation) : null;
    }

    public int getGlTextureId()
    {
        final int i = super.getGlTextureId();

        if (!this.textureUploaded && this.bufferedImage != null)
        {
            TextureUtil.uploadTextureImage(i, this.bufferedImage);
            this.textureUploaded = true;
        }

        return i;
    }

    public void getBufferedImage(final BufferedImage par1BufferedImage)
    {
        this.bufferedImage = par1BufferedImage;
    }

    public void loadTexture(final ResourceManager par1ResourceManager) throws IOException
    {
        if (this.bufferedImage == null)
        {
            if (this.imageLocation != null)
            {
                this.imageLocation.loadTexture(par1ResourceManager);
                this.glTextureId = this.imageLocation.getGlTextureId();
            }
        }
        else
        {
            TextureUtil.uploadTextureImage(this.getGlTextureId(), this.bufferedImage);
        }

        if (this.imageThread == null)
        {
            this.imageThread = new ThreadDownloadImageDataINNER1(this);
            this.imageThread.setDaemon(true);
            this.imageThread.setName("Skin downloader: " + this.imageUrl);
            this.imageThread.start();
        }
    }

    public boolean isTextureUploaded()
    {
        this.getGlTextureId();
        return this.textureUploaded;
    }

    static String getImageUrl(final ThreadDownloadImageData par0ThreadDownloadImageData)
    {
        return par0ThreadDownloadImageData.imageUrl;
    }

    static IImageBuffer getImageBuffer(final ThreadDownloadImageData par0ThreadDownloadImageData)
    {
        return par0ThreadDownloadImageData.imageBuffer;
    }
}
