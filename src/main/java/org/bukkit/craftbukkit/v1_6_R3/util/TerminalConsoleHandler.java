package org.bukkit.craftbukkit.v1_6_R3.util;

import jline.console.ConsoleReader;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerminalConsoleHandler extends ConsoleHandler {
    private final ConsoleReader reader;

    public TerminalConsoleHandler(final ConsoleReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public synchronized void flush() {
        try {
            if (MinecraftServer.useJline) { // Cauldron
                reader.print(ConsoleReader.RESET_LINE + "");
                reader.flush();
                super.flush();
                try {
                    reader.drawLine();
                } catch (final Throwable ex) {
                    reader.getCursorBuffer().clear();
                }
                reader.flush();
            } else {
                super.flush();
            }
        } catch (final IOException ex) {
            Logger.getLogger(TerminalConsoleHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
