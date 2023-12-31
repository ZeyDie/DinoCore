package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileFilter;

@SideOnly(Side.CLIENT)
final class ResourcePackRepositoryFilter implements FileFilter
{
    public boolean accept(final File par1File)
    {
        final boolean flag = par1File.isFile() && par1File.getName().endsWith(".zip");
        final boolean flag1 = par1File.isDirectory() && (new File(par1File, "pack.mcmeta")).isFile();
        return flag || flag1;
    }
}
