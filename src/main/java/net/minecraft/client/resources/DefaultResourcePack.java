package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.data.MetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class DefaultResourcePack implements ResourcePack
{
    public static final Set defaultResourceDomains = ImmutableSet.of("minecraft");
    private final Map mapResourceFiles = Maps.newHashMap();
    private final File fileAssets;

    public DefaultResourcePack(final File par1File)
    {
        this.fileAssets = par1File;
        this.readAssetsDir(this.fileAssets);
    }

    public InputStream getInputStream(final ResourceLocation par1ResourceLocation) throws IOException
    {
        final InputStream inputstream = this.getResourceStream(par1ResourceLocation);

        if (inputstream != null)
        {
            return inputstream;
        }
        else
        {
            final File file1 = (File)this.mapResourceFiles.get(par1ResourceLocation.toString());

            if (file1 != null)
            {
                return new FileInputStream(file1);
            }
            else
            {
                throw new FileNotFoundException(par1ResourceLocation.getResourcePath());
            }
        }
    }

    private InputStream getResourceStream(final ResourceLocation par1ResourceLocation)
    {
        return DefaultResourcePack.class.getResourceAsStream("/assets/minecraft/" + par1ResourceLocation.getResourcePath());
    }

    public void addResourceFile(final String par1Str, final File par2File)
    {
        this.mapResourceFiles.put((new ResourceLocation(par1Str)).toString(), par2File);
    }

    public boolean resourceExists(final ResourceLocation par1ResourceLocation)
    {
        return this.getResourceStream(par1ResourceLocation) != null || this.mapResourceFiles.containsKey(par1ResourceLocation.toString());
    }

    public Set getResourceDomains()
    {
        return defaultResourceDomains;
    }

    public void readAssetsDir(final File par1File)
    {
        if (par1File.isDirectory())
        {
            final File[] afile = par1File.listFiles();
            final int i = afile.length;

            for (int j = 0; j < i; ++j)
            {
                final File file2 = afile[j];
                this.readAssetsDir(file2);
            }
        }
        else
        {
            this.addResourceFile(AbstractResourcePack.getRelativeName(this.fileAssets, par1File), par1File);
        }
    }

    public MetadataSection getPackMetadata(final MetadataSerializer par1MetadataSerializer, final String par2Str) throws IOException
    {
        return AbstractResourcePack.readMetadata(par1MetadataSerializer, DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.mcmeta")).getResourcePath()), par2Str);
    }

    public BufferedImage getPackImage() throws IOException
    {
        return ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.png")).getResourcePath()));
    }

    public String getPackName()
    {
        return "Default";
    }
}
