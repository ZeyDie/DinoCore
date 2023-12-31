package net.minecraft.block;

final class StepSoundAnvil extends StepSound
{
    StepSoundAnvil(final String par1Str, final float par2, final float par3)
    {
        super(par1Str, par2, par3);
    }

    /**
     * Used when a block breaks, EXA: Player break, Shep eating grass, etc..
     */
    public String getBreakSound()
    {
        return "dig.stone";
    }

    /**
     * Used when a player places a block.
     */
    public String getPlaceSound()
    {
        return "random.anvil_land";
    }
}
