package net.minecraft.entity;

public class EntityEggInfo
{
    /** The entityID of the spawned mob */
    public int spawnedID;

    /** Base color of the egg */
    public int primaryColor;

    /** Color of the egg spots */
    public int secondaryColor;

    public EntityEggInfo(final int par1, final int par2, final int par3)
    {
        this.spawnedID = par1;
        this.primaryColor = par2;
        this.secondaryColor = par3;
    }
}
