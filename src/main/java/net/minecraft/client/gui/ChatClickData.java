package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class ChatClickData
{
    public static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
    private final FontRenderer fontR;
    private final ChatLine line;
    private final int field_78312_d;
    private final int field_78313_e;
    private final String field_78310_f;

    /** The URL which was clicked on. */
    private final String clickedUrl;

    public ChatClickData(final FontRenderer par1FontRenderer, final ChatLine par2ChatLine, final int par3, final int par4)
    {
        this.fontR = par1FontRenderer;
        this.line = par2ChatLine;
        this.field_78312_d = par3;
        this.field_78313_e = par4;
        this.field_78310_f = par1FontRenderer.trimStringToWidth(par2ChatLine.getChatLineString(), par3);
        this.clickedUrl = this.findClickedUrl();
    }

    /**
     * Gets the URL which was clicked on.
     */
    public String getClickedUrl()
    {
        return this.clickedUrl;
    }

    /**
     * computes the URI from the clicked chat data object
     */
    public URI getURI()
    {
        final String s = this.getClickedUrl();

        if (s == null)
        {
            return null;
        }
        else
        {
            final Matcher matcher = pattern.matcher(s);

            if (matcher.matches())
            {
                try
                {
                    String s1 = matcher.group(0);

                    if (matcher.group(1) == null)
                    {
                        s1 = "http://" + s1;
                    }

                    return new URI(s1);
                }
                catch (final URISyntaxException urisyntaxexception)
                {
                    Minecraft.getMinecraft().getLogAgent().logSevereException("Couldn\'t create URI from chat", urisyntaxexception);
                }
            }

            return null;
        }
    }

    private String findClickedUrl()
    {
        int i = this.field_78310_f.lastIndexOf(" ", this.field_78310_f.length()) + 1;

        if (i < 0)
        {
            i = 0;
        }

        int j = this.line.getChatLineString().indexOf(" ", i);

        if (j < 0)
        {
            j = this.line.getChatLineString().length();
        }

        return StringUtils.stripControlCodes(this.line.getChatLineString().substring(i, j));
    }
}
