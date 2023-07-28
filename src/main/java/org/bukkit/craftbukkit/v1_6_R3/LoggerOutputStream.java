package org.bukkit.craftbukkit.v1_6_R3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerOutputStream extends ByteArrayOutputStream {
    private final String separator = System.getProperty("line.separator");
    private final Logger logger;
    private final Level level;

    public LoggerOutputStream(final Logger logger, final Level level) {
        super();
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            final String record = this.toString();
            super.reset();

            if ((!record.isEmpty()) && (!record.equals(separator))) {
                logger.logp(level, "", "", record);
            }
        }
    }
}
