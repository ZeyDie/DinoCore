package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class TexturedQuad
{
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;

    public TexturedQuad(final PositionTextureVertex[] par1ArrayOfPositionTextureVertex)
    {
        this.vertexPositions = par1ArrayOfPositionTextureVertex;
        this.nVertices = par1ArrayOfPositionTextureVertex.length;
    }

    public TexturedQuad(final PositionTextureVertex[] par1ArrayOfPositionTextureVertex, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7)
    {
        this(par1ArrayOfPositionTextureVertex);
        final float f2 = 0.0F / par6;
        final float f3 = 0.0F / par7;
        par1ArrayOfPositionTextureVertex[0] = par1ArrayOfPositionTextureVertex[0].setTexturePosition((float)par4 / par6 - f2, (float)par3 / par7 + f3);
        par1ArrayOfPositionTextureVertex[1] = par1ArrayOfPositionTextureVertex[1].setTexturePosition((float)par2 / par6 + f2, (float)par3 / par7 + f3);
        par1ArrayOfPositionTextureVertex[2] = par1ArrayOfPositionTextureVertex[2].setTexturePosition((float)par2 / par6 + f2, (float)par5 / par7 - f3);
        par1ArrayOfPositionTextureVertex[3] = par1ArrayOfPositionTextureVertex[3].setTexturePosition((float)par4 / par6 - f2, (float)par5 / par7 - f3);
    }

    public void flipFace()
    {
        final PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

        for (int i = 0; i < this.vertexPositions.length; ++i)
        {
            apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }

        this.vertexPositions = apositiontexturevertex;
    }

    public void draw(final Tessellator par1Tessellator, final float par2)
    {
        final Vec3 vec3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        final Vec3 vec31 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        final Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        par1Tessellator.startDrawingQuads();

        if (this.invertNormal)
        {
            par1Tessellator.setNormal(-((float)vec32.xCoord), -((float)vec32.yCoord), -((float)vec32.zCoord));
        }
        else
        {
            par1Tessellator.setNormal((float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord);
        }

        for (int i = 0; i < 4; ++i)
        {
            final PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            par1Tessellator.addVertexWithUV((double)((float)positiontexturevertex.vector3D.xCoord * par2), (double)((float)positiontexturevertex.vector3D.yCoord * par2), (double)((float)positiontexturevertex.vector3D.zCoord * par2), (double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY);
        }

        par1Tessellator.draw();
    }
}
