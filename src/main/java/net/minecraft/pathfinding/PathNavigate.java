package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate
{
    private EntityLiving theEntity;
    private World worldObj;

    /** The PathEntity being followed. */
    private PathEntity currentPath;
    private double speed;

    /**
     * The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space
     */
    private AttributeInstance pathSearchRange;
    private boolean noSunPathfind;

    /** Time, in number of ticks, following the current path */
    private int totalTicks;

    /**
     * The time when the last position check was done (to detect successful movement)
     */
    private int ticksAtLastPos;

    /**
     * Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck')
     */
    private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

    /**
     * Specifically, if a wooden door block is even considered to be passable by the pathfinder
     */
    private boolean canPassOpenWoodenDoors = true;

    /** If door blocks are considered passable even when closed */
    private boolean canPassClosedWoodenDoors;

    /** If water blocks are avoided (at least by the pathfinder) */
    private boolean avoidsWater;

    /**
     * If the entity can swim. Swimming AI enables this and the pathfinder will also cause the entity to swim straight
     * upwards when underwater
     */
    private boolean canSwim;

    public PathNavigate(final EntityLiving par1EntityLiving, final World par2World)
    {
        this.theEntity = par1EntityLiving;
        this.worldObj = par2World;
        this.pathSearchRange = par1EntityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
    }

    public void setAvoidsWater(final boolean par1)
    {
        this.avoidsWater = par1;
    }

    public boolean getAvoidsWater()
    {
        return this.avoidsWater;
    }

    public void setBreakDoors(final boolean par1)
    {
        this.canPassClosedWoodenDoors = par1;
    }

    /**
     * Sets if the entity can enter open doors
     */
    public void setEnterDoors(final boolean par1)
    {
        this.canPassOpenWoodenDoors = par1;
    }

    /**
     * Returns true if the entity can break doors, false otherwise
     */
    public boolean getCanBreakDoors()
    {
        return this.canPassClosedWoodenDoors;
    }

    /**
     * Sets if the path should avoid sunlight
     */
    public void setAvoidSun(final boolean par1)
    {
        this.noSunPathfind = par1;
    }

    /**
     * Sets the speed
     */
    public void setSpeed(final double par1)
    {
        this.speed = par1;
    }

    /**
     * Sets if the entity can swim
     */
    public void setCanSwim(final boolean par1)
    {
        this.canSwim = par1;
    }

    /**
     * Gets the maximum distance that the path finding will search in.
     */
    public float getPathSearchRange()
    {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    /**
     * Returns the path to the given coordinates
     */
    public PathEntity getPathToXYZ(final double par1, final double par3, final double par5)
    {
        return !this.canNavigate() ? null : this.worldObj.getEntityPathToXYZ(this.theEntity, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    /**
     * Try to find and set a path to XYZ. Returns true if successful.
     */
    public boolean tryMoveToXYZ(final double par1, final double par3, final double par5, final double par7)
    {
        final PathEntity pathentity = this.getPathToXYZ((double)MathHelper.floor_double(par1), (double)((int)par3), (double)MathHelper.floor_double(par5));
        return this.setPath(pathentity, par7);
    }

    /**
     * Returns the path to the given EntityLiving
     */
    public PathEntity getPathToEntityLiving(final Entity par1Entity)
    {
        return !this.canNavigate() ? null : this.worldObj.getPathEntityToEntity(this.theEntity, par1Entity, this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful.
     */
    public boolean tryMoveToEntityLiving(final Entity par1Entity, final double par2)
    {
        final PathEntity pathentity = this.getPathToEntityLiving(par1Entity);
        return pathentity != null ? this.setPath(pathentity, par2) : false;
    }

    /**
     * sets the active path data if path is 100% unique compared to old path, checks to adjust path for sun avoiding
     * ents and stores end coords
     */
    public boolean setPath(final PathEntity par1PathEntity, final double par2)
    {
        if (par1PathEntity == null)
        {
            this.currentPath = null;
            return false;
        }
        else
        {
            if (!par1PathEntity.isSamePath(this.currentPath))
            {
                this.currentPath = par1PathEntity;
            }

            if (this.noSunPathfind)
            {
                this.removeSunnyPath();
            }

            if (this.currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.speed = par2;
                final Vec3 vec3 = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck.xCoord = vec3.xCoord;
                this.lastPosCheck.yCoord = vec3.yCoord;
                this.lastPosCheck.zCoord = vec3.zCoord;
                return true;
            }
        }
    }

    /**
     * gets the actively used PathEntity
     */
    public PathEntity getPath()
    {
        return this.currentPath;
    }

    public void onUpdateNavigation()
    {
        ++this.totalTicks;

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }

            if (!this.noPath())
            {
                final Vec3 vec3 = this.currentPath.getPosition(this.theEntity);

                if (vec3 != null)
                {
                    this.theEntity.getMoveHelper().setMoveTo(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.speed);
                }
            }
        }
    }

    private void pathFollow()
    {
        final Vec3 vec3 = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j)
        {
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord)
            {
                i = j;
                break;
            }
        }

        final float f = this.theEntity.width * this.theEntity.width;
        int k;

        for (k = this.currentPath.getCurrentPathIndex(); k < i; ++k)
        {
            if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, k)) < (double)f)
            {
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        k = MathHelper.ceiling_float_int(this.theEntity.width);
        final int l = (int)this.theEntity.height + 1;
        final int i1 = k;

        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1)
        {
            if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, j1), k, l, i1))
            {
                this.currentPath.setCurrentPathIndex(j1);
                break;
            }
        }

        if (this.totalTicks - this.ticksAtLastPos > 100)
        {
            if (vec3.squareDistanceTo(this.lastPosCheck) < 2.25D)
            {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = vec3.xCoord;
            this.lastPosCheck.yCoord = vec3.yCoord;
            this.lastPosCheck.zCoord = vec3.zCoord;
        }
    }

    /**
     * If null path or reached the end
     */
    public boolean noPath()
    {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    /**
     * sets active PathEntity to null
     */
    public void clearPathEntity()
    {
        this.currentPath = null;
    }

    private Vec3 getEntityPosition()
    {
        return this.worldObj.getWorldVec3Pool().getVecFromPool(this.theEntity.posX, (double)this.getPathableYPos(), this.theEntity.posZ);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    private int getPathableYPos()
    {
        if (this.theEntity.isInWater() && this.canSwim)
        {
            int i = (int)this.theEntity.boundingBox.minY;
            int j = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ));
            int k = 0;

            do
            {
                if (j != Block.waterMoving.blockID && j != Block.waterStill.blockID)
                {
                    return i;
                }

                ++i;
                j = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ));
                ++k;
            }
            while (k <= 16);

            return (int)this.theEntity.boundingBox.minY;
        }
        else
        {
            return (int)(this.theEntity.boundingBox.minY + 0.5D);
        }
    }

    /**
     * If on ground or swimming and can swim
     */
    private boolean canNavigate()
    {
        return this.theEntity.onGround || this.canSwim && this.isInFluid();
    }

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    private boolean isInFluid()
    {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    private void removeSunnyPath()
    {
        if (!this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.boundingBox.minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ)))
        {
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i)
            {
                final PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.worldObj.canBlockSeeTheSky(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord))
                {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    private boolean isDirectPathBetweenPoints(final Vec3 par1Vec3, final Vec3 par2Vec3, int par3, final int par4, int par5)
    {
        //todo sa1zer_
        int par31 = par3;
        int par51 = par5;
        if(par1Vec3 == null || par2Vec3 == null)
            return false;
        //todo sa1zer_ end
        int l = MathHelper.floor_double(par1Vec3.xCoord);
        int i1 = MathHelper.floor_double(par1Vec3.zCoord);
        double d0 = par2Vec3.xCoord - par1Vec3.xCoord;
        double d1 = par2Vec3.zCoord - par1Vec3.zCoord;
        final double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            final double d3 = 1.0D / Math.sqrt(d2);
            d0 *= d3;
            d1 *= d3;
            par31 += 2;
            par51 += 2;

            if (!this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par31, par4, par51, par1Vec3, d0, d1))
            {
                return false;
            }
            else
            {
                par31 -= 2;
                par51 -= 2;
                final double d4 = 1.0D / Math.abs(d0);
                final double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)(l * 1) - par1Vec3.xCoord;
                double d7 = (double)(i1 * 1) - par1Vec3.zCoord;

                if (d0 >= 0.0D)
                {
                    ++d6;
                }

                if (d1 >= 0.0D)
                {
                    ++d7;
                }

                d6 /= d0;
                d7 /= d1;
                final int j1 = d0 < 0.0D ? -1 : 1;
                final int k1 = d1 < 0.0D ? -1 : 1;
                final int l1 = MathHelper.floor_double(par2Vec3.xCoord);
                final int i2 = MathHelper.floor_double(par2Vec3.zCoord);
                int j2 = l1 - l;
                int k2 = i2 - i1;

                do
                {
                    if (j2 * j1 <= 0 && k2 * k1 <= 0)
                    {
                        return true;
                    }

                    if (d6 < d7)
                    {
                        d6 += d4;
                        l += j1;
                        j2 = l1 - l;
                    }
                    else
                    {
                        d7 += d5;
                        i1 += k1;
                        k2 = i2 - i1;
                    }
                }
                while (this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par31, par4, par51, par1Vec3, d0, d1));

                return false;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity. Args:
     * xOffset, yOffset, zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
     */
    private boolean isSafeToStandAt(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6, final Vec3 par7Vec3, final double par8, final double par10)
    {
        final int k1 = par1 - par4 / 2;
        final int l1 = par3 - par6 / 2;

        if (!this.isPositionClear(k1, par2, l1, par4, par5, par6, par7Vec3, par8, par10))
        {
            return false;
        }
        else
        {
            for (int i2 = k1; i2 < k1 + par4; ++i2)
            {
                for (int j2 = l1; j2 < l1 + par6; ++j2)
                {
                    final double d2 = (double)i2 + 0.5D - par7Vec3.xCoord;
                    final double d3 = (double)j2 + 0.5D - par7Vec3.zCoord;

                    if (d2 * par8 + d3 * par10 >= 0.0D)
                    {
                        final int k2 = this.worldObj.getBlockId(i2, par2 - 1, j2);

                        if (k2 <= 0)
                        {
                            return false;
                        }

                        final Material material = Block.blocksList[k2].blockMaterial;

                        if (material == Material.water && !this.theEntity.isInWater())
                        {
                            return false;
                        }

                        if (material == Material.lava)
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position. Args: xOffset, yOffset,
     * zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
     */
    private boolean isPositionClear(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6, final Vec3 par7Vec3, final double par8, final double par10)
    {
        for (int k1 = par1; k1 < par1 + par4; ++k1)
        {
            for (int l1 = par2; l1 < par2 + par5; ++l1)
            {
                for (int i2 = par3; i2 < par3 + par6; ++i2)
                {
                    final double d2 = (double)k1 + 0.5D - par7Vec3.xCoord;
                    final double d3 = (double)i2 + 0.5D - par7Vec3.zCoord;

                    if (d2 * par8 + d3 * par10 >= 0.0D)
                    {
                        final int j2 = this.worldObj.getBlockId(k1, l1, i2);

                        if (j2 > 0 && !Block.blocksList[j2].getBlocksMovement(this.worldObj, k1, l1, i2))
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
