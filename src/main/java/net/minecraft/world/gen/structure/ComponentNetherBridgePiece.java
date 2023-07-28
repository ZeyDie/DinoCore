package net.minecraft.world.gen.structure;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

abstract class ComponentNetherBridgePiece extends StructureComponent
{
    protected static final WeightedRandomChestContent[] field_111019_a = {new WeightedRandomChestContent(Item.diamond.itemID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.ingotGold.itemID, 0, 1, 3, 15), new WeightedRandomChestContent(Item.swordGold.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.plateGold.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.flintAndSteel.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.netherStalkSeeds.itemID, 0, 3, 7, 5), new WeightedRandomChestContent(Item.saddle.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.horseArmorGold.itemID, 0, 1, 1, 8), new WeightedRandomChestContent(Item.horseArmorIron.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.horseArmorDiamond.itemID, 0, 1, 1, 3)};

    public ComponentNetherBridgePiece() {}

    protected ComponentNetherBridgePiece(final int par1)
    {
        super(par1);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound) {}

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound) {}

    private int getTotalWeight(final List par1List)
    {
        boolean flag = false;
        int i = 0;
        StructureNetherBridgePieceWeight structurenetherbridgepieceweight;

        for (final Iterator iterator = par1List.iterator(); iterator.hasNext(); i += structurenetherbridgepieceweight.field_78826_b)
        {
            structurenetherbridgepieceweight = (StructureNetherBridgePieceWeight)iterator.next();

            if (structurenetherbridgepieceweight.field_78824_d > 0 && structurenetherbridgepieceweight.field_78827_c < structurenetherbridgepieceweight.field_78824_d)
            {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private ComponentNetherBridgePiece getNextComponent(final ComponentNetherBridgeStartPiece par1ComponentNetherBridgeStartPiece, final List par2List, final List par3List, final Random par4Random, final int par5, final int par6, final int par7, final int par8, final int par9)
    {
        final int j1 = this.getTotalWeight(par2List);
        final boolean flag = j1 > 0 && par9 <= 30;
        int k1 = 0;

        while (k1 < 5 && flag)
        {
            ++k1;
            int l1 = par4Random.nextInt(j1);
            final Iterator iterator = par2List.iterator();

            while (iterator.hasNext())
            {
                final StructureNetherBridgePieceWeight structurenetherbridgepieceweight = (StructureNetherBridgePieceWeight)iterator.next();
                l1 -= structurenetherbridgepieceweight.field_78826_b;

                if (l1 < 0)
                {
                    if (!structurenetherbridgepieceweight.func_78822_a(par9) || structurenetherbridgepieceweight == par1ComponentNetherBridgeStartPiece.theNetherBridgePieceWeight && !structurenetherbridgepieceweight.field_78825_e)
                    {
                        break;
                    }

                    final ComponentNetherBridgePiece componentnetherbridgepiece = StructureNetherBridgePieces.createNextComponent(structurenetherbridgepieceweight, par3List, par4Random, par5, par6, par7, par8, par9);

                    if (componentnetherbridgepiece != null)
                    {
                        ++structurenetherbridgepieceweight.field_78827_c;
                        par1ComponentNetherBridgeStartPiece.theNetherBridgePieceWeight = structurenetherbridgepieceweight;

                        if (!structurenetherbridgepieceweight.func_78823_a())
                        {
                            par2List.remove(structurenetherbridgepieceweight);
                        }

                        return componentnetherbridgepiece;
                    }
                }
            }
        }

        return ComponentNetherBridgeEnd.func_74971_a(par3List, par4Random, par5, par6, par7, par8, par9);
    }

    /**
     * Finds a random component to tack on to the bridge. Or builds the end.
     */
    private StructureComponent getNextComponent(final ComponentNetherBridgeStartPiece par1ComponentNetherBridgeStartPiece, final List par2List, final Random par3Random, final int par4, final int par5, final int par6, final int par7, final int par8, final boolean par9)
    {
        if (Math.abs(par4 - par1ComponentNetherBridgeStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par6 - par1ComponentNetherBridgeStartPiece.getBoundingBox().minZ) <= 112)
        {
            List list1 = par1ComponentNetherBridgeStartPiece.primaryWeights;

            if (par9)
            {
                list1 = par1ComponentNetherBridgeStartPiece.secondaryWeights;
            }

            final ComponentNetherBridgePiece componentnetherbridgepiece = this.getNextComponent(par1ComponentNetherBridgeStartPiece, list1, par2List, par3Random, par4, par5, par6, par7, par8 + 1);

            if (componentnetherbridgepiece != null)
            {
                par2List.add(componentnetherbridgepiece);
                par1ComponentNetherBridgeStartPiece.field_74967_d.add(componentnetherbridgepiece);
            }

            return componentnetherbridgepiece;
        }
        else
        {
            return ComponentNetherBridgeEnd.func_74971_a(par2List, par3Random, par4, par5, par6, par7, par8);
        }
    }

    /**
     * Gets the next component in any cardinal direction
     */
    protected StructureComponent getNextComponentNormal(final ComponentNetherBridgeStartPiece par1ComponentNetherBridgeStartPiece, final List par2List, final Random par3Random, final int par4, final int par5, final boolean par6)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.maxZ + 1, this.coordBaseMode, this.getComponentType(), par6);
            case 1:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType(), par6);
            case 2:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.minZ - 1, this.coordBaseMode, this.getComponentType(), par6);
            case 3:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType(), par6);
            default:
                return null;
        }
    }

    /**
     * Gets the next component in the +/- X direction
     */
    protected StructureComponent getNextComponentX(final ComponentNetherBridgeStartPiece par1ComponentNetherBridgeStartPiece, final List par2List, final Random par3Random, final int par4, final int par5, final boolean par6)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType(), par6);
            case 1:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType(), par6);
            case 2:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType(), par6);
            case 3:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType(), par6);
            default:
                return null;
        }
    }

    /**
     * Gets the next component in the +/- Z direction
     */
    protected StructureComponent getNextComponentZ(final ComponentNetherBridgeStartPiece par1ComponentNetherBridgeStartPiece, final List par2List, final Random par3Random, final int par4, final int par5, final boolean par6)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType(), par6);
            case 1:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType(), par6);
            case 2:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType(), par6);
            case 3:
                return this.getNextComponent(par1ComponentNetherBridgeStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType(), par6);
            default:
                return null;
        }
    }

    /**
     * Checks if the bounding box's minY is > 10
     */
    protected static boolean isAboveGround(final StructureBoundingBox par0StructureBoundingBox)
    {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }
}
