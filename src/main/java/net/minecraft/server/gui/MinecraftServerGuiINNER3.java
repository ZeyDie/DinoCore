package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@SideOnly(Side.SERVER)
class MinecraftServerGuiINNER3 extends FocusAdapter
{
    final MinecraftServerGui field_120032_a;

    MinecraftServerGuiINNER3(final MinecraftServerGui par1MinecraftServerGui)
    {
        this.field_120032_a = par1MinecraftServerGui;
    }

    public void focusGained(final FocusEvent par1FocusEvent) {}
}
