package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.dedicated.DedicatedServer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

@SideOnly(Side.SERVER)
public class MinecraftServerGui extends JComponent
{
    private static boolean field_120022_a;
    private DedicatedServer field_120021_b;

    public static void func_120016_a(final DedicatedServer par0DedicatedServer)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception exception)
        {
            ;
        }

        final MinecraftServerGui minecraftservergui = new MinecraftServerGui(par0DedicatedServer);
        field_120022_a = true;
        final JFrame jframe = new JFrame("Minecraft server");
        jframe.add(minecraftservergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component)null);
        jframe.setVisible(true);
        jframe.addWindowListener(new MinecraftServerGuiINNER1(par0DedicatedServer));
    }

    public MinecraftServerGui(final DedicatedServer par1DedicatedServer)
    {
        this.field_120021_b = par1DedicatedServer;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try
        {
            this.add(this.func_120018_d(), "Center");
            this.add(this.func_120019_b(), "West");
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private JComponent func_120019_b()
    {
        final JPanel jpanel = new JPanel(new BorderLayout());
        jpanel.add(new StatsComponent(this.field_120021_b), "North");
        jpanel.add(this.func_120020_c(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    private JComponent func_120020_c()
    {
        final PlayerListComponent playerlistcomponent = new PlayerListComponent(this.field_120021_b);
        final JScrollPane jscrollpane = new JScrollPane(playerlistcomponent, 22, 30);
        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

    private JComponent func_120018_d()
    {
        final JPanel jpanel = new JPanel(new BorderLayout());
        final JTextArea jtextarea = new JTextArea();
        this.field_120021_b.getLogAgent().func_120013_a().addHandler(new TextAreaLogHandler(jtextarea));
        final JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
        jtextarea.setEditable(false);
        final JTextField jtextfield = new JTextField();
        jtextfield.addActionListener(new MinecraftServerGuiINNER2(this, jtextfield));
        jtextarea.addFocusListener(new MinecraftServerGuiINNER3(this));
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return jpanel;
    }

    static DedicatedServer func_120017_a(final MinecraftServerGui par0MinecraftServerGui)
    {
        return par0MinecraftServerGui.field_120021_b;
    }
}
