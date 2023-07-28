package net.minecraft.block;

final class StepSoundStone extends StepSound
{
    StepSoundStone(final String par1Str, final float par2, final float par3)
    {
        super(par1Str, par2, par3);
    }

    /**
     * Used when a block breaks, EXA: Player break, Shep eating grass, etc..
     */
    public String getBreakSound()
    {
        return "random.glass";
    }

    /**
     * Used when a player places a block.
     */
    public String getPlaceSound()
    {
        return "step.stone";
    }
}
