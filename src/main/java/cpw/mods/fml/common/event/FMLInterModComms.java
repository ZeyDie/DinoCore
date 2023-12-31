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

package cpw.mods.fml.common.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Simple intermod communications to receive simple messages directed at you
 * from other mods
 *
 * @author cpw
 *
 */
public class FMLInterModComms {
    private static final ImmutableList<IMCMessage> emptyIMCList = ImmutableList.<IMCMessage>of();
    private static ArrayListMultimap<String, IMCMessage> modMessages = ArrayListMultimap.create();

    /**
     * Subscribe to this event to receive your messages (they are sent between
     * {@link Init} and {@link PostInit})
     *
     * @author cpw
     *
     */
    public static class IMCEvent extends FMLEvent {
        private ModContainer activeContainer;

        @Override
        public void applyModContainer(final ModContainer activeContainer)
        {
            this.activeContainer = activeContainer;
            this.currentList = null;
            FMLLog.finest("Attempting to deliver %d IMC messages to mod %s", modMessages.get(activeContainer.getModId()).size(), activeContainer.getModId());
        }

        private ImmutableList<IMCMessage> currentList;

        public ImmutableList<IMCMessage> getMessages()
        {
            if (currentList == null)
            {
                currentList = ImmutableList.copyOf(modMessages.removeAll(activeContainer.getModId()));
            }
            return currentList;
        }
    }

    /**
     * You will receive an instance of this for each message sent
     *
     * @author cpw
     *
     */
    public static final class IMCMessage {
        /**
         * This is the modid of the mod that sent you the message
         */
        private String sender;
        /**
         * This field, and {@link #value} are both at the mod's discretion
         */
        public final String key;
        /**
         * This field, and {@link #key} are both at the mod's discretion
         */
        private Object value;

        private IMCMessage(final String key, final Object value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return sender;
        }

        public String getSender()
        {
            return this.sender;
        }

        void setSender(final ModContainer activeModContainer)
        {
            this.sender = activeModContainer.getModId();
        }

        public String getStringValue()
        {
            return (String) value;
        }

        public NBTTagCompound getNBTValue()
        {
            return (NBTTagCompound) value;
        }

        public ItemStack getItemStackValue()
        {
            return (ItemStack) value;
        }

        public Class<?> getMessageType()
        {
            return value.getClass();
        }

        public boolean isStringMessage()
        {
            return String.class.isAssignableFrom(getMessageType());
        }

        public boolean isItemStackMessage()
        {
            return ItemStack.class.isAssignableFrom(getMessageType());
        }

        public boolean isNBTMessage()
        {
            return NBTTagCompound.class.isAssignableFrom(getMessageType());
        }
    }

    public static boolean sendMessage(final String modId, final String key, final NBTTagCompound value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }
    public static boolean sendMessage(final String modId, final String key, final ItemStack value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }
    public static boolean sendMessage(final String modId, final String key, final String value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }

    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final NBTTagCompound value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final ItemStack value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final String value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    private static boolean enqueueStartupMessage(final String modTarget, final IMCMessage message)
    {
        if (Loader.instance().activeModContainer() == null)
        {
            return false;
        }
        enqueueMessage(Loader.instance().activeModContainer(), modTarget, message);
        return Loader.isModLoaded(modTarget) && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);

    }
    private static void enqueueMessage(final Object sourceMod, final String modTarget, final IMCMessage message)
    {
        final ModContainer mc;
        if (sourceMod instanceof ModContainer) {
            mc = (ModContainer) sourceMod;
        }
        else
        {
            mc = FMLCommonHandler.instance().findContainerFor(sourceMod);
        }
        if (mc != null && Loader.isModLoaded(modTarget))
        {
            message.setSender(mc);
            modMessages.put(modTarget, message);
        }
    }

    /**
     * Retrieve any pending runtime messages for the mod
     * @param forMod The {@link Instance} of the Mod to fetch messages for
     * @return any messages - the collection will never be null
     */
    public static ImmutableList<IMCMessage> fetchRuntimeMessages(final Object forMod)
    {
        final ModContainer mc = FMLCommonHandler.instance().findContainerFor(forMod);
        if (mc != null)
        {
            return ImmutableList.copyOf(modMessages.removeAll(mc.getModId()));
        }
        else
        {
            return emptyIMCList;
        }
    }
}
