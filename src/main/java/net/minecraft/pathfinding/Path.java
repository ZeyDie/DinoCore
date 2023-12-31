package net.minecraft.pathfinding;

public class Path
{
    /** Contains the points in this path */
    private PathPoint[] pathPoints = new PathPoint[128]; // CraftBukkit - reduce default size

    /** The number of points in this path */
    private int count;

    /**
     * Adds a point to the path
     */
    public PathPoint addPoint(final PathPoint par1PathPoint)
    {
        if (par1PathPoint.index >= 0)
        {
            throw new IllegalStateException("OW KNOWS!");
        }
        else
        {
            if (this.count == this.pathPoints.length)
            {
                final PathPoint[] apathpoint = new PathPoint[this.count << 1];
                System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
                this.pathPoints = apathpoint;
            }

            this.pathPoints[this.count] = par1PathPoint;
            par1PathPoint.index = this.count;
            this.sortBack(this.count++);
            return par1PathPoint;
        }
    }

    /**
     * Clears the path
     */
    public void clearPath()
    {
        this.count = 0;
    }

    /**
     * Returns and removes the first point in the path
     */
    public PathPoint dequeue()
    {
        final PathPoint pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;

        if (this.count > 0)
        {
            this.sortForward(0);
        }

        pathpoint.index = -1;
        return pathpoint;
    }

    /**
     * Changes the provided point's distance to target
     */
    public void changeDistance(final PathPoint par1PathPoint, final float par2)
    {
        final float f1 = par1PathPoint.distanceToTarget;
        par1PathPoint.distanceToTarget = par2;

        if (par2 < f1)
        {
            this.sortBack(par1PathPoint.index);
        }
        else
        {
            this.sortForward(par1PathPoint.index);
        }
    }

    /**
     * Sorts a point to the left
     */
    private void sortBack(int par1)
    {
        int par11 = par1;
        final PathPoint pathpoint = this.pathPoints[par11];
        int j;

        for (final float f = pathpoint.distanceToTarget; par11 > 0; par11 = j)
        {
            j = par11 - 1 >> 1;
            final PathPoint pathpoint1 = this.pathPoints[j];

            if (f >= pathpoint1.distanceToTarget)
            {
                break;
            }

            this.pathPoints[par11] = pathpoint1;
            pathpoint1.index = par11;
        }

        this.pathPoints[par11] = pathpoint;
        pathpoint.index = par11;
    }

    /**
     * Sorts a point to the right
     */
    private void sortForward(int par1)
    {
        int par11 = par1;
        final PathPoint pathpoint = this.pathPoints[par11];
        final float f = pathpoint.distanceToTarget;

        while (true)
        {
            final int j = 1 + (par11 << 1);
            final int k = j + 1;

            if (j >= this.count)
            {
                break;
            }

            final PathPoint pathpoint1 = this.pathPoints[j];
            final float f1 = pathpoint1.distanceToTarget;
            final PathPoint pathpoint2;
            final float f2;

            if (k >= this.count)
            {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            }
            else
            {
                pathpoint2 = this.pathPoints[k];
                f2 = pathpoint2.distanceToTarget;
            }

            if (f1 < f2)
            {
                if (f1 >= f)
                {
                    break;
                }

                this.pathPoints[par11] = pathpoint1;
                pathpoint1.index = par11;
                par11 = j;
            }
            else
            {
                if (f2 >= f)
                {
                    break;
                }

                this.pathPoints[par11] = pathpoint2;
                pathpoint2.index = par11;
                par11 = k;
            }
        }

        this.pathPoints[par11] = pathpoint;
        pathpoint.index = par11;
    }

    /**
     * Returns true if this path contains no points
     */
    public boolean isPathEmpty()
    {
        return this.count == 0;
    }
}
