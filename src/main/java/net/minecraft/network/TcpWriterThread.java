package net.minecraft.network;

import java.io.IOException;

@Deprecated
public class TcpWriterThread extends Thread {
    final TcpConnection theTcpConnection;

    public TcpWriterThread(final TcpConnection par1TcpConnection, final String par2Str) {
        super(par2Str);
        this.theTcpConnection = par1TcpConnection;
    }

    public void run() {
        TcpConnection.field_74469_b.getAndIncrement();

        try {
            while (TcpConnection.isRunning(this.theTcpConnection)) {
                boolean flag;

                for (flag = false; TcpConnection.sendNetworkPacket(this.theTcpConnection); flag = true) {
                    ;
                }

                try {
                    if (flag && TcpConnection.getOutputStream(this.theTcpConnection) != null) {
                        TcpConnection.getOutputStream(this.theTcpConnection).flush();
                    }
                } catch (final IOException ioexception) {
                    if (!TcpConnection.isTerminating(this.theTcpConnection)) {
                        TcpConnection.sendError(this.theTcpConnection, ioexception);
                    }

                    // ioexception.printStackTrace(); // CraftBukkit - Don't spam console on unexpected disconnect
                }

                try {
                    sleep(2L);
                } catch (final InterruptedException interruptedexception) {
                    ;
                }
            }
        } finally {
            TcpConnection.field_74469_b.getAndDecrement();

            //TODO ZoomCodeStart
            this.stop();
            //TODO ZoomCodeEnd

        }
    }
}
