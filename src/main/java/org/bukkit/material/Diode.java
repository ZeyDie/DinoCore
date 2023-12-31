package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class Diode extends MaterialData implements Directional {
    public Diode() {
        super(Material.DIODE_BLOCK_ON);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Diode(final int type) {
        super(type);
    }

    public Diode(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Diode(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Diode(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * Sets the delay of the repeater
     *
     * @param delay
     *            The new delay (1-4)
     */
    public void setDelay(int delay) {
        int delay1 = delay;
        if (delay1 > 4) {
            delay1 = 4;
        }
        if (delay1 < 1) {
            delay1 = 1;
        }
        final byte newData = (byte) (getData() & 0x3);

        setData((byte) (newData | ((delay1 - 1) << 2)));
    }

    /**
     * Gets the delay of the repeater in ticks
     *
     * @return The delay (1-4)
     */
    public int getDelay() {
        return (getData() >> 2) + 1;
    }

    public void setFacingDirection(final BlockFace face) {
        final int delay = getDelay();
        final byte data;

        switch (face) {
        case EAST:
            data = 0x1;
            break;

        case SOUTH:
            data = 0x2;
            break;

        case WEST:
            data = 0x3;
            break;

        case NORTH:
        default:
            data = 0x0;
        }

        setData(data);
        setDelay(delay);
    }

    public BlockFace getFacing() {
        final byte data = (byte) (getData() & 0x3);

        switch (data) {
        case 0x0:
        default:
            return BlockFace.NORTH;

        case 0x1:
            return BlockFace.EAST;

        case 0x2:
            return BlockFace.SOUTH;

        case 0x3:
            return BlockFace.WEST;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing() + " with " + getDelay() + " ticks delay";
    }

    @Override
    public Diode clone() {
        return (Diode) super.clone();
    }
}
