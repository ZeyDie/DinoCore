package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.FIRST_PERSON_MAP;

@SideOnly(Side.CLIENT)
public class ItemRenderer
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /** A reference to the Minecraft object. */
    private Minecraft mc;
    private ItemStack itemToRender;

    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;

    /** Instance of RenderBlocks. */
    private RenderBlocks renderBlocksInstance = new RenderBlocks();
    public final MapItemRenderer mapItemRenderer;

    /** The index of the currently held item (0-8, or -1 if not yet updated) */
    private int equippedItemSlot = -1;

    public ItemRenderer(final Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.mapItemRenderer = new MapItemRenderer(par1Minecraft.gameSettings, par1Minecraft.getTextureManager());
    }

    public void renderItem(final EntityLivingBase par1EntityLivingBase, final ItemStack par2ItemStack, final int par3)
    {
        this.renderItem(par1EntityLivingBase, par2ItemStack, par3, ItemRenderType.EQUIPPED);
    }

    /**
     * Renders the item stack for being in an entity's hand Args: itemStack
     */
    public void renderItem(final EntityLivingBase par1EntityLivingBase, final ItemStack par2ItemStack, final int par3, final ItemRenderType type)
    {
        GL11.glPushMatrix();
        final TextureManager texturemanager = this.mc.getTextureManager();

        Block block = null;
        if (par2ItemStack.getItem() instanceof ItemBlock && par2ItemStack.itemID < Block.blocksList.length)
        {
            block = Block.blocksList[par2ItemStack.itemID];
        }

        final IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(par2ItemStack, type);
        if (customRenderer != null)
        {
            texturemanager.bindTexture(texturemanager.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
            ForgeHooksClient.renderEquippedItem(type, customRenderer, renderBlocksInstance, par1EntityLivingBase, par2ItemStack);
        }
        else if (block != null && par2ItemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.blocksList[par2ItemStack.itemID].getRenderType()))
        {
            texturemanager.bindTexture(texturemanager.getResourceLocation(0));
            this.renderBlocksInstance.renderBlockAsItem(Block.blocksList[par2ItemStack.itemID], par2ItemStack.getItemDamage(), 1.0F);
        }
        else
        {
            final Icon icon = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);

            if (icon == null)
            {
                GL11.glPopMatrix();
                return;
            }

            texturemanager.bindTexture(texturemanager.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
            final Tessellator tessellator = Tessellator.instance;
            final float f = icon.getMinU();
            final float f1 = icon.getMaxU();
            final float f2 = icon.getMinV();
            final float f3 = icon.getMaxV();
            final float f4 = 0.0F;
            final float f5 = 0.3F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-f4, -f5, 0.0F);
            final float f6 = 1.5F;
            GL11.glScalef(f6, f6, f6);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(tessellator, f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

            if (par2ItemStack.hasEffect(par3))
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                texturemanager.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                final float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                final float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }

        GL11.glPopMatrix();
    }

    /**
     * Renders an item held in hand as a 2D texture with thickness
     */
    public static void renderItemIn2D(final Tessellator par0Tessellator, final float par1, final float par2, final float par3, final float par4, final int par5, final int par6, final float par7)
    {
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)par1, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)par1, (double)par2);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - par7), (double)par1, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, (double)(0.0F - par7), (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, (double)(0.0F - par7), (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - par7), (double)par1, (double)par4);
        par0Tessellator.draw();
        final float f5 = 0.5F * (par1 - par3) / (float)par5;
        final float f6 = 0.5F * (par4 - par2) / (float)par6;
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;

        for (k = 0; k < par5; ++k)
        {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, (double)(0.0F - par7), (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, 0.0D, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, 0.0D, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, (double)(0.0F - par7), (double)f8, (double)par2);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < par5; ++k)
        {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            f9 = f7 + 1.0F / (float)par5;
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, (double)(0.0F - par7), (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, 0.0D, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, 0.0D, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, (double)(0.0F - par7), (double)f8, (double)par4);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < par6; ++k)
        {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            f9 = f7 + 1.0F / (float)par6;
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, 0.0D, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, 0.0D, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, (double)(0.0F - par7), (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, (double)(0.0F - par7), (double)par1, (double)f8);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < par6; ++k)
        {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, 0.0D, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, 0.0D, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, (double)(0.0F - par7), (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, (double)(0.0F - par7), (double)par3, (double)f8);
        }

        par0Tessellator.draw();
    }

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     */
    public void renderItemInFirstPerson(final float par1)
    {
        final float f1 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * par1;
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        final float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * par1;
        GL11.glPushMatrix();
        GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * par1, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        final EntityPlayerSP entityplayersp = (EntityPlayerSP)entityclientplayermp;
        final float f3 = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * par1;
        final float f4 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * par1;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);
        final ItemStack itemstack = this.itemToRender;
        float f5 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
        f5 = 1.0F;
        int i = this.mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), 0);
        final int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f6;
        float f7;
        float f8;

        if (itemstack != null)
        {
            i = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, 0);
            f7 = (float)(i >> 16 & 255) / 255.0F;
            f8 = (float)(i >> 8 & 255) / 255.0F;
            f6 = (float)(i & 255) / 255.0F;
            GL11.glColor4f(f5 * f7, f5 * f8, f5 * f6, 1.0F);
        }
        else
        {
            GL11.glColor4f(f5, f5, f5, 1.0F);
        }

        float f9;
        final float f10;
        float f11;
        final float f12;
        Render render;
        RenderPlayer renderplayer;

        if (itemstack != null && itemstack.getItem() instanceof ItemMap)
        {
            GL11.glPushMatrix();
            f12 = 0.8F;
            f7 = entityclientplayermp.getSwingProgress(par1);
            f8 = MathHelper.sin(f7 * (float)Math.PI);
            f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            GL11.glTranslatef(-f6 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.2F, -f8 * 0.2F);
            f7 = 1.0F - f2 / 45.0F + 0.1F;

            if (f7 < 0.0F)
            {
                f7 = 0.0F;
            }

            if (f7 > 1.0F)
            {
                f7 = 1.0F;
            }

            f7 = -MathHelper.cos(f7 * (float)Math.PI) * 0.5F + 0.5F;
            GL11.glTranslatef(0.0F, 0.0F * f12 - (1.0F - f1) * 1.2F - f7 * 0.5F + 0.04F, -0.9F * f12);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f7 * -85.0F, 0.0F, 0.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            this.mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());

            for (k = 0; k < 2; ++k)
            {
                final int l = k * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)l);
                GL11.glRotatef((float)(-45 * l), 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float)(-65 * l), 0.0F, 1.0F, 0.0F);
                render = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
                renderplayer = (RenderPlayer)render;
                f11 = 1.0F;
                GL11.glScalef(f11, f11, f11);
                renderplayer.renderFirstPersonArm(this.mc.thePlayer);
                GL11.glPopMatrix();
            }

            f8 = entityclientplayermp.getSwingProgress(par1);
            f6 = MathHelper.sin(f8 * f8 * (float)Math.PI);
            f9 = MathHelper.sin(MathHelper.sqrt_float(f8) * (float)Math.PI);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f9 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f9 * 80.0F, 1.0F, 0.0F, 0.0F);
            f10 = 0.38F;
            GL11.glScalef(f10, f10, f10);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
            f11 = 0.015625F;
            GL11.glScalef(f11, f11, f11);
            this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
            final Tessellator tessellator = Tessellator.instance;
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            tessellator.startDrawingQuads();
            final byte b0 = 7;
            tessellator.addVertexWithUV((double)(0 - b0), (double)(128 + b0), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(128 + b0), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(0 - b0), 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)(0 - b0), (double)(0 - b0), 0.0D, 0.0D, 0.0D);
            tessellator.draw();

            final IItemRenderer custom = MinecraftForgeClient.getItemRenderer(itemstack, FIRST_PERSON_MAP);
            final MapData mapdata = ((ItemMap)itemstack.getItem()).getMapData(itemstack, this.mc.theWorld);

            if (custom == null)
            {
                if (mapdata != null)
                {
                    this.mapItemRenderer.renderMap(this.mc.thePlayer, this.mc.getTextureManager(), mapdata);
                }
            }
            else
            {
                custom.renderItem(FIRST_PERSON_MAP, itemstack, mc.thePlayer, mc.getTextureManager(), mapdata);
            }

            GL11.glPopMatrix();
        }
        else if (itemstack != null)
        {
            GL11.glPushMatrix();
            f12 = 0.8F;

            if (entityclientplayermp.getItemInUseCount() > 0)
            {
                final EnumAction enumaction = itemstack.getItemUseAction();

                if (enumaction == EnumAction.eat || enumaction == EnumAction.drink)
                {
                    f8 = (float)entityclientplayermp.getItemInUseCount() - par1 + 1.0F;
                    f6 = 1.0F - f8 / (float)itemstack.getMaxItemUseDuration();
                    f9 = 1.0F - f6;
                    f9 = f9 * f9 * f9;
                    f9 = f9 * f9 * f9;
                    f9 = f9 * f9 * f9;
                    f10 = 1.0F - f9;
                    GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f8 / 4.0F * (float)Math.PI) * 0.1F) * (float)((double)f6 > 0.2D ? 1 : 0), 0.0F);
                    GL11.glTranslatef(f10 * 0.6F, -f10 * 0.5F, 0.0F);
                    GL11.glRotatef(f10 * 90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(f10 * 10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f10 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            }
            else
            {
                f7 = entityclientplayermp.getSwingProgress(par1);
                f8 = MathHelper.sin(f7 * (float)Math.PI);
                f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
                GL11.glTranslatef(-f6 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.2F, -f8 * 0.2F);
            }

            GL11.glTranslatef(0.7F * f12, -0.65F * f12 - (1.0F - f1) * 0.6F, -0.9F * f12);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            f7 = entityclientplayermp.getSwingProgress(par1);
            f8 = MathHelper.sin(f7 * f7 * (float)Math.PI);
            f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            GL11.glRotatef(-f8 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f6 * 80.0F, 1.0F, 0.0F, 0.0F);
            f9 = 0.4F;
            GL11.glScalef(f9, f9, f9);
            float f13;
            float f14;

            if (entityclientplayermp.getItemInUseCount() > 0)
            {
                final EnumAction enumaction1 = itemstack.getItemUseAction();

                if (enumaction1 == EnumAction.block)
                {
                    GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                    GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                }
                else if (enumaction1 == EnumAction.bow)
                {
                    GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                    f11 = (float)itemstack.getMaxItemUseDuration() - ((float)entityclientplayermp.getItemInUseCount() - par1 + 1.0F);
                    f13 = f11 / 20.0F;
                    f13 = (f13 * f13 + f13 * 2.0F) / 3.0F;

                    if (f13 > 1.0F)
                    {
                        f13 = 1.0F;
                    }

                    if (f13 > 0.1F)
                    {
                        GL11.glTranslatef(0.0F, MathHelper.sin((f11 - 0.1F) * 1.3F) * 0.01F * (f13 - 0.1F), 0.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.0F, f13 * 0.1F);
                    GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    f14 = 1.0F + f13 * 0.2F;
                    GL11.glScalef(1.0F, 1.0F, f14);
                    GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                }
            }

            if (itemstack.getItem().shouldRotateAroundWhenRendering())
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (itemstack.getItem().requiresMultipleRenderPasses())
            {
                this.renderItem(entityclientplayermp, itemstack, 0, ItemRenderType.EQUIPPED_FIRST_PERSON);
                for (int x = 1; x < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); x++)
                {
                    final int i1 = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, x);
                    f11 = (float)(i1 >> 16 & 255) / 255.0F;
                    f13 = (float)(i1 >> 8 & 255) / 255.0F;
                    f14 = (float)(i1 & 255) / 255.0F;
                    GL11.glColor4f(f5 * f11, f5 * f13, f5 * f14, 1.0F);
                    this.renderItem(entityclientplayermp, itemstack, x, ItemRenderType.EQUIPPED_FIRST_PERSON);
                }
            }
            else
            {
                this.renderItem(entityclientplayermp, itemstack, 0, ItemRenderType.EQUIPPED_FIRST_PERSON);
            }

            GL11.glPopMatrix();
        }
        else if (!entityclientplayermp.isInvisible())
        {
            GL11.glPushMatrix();
            f12 = 0.8F;
            f7 = entityclientplayermp.getSwingProgress(par1);
            f8 = MathHelper.sin(f7 * (float)Math.PI);
            f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            GL11.glTranslatef(-f6 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.4F, -f8 * 0.4F);
            GL11.glTranslatef(0.8F * f12, -0.75F * f12 - (1.0F - f1) * 0.6F, -0.9F * f12);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            f7 = entityclientplayermp.getSwingProgress(par1);
            f8 = MathHelper.sin(f7 * f7 * (float)Math.PI);
            f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
            GL11.glRotatef(f6 * 70.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f8 * 20.0F, 0.0F, 0.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
            GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
            GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(5.6F, 0.0F, 0.0F);
            render = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
            renderplayer = (RenderPlayer)render;
            f11 = 1.0F;
            GL11.glScalef(f11, f11, f11);
            renderplayer.renderFirstPersonArm(this.mc.thePlayer);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(final float par1)
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        if (this.mc.thePlayer.isBurning())
        {
            this.renderFireInFirstPerson(par1);
        }

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock())
        {
            final int i = MathHelper.floor_double(this.mc.thePlayer.posX);
            final int j = MathHelper.floor_double(this.mc.thePlayer.posY);
            final int k = MathHelper.floor_double(this.mc.thePlayer.posZ);
            int l = this.mc.theWorld.getBlockId(i, j, k);

            if (this.mc.theWorld.isBlockNormalCube(i, j, k))
            {
                this.renderInsideOfBlock(par1, Block.blocksList[l].getBlockTextureFromSide(2));
            }
            else
            {
                for (int i1 = 0; i1 < 8; ++i1)
                {
                    final float f1 = ((float)((i1 >> 0) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
                    final float f2 = ((float)((i1 >> 1) % 2) - 0.5F) * this.mc.thePlayer.height * 0.2F;
                    final float f3 = ((float)((i1 >> 2) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
                    final int j1 = MathHelper.floor_float((float)i + f1);
                    final int k1 = MathHelper.floor_float((float)j + f2);
                    final int l1 = MathHelper.floor_float((float)k + f3);

                    if (this.mc.theWorld.isBlockNormalCube(j1, k1, l1))
                    {
                        l = this.mc.theWorld.getBlockId(j1, k1, l1);
                    }
                }
            }

            if (Block.blocksList[l] != null)
            {
                this.renderInsideOfBlock(par1, Block.blocksList[l].getBlockTextureFromSide(2));
            }
        }

        if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
        {
            this.renderWarpedTextureOverlay(par1);
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    /**
     * Renders the texture of the block the player is inside as an overlay. Args: partialTickTime, blockTextureIndex
     */
    private void renderInsideOfBlock(final float par1, final Icon par2Icon)
    {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        final Tessellator tessellator = Tessellator.instance;
        final float f1 = 0.1F;
        GL11.glColor4f(f1, f1, f1, 0.5F);
        GL11.glPushMatrix();
        final float f2 = -1.0F;
        final float f3 = 1.0F;
        final float f4 = -1.0F;
        final float f5 = 1.0F;
        final float f6 = -0.5F;
        final float f7 = par2Icon.getMinU();
        final float f8 = par2Icon.getMaxU();
        final float f9 = par2Icon.getMinV();
        final float f10 = par2Icon.getMaxV();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)f2, (double)f4, (double)f6, (double)f8, (double)f10);
        tessellator.addVertexWithUV((double)f3, (double)f4, (double)f6, (double)f7, (double)f10);
        tessellator.addVertexWithUV((double)f3, (double)f5, (double)f6, (double)f7, (double)f9);
        tessellator.addVertexWithUV((double)f2, (double)f5, (double)f6, (double)f8, (double)f9);
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     */
    private void renderWarpedTextureOverlay(final float par1)
    {
        this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
        final Tessellator tessellator = Tessellator.instance;
        final float f1 = this.mc.thePlayer.getBrightness(par1);
        GL11.glColor4f(f1, f1, f1, 0.5F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        final float f2 = 4.0F;
        final float f3 = -1.0F;
        final float f4 = 1.0F;
        final float f5 = -1.0F;
        final float f6 = 1.0F;
        final float f7 = -0.5F;
        final float f8 = -this.mc.thePlayer.rotationYaw / 64.0F;
        final float f9 = this.mc.thePlayer.rotationPitch / 64.0F;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)f3, (double)f5, (double)f7, (double)(f2 + f8), (double)(f2 + f9));
        tessellator.addVertexWithUV((double)f4, (double)f5, (double)f7, (double)(0.0F + f8), (double)(f2 + f9));
        tessellator.addVertexWithUV((double)f4, (double)f6, (double)f7, (double)(0.0F + f8), (double)(0.0F + f9));
        tessellator.addVertexWithUV((double)f3, (double)f6, (double)f7, (double)(f2 + f8), (double)(0.0F + f9));
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson(final float par1)
    {
        final Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final float f1 = 1.0F;

        for (int i = 0; i < 2; ++i)
        {
            GL11.glPushMatrix();
            final Icon icon = Block.fire.getFireIcon(1);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            final float f2 = icon.getMinU();
            final float f3 = icon.getMaxU();
            final float f4 = icon.getMinV();
            final float f5 = icon.getMaxV();
            final float f6 = (0.0F - f1) / 2.0F;
            final float f7 = f6 + f1;
            final float f8 = 0.0F - f1 / 2.0F;
            final float f9 = f8 + f1;
            final float f10 = -0.5F;
            GL11.glTranslatef((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GL11.glRotatef((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)f6, (double)f8, (double)f10, (double)f3, (double)f5);
            tessellator.addVertexWithUV((double)f7, (double)f8, (double)f10, (double)f2, (double)f5);
            tessellator.addVertexWithUV((double)f7, (double)f9, (double)f10, (double)f2, (double)f4);
            tessellator.addVertexWithUV((double)f6, (double)f9, (double)f10, (double)f3, (double)f4);
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void updateEquippedItem()
    {
        this.prevEquippedProgress = this.equippedProgress;
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        final ItemStack itemstack = entityclientplayermp.inventory.getCurrentItem();
        boolean flag = this.equippedItemSlot == entityclientplayermp.inventory.currentItem && itemstack == this.itemToRender;

        if (this.itemToRender == null && itemstack == null)
        {
            flag = true;
        }

        if (itemstack != null && this.itemToRender != null && itemstack != this.itemToRender && itemstack.itemID == this.itemToRender.itemID && itemstack.getItemDamage() == this.itemToRender.getItemDamage())
        {
            this.itemToRender = itemstack;
            flag = true;
        }

        final float f = 0.4F;
        final float f1 = flag ? 1.0F : 0.0F;
        float f2 = f1 - this.equippedProgress;

        if (f2 < -f)
        {
            f2 = -f;
        }

        if (f2 > f)
        {
            f2 = f;
        }

        this.equippedProgress += f2;

        if (this.equippedProgress < 0.1F)
        {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityclientplayermp.inventory.currentItem;
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress()
    {
        this.equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2()
    {
        this.equippedProgress = 0.0F;
    }
}
