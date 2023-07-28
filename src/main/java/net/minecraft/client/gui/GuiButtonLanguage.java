package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonLanguage extends GuiButton
{
    public GuiButtonLanguage(final int par1, final int par2, final int par3)
    {
        super(par1, par2, par3, 20, 20, "");
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3)
    {
        if (this.drawButton)
        {
            par1Minecraft.getTextureManager().bindTexture(GuiButton.buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            final boolean flag = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int k = 106;

            if (flag)
            {
                k += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, k, this.width, this.height);
        }
    }
}
