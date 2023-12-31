package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class Locale
{
    /** Splits on "=" */
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    Map field_135032_a = Maps.newHashMap();
    private boolean field_135029_d;

    /**
     * par2 is a list of languages. For each language $L and domain $D, attempts to load the resource $D:lang/$L.lang
     */
    public synchronized void loadLocaleDataFiles(final ResourceManager par1ResourceManager, final List par2List)
    {
        this.field_135032_a.clear();
        final Iterator iterator = par2List.iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();
            final String s1 = String.format("lang/%s.lang", new Object[] {s});
            final Iterator iterator1 = par1ResourceManager.getResourceDomains().iterator();

            while (iterator1.hasNext())
            {
                final String s2 = (String)iterator1.next();

                try
                {
                    this.loadLocaleData(par1ResourceManager.getAllResources(new ResourceLocation(s2, s1)));
                }
                catch (final IOException ioexception)
                {
                    ;
                }
            }
        }

        this.checkUnicode();
    }

    public boolean isUnicode()
    {
        return this.field_135029_d;
    }

    private void checkUnicode()
    {
        this.field_135029_d = false;
        final Iterator iterator = this.field_135032_a.values().iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();

            for (int i = 0; i < s.length(); ++i)
            {
                if (s.charAt(i) >= 256)
                {
                    this.field_135029_d = true;
                    break;
                }
            }
        }
    }

    /**
     * par1 is a list of Resources
     */
    private void loadLocaleData(final List par1List) throws IOException
    {
        final Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            final Resource resource = (Resource)iterator.next();
            this.loadLocaleData(resource.getInputStream());
        }
    }

    private void loadLocaleData(final InputStream par1InputStream) throws IOException
    {
        final Iterator iterator = IOUtils.readLines(par1InputStream, Charsets.UTF_8).iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();

            if (!s.isEmpty() && s.charAt(0) != 35)
            {
                final String[] astring = (String[])Iterables.toArray(splitter.split(s), String.class);

                if (astring != null && astring.length == 2)
                {
                    final String s1 = astring[0];
                    final String s2 = field_135031_c.matcher(astring[1]).replaceAll("%$1s");
                    this.field_135032_a.put(s1, s2);
                }
            }
        }
    }

    /**
     * Returns the translation, or the key itself if the key could not be translated.
     */
    private String translateKeyPrivate(final String par1Str)
    {
        final String s1 = (String)this.field_135032_a.get(par1Str);
        return s1 == null ? par1Str : s1;
    }

    /**
     * Returns the translation, or the key itself if the key could not be translated.
     */
    public String translateKey(final String par1Str)
    {
        return this.translateKeyPrivate(par1Str);
    }

    /**
     * Calls String.format(translateKey(key), params)
     */
    public String formatMessage(final String par1Str, final Object[] par2ArrayOfObj)
    {
        final String s1 = this.translateKeyPrivate(par1Str);

        try
        {
            return String.format(s1, par2ArrayOfObj);
        }
        catch (final IllegalFormatException illegalformatexception)
        {
            return "Format error: " + s1;
        }
    }
}
