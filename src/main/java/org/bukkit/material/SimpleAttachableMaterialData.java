package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Simple utility class for attachable MaterialData subclasses
 */
public abstract class SimpleAttachableMaterialData extends MaterialData implements Attachable {

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public SimpleAttachableMaterialData(final int type) {
        super(type);
    }

    public SimpleAttachableMaterialData(final int type, final BlockFace direction) {
        this(type);
        setFacingDirection(direction);
    }

    public SimpleAttachableMaterialData(final Material type, final BlockFace direction) {
        this(type);
        setFacingDirection(direction);
    }

    public SimpleAttachableMaterialData(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public SimpleAttachableMaterialData(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public SimpleAttachableMaterialData(final Material type, final byte data) {
        super(type, data);
    }

    public BlockFace getFacing() {
        final BlockFace attachedFace = getAttachedFace();
        return attachedFace == null ? null : attachedFace.getOppositeFace();
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing();
    }

    @Override
    public SimpleAttachableMaterialData clone() {
        return (SimpleAttachableMaterialData) super.clone();
    }
}
