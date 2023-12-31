package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemTooltipEvent extends PlayerEvent
{
    /**
     * Whether the advanced information on item tooltips is being shown, toggled by F3+H.
     */
    public final boolean showAdvancedItemTooltips;
    /**
     * The {@link ItemStack} with the tooltip.
     */
    public final ItemStack itemStack;
    /**
     * The {@link ItemStack} tooltip.
     */
    public final List<String> toolTip;

    /**
     * This event is fired in {@link ItemStack#getTooltip(EntityPlayer, boolean)}, which in turn is called from it's respective GUIContainer.
     */
    public ItemTooltipEvent(final ItemStack itemStack, final EntityPlayer entityPlayer, final List<String> toolTip, final boolean showAdvancedItemTooltips)
    {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.showAdvancedItemTooltips = showAdvancedItemTooltips;
    }
}