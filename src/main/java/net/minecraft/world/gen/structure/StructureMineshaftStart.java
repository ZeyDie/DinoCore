package net.minecraft.world.gen.structure;

import net.minecraft.world.World;

import java.util.Random;

public class StructureMineshaftStart extends StructureStart
{
    public StructureMineshaftStart() {}

    public StructureMineshaftStart(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        super(par3, par4);
        final ComponentMineshaftRoom componentmineshaftroom = new ComponentMineshaftRoom(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(componentmineshaftroom);
        componentmineshaftroom.buildComponent(componentmineshaftroom, this.components, par2Random);
        this.updateBoundingBox();
        this.markAvailableHeight(par1World, par2Random, 10);
    }
}
