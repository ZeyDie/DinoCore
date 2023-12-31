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

import com.google.common.collect.MapDifference;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_IDMAP;

public class ModIdMapPacket extends FMLPacket {
    private byte[][] partials;

    public ModIdMapPacket()
    {
        super(MOD_IDMAP);
    }

    @Override
    public byte[] generatePacket(final Object... data)
    {
        final NBTTagList completeList = (NBTTagList) data[0];
        final NBTTagCompound wrap = new NBTTagCompound();
        wrap.setTag("List", completeList);
        try
        {
            return CompressedStreamTools.compress(wrap);
        }
        catch (final Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A critical error writing the id map");
            throw new FMLNetworkException(e);
        }
    }

    @Override
    public FMLPacket consumePacket(final byte[] data)
    {
        final ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
        final int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
        final int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
        final int chunkLength = bdi.readInt();
        if (partials == null)
        {
            partials = new byte[chunkTotal][];
        }
        partials[chunkIdx] = new byte[chunkLength];
        bdi.readFully(partials[chunkIdx]);
        for (int i = 0; i < partials.length; i++)
        {
            if (partials[i] == null)
            {
                return null;
            }
        }
        return this;
    }

    @Override
    public void execute(final INetworkManager network, final FMLNetworkHandler handler, final NetHandler netHandler, final String userName)
    {
        final byte[] allData = Bytes.concat(partials);
        GameData.initializeServerGate(1);
        try
        {
            final NBTTagCompound serverList = CompressedStreamTools.decompress(allData);
            final NBTTagList list = serverList.getTagList("List");
            final Set<ItemData> itemData = GameData.buildWorldItemData(list);
            GameData.validateWorldSave(itemData);
            final MapDifference<Integer, ItemData> serverDifference = GameData.gateWorldLoadingForValidation();
            if (serverDifference!=null)
            {
                FMLCommonHandler.instance().disconnectIDMismatch(serverDifference, netHandler, network);

            }
        }
        catch (final IOException e)
        {
        }
    }

}
