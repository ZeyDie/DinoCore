package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;

    protected void drawHorizontalLine(int par1, int par2, final int par3, final int par4)
    {
        int par11 = par1;
        int par21 = par2;
        if (par21 < par11)
        {
            final int i1 = par11;
            par11 = par21;
            par21 = i1;
        }

        drawRect(par11, par3, par21 + 1, par3 + 1, par4);
    }

    protected void drawVerticalLine(final int par1, int par2, int par3, final int par4)
    {
        int par21 = par2;
        int par31 = par3;
        if (par31 < par21)
        {
            final int i1 = par21;
            par21 = par31;
            par31 = i1;
        }

        drawRect(par1, par21 + 1, par1 + 1, par31, par4);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int par0, int par1, int par2, int par3, final int par4)
    {
        int par01 = par0;
        int par21 = par2;
        int par11 = par1;
        int par31 = par3;
        int j1;

        if (par01 < par21)
        {
            j1 = par01;
            par01 = par21;
            par21 = j1;
        }

        if (par11 < par31)
        {
            j1 = par11;
            par11 = par31;
            par31 = j1;
        }

        final float f = (float)(par4 >> 24 & 255) / 255.0F;
        final float f1 = (float)(par4 >> 16 & 255) / 255.0F;
        final float f2 = (float)(par4 >> 8 & 255) / 255.0F;
        final float f3 = (float)(par4 & 255) / 255.0F;
        final Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) par01, (double) par31, 0.0D);
        tessellator.addVertex((double) par21, (double) par31, 0.0D);
        tessellator.addVertex((double) par21, (double) par11, 0.0D);
        tessellator.addVertex((double) par01, (double) par11, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    protected void drawGradientRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final float f = (float)(par5 >> 24 & 255) / 255.0F;
        final float f1 = (float)(par5 >> 16 & 255) / 255.0F;
        final float f2 = (float)(par5 >> 8 & 255) / 255.0F;
        final float f3 = (float)(par5 & 255) / 255.0F;
        final float f4 = (float)(par6 >> 24 & 255) / 255.0F;
        final float f5 = (float)(par6 >> 16 & 255) / 255.0F;
        final float f6 = (float)(par6 >> 8 & 255) / 255.0F;
        final float f7 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)par3, (double)par2, (double)this.zLevel);
        tessellator.addVertex((double)par1, (double)par2, (double)this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)par1, (double)par4, (double)this.zLevel);
        tessellator.addVertex((double)par3, (double)par4, (double)this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public void drawCenteredString(final FontRenderer par1FontRenderer, final String par2Str, final int par3, final int par4, final int par5)
    {
        par1FontRenderer.drawStringWithShadow(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
    }

    /**
     * Renders the specified text to the screen.
     */
    public void drawString(final FontRenderer par1FontRenderer, final String par2Str, final int par3, final int par4, final int par5)
    {
        par1FontRenderer.drawStringWithShadow(par2Str, par3, par4, par5);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final float f = 0.00390625F;
        final float f1 = 0.00390625F;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
        tessellator.draw();
    }

    public void drawTexturedModelRectFromIcon(final int par1, final int par2, final Icon par3Icon, final int par4, final int par5)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
        tessellator.draw();
    }
}
