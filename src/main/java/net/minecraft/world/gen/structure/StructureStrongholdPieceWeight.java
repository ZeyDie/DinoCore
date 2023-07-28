package net.minecraft.world.gen.structure;

class StructureStrongholdPieceWeight
{
    public Class pieceClass;

    /**
     * This basically keeps track of the 'epicness' of a structure. Epic structure components have a higher 'weight',
     * and Structures may only grow up to a certain 'weight' before generation is stopped
     */
    public final int pieceWeight;
    public int instancesSpawned;

    /** How many Structure Pieces of this type may spawn in a structure */
    public int instancesLimit;

    public StructureStrongholdPieceWeight(final Class par1Class, final int par2, final int par3)
    {
        this.pieceClass = par1Class;
        this.pieceWeight = par2;
        this.instancesLimit = par3;
    }

    public boolean canSpawnMoreStructuresOfType(final int par1)
    {
        return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
    }

    public boolean canSpawnMoreStructures()
    {
        return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
    }
}
