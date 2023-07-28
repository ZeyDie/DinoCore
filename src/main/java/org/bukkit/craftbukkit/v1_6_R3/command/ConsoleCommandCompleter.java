package org.bukkit.craftbukkit.v1_6_R3.command;

import jline.console.completer.Completer;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConsoleCommandCompleter implements Completer {
    private final CraftServer server;

    public ConsoleCommandCompleter(final CraftServer server) {
        this.server = server;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        final Waitable<List<String>> waitable = new Waitable<List<String>>() {
            @Override
            protected List<String> evaluate() {
                return server.getCommandMap().tabComplete(server.getConsoleSender(), buffer);
            }
        };
        this.server.getServer().processQueue.add(waitable);
        try {
            final List<String> offers = waitable.get();
            if (offers == null) {
                return cursor;
            }
            candidates.addAll(offers);

            final int lastSpace = buffer.lastIndexOf(' ');
            if (lastSpace == -1) {
                return cursor - buffer.length();
            } else {
                return cursor - (buffer.length() - lastSpace - 1);
            }
        } catch (final ExecutionException e) {
            this.server.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return cursor;
    }
}
