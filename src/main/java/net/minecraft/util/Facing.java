package net.minecraft.util;

public class Facing
{
    /**
     * Converts a side to the opposite side. This is the same as XOR'ing it with 1.
     */
    public static final int[] oppositeSide = {1, 0, 3, 2, 5, 4};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsXForSide = {0, 0, 0, 0, -1, 1};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsYForSide = { -1, 1, 0, 0, 0, 0};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsZForSide = {0, 0, -1, 1, 0, 0};
    public static final String[] facings = {"DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST"};
}
