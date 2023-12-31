package net.minecraft.world.gen.structure;

import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapGenMineshaft extends MapGenStructure
{
    private double field_82673_e = 0.01D;

    public MapGenMineshaft() {}

    public String func_143025_a()
    {
        return "Mineshaft";
    }

    public MapGenMineshaft(final Map par1Map)
    {
        final Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();

            if (((String)entry.getKey()).equals("chance"))
            {
                this.field_82673_e = MathHelper.parseDoubleWithDefault((String)entry.getValue(), this.field_82673_e);
            }
        }
    }

    protected boolean canSpawnStructureAtCoords(final int par1, final int par2)
    {
        return this.rand.nextDouble() < this.field_82673_e && this.rand.nextInt(80) < Math.max(Math.abs(par1), Math.abs(par2));
    }

    protected StructureStart getStructureStart(final int par1, final int par2)
    {
        return new StructureMineshaftStart(this.worldObj, this.rand, par1, par2);
    }
}
