package net.minecraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableGLInfo implements Callable
{
    /** The Minecraft instance. */
    final Minecraft mc;

    CallableGLInfo(final Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public String getTexturePack()
    {
        return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
    }

    public Object call()
    {
        return this.getTexturePack();
    }
}
