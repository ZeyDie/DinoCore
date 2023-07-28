package net.minecraft.profiler;

import net.minecraft.util.HttpUtil;

import java.util.HashMap;
import java.util.TimerTask;

class PlayerUsageSnooperThread extends TimerTask
{
    /** The PlayerUsageSnooper object. */
    final PlayerUsageSnooper snooper;

    PlayerUsageSnooperThread(final PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        this.snooper = par1PlayerUsageSnooper;
    }

    public void run()
    {
        if (PlayerUsageSnooper.getStatsCollectorFor(this.snooper).isSnooperEnabled())
        {
            final HashMap hashmap;

            synchronized (PlayerUsageSnooper.getSyncLockFor(this.snooper))
            {
                hashmap = new HashMap(PlayerUsageSnooper.getDataMapFor(this.snooper));
                hashmap.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.getSelfCounterFor(this.snooper)));
            }

            HttpUtil.sendPost(PlayerUsageSnooper.getStatsCollectorFor(this.snooper).getLogAgent(), PlayerUsageSnooper.getServerUrlFor(this.snooper), hashmap, true);
        }
    }
}
