package org.bukkit.material;

import org.bukkit.Material;

/**
 * Represents a pressure plate
 */
public class PressurePlate extends MaterialData implements PressureSensor {
    public PressurePlate() {
        super(Material.WOOD_PLATE);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PressurePlate(final int type) {
        super(type);
    }

    public PressurePlate(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PressurePlate(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PressurePlate(final Material type, final byte data) {
        super(type, data);
    }

    public boolean isPressed() {
        return getData() == 0x1;
    }

    @Override
    public String toString() {
        return super.toString() + (isPressed() ? " PRESSED" : "");
    }

    @Override
    public PressurePlate clone() {
        return (PressurePlate) super.clone();
    }
}
