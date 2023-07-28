package org.bukkit.craftbukkit.v1_6_R3.util;

import java.io.File;
import java.io.FilenameFilter;

public class DatFileFilter implements FilenameFilter {
    public boolean accept(final File dir, final String name) {
        return name.endsWith(".dat");
    }
}
