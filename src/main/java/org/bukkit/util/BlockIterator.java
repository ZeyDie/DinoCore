package org.bukkit.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.bukkit.util.NumberConversions.floor;
import static org.bukkit.util.NumberConversions.round;

/**
 * This class performs ray tracing and iterates along blocks on a line
 */
public class BlockIterator implements Iterator<Block> {

    private final World world;
    private final int maxDistance;

    private static final int gridSize = 1 << 24;

    private boolean end = false;

    private Block[] blockQueue = new Block[3];
    private int currentBlock = 0;
    private int currentDistance = 0;
    private int maxDistanceInt;

    private int secondError;
    private int thirdError;

    private int secondStep;
    private int thirdStep;

    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;

    /**
     * Constructs the BlockIterator
     *
     * @param world The world to use for tracing
     * @param start A Vector giving the initial location for the trace
     * @param direction A Vector pointing in the direction for the trace
     * @param yOffset The trace begins vertically offset from the start vector by this value
     * @param maxDistance This is the maximum distance in blocks for the trace. Setting this value above 140 may lead to problems with unloaded chunks. A value of 0 indicates no limit
     *
     */
    public BlockIterator(final World world, final Vector start, final Vector direction, final double yOffset, final int maxDistance) {
        this.world = world;
        this.maxDistance = maxDistance;

        final Vector startClone = start.clone();

        startClone.setY(startClone.getY() + yOffset);

        currentDistance = 0;

        double mainDirection = 0;
        double secondDirection = 0;
        double thirdDirection = 0;

        double mainPosition = 0;
        double secondPosition = 0;
        double thirdPosition = 0;

        final Block startBlock = this.world.getBlockAt(floor(startClone.getX()), floor(startClone.getY()), floor(startClone.getZ()));

        if (getXLength(direction) > mainDirection) {
            mainFace = getXFace(direction);
            mainDirection = getXLength(direction);
            mainPosition = getXPosition(direction, startClone, startBlock);

            secondFace = getYFace(direction);
            secondDirection = getYLength(direction);
            secondPosition = getYPosition(direction, startClone, startBlock);

            thirdFace = getZFace(direction);
            thirdDirection = getZLength(direction);
            thirdPosition = getZPosition(direction, startClone, startBlock);
        }
        if (getYLength(direction) > mainDirection) {
            mainFace = getYFace(direction);
            mainDirection = getYLength(direction);
            mainPosition = getYPosition(direction, startClone, startBlock);

            secondFace = getZFace(direction);
            secondDirection = getZLength(direction);
            secondPosition = getZPosition(direction, startClone, startBlock);

            thirdFace = getXFace(direction);
            thirdDirection = getXLength(direction);
            thirdPosition = getXPosition(direction, startClone, startBlock);
        }
        if (getZLength(direction) > mainDirection) {
            mainFace = getZFace(direction);
            mainDirection = getZLength(direction);
            mainPosition = getZPosition(direction, startClone, startBlock);

            secondFace = getXFace(direction);
            secondDirection = getXLength(direction);
            secondPosition = getXPosition(direction, startClone, startBlock);

            thirdFace = getYFace(direction);
            thirdDirection = getYLength(direction);
            thirdPosition = getYPosition(direction, startClone, startBlock);
        }

        // trace line backwards to find intercept with plane perpendicular to the main axis

        final double d = mainPosition / mainDirection; // how far to hit face behind
        final double secondd = secondPosition - secondDirection * d;
        final double thirdd = thirdPosition - thirdDirection * d;

        // Guarantee that the ray will pass though the start block.
        // It is possible that it would miss due to rounding
        // This should only move the ray by 1 grid position
        secondError = floor(secondd * gridSize);
        secondStep = round(secondDirection / mainDirection * gridSize);
        thirdError = floor(thirdd * gridSize);
        thirdStep = round(thirdDirection / mainDirection * gridSize);

        if (secondError + secondStep <= 0) {
            secondError = -secondStep + 1;
        }

        if (thirdError + thirdStep <= 0) {
            thirdError = -thirdStep + 1;
        }

        Block lastBlock;

        lastBlock = startBlock.getRelative(mainFace.getOppositeFace());

        if (secondError < 0) {
            secondError += gridSize;
            lastBlock = lastBlock.getRelative(secondFace.getOppositeFace());
        }

        if (thirdError < 0) {
            thirdError += gridSize;
            lastBlock = lastBlock.getRelative(thirdFace.getOppositeFace());
        }

        // This means that when the variables are positive, it means that the coord=1 boundary has been crossed
        secondError -= gridSize;
        thirdError -= gridSize;

        blockQueue[0] = lastBlock;
        currentBlock = -1;

        scan();

        boolean startBlockFound = false;

        for (int cnt = currentBlock; cnt >= 0; cnt--) {
            if (blockEquals(blockQueue[cnt], startBlock)) {
                currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        // Calculate the number of planes passed to give max distance
        maxDistanceInt = round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));

    }

    private boolean blockEquals(final Block a, final Block b) {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    private BlockFace getXFace(final Vector direction) {
        return ((direction.getX() > 0) ? BlockFace.EAST : BlockFace.WEST);
    }

    private BlockFace getYFace(final Vector direction) {
        return ((direction.getY() > 0) ? BlockFace.UP : BlockFace.DOWN);
    }

    private BlockFace getZFace(final Vector direction) {
        return ((direction.getZ() > 0) ? BlockFace.SOUTH : BlockFace.NORTH);
    }

    private double getXLength(final Vector direction) {
        return Math.abs(direction.getX());
    }

    private double getYLength(final Vector direction) {
        return Math.abs(direction.getY());
    }

    private double getZLength(final Vector direction) {
        return Math.abs(direction.getZ());
    }

    private double getPosition(final double direction, final double position, final int blockPosition) {
        return direction > 0 ? (position - blockPosition) : (blockPosition + 1 - position);
    }

    private double getXPosition(final Vector direction, final Vector position, final Block block) {
        return getPosition(direction.getX(), position.getX(), block.getX());
    }

    private double getYPosition(final Vector direction, final Vector position, final Block block) {
        return getPosition(direction.getY(), position.getY(), block.getY());
    }

    private double getZPosition(final Vector direction, final Vector position, final Block block) {
        return getPosition(direction.getZ(), position.getZ(), block.getZ());
    }

    /**
     * Constructs the BlockIterator
     *
     * @param loc The location for the start of the ray trace
     * @param yOffset The trace begins vertically offset from the start vector by this value
     * @param maxDistance This is the maximum distance in blocks for the trace. Setting this value above 140 may lead to problems with unloaded chunks. A value of 0 indicates no limit
     */
    public BlockIterator(final Location loc, final double yOffset, final int maxDistance) {
        this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, maxDistance);
    }

    /**
     * Constructs the BlockIterator.
     *
     * @param loc The location for the start of the ray trace
     * @param yOffset The trace begins vertically offset from the start vector by this value
     */

    public BlockIterator(final Location loc, final double yOffset) {
        this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, 0);
    }

    /**
     * Constructs the BlockIterator.
     *
     * @param loc The location for the start of the ray trace
     */

    public BlockIterator(final Location loc) {
        this(loc, 0.0D);
    }

    /**
     * Constructs the BlockIterator.
     *
     * @param entity Information from the entity is used to set up the trace
     * @param maxDistance This is the maximum distance in blocks for the trace. Setting this value above 140 may lead to problems with unloaded chunks. A value of 0 indicates no limit
     */

    public BlockIterator(final LivingEntity entity, final int maxDistance) {
        this(entity.getLocation(), entity.getEyeHeight(), maxDistance);
    }

    /**
     * Constructs the BlockIterator.
     *
     * @param entity Information from the entity is used to set up the trace
     */

    public BlockIterator(final LivingEntity entity) {
        this(entity, 0);
    }

    /**
     * Returns true if the iteration has more elements
     */

    public boolean hasNext() {
        scan();
        return currentBlock != -1;
    }

    /**
     * Returns the next Block in the trace
     *
     * @return the next Block in the trace
     */

    public Block next() {
        scan();
        if (currentBlock <= -1) {
            throw new NoSuchElementException();
        } else {
            return blockQueue[currentBlock--];
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("[BlockIterator] doesn't support block removal");
    }

    private void scan() {
        if (currentBlock >= 0) {
            return;
        }
        if (maxDistance != 0 && currentDistance > maxDistanceInt) {
            end = true;
            return;
        }
        if (end) {
            return;
        }

        currentDistance++;

        secondError += secondStep;
        thirdError += thirdStep;

        if (secondError > 0 && thirdError > 0) {
            blockQueue[2] = blockQueue[0].getRelative(mainFace);
            if (((long) secondStep) * ((long) thirdError) < ((long) thirdStep) * ((long) secondError)) {
                blockQueue[1] = blockQueue[2].getRelative(secondFace);
                blockQueue[0] = blockQueue[1].getRelative(thirdFace);
            } else {
                blockQueue[1] = blockQueue[2].getRelative(thirdFace);
                blockQueue[0] = blockQueue[1].getRelative(secondFace);
            }
            thirdError -= gridSize;
            secondError -= gridSize;
            currentBlock = 2;
            return;
        } else if (secondError > 0) {
            blockQueue[1] = blockQueue[0].getRelative(mainFace);
            blockQueue[0] = blockQueue[1].getRelative(secondFace);
            secondError -= gridSize;
            currentBlock = 1;
            return;
        } else if (thirdError > 0) {
            blockQueue[1] = blockQueue[0].getRelative(mainFace);
            blockQueue[0] = blockQueue[1].getRelative(thirdFace);
            thirdError -= gridSize;
            currentBlock = 1;
            return;
        } else {
            blockQueue[0] = blockQueue[0].getRelative(mainFace);
            currentBlock = 0;
            return;
        }
    }
}
