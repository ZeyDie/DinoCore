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

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;

import java.util.Random;

public class ModLoaderWorldGenerator implements IWorldGenerator
{
    private BaseModProxy mod;

    public ModLoaderWorldGenerator(final BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkProviderGenerate)
        {
            mod.generateSurface(world, random, chunkX << 4, chunkZ << 4);
        }
        else if (chunkGenerator instanceof ChunkProviderHell)
        {
            mod.generateNether(world, random, chunkX << 4, chunkZ << 4);
        }
    }
}
