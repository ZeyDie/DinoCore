package net.minecraft.world.gen.structure;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class StructureNetherBridgeStart extends StructureStart
{
    public StructureNetherBridgeStart() {}

    public StructureNetherBridgeStart(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        super(par3, par4);
        final ComponentNetherBridgeStartPiece componentnetherbridgestartpiece = new ComponentNetherBridgeStartPiece(par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(componentnetherbridgestartpiece);
        componentnetherbridgestartpiece.buildComponent(componentnetherbridgestartpiece, this.components, par2Random);
        final ArrayList arraylist = componentnetherbridgestartpiece.field_74967_d;

        while (!arraylist.isEmpty())
        {
            final int k = par2Random.nextInt(arraylist.size());
            final StructureComponent structurecomponent = (StructureComponent)arraylist.remove(k);
            structurecomponent.buildComponent(componentnetherbridgestartpiece, this.components, par2Random);
        }

        this.updateBoundingBox();
        this.setRandomHeight(par1World, par2Random, 48, 70);
    }
}
