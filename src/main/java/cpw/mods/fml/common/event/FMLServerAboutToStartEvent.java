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

import cpw.mods.fml.common.LoaderState.ModState;
import net.minecraft.server.MinecraftServer;

public class FMLServerAboutToStartEvent extends FMLStateEvent {

    private MinecraftServer server;

    public FMLServerAboutToStartEvent(final Object... data)
    {
        super(data);
        this.server = (MinecraftServer) data[0];
        // Cauldron start
        // since we modify bukkit enums, we need to guarantee that plugins are
        // loaded after all mods have been loaded by FML to avoid race conditions.
        MinecraftServer.getServer().server.loadPlugins();
        MinecraftServer.getServer().server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.STARTUP);
        // Cauldron end        
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
