package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.EnumGameType;

@SideOnly(Side.CLIENT)
public class SaveFormatComparator implements Comparable
{
    /** the file name of this save */
    private final String fileName;

    /** the displayed name of this save file */
    private final String displayName;
    private final long lastTimePlayed;
    private final long sizeOnDisk;
    private final boolean requiresConversion;

    /** Instance of EnumGameType. */
    private final EnumGameType theEnumGameType;
    private final boolean hardcore;
    private final boolean cheatsEnabled;

    public SaveFormatComparator(final String par1Str, final String par2Str, final long par3, final long par5, final EnumGameType par7EnumGameType, final boolean par8, final boolean par9, final boolean par10)
    {
        this.fileName = par1Str;
        this.displayName = par2Str;
        this.lastTimePlayed = par3;
        this.sizeOnDisk = par5;
        this.theEnumGameType = par7EnumGameType;
        this.requiresConversion = par8;
        this.hardcore = par9;
        this.cheatsEnabled = par10;
    }

    /**
     * return the file name
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     * return the display name of the save
     */
    public String getDisplayName()
    {
        return this.displayName;
    }

    public boolean requiresConversion()
    {
        return this.requiresConversion;
    }

    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    public int compareTo(final SaveFormatComparator par1SaveFormatComparator)
    {
        return this.lastTimePlayed < par1SaveFormatComparator.lastTimePlayed ? 1 : (this.lastTimePlayed > par1SaveFormatComparator.lastTimePlayed ? -1 : this.fileName.compareTo(par1SaveFormatComparator.fileName));
    }

    /**
     * Gets the EnumGameType.
     */
    public EnumGameType getEnumGameType()
    {
        return this.theEnumGameType;
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    /**
     * @return {@code true} if cheats are enabled for this world
     */
    public boolean getCheatsEnabled()
    {
        return this.cheatsEnabled;
    }

    public int compareTo(final Object par1Obj)
    {
        return this.compareTo((SaveFormatComparator)par1Obj);
    }
}
