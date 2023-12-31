package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class EnchantmentNameParts
{
    /** The static instance of this class. */
    public static final EnchantmentNameParts instance = new EnchantmentNameParts();

    /** The RNG used to generate enchant names. */
    private Random rand = new Random();

    /** List of words used to generate an enchant name. */
    private String[] wordList = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale ".split(" ");

    /**
     * Generates a random enchant name.
     */
    public String generateRandomEnchantName()
    {
        final int i = this.rand.nextInt(2) + 3;
        String s = "";

        for (int j = 0; j < i; ++j)
        {
            if (j > 0)
            {
                s = s + " ";
            }

            s = s + this.wordList[this.rand.nextInt(this.wordList.length)];
        }

        return s;
    }

    /**
     * Sets the seed for the enchant name RNG.
     */
    public void setRandSeed(final long par1)
    {
        this.rand.setSeed(par1);
    }
}
