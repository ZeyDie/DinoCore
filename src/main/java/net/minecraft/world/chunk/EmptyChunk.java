package net.minecraft.world.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class EmptyChunk extends Chunk
{
    public EmptyChunk(final World par1World, final int par2, final int par3)
    {
        super(par1World, par2, par3);
    }

    /**
     * Checks whether the chunk is at the X/Z location specified
     */
    public boolean isAtLocation(final int par1, final int par2)
    {
        return par1 == this.xPosition && par2 == this.zPosition;
    }

    /**
     * Returns the value in the height map at this x, z coordinate in the chunk
     */
    public int getHeightValue(final int par1, final int par2)
    {
        return 0;
    }

    /**
     * Generates the initial skylight map for the chunk upon generation or load.
     */
    public void generateSkylightMap() {}

    @SideOnly(Side.CLIENT)

    /**
     * Generates the height map for a chunk from scratch
     */
    public void generateHeightMap() {}

    /**
     * Return the ID of a block in the chunk.
     */
    public int getBlockID(final int par1, final int par2, final int par3)
    {
        return 0;
    }

    public int getBlockLightOpacity(final int par1, final int par2, final int par3)
    {
        return 255;
    }

    /**
     * Sets a blockID of a position within a chunk with metadata. Args: x, y, z, blockID, metadata
     */
    public boolean setBlockIDWithMetadata(final int par1, final int par2, final int par3, final int par4, final int par5)
    {
        return true;
    }

    /**
     * Return the metadata corresponding to the given coordinates inside a chunk.
     */
    public int getBlockMetadata(final int par1, final int par2, final int par3)
    {
        return 0;
    }

    /**
     * Set the metadata of a block in the chunk
     */
    public boolean setBlockMetadata(final int par1, final int par2, final int par3, final int par4)
    {
        return false;
    }

    /**
     * Gets the amount of light saved in this block (doesn't adjust for daylight)
     */
    public int getSavedLightValue(final EnumSkyBlock par1EnumSkyBlock, final int par2, final int par3, final int par4)
    {
        return 0;
    }

    /**
     * Sets the light value at the coordinate. If enumskyblock is set to sky it sets it in the skylightmap and if its a
     * block then into the blocklightmap. Args enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(final EnumSkyBlock par1EnumSkyBlock, final int par2, final int par3, final int par4, final int par5) {}

    /**
     * Gets the amount of light on a block taking into account sunlight
     */
    public int getBlockLightValue(final int par1, final int par2, final int par3, final int par4)
    {
        return 0;
    }

    /**
     * Adds an entity to the chunk. Args: entity
     */
    public void addEntity(final Entity par1Entity) {}

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(final Entity par1Entity) {}

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(final Entity par1Entity, final int par2) {}

    /**
     * Returns whether is not a block above this one blocking sight to the sky (done via checking against the heightmap)
     */
    public boolean canBlockSeeTheSky(final int par1, final int par2, final int par3)
    {
        return false;
    }

    /**
     * Gets the TileEntity for a given block in this chunk
     */
    public TileEntity getChunkBlockTileEntity(final int par1, final int par2, final int par3)
    {
        return null;
    }

    /**
     * Adds a TileEntity to a chunk
     */
    public void addTileEntity(final TileEntity par1TileEntity) {}

    /**
     * Sets the TileEntity for a given block in this chunk
     */
    public void setChunkBlockTileEntity(final int par1, final int par2, final int par3, final TileEntity par4TileEntity) {}

    /**
     * Removes the TileEntity for a given block in this chunk
     */
    public void removeChunkBlockTileEntity(final int par1, final int par2, final int par3) {}

    /**
     * Called when this Chunk is loaded by the ChunkProvider
     */
    public void onChunkLoad() {}

    /**
     * Called when this Chunk is unloaded by the ChunkProvider
     */
    public void onChunkUnload() {}

    /**
     * Sets the isModified flag for this Chunk
     */
    public void setChunkModified() {}

    /**
     * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity
     * Args: entity, aabb, listToFill
     */
    public void getEntitiesWithinAABBForEntity(final Entity par1Entity, final AxisAlignedBB par2AxisAlignedBB, final List par3List, final IEntitySelector par4IEntitySelector) {}

    /**
     * Gets all entities that can be assigned to the specified class. Args: entityClass, aabb, listToFill
     */
    public void getEntitiesOfTypeWithinAAAB(final Class par1Class, final AxisAlignedBB par2AxisAlignedBB, final List par3List, final IEntitySelector par4IEntitySelector) {}

    /**
     * Returns true if this Chunk needs to be saved
     */
    public boolean needsSaving(final boolean par1)
    {
        return false;
    }

    public Random getRandomWithSeed(final long par1)
    {
        return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ par1);
    }

    public boolean isEmpty()
    {
        return true;
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
     * (true) or not (false).
     */
    public boolean getAreLevelsEmpty(final int par1, final int par2)
    {
        return true;
    }
}
