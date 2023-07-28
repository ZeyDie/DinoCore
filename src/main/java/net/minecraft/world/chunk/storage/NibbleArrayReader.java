package net.minecraft.world.chunk.storage;

public class NibbleArrayReader
{
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArrayReader(final byte[] par1ArrayOfByte, final int par2)
    {
        this.data = par1ArrayOfByte;
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    public int get(final int par1, final int par2, final int par3)
    {
        final int l = par1 << this.depthBitsPlusFour | par3 << this.depthBits | par2;
        final int i1 = l >> 1;
        final int j1 = l & 1;
        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }
}
