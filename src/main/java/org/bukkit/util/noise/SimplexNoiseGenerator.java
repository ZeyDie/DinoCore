package org.bukkit.util.noise;

import org.bukkit.World;

import java.util.Random;

/**
 * Generates simplex-based noise.
 * <p>
 * This is a modified version of the freely published version in the paper by
 * Stefan Gustavson at http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf
 */
public class SimplexNoiseGenerator extends PerlinNoiseGenerator {
    protected static final double SQRT_3 = 1.7320508075688772;
    protected static final double SQRT_5 = 2.23606797749979;
    protected static final double F2 = 0.5 * (SQRT_3 - 1);
    protected static final double G2 = (3 - SQRT_3) / 6;
    protected static final double G22 = G2 * 2.0 - 1;
    protected static final double F3 = 1.0 / 3.0;
    protected static final double G3 = 1.0 / 6.0;
    protected static final double F4 = (SQRT_5 - 1.0) / 4.0;
    protected static final double G4 = (5.0 - SQRT_5) / 20.0;
    protected static final double G42 = G4 * 2.0;
    protected static final double G43 = G4 * 3.0;
    protected static final double G44 = G4 * 4.0 - 1.0;
    protected static final int[][] grad4 = {{0, 1, 1, 1}, {0, 1, 1, -1}, {0, 1, -1, 1}, {0, 1, -1, -1},
        {0, -1, 1, 1}, {0, -1, 1, -1}, {0, -1, -1, 1}, {0, -1, -1, -1},
        {1, 0, 1, 1}, {1, 0, 1, -1}, {1, 0, -1, 1}, {1, 0, -1, -1},
        {-1, 0, 1, 1}, {-1, 0, 1, -1}, {-1, 0, -1, 1}, {-1, 0, -1, -1},
        {1, 1, 0, 1}, {1, 1, 0, -1}, {1, -1, 0, 1}, {1, -1, 0, -1},
        {-1, 1, 0, 1}, {-1, 1, 0, -1}, {-1, -1, 0, 1}, {-1, -1, 0, -1},
        {1, 1, 1, 0}, {1, 1, -1, 0}, {1, -1, 1, 0}, {1, -1, -1, 0},
        {-1, 1, 1, 0}, {-1, 1, -1, 0}, {-1, -1, 1, 0}, {-1, -1, -1, 0}};
    protected static final int[][] simplex = {
        {0, 1, 2, 3}, {0, 1, 3, 2}, {0, 0, 0, 0}, {0, 2, 3, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 3, 0},
        {0, 2, 1, 3}, {0, 0, 0, 0}, {0, 3, 1, 2}, {0, 3, 2, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 3, 2, 0},
        {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0},
        {1, 2, 0, 3}, {0, 0, 0, 0}, {1, 3, 0, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 3, 0, 1}, {2, 3, 1, 0},
        {1, 0, 2, 3}, {1, 0, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 3, 1}, {0, 0, 0, 0}, {2, 1, 3, 0},
        {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0},
        {2, 0, 1, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {0, 0, 0, 0}, {3, 1, 2, 0},
        {2, 1, 0, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 1, 0, 2}, {0, 0, 0, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    protected static double offsetW;
    private static final SimplexNoiseGenerator instance = new SimplexNoiseGenerator();

    protected SimplexNoiseGenerator() {
        super();
    }

    /**
     * Creates a seeded simplex noise generator for the given world
     *
     * @param world World to construct this generator for
     */
    public SimplexNoiseGenerator(final World world) {
        this(new Random(world.getSeed()));
    }

    /**
     * Creates a seeded simplex noise generator for the given seed
     *
     * @param seed Seed to construct this generator for
     */
    public SimplexNoiseGenerator(final long seed) {
        this(new Random(seed));
    }

    /**
     * Creates a seeded simplex noise generator with the given Random
     *
     * @param rand Random to construct with
     */
    public SimplexNoiseGenerator(final Random rand) {
        super(rand);
        offsetW = rand.nextDouble() * 256;
    }

    protected static double dot(final int[] g, final double x, final double y) {
        return g[0] * x + g[1] * y;
    }

    protected static double dot(final int[] g, final double x, final double y, final double z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    protected static double dot(final int[] g, final double x, final double y, final double z, final double w) {
        return g[0] * x + g[1] * y + g[2] * z + g[3] * w;
    }

    /**
     * Computes and returns the 1D unseeded simplex noise for the given coordinates in 1D space
     *
     * @param xin X coordinate
     * @return Noise at given location, from range -1 to 1
     */
    public static double getNoise(final double xin) {
        return instance.noise(xin);
    }

    /**
     * Computes and returns the 2D unseeded simplex noise for the given coordinates in 2D space
     *
     * @param xin X coordinate
     * @param yin Y coordinate
     * @return Noise at given location, from range -1 to 1
     */
    public static double getNoise(final double xin, final double yin) {
        return instance.noise(xin, yin);
    }

    /**
     * Computes and returns the 3D unseeded simplex noise for the given coordinates in 3D space
     *
     * @param xin X coordinate
     * @param yin Y coordinate
     * @param zin Z coordinate
     * @return Noise at given location, from range -1 to 1
     */
    public static double getNoise(final double xin, final double yin, final double zin) {
        return instance.noise(xin, yin, zin);
    }

    /**
     * Computes and returns the 4D simplex noise for the given coordinates in 4D space
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param w W coordinate
     * @return Noise at given location, from range -1 to 1
     */
    public static double getNoise(final double x, final double y, final double z, final double w) {
        return instance.noise(x, y, z, w);
    }

    @Override
    public double noise(double xin, double yin, double zin) {
        double xin1 = xin;
        double yin1 = yin;
        double zin1 = zin;
        xin1 += offsetX;
        yin1 += offsetY;
        zin1 += offsetZ;

        final double n0;  // Noise contributions from the four corners
        double n1;
        double n2;
        final double n3;

        // Skew the input space to determine which simplex cell we're in
        final double s = (xin1 + yin1 + zin1) * F3; // Very nice and simple skew factor for 3D
        final int i = floor(xin1 + s);
        final int j = floor(yin1 + s);
        final int k = floor(zin1 + s);
        final double t = (i + j + k) * G3;
        final double X0 = i - t; // Unskew the cell origin back to (x,y,z) space
        final double Y0 = j - t;
        final double Z0 = k - t;
        final double x0 = xin1 - X0; // The x,y,z distances from the cell origin
        final double y0 = yin1 - Y0;
        final double z0 = zin1 - Z0;

        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.

        // Determine which simplex we are in.
        final int i1;  // Offsets for second corner of simplex in (i,j,k) coords
        int j1;
        final int k1;
        final int i2;  // Offsets for third corner of simplex in (i,j,k) coords
        int j2;
        final int k2;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } // X Y Z order
            else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } // X Z Y order
            else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } // Z X Y order
        } else { // x0<y0
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Z Y X order
            else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Y Z X order
            else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } // Y X Z order
        }

        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.
        final double x1 = x0 - i1 + G3; // Offsets for second corner in (x,y,z) coords
        final double y1 = y0 - j1 + G3;
        final double z1 = z0 - k1 + G3;
        final double x2 = x0 - i2 + 2.0 * G3; // Offsets for third corner in (x,y,z) coords
        final double y2 = y0 - j2 + 2.0 * G3;
        final double z2 = z0 - k2 + 2.0 * G3;
        final double x3 = x0 - 1.0 + 3.0 * G3; // Offsets for last corner in (x,y,z) coords
        final double y3 = y0 - 1.0 + 3.0 * G3;
        final double z3 = z0 - 1.0 + 3.0 * G3;

        // Work out the hashed gradient indices of the four simplex corners
        final int ii = i & 255;
        final int jj = j & 255;
        final int kk = k & 255;
        final int gi0 = perm[ii + perm[jj + perm[kk]]] % 12;
        final int gi1 = perm[ii + i1 + perm[jj + j1 + perm[kk + k1]]] % 12;
        final int gi2 = perm[ii + i2 + perm[jj + j2 + perm[kk + k2]]] % 12;
        final int gi3 = perm[ii + 1 + perm[jj + 1 + perm[kk + 1]]] % 12;

        // Calculate the contribution from the four corners
        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0) {
            n0 = 0.0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0, z0);
        }

        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0) {
            n1 = 0.0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1, z1);
        }

        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0) {
            n2 = 0.0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2, z2);
        }

        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0) {
            n3 = 0.0;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * dot(grad3[gi3], x3, y3, z3);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0 * (n0 + n1 + n2 + n3);
    }

    @Override
    public double noise(double xin, double yin) {
        double xin1 = xin;
        double yin1 = yin;
        xin1 += offsetX;
        yin1 += offsetY;

        final double n0;  // Noise contributions from the three corners
        double n1;
        final double n2;

        // Skew the input space to determine which simplex cell we're in
        final double s = (xin1 + yin1) * F2; // Hairy factor for 2D
        final int i = floor(xin1 + s);
        final int j = floor(yin1 + s);
        final double t = (i + j) * G2;
        final double X0 = i - t; // Unskew the cell origin back to (x,y) space
        final double Y0 = j - t;
        final double x0 = xin1 - X0; // The x,y distances from the cell origin
        final double y0 = yin1 - Y0;

        // For the 2D case, the simplex shape is an equilateral triangle.

        // Determine which simplex we are in.
        final int i1;  // Offsets for second (middle) corner of simplex in (i,j) coords
        final int j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        else {
            i1 = 0;
            j1 = 1;
        } // upper triangle, YX order: (0,0)->(0,1)->(1,1)

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        final double x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
        final double y1 = y0 - j1 + G2;
        final double x2 = x0 + G22; // Offsets for last corner in (x,y) unskewed coords
        final double y2 = y0 + G22;

        // Work out the hashed gradient indices of the three simplex corners
        final int ii = i & 255;
        final int jj = j & 255;
        final int gi0 = perm[ii + perm[jj]] % 12;
        final int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        final int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

        // Calculate the contribution from the three corners
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0) {
            n0 = 0.0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0); // (x,y) of grad3 used for 2D gradient
        }

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0) {
            n1 = 0.0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
        }

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0) {
            n2 = 0.0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0 * (n0 + n1 + n2);
    }

    /**
     * Computes and returns the 4D simplex noise for the given coordinates in 4D space
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param w W coordinate
     * @return Noise at given location, from range -1 to 1
     */
    public double noise(double x, double y, double z, double w) {
        double x5 = x;
        double y5 = y;
        double z5 = z;
        double w5 = w;
        x5 += offsetX;
        y5 += offsetY;
        z5 += offsetZ;
        w5 += offsetW;

        final double n0;  // Noise contributions from the five corners
        double n1;
        double n2;
        double n3;
        final double n4;

        // Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
        final double s = (x5 + y5 + z5 + w5) * F4; // Factor for 4D skewing
        final int i = floor(x5 + s);
        final int j = floor(y5 + s);
        final int k = floor(z5 + s);
        final int l = floor(w5 + s);

        final double t = (i + j + k + l) * G4; // Factor for 4D unskewing
        final double X0 = i - t; // Unskew the cell origin back to (x,y,z,w) space
        final double Y0 = j - t;
        final double Z0 = k - t;
        final double W0 = l - t;
        final double x0 = x5 - X0; // The x,y,z,w distances from the cell origin
        final double y0 = y5 - Y0;
        final double z0 = z5 - Z0;
        final double w0 = w5 - W0;

        // For the 4D case, the simplex is a 4D shape I won't even try to describe.
        // To find out which of the 24 possible simplices we're in, we need to
        // determine the magnitude ordering of x0, y0, z0 and w0.
        // The method below is a good way of finding the ordering of x,y,z,w and
        // then find the correct traversal order for the simplex we’re in.
        // First, six pair-wise comparisons are performed between each possible pair
        // of the four coordinates, and the results are used to add up binary bits
        // for an integer index.
        final int c1 = (x0 > y0) ? 32 : 0;
        final int c2 = (x0 > z0) ? 16 : 0;
        final int c3 = (y0 > z0) ? 8 : 0;
        final int c4 = (x0 > w0) ? 4 : 0;
        final int c5 = (y0 > w0) ? 2 : 0;
        final int c6 = (z0 > w0) ? 1 : 0;
        final int c = c1 + c2 + c3 + c4 + c5 + c6;
        final int i1;  // The integer offsets for the second simplex corner
        int j1;
        int k1;
        final int l1;
        final int i2;  // The integer offsets for the third simplex corner
        int j2;
        int k2;
        final int l2;
        final int i3;  // The integer offsets for the fourth simplex corner
        int j3;
        int k3;
        final int l3;

        // simplex[c] is a 4-vector with the numbers 0, 1, 2 and 3 in some order.
        // Many values of c will never occur, since e.g. x>y>z>w makes x<z, y<w and x<w
        // impossible. Only the 24 indices which have non-zero entries make any sense.
        // We use a thresholding to set the coordinates in turn from the largest magnitude.

        // The number 3 in the "simplex" array is at the position of the largest coordinate.
        i1 = simplex[c][0] >= 3 ? 1 : 0;
        j1 = simplex[c][1] >= 3 ? 1 : 0;
        k1 = simplex[c][2] >= 3 ? 1 : 0;
        l1 = simplex[c][3] >= 3 ? 1 : 0;

        // The number 2 in the "simplex" array is at the second largest coordinate.
        i2 = simplex[c][0] >= 2 ? 1 : 0;
        j2 = simplex[c][1] >= 2 ? 1 : 0;
        k2 = simplex[c][2] >= 2 ? 1 : 0;
        l2 = simplex[c][3] >= 2 ? 1 : 0;

        // The number 1 in the "simplex" array is at the second smallest coordinate.
        i3 = simplex[c][0] >= 1 ? 1 : 0;
        j3 = simplex[c][1] >= 1 ? 1 : 0;
        k3 = simplex[c][2] >= 1 ? 1 : 0;
        l3 = simplex[c][3] >= 1 ? 1 : 0;

        // The fifth corner has all coordinate offsets = 1, so no need to look that up.

        final double x1 = x0 - i1 + G4; // Offsets for second corner in (x,y,z,w) coords
        final double y1 = y0 - j1 + G4;
        final double z1 = z0 - k1 + G4;
        final double w1 = w0 - l1 + G4;

        final double x2 = x0 - i2 + G42; // Offsets for third corner in (x,y,z,w) coords
        final double y2 = y0 - j2 + G42;
        final double z2 = z0 - k2 + G42;
        final double w2 = w0 - l2 + G42;

        final double x3 = x0 - i3 + G43; // Offsets for fourth corner in (x,y,z,w) coords
        final double y3 = y0 - j3 + G43;
        final double z3 = z0 - k3 + G43;
        final double w3 = w0 - l3 + G43;

        final double x4 = x0 + G44; // Offsets for last corner in (x,y,z,w) coords
        final double y4 = y0 + G44;
        final double z4 = z0 + G44;
        final double w4 = w0 + G44;

        // Work out the hashed gradient indices of the five simplex corners
        final int ii = i & 255;
        final int jj = j & 255;
        final int kk = k & 255;
        final int ll = l & 255;

        final int gi0 = perm[ii + perm[jj + perm[kk + perm[ll]]]] % 32;
        final int gi1 = perm[ii + i1 + perm[jj + j1 + perm[kk + k1 + perm[ll + l1]]]] % 32;
        final int gi2 = perm[ii + i2 + perm[jj + j2 + perm[kk + k2 + perm[ll + l2]]]] % 32;
        final int gi3 = perm[ii + i3 + perm[jj + j3 + perm[kk + k3 + perm[ll + l3]]]] % 32;
        final int gi4 = perm[ii + 1 + perm[jj + 1 + perm[kk + 1 + perm[ll + 1]]]] % 32;

        // Calculate the contribution from the five corners
        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if (t0 < 0) {
            n0 = 0.0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad4[gi0], x0, y0, z0, w0);
        }

        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 < 0) {
            n1 = 0.0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad4[gi1], x1, y1, z1, w1);
        }

        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 < 0) {
            n2 = 0.0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad4[gi2], x2, y2, z2, w2);
        }

        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 < 0) {
            n3 = 0.0;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * dot(grad4[gi3], x3, y3, z3, w3);
        }

        double t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 < 0) {
            n4 = 0.0;
        } else {
            t4 *= t4;
            n4 = t4 * t4 * dot(grad4[gi4], x4, y4, z4, w4);
        }

        // Sum up and scale the result to cover the range [-1,1]
        return 27.0 * (n0 + n1 + n2 + n3 + n4);
    }

    /**
     * Gets the singleton unseeded instance of this generator
     *
     * @return Singleton
     */
    public static SimplexNoiseGenerator getInstance() {
        return instance;
    }
}
