package net.minecraft.network;

import net.minecraft.network.packet.Packet;

import java.util.concurrent.Callable;

class CallablePacketClass implements Callable
{
    final Packet thePacket;

    final NetServerHandler theNetServerHandler;

    CallablePacketClass(final NetServerHandler par1NetServerHandler, final Packet par2Packet)
    {
        this.theNetServerHandler = par1NetServerHandler;
        this.thePacket = par2Packet;
    }

    public String getPacketClass()
    {
        return this.thePacket.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.getPacketClass();
    }
}
