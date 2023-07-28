package net.minecraft.client.gui.achievement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;

import java.util.Comparator;

@SideOnly(Side.CLIENT)
class SorterStatsItem implements Comparator
{
    final GuiStats statsGUI;

    final GuiSlotStatsItem slotStatsItemGUI;

    SorterStatsItem(final GuiSlotStatsItem par1GuiSlotStatsItem, final GuiStats par2GuiStats)
    {
        this.slotStatsItemGUI = par1GuiSlotStatsItem;
        this.statsGUI = par2GuiStats;
    }

    public int func_78337_a(final StatCrafting par1StatCrafting, final StatCrafting par2StatCrafting)
    {
        final int i = par1StatCrafting.getItemID();
        final int j = par2StatCrafting.getItemID();
        StatBase statbase = null;
        StatBase statbase1 = null;

        if (this.slotStatsItemGUI.field_77264_j == 0)
        {
            statbase = StatList.objectBreakStats[i];
            statbase1 = StatList.objectBreakStats[j];
        }
        else if (this.slotStatsItemGUI.field_77264_j == 1)
        {
            statbase = StatList.objectCraftStats[i];
            statbase1 = StatList.objectCraftStats[j];
        }
        else if (this.slotStatsItemGUI.field_77264_j == 2)
        {
            statbase = StatList.objectUseStats[i];
            statbase1 = StatList.objectUseStats[j];
        }

        if (statbase != null || statbase1 != null)
        {
            if (statbase == null)
            {
                return 1;
            }

            if (statbase1 == null)
            {
                return -1;
            }

            final int k = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.slotGuiStats).writeStat(statbase);
            final int l = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.slotGuiStats).writeStat(statbase1);

            if (k != l)
            {
                return (k - l) * this.slotStatsItemGUI.field_77265_k;
            }
        }

        return i - j;
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.func_78337_a((StatCrafting)par1Obj, (StatCrafting)par2Obj);
    }
}
