package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class Packet20NamedEntitySpawn extends Packet
{
    /** The entity ID, in this case it's the player ID. */
    public int entityId;

    /** The player's name. */
    public String name;

    /** The player's X position. */
    public int xPosition;

    /** The player's Y position. */
    public int yPosition;

    /** The player's Z position. */
    public int zPosition;

    /** The player's rotation. */
    public byte rotation;

    /** The player's pitch. */
    public byte pitch;

    /** The current item the player is holding. */
    public int currentItem;
    private DataWatcher metadata;
    private List metadataWatchableObjects;

    public Packet20NamedEntitySpawn() {}

    public Packet20NamedEntitySpawn(final EntityPlayer par1EntityPlayer)
    {
        this.entityId = par1EntityPlayer.entityId;

        // CraftBukkit start - Limit name length to 16 characters
        if (par1EntityPlayer.username.length() > 16)
        {
            this.name = par1EntityPlayer.username.substring(0, 16);
        }
        else
        {
            this.name = par1EntityPlayer.username;
        }

        // CraftBukkit end
        this.name = par1EntityPlayer.getCommandSenderName();
        this.xPosition = MathHelper.floor_double(par1EntityPlayer.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityPlayer.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityPlayer.posZ * 32.0D);
        this.rotation = (byte)((int)(par1EntityPlayer.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1EntityPlayer.rotationPitch * 256.0F / 360.0F));
        final ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        this.currentItem = itemstack == null ? 0 : itemstack.itemID;
        this.metadata = par1EntityPlayer.getDataWatcher();
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.name = readString(par1DataInput, 16);
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readInt();
        this.zPosition = par1DataInput.readInt();
        this.rotation = par1DataInput.readByte();
        this.pitch = par1DataInput.readByte();
        this.currentItem = par1DataInput.readShort();
        this.metadataWatchableObjects = DataWatcher.readWatchableObjects(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        writeString(this.name, par1DataOutput);
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeInt(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.writeByte(this.rotation);
        par1DataOutput.writeByte(this.pitch);
        par1DataOutput.writeShort(this.currentItem);
        this.metadata.writeWatchableObjects(par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleNamedEntitySpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 28;
    }

    @SideOnly(Side.CLIENT)
    public List getWatchedMetadata()
    {
        if (this.metadataWatchableObjects == null)
        {
            this.metadataWatchableObjects = this.metadata.getAllWatched();
        }

        return this.metadataWatchableObjects;
    }
}
