package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public abstract class StructureStart
{
    /** List of all StructureComponents that are part of this structure */
    public LinkedList components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int field_143024_c;
    private int field_143023_d;

    public StructureStart() {}

    public StructureStart(final int par1, final int par2)
    {
        this.field_143024_c = par1;
        this.field_143023_d = par2;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public LinkedList getComponents()
    {
        return this.components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    public void generateStructure(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent = (StructureComponent)iterator.next();

            // Cauldron - validate structurecomponent
            if ((structurecomponent == null || structurecomponent.getBoundingBox() == null) || (structurecomponent.getBoundingBox().intersectsWith(par3StructureBoundingBox) && !structurecomponent.addComponentParts(par1World, par2Random, par3StructureBoundingBox)))
            {
                iterator.remove();
            }
        }
    }

    /**
     * Calculates total bounding box based on components' bounding boxes and saves it to boundingBox
     */
    protected void updateBoundingBox()
    {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent = (StructureComponent)iterator.next();
            this.boundingBox.expandTo(structurecomponent.getBoundingBox());
        }
    }

    public NBTTagCompound func_143021_a(final int par1, final int par2)
    {
        if (MapGenStructureIO.func_143033_a(this) == null)
        {
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
        }

        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.func_143033_a(this));
        nbttagcompound.setInteger("ChunkX", par1);
        nbttagcompound.setInteger("ChunkZ", par2);
        nbttagcompound.setTag("BB", this.boundingBox.func_143047_a("BB"));
        final NBTTagList nbttaglist = new NBTTagList("Children");
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent = (StructureComponent)iterator.next();
            nbttaglist.appendTag(structurecomponent.func_143010_b());
        }

        nbttagcompound.setTag("Children", nbttaglist);
        this.func_143022_a(nbttagcompound);
        return nbttagcompound;
    }

    public void func_143022_a(final NBTTagCompound par1NBTTagCompound) {}

    public void func_143020_a(final World par1World, final NBTTagCompound par2NBTTagCompound)
    {
        this.field_143024_c = par2NBTTagCompound.getInteger("ChunkX");
        this.field_143023_d = par2NBTTagCompound.getInteger("ChunkZ");

        if (par2NBTTagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(par2NBTTagCompound.getIntArray("BB"));
        }

        final NBTTagList nbttaglist = par2NBTTagCompound.getTagList("Children");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            this.components.add(MapGenStructureIO.func_143032_b((NBTTagCompound)nbttaglist.tagAt(i), par1World));
        }

        this.func_143017_b(par2NBTTagCompound);
    }

    public void func_143017_b(final NBTTagCompound par1NBTTagCompound) {}

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
     */
    protected void markAvailableHeight(final World par1World, final Random par2Random, final int par3)
    {
        final int j = 63 - par3;
        int k = this.boundingBox.getYSize() + 1;

        if (k < j)
        {
            k += par2Random.nextInt(j - k);
        }

        final int l = k - this.boundingBox.maxY;
        this.boundingBox.offset(0, l, 0);
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent = (StructureComponent)iterator.next();
            structurecomponent.getBoundingBox().offset(0, l, 0);
        }
    }

    protected void setRandomHeight(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        final int k = par4 - par3 + 1 - this.boundingBox.getYSize();
        final boolean flag = true;
        final int l;

        if (k > 1)
        {
            l = par3 + par2Random.nextInt(k);
        }
        else
        {
            l = par3;
        }

        final int i1 = l - this.boundingBox.minY;
        this.boundingBox.offset(0, i1, 0);
        final Iterator iterator = this.components.iterator();

        while (iterator.hasNext())
        {
            final StructureComponent structurecomponent = (StructureComponent)iterator.next();
            structurecomponent.getBoundingBox().offset(0, i1, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return true;
    }

    public int func_143019_e()
    {
        return this.field_143024_c;
    }

    public int func_143018_f()
    {
        return this.field_143023_d;
    }
}
