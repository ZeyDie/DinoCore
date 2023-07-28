package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class TextureMap extends AbstractTexture implements TickableTextureObject, IconRegister
{
    public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
    private final List listAnimatedSprites = Lists.newArrayList();
    private final Map mapRegisteredSprites = Maps.newHashMap();
    private final Map mapUploadedSprites = Maps.newHashMap();

    /** 0 = terrain.png, 1 = items.png */
    public final int textureType;
    public final String basePath;
    private final TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");

    public TextureMap(final int par1, final String par2Str)
    {
        this.textureType = par1;
        this.basePath = par2Str;
        this.registerIcons();
    }

    private void initMissingImage()
    {
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][] {TextureUtil.missingTextureData}));
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
    }

    public void loadTexture(final ResourceManager par1ResourceManager) throws IOException
    {
        this.initMissingImage();
        this.loadTextureAtlas(par1ResourceManager);
    }

    public void loadTextureAtlas(final ResourceManager par1ResourceManager)
    {
        registerIcons(); //Re-gather list of Icons, allows for addition/removal of blocks/items after this map was initially constructed.

        final int i = Minecraft.getGLMaximumTextureSize();
        final Stitcher stitcher = new Stitcher(i, i, true);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        ForgeHooksClient.onTextureStitchedPre(this);
        final Iterator iterator = this.mapRegisteredSprites.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            final ResourceLocation resourcelocation = new ResourceLocation((String)entry.getKey());
            final TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)entry.getValue();
            final ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", new Object[] {this.basePath, resourcelocation.getResourcePath(), ".png"}));

            try
            {
                if (!textureatlassprite.load(par1ResourceManager, resourcelocation1)) continue;
            }
            catch (final RuntimeException runtimeexception)
            {
                Minecraft.getMinecraft().getLogAgent().logSevere(String.format("Unable to parse animation metadata from %s: %s", new Object[] {resourcelocation1, runtimeexception.getMessage()}));
                continue;
            }
            catch (final IOException ioexception)
            {
                Minecraft.getMinecraft().getLogAgent().logSevere("Using missing texture, unable to load: " + resourcelocation1);
                continue;
            }

            stitcher.addSprite(textureatlassprite);
        }

        stitcher.addSprite(this.missingImage);

        try
        {
            stitcher.doStitch();
        }
        catch (final StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        TextureUtil.allocateTexture(this.getGlTextureId(), stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        final HashMap hashmap = Maps.newHashMap(this.mapRegisteredSprites);
        Iterator iterator1 = stitcher.getStichSlots().iterator();
        TextureAtlasSprite textureatlassprite1;

        while (iterator1.hasNext())
        {
            textureatlassprite1 = (TextureAtlasSprite)iterator1.next();
            final String s = textureatlassprite1.getIconName();
            hashmap.remove(s);
            this.mapUploadedSprites.put(s, textureatlassprite1);

            try
            {
                TextureUtil.uploadTextureSub(textureatlassprite1.getFrameTextureData(0), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
            }
            catch (final Throwable throwable)
            {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                crashreportcategory.addCrashSection("Atlas path", this.basePath);
                crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
                throw new ReportedException(crashreport);
            }

            if (textureatlassprite1.hasAnimationMetadata())
            {
                this.listAnimatedSprites.add(textureatlassprite1);
            }
            else
            {
                textureatlassprite1.clearFramesTextureData();
            }
        }

        iterator1 = hashmap.values().iterator();

        while (iterator1.hasNext())
        {
            textureatlassprite1 = (TextureAtlasSprite)iterator1.next();
            textureatlassprite1.copyFrom(this.missingImage);
        }
        ForgeHooksClient.onTextureStitchedPost(this);
    }

    private void registerIcons()
    {
        this.mapRegisteredSprites.clear();
        int i;
        int j;

        if (this.textureType == 0)
        {
            final Block[] ablock = Block.blocksList;
            i = ablock.length;

            for (j = 0; j < i; ++j)
            {
                final Block block = ablock[j];

                if (block != null)
                {
                    block.registerIcons(this);
                }
            }

            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
            RenderManager.instance.updateIcons(this);
        }

        final Item[] aitem = Item.itemsList;
        i = aitem.length;

        for (j = 0; j < i; ++j)
        {
            final Item item = aitem[j];

            if (item != null && item.getSpriteNumber() == this.textureType)
            {
                item.registerIcons(this);
            }
        }
    }

    public TextureAtlasSprite getAtlasSprite(final String par1Str)
    {
        TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.mapUploadedSprites.get(par1Str);

        if (textureatlassprite == null)
        {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations()
    {
        TextureUtil.bindTexture(this.getGlTextureId());
        final Iterator iterator = this.listAnimatedSprites.iterator();

        while (iterator.hasNext())
        {
            final TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)iterator.next();
            textureatlassprite.updateAnimation();
        }
    }

    public Icon registerIcon(String par1Str)
    {
        String par1Str1 = par1Str;
        if (par1Str1 == null)
        {
            (new RuntimeException("Don\'t register null!")).printStackTrace();
            par1Str1 = "null"; //Don't allow things to actually register null..
        }

        Object object = (TextureAtlasSprite)this.mapRegisteredSprites.get(par1Str1);

        if (object == null)
        {
            if (this.textureType == 1)
            {
                if ("clock".equals(par1Str1))
                {
                    object = new TextureClock(par1Str1);
                }
                else if ("compass".equals(par1Str1))
                {
                    object = new TextureCompass(par1Str1);
                }
                else
                {
                    object = new TextureAtlasSprite(par1Str1);
                }
            }
            else
            {
                object = new TextureAtlasSprite(par1Str1);
            }

            this.mapRegisteredSprites.put(par1Str1, object);
        }

        return (Icon)object;
    }

    public int getTextureType()
    {
        return this.textureType;
    }

    public void tick()
    {
        this.updateAnimations();
    }

    //===================================================================================================
    //                                           Forge Start
    //===================================================================================================
    /**
     * Grabs the registered entry for the specified name, returning null if there was not a entry.
     * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
     *
     * @param name The name of the entry to find
     * @return The registered entry, null if nothing was registered.
     */
    public TextureAtlasSprite getTextureExtry(final String name)
    {
        return (TextureAtlasSprite)mapRegisteredSprites.get(name);
    }

    /**
     * Adds a texture registry entry to this map for the specified name if one does not already exist.
     * Returns false if the map already contains a entry for the specified name.
     *
     * @param name Entry name
     * @param entry Entry instance
     * @return True if the entry was added to the map, false otherwise.
     */
    public boolean setTextureEntry(final String name, final TextureAtlasSprite entry)
    {
        if (!mapRegisteredSprites.containsKey(name))
        {
            mapRegisteredSprites.put(name, entry);
            return true;
        }
        return false;
    }
}
