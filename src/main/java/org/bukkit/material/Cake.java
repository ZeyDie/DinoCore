package org.bukkit.material;

import org.bukkit.Material;

public class Cake extends MaterialData {
    public Cake() {
        super(Material.CAKE_BLOCK);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Cake(final int type) {
        super(type);
    }

    public Cake(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Cake(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Cake(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * Gets the number of slices eaten from this cake
     *
     * @return The number of slices eaten
     */
    public int getSlicesEaten() {
        return getData();
    }

    /**
     * Gets the number of slices remaining on this cake
     *
     * @return The number of slices remaining
     */
    public int getSlicesRemaining() {
        return 6 - getData();
    }

    /**
     * Sets the number of slices eaten from this cake
     *
     * @param n The number of slices eaten
     */
    public void setSlicesEaten(final int n) {
        if (n < 6) {
            setData((byte) n);
        } // TODO: else destroy the block? Probably not possible though
    }

    /**
     * Sets the number of slices remaining on this cake
     *
     * @param n The number of slices remaining
     */
    public void setSlicesRemaining(int n) {
        int n1 = n;
        if (n1 > 6) {
            n1 = 6;
        }
        setData((byte) (6 - n1));
    }

    @Override
    public String toString() {
        return super.toString() + " " + getSlicesEaten() + "/" + getSlicesRemaining() + " slices eaten/remaining";
    }

    @Override
    public Cake clone() {
        return (Cake) super.clone();
    }
}
