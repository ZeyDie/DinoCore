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

package cpw.mods.fml.common.registry;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class ItemData {

    private static Map<String, Multiset<String>> modOrdinals = Maps.newHashMap();
    private final String modId;
    private final String itemType;
    private final int itemId;
    private final int ordinal;
    private String forcedModId;
    private String forcedName;

    public ItemData(final Item item, final ModContainer mc)
    {
        this.itemId = item.itemID;
        if (item.getClass().equals(ItemBlock.class))
        {
            this.itemType =  Block.blocksList[this.getItemId()].getClass().getName();
        }
        else
        {
            this.itemType = item.getClass().getName();
        }
        this.modId = mc.getModId();
        if (!modOrdinals.containsKey(mc.getModId()))
        {
            modOrdinals.put(mc.getModId(), HashMultiset.<String>create());
        }
        this.ordinal = modOrdinals.get(mc.getModId()).add(itemType, 1);
    }

    public ItemData(final NBTTagCompound tag)
    {
        this.modId = tag.getString("ModId");
        this.itemType = tag.getString("ItemType");
        this.itemId = tag.getInteger("ItemId");
        this.ordinal = tag.getInteger("ordinal");
        this.forcedModId = tag.hasKey("ForcedModId") ? tag.getString("ForcedModId") : null;
        this.forcedName = tag.hasKey("ForcedName") ? tag.getString("ForcedName") : null;
    }

    public String getItemType()
    {
        return this.forcedName !=null ? forcedName : itemType;
    }

    public String getModId()
    {
        return this.forcedModId != null ? forcedModId : modId;
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    public int getItemId()
    {
        return itemId;
    }

    public NBTTagCompound toNBT()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ModId", modId);
        tag.setString("ItemType", itemType);
        tag.setInteger("ItemId", itemId);
        tag.setInteger("ordinal", ordinal);
        if (forcedModId != null)
        {
            tag.setString("ForcedModId", forcedModId);
        }
        if (forcedName != null)
        {
            tag.setString("ForcedName", forcedName);
        }
        return tag;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(itemId, ordinal);
    }

    @Override
    public boolean equals(final Object obj)
    {
        try
        {
            final ItemData other = (ItemData) obj;
            return Objects.equal(getModId(), other.getModId()) && Objects.equal(getItemType(), other.getItemType()) && Objects.equal(itemId, other.itemId) && ( isOveridden() || Objects.equal(ordinal, other.ordinal));
        }
        catch (final ClassCastException cce)
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return String.format("Item %d, Type %s, owned by %s, ordinal %d, name %s, claimedModId %s", itemId, itemType, modId, ordinal, forcedName, forcedModId);
    }

    public boolean mayDifferByOrdinal(final ItemData rightValue)
    {
        return Objects.equal(getItemType(), rightValue.getItemType()) && Objects.equal(getModId(), rightValue.getModId());
    }

    public boolean isOveridden()
    {
        return forcedName != null;
    }

    public void setName(final String name, final String modId)
    {
        if (name == null)
        {
            this.forcedName = null;
            this.forcedModId = null;
            return;
        }
        String localModId = modId;
        if (modId == null)
        {
            localModId = Loader.instance().activeModContainer().getModId();
        }
        if (modOrdinals.get(localModId).count(name)>0)
        {
            FMLLog.severe("The mod %s is attempting to redefine the item at id %d with a non-unique name (%s.%s)", Loader.instance().activeModContainer(), itemId, localModId, name);
            throw new LoaderException();
        }
        modOrdinals.get(localModId).add(name);
        this.forcedModId = modId;
        this.forcedName = name;
    }
}
