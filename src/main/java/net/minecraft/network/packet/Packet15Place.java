package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet15Place extends Packet
{
    private int xPosition;
    private int yPosition;
    private int zPosition;

    /** The offset to use for block/item placement. */
    private int direction;
    private ItemStack itemStack;

    /** The offset from xPosition where the actual click took place */
    private float xOffset;

    /** The offset from yPosition where the actual click took place */
    private float yOffset;

    /** The offset from zPosition where the actual click took place */
    private float zOffset;

    public Packet15Place() {}

    @SideOnly(Side.CLIENT)
    public Packet15Place(final int par1, final int par2, final int par3, final int par4, final ItemStack par5ItemStack, final float par6, final float par7, final float par8)
    {
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.direction = par4;
        this.itemStack = par5ItemStack != null ? par5ItemStack.copy() : null;
        this.xOffset = par6;
        this.yOffset = par7;
        this.zOffset = par8;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readUnsignedByte();
        this.zPosition = par1DataInput.readInt();
        this.direction = par1DataInput.readUnsignedByte();
        this.itemStack = readItemStack(par1DataInput);
        this.xOffset = (float)par1DataInput.readUnsignedByte() / 16.0F;
        this.yOffset = (float)par1DataInput.readUnsignedByte() / 16.0F;
        this.zOffset = (float)par1DataInput.readUnsignedByte() / 16.0F;
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.write(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.write(this.direction);
        writeItemStack(this.itemStack, par1DataOutput);
        par1DataOutput.write((int)(this.xOffset * 16.0F));
        par1DataOutput.write((int)(this.yOffset * 16.0F));
        par1DataOutput.write((int)(this.zOffset * 16.0F));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handlePlace(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 19;
    }

    public int getXPosition()
    {
        return this.xPosition;
    }

    public int getYPosition()
    {
        return this.yPosition;
    }

    public int getZPosition()
    {
        return this.zPosition;
    }

    public int getDirection()
    {
        return this.direction;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    /**
     * Returns the offset from xPosition where the actual click took place
     */
    public float getXOffset()
    {
        return this.xOffset;
    }

    /**
     * Returns the offset from yPosition where the actual click took place
     */
    public float getYOffset()
    {
        return this.yOffset;
    }

    /**
     * Returns the offset from zPosition where the actual click took place
     */
    public float getZOffset()
    {
        return this.zOffset;
    }
}
