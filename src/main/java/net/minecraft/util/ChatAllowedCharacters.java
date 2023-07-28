package net.minecraft.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ChatAllowedCharacters
{
    /**
     * This String have the characters allowed in any text drawing of minecraft.
     */
    public static final String allowedCharacters = getAllowedCharacters();

    /**
     * Array of the special characters that are allowed in any text drawing of Minecraft.
     */
    public static final char[] allowedCharactersArray = {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    /**
     * Load the font.txt resource file, that is on UTF-8 format. This file contains the characters that minecraft can
     * render Strings on screen.
     */
    private static String getAllowedCharacters()
    {
        String s = "";

        try
        {
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(ChatAllowedCharacters.class.getResourceAsStream("/font.txt"), StandardCharsets.UTF_8));
            String s1 = "";

            while ((s1 = bufferedreader.readLine()) != null)
            {
                if (!s1.startsWith("#"))
                {
                    s = s + s1;
                }
            }

            bufferedreader.close();
        }
        catch (final Exception exception)
        {
            ;
        }

        return s;
    }

    public static final boolean isAllowedCharacter(final char par0)
    {
        return par0 != 167 && (allowedCharacters.indexOf(par0) >= 0 || par0 > 32);
    }

    /**
     * Filter string by only keeping those characters for which isAllowedCharacter() returns true.
     */
    public static String filerAllowedCharacters(final String par0Str)
    {
        final StringBuilder stringbuilder = new StringBuilder();
        final char[] achar = par0Str.toCharArray();
        final int i = achar.length;

        for (int j = 0; j < i; ++j)
        {
            final char c0 = achar[j];

            if (isAllowedCharacter(c0))
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }
}
