package net.minecraftforge.client.model.obj;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vertex
{

    public float x, y, z;

    public Vertex(final float x, final float y)
    {
        this(x, y, 0.0F);
    }

    public Vertex(final float x, final float y, final float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
