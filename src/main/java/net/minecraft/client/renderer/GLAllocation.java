package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class GLAllocation
{
    private static final Map field_74531_a = new HashMap();
    private static final List field_74530_b = new ArrayList();

    /**
     * Generates the specified number of display lists and returns the first index.
     */
    public static synchronized int generateDisplayLists(final int par0)
    {
        final int j = GL11.glGenLists(par0);
        field_74531_a.put(Integer.valueOf(j), Integer.valueOf(par0));
        return j;
    }

    public static synchronized void deleteDisplayLists(final int par0)
    {
        GL11.glDeleteLists(par0, ((Integer)field_74531_a.remove(Integer.valueOf(par0))).intValue());
    }

    public static synchronized void func_98302_b()
    {
        for (int i = 0; i < field_74530_b.size(); ++i)
        {
            GL11.glDeleteTextures(((Integer)field_74530_b.get(i)).intValue());
        }

        field_74530_b.clear();
    }

    /**
     * Deletes all textures and display lists. Called when Minecraft is shutdown to free up resources.
     */
    public static synchronized void deleteTexturesAndDisplayLists()
    {
        final Iterator iterator = field_74531_a.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            GL11.glDeleteLists(((Integer)entry.getKey()).intValue(), ((Integer)entry.getValue()).intValue());
        }

        field_74531_a.clear();
        func_98302_b();
    }

    /**
     * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static synchronized ByteBuffer createDirectByteBuffer(final int par0)
    {
        return ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
    }

    /**
     * Creates and returns a direct int buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static IntBuffer createDirectIntBuffer(final int par0)
    {
        return createDirectByteBuffer(par0 << 2).asIntBuffer();
    }

    /**
     * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up
     * access.
     */
    public static FloatBuffer createDirectFloatBuffer(final int par0)
    {
        return createDirectByteBuffer(par0 << 2).asFloatBuffer();
    }
}
