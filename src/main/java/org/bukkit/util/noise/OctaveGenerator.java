package org.bukkit.util.noise;

/**
 * Creates noise using unbiased octaves
 */
public abstract class OctaveGenerator {
    protected final NoiseGenerator[] octaves;
    protected double xScale = 1;
    protected double yScale = 1;
    protected double zScale = 1;

    protected OctaveGenerator(final NoiseGenerator[] octaves) {
        this.octaves = octaves;
    }

    /**
     * Sets the scale used for all coordinates passed to this generator.
     * <p>
     * This is the equivalent to setting each coordinate to the specified value.
     *
     * @param scale New value to scale each coordinate by
     */
    public void setScale(final double scale) {
        setXScale(scale);
        setYScale(scale);
        setZScale(scale);
    }

    /**
     * Gets the scale used for each X-coordinates passed
     *
     * @return X scale
     */
    public double getXScale() {
        return xScale;
    }

    /**
     * Sets the scale used for each X-coordinates passed
     *
     * @param scale New X scale
     */
    public void setXScale(final double scale) {
        xScale = scale;
    }

    /**
     * Gets the scale used for each Y-coordinates passed
     *
     * @return Y scale
     */
    public double getYScale() {
        return yScale;
    }

    /**
     * Sets the scale used for each Y-coordinates passed
     *
     * @param scale New Y scale
     */
    public void setYScale(final double scale) {
        yScale = scale;
    }

    /**
     * Gets the scale used for each Z-coordinates passed
     *
     * @return Z scale
     */
    public double getZScale() {
        return zScale;
    }

    /**
     * Sets the scale used for each Z-coordinates passed
     *
     * @param scale New Z scale
     */
    public void setZScale(final double scale) {
        zScale = scale;
    }

    /**
     * Gets a clone of the individual octaves used within this generator
     *
     * @return Clone of the individual octaves
     */
    public NoiseGenerator[] getOctaves() {
        return octaves.clone();
    }

    /**
     * Generates noise for the 1D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @return Resulting noise
     */
    public double noise(final double x, final double frequency, final double amplitude) {
        return noise(x, 0, 0, frequency, amplitude);
    }

    /**
     * Generates noise for the 1D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @param normalized If true, normalize the value to [-1, 1]
     * @return Resulting noise
     */
    public double noise(final double x, final double frequency, final double amplitude, final boolean normalized) {
        return noise(x, 0, 0, frequency, amplitude, normalized);
    }

    /**
     * Generates noise for the 2D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @return Resulting noise
     */
    public double noise(final double x, final double y, final double frequency, final double amplitude) {
        return noise(x, y, 0, frequency, amplitude);
    }

    /**
     * Generates noise for the 2D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @param normalized If true, normalize the value to [-1, 1]
     * @return Resulting noise
     */
    public double noise(final double x, final double y, final double frequency, final double amplitude, final boolean normalized) {
        return noise(x, y, 0, frequency, amplitude, normalized);
    }

    /**
     * Generates noise for the 3D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @return Resulting noise
     */
    public double noise(final double x, final double y, final double z, final double frequency, final double amplitude) {
        return noise(x, y, z, frequency, amplitude, false);
    }

    /**
     * Generates noise for the 3D coordinates using the specified number of octaves and parameters
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @param frequency How much to alter the frequency by each octave
     * @param amplitude How much to alter the amplitude by each octave
     * @param normalized If true, normalize the value to [-1, 1]
     * @return Resulting noise
     */
    public double noise(double x, double y, double z, final double frequency, final double amplitude, final boolean normalized) {
        double x1 = x;
        double y1 = y;
        double z1 = z;
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;

        x1 *= xScale;
        y1 *= yScale;
        z1 *= zScale;

        for (final NoiseGenerator octave : octaves) {
            result += octave.noise(x1 * freq, y1 * freq, z1 * freq) * amp;
            max += amp;
            freq *= frequency;
            amp *= amplitude;
        }

        if (normalized) {
            result /= max;
        }

        return result;
    }
}
