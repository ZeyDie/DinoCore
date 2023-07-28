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
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static cpw.mods.fml.common.network.FMLPacket.Type.*;

public class ModListResponsePacket extends FMLPacket
{
    private Map<String,String> modVersions;
    private List<String> missingMods;

    public ModListResponsePacket()
    {
        super(MOD_LIST_RESPONSE);
    }

    @Override
    public byte[] generatePacket(final Object... data)
    {
        final Map<String,String> modVersions = (Map<String, String>) data[0];
        final List<String> missingMods = (List<String>) data[1];
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(modVersions.size());
        for (final Entry<String, String> version : modVersions.entrySet())
        {
            dat.writeUTF(version.getKey());
            dat.writeUTF(version.getValue());
        }
        dat.writeInt(missingMods.size());
        for (final String missing : missingMods)
        {
            dat.writeUTF(missing);
        }
        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(final byte[] data)
    {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        final int versionListSize = dat.readInt();
        modVersions = Maps.newHashMapWithExpectedSize(versionListSize);
        for (int i = 0; i < versionListSize; i++)
        {
            final String modName = dat.readUTF();
            final String modVersion = dat.readUTF();
            modVersions.put(modName, modVersion);
        }

        final int missingModSize = dat.readInt();
        missingMods = Lists.newArrayListWithExpectedSize(missingModSize);

        for (int i = 0; i < missingModSize; i++)
        {
            missingMods.add(dat.readUTF());
        }
        return this;
    }

    @Override
    public void execute(final INetworkManager network, final FMLNetworkHandler handler, final NetHandler netHandler, final String userName)
    {
        final Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());
        final List<String> missingClientMods = Lists.newArrayList();
        final List<String> versionIncorrectMods = Lists.newArrayList();

        for (final String m : missingMods)
        {
            final ModContainer mc = indexedModList.get(m);
            final NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (networkMod.requiresClientSide())
            {
                missingClientMods.add(m);
            }
        }

        for (final Entry<String,String> modVersion : modVersions.entrySet())
        {
            final ModContainer mc = indexedModList.get(modVersion.getKey());
            final NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (!networkMod.acceptVersion(modVersion.getValue()))
            {
                versionIncorrectMods.add(modVersion.getKey());
            }
        }

        final Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "FML";
        if (!missingClientMods.isEmpty() || !versionIncorrectMods.isEmpty())
        {
            pkt.data = FMLPacket.makePacket(MOD_MISSING, missingClientMods, versionIncorrectMods);
            // Cauldron start - disable unneeded console spam
            if (MinecraftServer.getServer().cauldronConfig.connectionLogging.getValue()) {
                Logger.getLogger("Minecraft").info(String.format("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods));
                FMLLog.info("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods);
            }
            // Cauldron end// Mark this as bad
            FMLNetworkHandler.setHandlerState((NetLoginHandler) netHandler, FMLNetworkHandler.MISSING_MODS_OR_VERSIONS);
            pkt.length = pkt.data.length;
            network.addToSendQueue(pkt);
        }
        else
        {
            pkt.data = FMLPacket.makePacket(MOD_IDENTIFIERS, netHandler);
            // Cauldron start - disable unneeded console spam
            if (MinecraftServer.getServer().cauldronConfig.connectionLogging.getValue()) {
                Logger.getLogger("Minecraft").info(String.format("User %s connecting with mods %s", userName, modVersions.keySet()));
                FMLLog.info("User %s connecting with mods %s", userName, modVersions.keySet());
            }
            // Cauldron end
            pkt.length = pkt.data.length;
            network.addToSendQueue(pkt);
            final NBTTagList itemList = new NBTTagList();
            GameData.writeItemData(itemList);
            final byte[][] registryPackets = FMLPacket.makePacketSet(MOD_IDMAP, itemList);
            for (int i = 0; i < registryPackets.length; i++)
            {
                network.addToSendQueue(PacketDispatcher.getPacket("FML", registryPackets[i]));
            }
        }

        // reset the continuation flag - we have completed extra negotiation and the login should complete now
        NetLoginHandler.func_72531_a((NetLoginHandler) netHandler, true);
    }

}
