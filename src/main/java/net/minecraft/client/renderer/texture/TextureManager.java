package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.client.resources.ResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class TextureManager implements Tickable, ResourceManagerReloadListener
{
    private final Map mapTextureObjects = Maps.newHashMap();
    private final Map mapResourceLocations = Maps.newHashMap();
    private final List listTickables = Lists.newArrayList();
    private final Map mapTextureCounters = Maps.newHashMap();
    private ResourceManager theResourceManager;

    public TextureManager(final ResourceManager par1ResourceManager)
    {
        this.theResourceManager = par1ResourceManager;
    }

    public void bindTexture(final ResourceLocation par1ResourceLocation)
    {
        Object object = (TextureObject)this.mapTextureObjects.get(par1ResourceLocation);

        if (object == null)
        {
            object = new SimpleTexture(par1ResourceLocation);
            this.loadTexture(par1ResourceLocation, (TextureObject)object);
        }

        TextureUtil.bindTexture(((TextureObject)object).getGlTextureId());
    }

    public ResourceLocation getResourceLocation(final int par1)
    {
        return (ResourceLocation)this.mapResourceLocations.get(Integer.valueOf(par1));
    }

    public boolean loadTextureMap(final ResourceLocation par1ResourceLocation, final TextureMap par2TextureMap)
    {
        if (this.loadTickableTexture(par1ResourceLocation, par2TextureMap))
        {
            this.mapResourceLocations.put(Integer.valueOf(par2TextureMap.getTextureType()), par1ResourceLocation);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTickableTexture(final ResourceLocation par1ResourceLocation, final TickableTextureObject par2TickableTextureObject)
    {
        if (this.loadTexture(par1ResourceLocation, par2TickableTextureObject))
        {
            this.listTickables.add(par2TickableTextureObject);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTexture(final ResourceLocation par1ResourceLocation, TextureObject par2TextureObject)
    {
        TextureObject par2TextureObject1 = par2TextureObject;
        boolean flag = true;

        try
        {
            ((TextureObject) par2TextureObject1).loadTexture(this.theResourceManager);
        }
        catch (final IOException ioexception)
        {
            Minecraft.getMinecraft().getLogAgent().logWarningException("Failed to load texture: " + par1ResourceLocation, ioexception);
            par2TextureObject1 = TextureUtil.missingTexture;
            this.mapTextureObjects.put(par1ResourceLocation, par2TextureObject1);
            flag = false;
        }
        catch (final Throwable throwable)
        {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", par1ResourceLocation);
            crashreportcategory.addCrashSectionCallable("Texture object class", new TextureManagerINNER1(this, (TextureObject) par2TextureObject1));
            throw new ReportedException(crashreport);
        }

        this.mapTextureObjects.put(par1ResourceLocation, par2TextureObject1);
        return flag;
    }

    public TextureObject getTexture(final ResourceLocation par1ResourceLocation)
    {
        return (TextureObject)this.mapTextureObjects.get(par1ResourceLocation);
    }

    public ResourceLocation getDynamicTextureLocation(final String par1Str, final DynamicTexture par2DynamicTexture)
    {
        Integer integer = (Integer)this.mapTextureCounters.get(par1Str);

        if (integer == null)
        {
            integer = Integer.valueOf(1);
        }
        else
        {
            integer = Integer.valueOf(integer.intValue() + 1);
        }

        this.mapTextureCounters.put(par1Str, integer);
        final ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] {par1Str, integer}));
        this.loadTexture(resourcelocation, par2DynamicTexture);
        return resourcelocation;
    }

    public void tick()
    {
        final Iterator iterator = this.listTickables.iterator();

        while (iterator.hasNext())
        {
            final Tickable tickable = (Tickable)iterator.next();
            tickable.tick();
        }
    }

    public void onResourceManagerReload(final ResourceManager par1ResourceManager)
    {
        final Iterator iterator = this.mapTextureObjects.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            this.loadTexture((ResourceLocation)entry.getKey(), (TextureObject)entry.getValue());
        }
    }
}
