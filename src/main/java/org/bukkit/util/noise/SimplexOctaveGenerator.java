package org.bukkit.util.noise;

import org.bukkit.World;

import java.util.Random;

/**
 * Creates simplex noise through unbiased octaves
 */
public class SimplexOctaveGenerator extends OctaveGenerator {
    private double wScale = 1;

    /**
     * Creates a simplex octave generator for the given world
     *
     * @param world World to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public SimplexOctaveGenerator(final World world, final int octaves) {
        this(new Random(world.getSeed()), octaves);
    }

    /**
     * Creates a simplex octave generator for the given world
     *
     * @param seed Seed to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public SimplexOctaveGenerator(final long seed, final int octaves) {
        this(new Random(seed), octaves);
    }

    /**
     * Creates a simplex octave generator for the given {@link Random}
     *
     * @param rand Random object to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public SimplexOctaveGenerator(final Random rand, final int octaves) {
        super(createOctaves(rand, octaves));
    }

    @Override
    public void setScale(final double scale) {
        super.setScale(scale);
        setWScale(scale);
    }

    /**
     * Gets the scale used for each W-coordinates passed
     *
     * @return W scale
     */
    public double getWScale() {
        return wScale;
    }

    /**
     * Sets the scale used for each W-coordinates passed
     *
     * @param scale New W scale
     */
    public void setWScale(final double scale) {
        wScale = scale;
    }

    /**
     * Generates noise for the 3D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @param w W-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @return Resulting noise
     */
    public double noise(final double x, final double y, final double z, final double w, final double frequency, final double amplitude) {
        return noise(x, y, z, w, frequency, amplitude, false);
    }

    /**
     * Generates noise for the 3D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @param w W-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @param normalized If true, normalize the value to [-1, 1]
     * @return Resulting noise
     */
    public double noise(double x, double y, double z, double w, final double frequency, final double amplitude, final boolean normalized) {
        double x1 = x;
        double y1 = y;
        double z1 = z;
        double w1 = w;
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;

        x1 *= xScale;
        y1 *= yScale;
        z1 *= zScale;
        w1 *= wScale;

        for (final NoiseGenerator octave : octaves) {
            result += ((SimplexNoiseGenerator) octave).noise(x1 * freq, y1 * freq, z1 * freq, w1 * freq) * amp;
            max += amp;
            freq *= frequency;
            amp *= amplitude;
        }

        if (normalized) {
            result /= max;
        }

        return result;
    }

    private static NoiseGenerator[] createOctaves(final Random rand, final int octaves) {
        final NoiseGenerator[] result = new NoiseGenerator[octaves];

        for (int i = 0; i < octaves; i++) {
            result[i] = new SimplexNoiseGenerator(rand);
        }

        return result;
    }
}
