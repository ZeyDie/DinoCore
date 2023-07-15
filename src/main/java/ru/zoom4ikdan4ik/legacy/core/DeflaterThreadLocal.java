package ru.zoom4ikdan4ik.legacy.core;

import java.util.zip.Deflater;

public final class DeflaterThreadLocal extends ThreadLocal<Deflater> {
    @Override
    protected Deflater initialValue() {
        // Don't use higher compression level, slows things down too much
        return new Deflater(4); // Spigot - use lower compression level still
    }
}
