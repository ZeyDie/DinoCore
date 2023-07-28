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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_REQUEST;
import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_RESPONSE;

public class ModListRequestPacket extends FMLPacket
{
    private List<String> sentModList;
    private byte compatibilityLevel;

    public ModListRequestPacket()
    {
        super(MOD_LIST_REQUEST);
    }

    @Override
    public byte[] generatePacket(final Object... data)
    {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        final Set<ModContainer> activeMods = FMLNetworkHandler.instance().getNetworkModList();
        dat.writeInt(activeMods.size());
        for (final ModContainer mc : activeMods)
        {
            dat.writeUTF(mc.getModId());
        }
        dat.writeByte(FMLNetworkHandler.getCompatibilityLevel());
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(final byte[] data)
    {
        sentModList = Lists.newArrayList();
        final ByteArrayDataInput in = ByteStreams.newDataInput(data);
        final int listSize = in.readInt();
        for (int i = 0; i < listSize; i++)
        {
            sentModList.add(in.readUTF());
        }
        try
        {
            compatibilityLevel = in.readByte();
        }
        catch (final IllegalStateException e)
        {
            FMLLog.fine("No compatibility byte found - the server is too old");
        }
        return this;
    }

    /**
     *
     * This packet is executed on the client to evaluate the server's mod list against
     * the client
     *
     * @see cpw.mods.fml.common.network.FMLPacket#execute(INetworkManager, FMLNetworkHandler, NetHandler, String)
     */
    @Override
    public void execute(final INetworkManager mgr, final FMLNetworkHandler handler, final NetHandler netHandler, final String userName)
    {
        final List<String> missingMods = Lists.newArrayList();
        final Map<String,String> modVersions = Maps.newHashMap();
        final Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());

        for (final String m : sentModList)
        {
            final ModContainer mc = indexedModList.get(m);
            if (mc == null)
            {
                missingMods.add(m);
                continue;
            }
            indexedModList.remove(m);
            modVersions.put(m, mc.getVersion());
        }

        if (!indexedModList.isEmpty())
        {
            for (final Entry<String, ModContainer> e : indexedModList.entrySet())
            {
                if (e.getValue().isNetworkMod())
                {
                    final NetworkModHandler missingHandler = FMLNetworkHandler.instance().findNetworkModHandler(e.getValue());
                    if (missingHandler.requiresServerSide())
                    {
                        // TODO : what should we do if a mod is marked "serverSideRequired"? Stop the connection?
                        FMLLog.warning("The mod %s was not found on the server you connected to, but requested that the server side be present", e.getKey());
                    }
                }
            }
        }

        FMLLog.fine("The server has compatibility level %d", compatibilityLevel);
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel(compatibilityLevel);

        mgr.addToSendQueue(PacketDispatcher.getPacket("FML", FMLPacket.makePacket(MOD_LIST_RESPONSE, modVersions, missingMods)));
    }
}
