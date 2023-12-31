package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileNotFoundException;

@SideOnly(Side.CLIENT)
public class ResourcePackFileNotFoundException extends FileNotFoundException
{
    public ResourcePackFileNotFoundException(final File par1File, final String par2Str)
    {
        super(String.format("\'%s\' in ResourcePack \'%s\'", new Object[] {par2Str, par1File}));
    }
}
