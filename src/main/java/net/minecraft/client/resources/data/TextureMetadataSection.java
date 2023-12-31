package net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureMetadataSection implements MetadataSection
{
    private final boolean textureBlur;
    private final boolean textureClamp;

    public TextureMetadataSection(final boolean par1, final boolean par2)
    {
        this.textureBlur = par1;
        this.textureClamp = par2;
    }

    public boolean getTextureBlur()
    {
        return this.textureBlur;
    }

    public boolean getTextureClamp()
    {
        return this.textureClamp;
    }
}
