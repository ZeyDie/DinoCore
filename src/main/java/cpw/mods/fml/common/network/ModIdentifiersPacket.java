/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.network;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDENTIFIERS;

public class ModIdentifiersPacket extends FMLPacket
{

    private Map<String, Integer> modIds = Maps.newHashMap();

    public ModIdentifiersPacket()
    {
        super(MOD_IDENTIFIERS);
    }

    @Override
    public byte[] generatePacket(final Object... data)
    {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        final Collection<NetworkModHandler >networkMods = FMLNetworkHandler.instance().getNetworkIdMap().values();

        dat.writeInt(networkMods.size());
        for (final NetworkModHandler handler : networkMods)
        {
            dat.writeUTF(handler.getContainer().getModId());
            dat.writeInt(handler.getNetworkId());
        }

        // TODO send the other id maps as well
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(final byte[] data)
    {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        final int listSize = dat.readInt();
        for (int i = 0; i < listSize; i++)
        {
            final String modId = dat.readUTF();
            final int networkId = dat.readInt();
            modIds.put(modId, networkId);
        }
        return this;
    }

    @Override
    public void execute(final INetworkManager network, final FMLNetworkHandler handler, final NetHandler netHandler, final String userName)
    {
        for (final Entry<String,Integer> idEntry : modIds.entrySet())
        {
            handler.bindNetworkId(idEntry.getKey(), idEntry.getValue());
        }
        // TODO other id maps
    }
}
