package net.minecraft.world;

public enum EnumSkyBlock
{
    Sky(15),
    Block(0);
    public final int defaultLightValue;

    private EnumSkyBlock(final int par3)
    {
        this.defaultLightValue = par3;
    }
}
