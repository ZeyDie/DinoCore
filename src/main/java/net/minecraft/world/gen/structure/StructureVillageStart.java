package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StructureVillageStart extends StructureStart
{
    /** well ... thats what it does */
    private boolean hasMoreThanTwoComponents;

    public StructureVillageStart() {}

    public StructureVillageStart(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        super(par3, par4);
        final List list = StructureVillagePieces.getStructureVillageWeightedPieceList(par2Random, par5);
        final ComponentVillageStartPiece componentvillagestartpiece = new ComponentVillageStartPiece(par1World.getWorldChunkManager(), 0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, list, par5);
        this.components.add(componentvillagestartpiece);
        componentvillagestartpiece.buildComponent(componentvillagestartpiece, this.components, par2Random);
        final List list1 = componentvillagestartpiece.field_74930_j;
        final List list2 = componentvillagestartpiece.field_74932_i;
        int l;

        while (!list1.isEmpty() || !list2.isEmpty())
        {
            final StructureComponent structurecomponent;

            if (list1.isEmpty())
            {
                l = par2Random.nextInt(list2.size());
                structurecomponent = (StructureComponent)list2.remove(l);
                structurecomponent.buildComponent(componentvillagestartpiece, this.components, par2Random);
            }
            else
            {
                l = par2Random.nextInt(list1.size());
                structurecomponent = (StructureComponent)list1.remove(l);
                structurecomponent.buildComponent(componentvillagestartpiece, this.components, par2Random);
            }
        }

        this.updateBoundingBox();
        l = 0;
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent1 = (StructureComponent)iterator.next();

            if (!(structurecomponent1 instanceof ComponentVillageRoadPiece))
            {
                ++l;
            }
        }

        this.hasMoreThanTwoComponents = l > 2;
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return this.hasMoreThanTwoComponents;
    }

    public void func_143022_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143022_a(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
    }

    public void func_143017_b(final NBTTagCompound nbttagcompound)
    {
        super.func_143017_b(nbttagcompound);
        this.hasMoreThanTwoComponents = nbttagcompound.getBoolean("Valid");
    }
}
