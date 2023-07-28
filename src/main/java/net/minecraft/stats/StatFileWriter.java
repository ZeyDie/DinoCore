package net.minecraft.stats;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import cpw.mods.fml.common.asm.ReobfuscationMarker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.stats.StatPlaceholder;
import net.minecraft.util.MD5String;
import net.minecraft.util.Session;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ReobfuscationMarker
@SideOnly(Side.CLIENT)
public class StatFileWriter
{
    private Map field_77457_a = new HashMap();
    private Map field_77455_b = new HashMap();
    private boolean field_77456_c;
    private StatsSyncher statsSyncher;

    public StatFileWriter(final Session par1Session, final File par2File)
    {
        final File file2 = new File(par2File, "stats");

        if (!file2.exists())
        {
            file2.mkdir();
        }

        final File[] afile = par2File.listFiles();
        final int i = afile.length;

        for (int j = 0; j < i; ++j)
        {
            final File file3 = afile[j];

            if (file3.getName().startsWith("stats_") && file3.getName().endsWith(".dat"))
            {
                final File file4 = new File(file2, file3.getName());

                if (!file4.exists())
                {
                    System.out.println("Relocating " + file3.getName());
                    file3.renameTo(file4);
                }
            }
        }

        this.statsSyncher = new StatsSyncher(par1Session, this, file2);
    }

    public void readStat(final StatBase par1StatBase, final int par2)
    {
        this.writeStatToMap(this.field_77455_b, par1StatBase, par2);
        this.writeStatToMap(this.field_77457_a, par1StatBase, par2);
        this.field_77456_c = true;
    }

    private void writeStatToMap(final Map par1Map, final StatBase par2StatBase, final int par3)
    {
        final Integer integer = (Integer)par1Map.get(par2StatBase);
        final int j = integer == null ? 0 : integer.intValue();
        par1Map.put(par2StatBase, Integer.valueOf(j + par3));
    }

    public Map func_77445_b()
    {
        return new HashMap(this.field_77455_b);
    }

    /**
     * write a whole Map of stats to the statmap
     */
    public void writeStats(final Map par1Map)
    {
        if (par1Map != null)
        {
            this.field_77456_c = true;
            final Iterator iterator = par1Map.keySet().iterator();

            while (iterator.hasNext())
            {
                final StatBase statbase = (StatBase)iterator.next();
                this.writeStatToMap(this.field_77455_b, statbase, ((Integer)par1Map.get(statbase)).intValue());
                this.writeStatToMap(this.field_77457_a, statbase, ((Integer)par1Map.get(statbase)).intValue());
            }
        }
    }

    public void func_77452_b(final Map par1Map)
    {
        if (par1Map != null)
        {
            final Iterator iterator = par1Map.keySet().iterator();

            while (iterator.hasNext())
            {
                final StatBase statbase = (StatBase)iterator.next();
                final Integer integer = (Integer)this.field_77455_b.get(statbase);
                final int i = integer == null ? 0 : integer.intValue();
                this.field_77457_a.put(statbase, Integer.valueOf(((Integer)par1Map.get(statbase)).intValue() + i));
            }
        }
    }

    public void func_77448_c(final Map par1Map)
    {
        if (par1Map != null)
        {
            this.field_77456_c = true;
            final Iterator iterator = par1Map.keySet().iterator();

            while (iterator.hasNext())
            {
                final StatBase statbase = (StatBase)iterator.next();
                this.writeStatToMap(this.field_77455_b, statbase, ((Integer)par1Map.get(statbase)).intValue());
            }
        }
    }

    public static Map func_77453_b(final String par0Str)
    {
        final HashMap hashmap = new HashMap();

        try
        {
            final String s1 = "local";
            final StringBuilder stringbuilder = new StringBuilder();
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);
            final List list = jsonrootnode.getArrayNode(new Object[] {"stats-change"});
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final JsonNode jsonnode = (JsonNode)iterator.next();
                final Map map = jsonnode.getFields();
                final Entry entry = (Entry)map.entrySet().iterator().next();
                final int i = Integer.parseInt(((JsonStringNode)entry.getKey()).getText());
                final int j = Integer.parseInt(((JsonNode)entry.getValue()).getText());
                boolean flag = true;
                StatBase statbase = StatList.getOneShotStat(i);

                if (statbase == null)
                {
                    flag = false;
                    statbase = (new StatPlaceholder(i)).registerStat();
                }

                stringbuilder.append(StatList.getOneShotStat(i).statGuid).append(",");
                stringbuilder.append(j).append(",");

                if (flag)
                {
                    hashmap.put(statbase, Integer.valueOf(j));
                }
            }

            final MD5String md5string = new MD5String(s1);
            final String s2 = md5string.getMD5String(stringbuilder.toString());

            if (!s2.equals(jsonrootnode.getStringValue(new Object[] {"checksum"})))
            {
                System.out.println("CHECKSUM MISMATCH");
                return null;
            }
        }
        catch (final InvalidSyntaxException invalidsyntaxexception)
        {
            invalidsyntaxexception.printStackTrace();
        }

        return hashmap;
    }

    public static String func_77441_a(final String par0Str, final String par1Str, final Map par2Map)
    {
        final StringBuilder stringbuilder = new StringBuilder();
        final StringBuilder stringbuilder1 = new StringBuilder();
        boolean flag = true;
        stringbuilder.append("{\r\n");

        if (par0Str != null && par1Str != null)
        {
            stringbuilder.append("  \"user\":{\r\n");
            stringbuilder.append("    \"name\":\"").append(par0Str).append("\",\r\n");
            stringbuilder.append("    \"sessionid\":\"").append(par1Str).append("\"\r\n");
            stringbuilder.append("  },\r\n");
        }

        stringbuilder.append("  \"stats-change\":[");
        final Iterator iterator = par2Map.keySet().iterator();

        while (iterator.hasNext())
        {
            final StatBase statbase = (StatBase)iterator.next();

            if (flag)
            {
                flag = false;
            }
            else
            {
                stringbuilder.append("},");
            }

            stringbuilder.append("\r\n    {\"").append(statbase.statId).append("\":").append(par2Map.get(statbase));
            stringbuilder1.append(statbase.statGuid).append(",");
            stringbuilder1.append(par2Map.get(statbase)).append(",");
        }

        if (!flag)
        {
            stringbuilder.append("}");
        }

        final MD5String md5string = new MD5String(par1Str);
        stringbuilder.append("\r\n  ],\r\n");
        stringbuilder.append("  \"checksum\":\"").append(md5string.getMD5String(stringbuilder1.toString())).append("\"\r\n");
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    /**
     * Returns true if the achievement has been unlocked.
     */
    public boolean hasAchievementUnlocked(final Achievement par1Achievement)
    {
        return this.field_77457_a.containsKey(par1Achievement);
    }

    /**
     * Returns true if the parent has been unlocked, or there is no parent
     */
    public boolean canUnlockAchievement(final Achievement par1Achievement)
    {
        return par1Achievement.parentAchievement == null || this.hasAchievementUnlocked(par1Achievement.parentAchievement);
    }

    public int writeStat(final StatBase par1StatBase)
    {
        final Integer integer = (Integer)this.field_77457_a.get(par1StatBase);
        return integer == null ? 0 : integer.intValue();
    }

    public void syncStats()
    {
        this.statsSyncher.syncStatsFileWithMap(this.func_77445_b());
    }

    public void func_77449_e()
    {
        if (this.field_77456_c && this.statsSyncher.func_77425_c())
        {
            this.statsSyncher.beginSendStats(this.func_77445_b());
        }

        this.statsSyncher.func_77422_e();
    }
}
