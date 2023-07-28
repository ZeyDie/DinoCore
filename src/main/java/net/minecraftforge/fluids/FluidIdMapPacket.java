
package net.minecraftforge.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraftforge.common.network.ForgePacket;

import java.util.Map;

public class FluidIdMapPacket extends ForgePacket
{
    private BiMap<String, Integer> fluidIds = HashBiMap.create();

    @Override
    public byte[] generatePacket()
    {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();

        dat.writeInt(FluidRegistry.maxID);
        for (final Map.Entry<String, Integer> entry : FluidRegistry.fluidIDs.entrySet())
        {
            dat.writeUTF(entry.getKey());
            dat.writeInt(entry.getValue());
        }
        return dat.toByteArray();
    }

    @Override
    public ForgePacket consumePacket(final byte[] data)
    {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        final int listSize = dat.readInt();
        for (int i = 0; i < listSize; i++) {
            final String fluidName = dat.readUTF();
            final int fluidId = dat.readInt();
            fluidIds.put(fluidName, fluidId);
        }
        return this;
    }

    @Override
    public void execute(final INetworkManager network, final EntityPlayer player)
    {
        FluidRegistry.initFluidIDs(fluidIds);
    }
}
