package net.minecraftforge.client.model.obj;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GroupObject
{

    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(final String name)
    {
        this(name, -1);
    }

    public GroupObject(final String name, final int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    public void render()
    {
        if (!faces.isEmpty())
        {
            final Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawing(glDrawingMode);
            render(tessellator);
            tessellator.draw();
        }
    }

    public void render(final Tessellator tessellator)
    {
        if (!faces.isEmpty())
        {
            for (final Face face : faces)
            {
                face.addFaceForRender(tessellator);
            }
        }
    }
}
