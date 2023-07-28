package net.minecraft.stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AchievementMap
{
    /** Holds the singleton instance of AchievementMap. */
    public static AchievementMap instance = new AchievementMap();

    /** Maps a achievement id with it's unique GUID. */
    private Map guidMap = new HashMap();

    private AchievementMap()
    {
        try
        {
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(AchievementMap.class.getResourceAsStream("/achievement/map.txt")));
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                final String[] astring = s.split(",");
                final int i = Integer.parseInt(astring[0]);
                this.guidMap.put(Integer.valueOf(i), astring[1]);
            }

            bufferedreader.close();
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the unique GUID of a achievement id.
     */
    public static String getGuid(final int par0)
    {
        return (String)instance.guidMap.get(Integer.valueOf(par0));
    }
}
