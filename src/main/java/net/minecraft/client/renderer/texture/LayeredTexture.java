package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LayeredTexture extends AbstractTexture
{
    public final List layeredTextureNames;

    public LayeredTexture(final String ... par1ArrayOfStr)
    {
        this.layeredTextureNames = Lists.newArrayList(par1ArrayOfStr);
    }

    public void loadTexture(final ResourceManager par1ResourceManager) throws IOException
    {
        BufferedImage bufferedimage = null;

        try
        {
            final Iterator iterator = this.layeredTextureNames.iterator();

            while (iterator.hasNext())
            {
                final String s = (String)iterator.next();

                if (s != null)
                {
                    final InputStream inputstream = par1ResourceManager.getResource(new ResourceLocation(s)).getInputStream();
                    final BufferedImage bufferedimage1 = ImageIO.read(inputstream);

                    if (bufferedimage == null)
                    {
                        bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), 2);
                    }

                    bufferedimage.getGraphics().drawImage(bufferedimage1, 0, 0, (ImageObserver)null);
                }
            }
        }
        catch (final IOException ioexception)
        {
            ioexception.printStackTrace();
            return;
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
    }
}
