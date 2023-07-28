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

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import java.util.EnumSet;

/**
 * @author cpw
 *
 */
public class BaseModTicker implements ITickHandler
{

    private BaseModProxy mod;
    private EnumSet<TickType> ticks;
    private boolean clockTickTrigger;
    private boolean sendGuiTicks;


    BaseModTicker(final BaseModProxy mod, final boolean guiTicker)
    {
        this.mod = mod;
        this.ticks = EnumSet.of(TickType.WORLDLOAD);
        this.sendGuiTicks = guiTicker;
    }

    BaseModTicker(final EnumSet<TickType> ticks, final boolean guiTicker)
    {
        this.ticks = ticks;
        this.sendGuiTicks = guiTicker;
    }

    @Override
    public void tickStart(final EnumSet<TickType> types, final Object... tickData)
    {
        tickBaseMod(types, false, tickData);
    }

    @Override
    public void tickEnd(final EnumSet<TickType> types, final Object... tickData)
    {
        tickBaseMod(types, true, tickData);
    }

    private void tickBaseMod(final EnumSet<TickType> types, final boolean end, final Object... tickData)
    {
        if (FMLCommonHandler.instance().getSide().isClient() && ( ticks.contains(TickType.CLIENT) || ticks.contains(TickType.WORLDLOAD)))
        {
            final EnumSet cTypes=EnumSet.copyOf(types);
            if ( ( end && types.contains(TickType.CLIENT)) || types.contains(TickType.WORLDLOAD))
            {
                clockTickTrigger =  true;
                cTypes.remove(TickType.CLIENT);
                cTypes.remove(TickType.WORLDLOAD);
            }

            if (end && clockTickTrigger && types.contains(TickType.RENDER))
            {
                clockTickTrigger = false;
                cTypes.remove(TickType.RENDER);
                cTypes.add(TickType.CLIENT);
            }

            sendTick(cTypes, end, tickData);
        }
        else
        {
            sendTick(types, end, tickData);
        }
    }

    private void sendTick(final EnumSet<TickType> types, final boolean end, final Object... tickData)
    {
        for (final TickType type : types)
        {
            if (!ticks.contains(type))
            {
                continue;
            }

            boolean keepTicking=true;
            if (sendGuiTicks)
            {
                keepTicking = mod.doTickInGUI(type, end, tickData);
            }
            else
            {
                keepTicking = mod.doTickInGame(type, end, tickData);
            }
            if (!keepTicking) {
                ticks.remove(type);
                ticks.removeAll(type.partnerTicks());
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return (clockTickTrigger ? EnumSet.of(TickType.RENDER) : ticks);
    }

    @Override
    public String getLabel()
    {
        return mod.getClass().getSimpleName();
    }

    public void setMod(final BaseModProxy mod)
    {
        this.mod = mod;
    }
}
