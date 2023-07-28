package cpw.mods.fml.client;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.resources.FolderResourcePack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class FMLFolderResourcePack extends FolderResourcePack {

    private ModContainer container;

    public FMLFolderResourcePack(final ModContainer container)
    {
        super(container.getSource());
        this.container = container;
    }

    @Override
    public String getPackName()
    {
        return "FMLFileResourcePack:"+container.getName();
    }
    @Override
    protected InputStream getInputStreamByName(final String resourceName) throws IOException
    {
        try
        {
            return super.getInputStreamByName(resourceName);
        }
        catch (final IOException ioe)
        {
            if ("pack.mcmeta".equals(resourceName))
            {
                FMLLog.log(container.getName(), Level.WARNING, "Mod %s is missing a pack.mcmeta file, things may not work well", container.getName());
                return new ByteArrayInputStream(("{\n" +
                        " \"pack\": {\n"+
                        "   \"description\": \"dummy FML pack for "+container.getName()+"\",\n"+
                        "   \"pack_format\": 1\n"+
                        "}\n" +
                        "}").getBytes(Charsets.UTF_8));
            }
            else throw ioe;
        }
    }

    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return ImageIO.read(getInputStreamByName(container.getMetadata().logoFile));
    }

}
