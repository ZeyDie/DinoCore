package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Comparator;

@SideOnly(Side.CLIENT)
class McoServerListUpdateTaskComparator implements Comparator
{
    private final String field_140069_b;

    final McoServerListUpdateTask field_140070_a;

    private McoServerListUpdateTaskComparator(final McoServerListUpdateTask par1McoServerListUpdateTask, final String par2Str)
    {
        this.field_140070_a = par1McoServerListUpdateTask;
        this.field_140069_b = par2Str;
    }

    public int func_140068_a(final McoServer par1McoServer, final McoServer par2McoServer)
    {
        if (par1McoServer.field_96405_e.equals(par2McoServer.field_96405_e))
        {
            return par1McoServer.field_96408_a < par2McoServer.field_96408_a ? 1 : (par1McoServer.field_96408_a > par2McoServer.field_96408_a ? -1 : 0);
        }
        else if (par1McoServer.field_96405_e.equals(this.field_140069_b))
        {
            return -1;
        }
        else if (par2McoServer.field_96405_e.equals(this.field_140069_b))
        {
            return 1;
        }
        else
        {
            if (par1McoServer.field_96404_d.equals("CLOSED") || par2McoServer.field_96404_d.equals("CLOSED"))
            {
                if (par1McoServer.field_96404_d.equals("CLOSED"))
                {
                    return 1;
                }

                if (par2McoServer.field_96404_d.equals("CLOSED"))
                {
                    return 0;
                }
            }

            return par1McoServer.field_96408_a < par2McoServer.field_96408_a ? 1 : (par1McoServer.field_96408_a > par2McoServer.field_96408_a ? -1 : 0);
        }
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.func_140068_a((McoServer)par1Obj, (McoServer)par2Obj);
    }

    McoServerListUpdateTaskComparator(final McoServerListUpdateTask par1McoServerListUpdateTask, final String par2Str, final McoServerListEmptyAnon par3McoServerListEmptyAnon)
    {
        this(par1McoServerListUpdateTask, par2Str);
    }
}
