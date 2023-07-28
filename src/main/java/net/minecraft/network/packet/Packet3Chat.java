package net.minecraft.network.packet;

import net.minecraft.util.ChatMessageComponent;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet3Chat extends Packet
{
    /** The message being sent. */
    public String message;
    private boolean isServer;

    public Packet3Chat()
    {
        this.isServer = true;
    }

    public Packet3Chat(final ChatMessageComponent par1ChatMessageComponent)
    {
        this(par1ChatMessageComponent.toJson());
    }

    public Packet3Chat(final ChatMessageComponent par1ChatMessageComponent, final boolean par2)
    {
        this(par1ChatMessageComponent.toJson(), par2);
    }

    public Packet3Chat(final String par1Str)
    {
        this(par1Str, true);
    }

    public Packet3Chat(String par1Str, final boolean par2)
    {
        String par1Str1 = par1Str;
        this.isServer = true;

        if (par1Str1.length() > 32767)
        {
            par1Str1 = par1Str1.substring(0, 32767);
        }

        this.message = par1Str1;
        this.isServer = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.message = readString(par1DataInput, 32767);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        writeString(this.message, par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleChat(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.message.length() * 2;
    }

    /**
     * Get whether this is a server
     */
    public boolean getIsServer()
    {
        return this.isServer;
    }

    /**
     * If this returns true, the packet may be processed on any thread; otherwise it is queued for the main thread to
     * handle.
     */
    public boolean canProcessAsync()
    {
        return !this.message.startsWith("/");
    }
}
