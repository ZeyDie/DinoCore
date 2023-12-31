/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraftforge.liquids;

import net.minecraft.item.ItemStack;
@Deprecated //See new net.minecraftforge.fluids
public class LiquidContainerData {

    public final LiquidStack stillLiquid;
    public final ItemStack filled;
    public final ItemStack container;


    public LiquidContainerData(final LiquidStack stillLiquid, final ItemStack filled, final ItemStack container) {
        this.stillLiquid = stillLiquid;
        this.filled = filled;
        this.container = container;

        if(stillLiquid == null || filled == null || container == null)
            throw new RuntimeException("stillLiquid, filled, or container is null, this is an error");
    }

}
