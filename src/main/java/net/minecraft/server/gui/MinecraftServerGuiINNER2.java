package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SideOnly(Side.SERVER)
class MinecraftServerGuiINNER2 implements ActionListener
{
    final JTextField field_120025_a;

    final MinecraftServerGui field_120024_b;

    MinecraftServerGuiINNER2(final MinecraftServerGui par1MinecraftServerGui, final JTextField par2JTextField)
    {
        this.field_120024_b = par1MinecraftServerGui;
        this.field_120025_a = par2JTextField;
    }

    public void actionPerformed(final ActionEvent par1ActionEvent)
    {
        final String s = this.field_120025_a.getText().trim();

        if (!s.isEmpty())
        {
            MinecraftServerGui.func_120017_a(this.field_120024_b).addPendingCommand(s, MinecraftServer.getServer());
        }

        this.field_120025_a.setText("");
    }
}
