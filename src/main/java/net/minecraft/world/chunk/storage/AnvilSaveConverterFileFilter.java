package net.minecraft.world.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverterFileFilter implements FilenameFilter
{
    final AnvilSaveConverter parent;

    AnvilSaveConverterFileFilter(final AnvilSaveConverter par1AnvilSaveConverter)
    {
        this.parent = par1AnvilSaveConverter;
    }

    public boolean accept(final File par1File, final String par2Str)
    {
        return par2Str.endsWith(".mcr");
    }
}
