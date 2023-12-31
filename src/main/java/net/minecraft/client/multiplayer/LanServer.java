package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class LanServer
{
    private String lanServerMotd;
    private String lanServerIpPort;

    /** Last time this LanServer was seen. */
    private long timeLastSeen;

    public LanServer(final String par1Str, final String par2Str)
    {
        this.lanServerMotd = par1Str;
        this.lanServerIpPort = par2Str;
        this.timeLastSeen = Minecraft.getSystemTime();
    }

    public String getServerMotd()
    {
        return this.lanServerMotd;
    }

    public String getServerIpPort()
    {
        return this.lanServerIpPort;
    }

    /**
     * Updates the time this LanServer was last seen.
     */
    public void updateLastSeen()
    {
        this.timeLastSeen = Minecraft.getSystemTime();
    }
}
