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

package net.minecraft.src;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * A static hook library for optifine and other basemod editing code to access FML functions
 *
 * @author cpw
 *
 */
public class FMLRenderAccessLibrary
{
    public static Logger getLogger()
    {
        final Logger l = Logger.getLogger("FMLRenderAccessLibrary");
        l.setParent(FMLLog.getLogger());
        return l;
    }

    public static void log(final Level level, final String message)
    {
        FMLLog.log("FMLRenderAccessLibrary", level, message);
    }

    public static void log(final Level level, final String message, final Throwable throwable)
    {
        FMLLog.log(level, throwable, message);
    }

    @SuppressWarnings("deprecation")
    public static boolean renderWorldBlock(final RenderBlocks renderer, final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId)
    {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }

    @SuppressWarnings("deprecation")
    public static void renderInventoryBlock(final RenderBlocks renderer, final Block block, final int metadata, final int modelID)
    {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    @SuppressWarnings("deprecation")
    public static boolean renderItemAsFull3DBlock(final int modelId)
    {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelId);
    }

//    public static void doTextureCopy(Texture atlas, Texture source, int targetX, int targetY)
//    {
//        TextureFXManager.instance().getHelper().doTextureCopy(atlas, source, targetX, targetY);
//    }
}
