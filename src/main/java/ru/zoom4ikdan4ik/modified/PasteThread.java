package ru.zoom4ikdan4ik.modified;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public final class PasteThread extends Thread {
    private final CommandSender commandSender;
    private final ByteArrayOutputStream byteArrayOutputStream;

    public PasteThread(final CommandSender commandSender, final ByteArrayOutputStream byteArrayOutputStream) {
        this.commandSender = commandSender;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public synchronized void start() {
        if (this.commandSender instanceof RemoteConsoleCommandSender) {
            run();
        } else {
            super.start();
        }
    }

    public void run() {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL("https://timings.spigotmc.org/paste")).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setInstanceFollowRedirects(false);

            final OutputStream out = httpURLConnection.getOutputStream();

            out.write(this.byteArrayOutputStream.toByteArray());
            out.close();

            JsonObject location = (new Gson()).fromJson(new InputStreamReader(httpURLConnection.getInputStream()), JsonObject.class);
            httpURLConnection.getInputStream().close();
            this.commandSender.sendMessage(ChatColor.GREEN + "Timings results can be viewed at https://www.spigotmc.org/go/timings?url=" + location.get("key").getAsString());
        } catch (IOException ex) {
            this.commandSender.sendMessage(ChatColor.RED + "Error pasting timings, check your console for more information");
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not paste timings", ex);
        }
    }
}
