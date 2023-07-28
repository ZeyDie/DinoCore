package net.minecraft.client.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IntHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class KeyBinding
{
    public static List keybindArray = new ArrayList();
    public static IntHashMap hash = new IntHashMap();
    public String keyDescription;
    public int keyCode;

    /** because _303 wanted me to call it that(Caironater) */
    public boolean pressed;
    public int pressTime;

    public static void onTick(final int par0)
    {
        final KeyBinding keybinding = (KeyBinding)hash.lookup(par0);

        if (keybinding != null)
        {
            ++keybinding.pressTime;
        }
    }

    public static void setKeyBindState(final int par0, final boolean par1)
    {
        final KeyBinding keybinding = (KeyBinding)hash.lookup(par0);

        if (keybinding != null)
        {
            keybinding.pressed = par1;
        }
    }

    public static void unPressAllKeys()
    {
        final Iterator iterator = keybindArray.iterator();

        while (iterator.hasNext())
        {
            final KeyBinding keybinding = (KeyBinding)iterator.next();
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash()
    {
        hash.clearMap();
        final Iterator iterator = keybindArray.iterator();

        while (iterator.hasNext())
        {
            final KeyBinding keybinding = (KeyBinding)iterator.next();
            hash.addKey(keybinding.keyCode, keybinding);
        }
    }

    public KeyBinding(final String par1Str, final int par2)
    {
        this.keyDescription = par1Str;
        this.keyCode = par2;
        keybindArray.add(this);
        hash.addKey(par2, this);
    }

    public boolean isPressed()
    {
        if (this.pressTime == 0)
        {
            return false;
        }
        else
        {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey()
    {
        this.pressTime = 0;
        this.pressed = false;
    }
}
