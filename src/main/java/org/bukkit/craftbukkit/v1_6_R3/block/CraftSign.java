package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;

public class CraftSign extends CraftBlockState implements Sign {
    private final net.minecraft.tileentity.TileEntitySign sign;
    private final String[] lines;

    public CraftSign(final Block block) {
        super(block);

        final CraftWorld world = (CraftWorld) block.getWorld();
        sign = (net.minecraft.tileentity.TileEntitySign) world.getTileEntityAt(getX(), getY(), getZ());
        // Spigot start
        if (sign == null) {
            lines = new String[]{"", "", "", ""};
            return;
        }
        // Spigot end
        lines = new String[sign.signText.length];
        System.arraycopy(sign.signText, 0, lines, 0, lines.length);
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(final int index) throws IndexOutOfBoundsException {
        return lines[index];
    }

    public void setLine(final int index, final String line) throws IndexOutOfBoundsException {
        lines[index] = line;
    }

    @Override
    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result && sign != null) { // Spigot, add null check
            for(int i = 0; i < 4; i++) {
                if(lines[i] != null) {
                    sign.signText[i] = lines[i];
                } else {
                    sign.signText[i] = "";
                }
            }
            sign.onInventoryChanged();
        }

        return result;
    }
}
