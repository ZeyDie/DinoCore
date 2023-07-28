import net.minecraft.client.main.Main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public class Start
{
    public static void main(String[] args) throws Exception
    {
        String[] args1 = args;
        int userIndex = -1;
        int passIndex = -1;
        int sessIndex = -1;
        int versIndex = -1;

        for (int x = 0; x < args1.length; x++)
        {
            if (args1[x].equals("--username"))
            {
                userIndex = x + 1;
            }
            else if (args1[x].equals("--password"))
            {
                passIndex = x + 1;
            }
            else if (args1[x].equals("--session"))
            {
                sessIndex = x + 1;
            }
            else if (args1[x].equals("--version"))
            {
                versIndex = x + 1;
            }
        }

        if (userIndex != 0 - 1 && passIndex != -1 && sessIndex == -1)
        {
            final String[] session = getSession(args1[userIndex], args1[passIndex]);

            if (session != null)
            {
                args1[userIndex] = session[0];
                args1 = concat(args1, new String[] {"--session", session[1]});
            }
        }

        if (passIndex != -1)
        {
            args1[passIndex - 1] = "no_password_for_joo";
            args1[passIndex] = "no_password_for_joo";
        }

        if (versIndex == -1)
        {
            args1 = concat(args1, new String[] { "--version", "fml_mcp" });
        }

        Main.main(args1);
    }

    private static String[] getSession(final String username, final String password) throws UnsupportedEncodingException
    {
        final String parameters = "http://login.minecraft.net/?user=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&version=" + 13;
        final String result = openUrl(parameters);

        if (result == null)
        {
            System.out.println("Can't connect to minecraft.net");
            return null;
        }

        if (!result.contains(":"))
        {
            System.out.println("Login Failed: " + result);
            return null;
        }

        final String[] values = result.split(":");
        return new String[] { values[2].trim(), values[3].trim() };
    }

    private static String openUrl(final String addr)
    {
        try
        {
            final URL url = new URL(addr);
            final java.io.InputStream is;
            is = url.openConnection().getInputStream();
            final java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String buf = "";
            String line = null;

            while ((line = reader.readLine()) != null)
            {
                buf += "\n" + line;
            }

            reader.close();
            return buf;
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static <T> T[] concat(final T[] first, final T[] second)
    {
        final T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}