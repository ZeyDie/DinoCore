package net.minecraft.block.material;

final class MaterialWeb extends Material
{
    MaterialWeb(final MapColor par1MapColor)
    {
        super(par1MapColor);
    }

    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return false;
    }
}
