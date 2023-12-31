package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableMouseLocation implements Callable
{
    final int field_90026_a;

    final int field_90024_b;

    final EntityRenderer theEntityRenderer;

    CallableMouseLocation(final EntityRenderer par1EntityRenderer, final int par2, final int par3)
    {
        this.theEntityRenderer = par1EntityRenderer;
        this.field_90026_a = par2;
        this.field_90024_b = par3;
    }

    public String callMouseLocation()
    {
        return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] {Integer.valueOf(this.field_90026_a), Integer.valueOf(this.field_90024_b), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
    }

    public Object call()
    {
        return this.callMouseLocation();
    }
}
