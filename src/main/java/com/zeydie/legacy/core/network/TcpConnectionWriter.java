package com.zeydie.legacy.core.network;

import net.minecraft.network.TcpConnection;

import java.io.IOException;

public final class TcpConnectionWriter implements Runnable {
    private final TcpConnection tcpConnection;

    public TcpConnectionWriter(final TcpConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    @Override
    public synchronized void run() {
        TcpConnection.field_74469_b.getAndIncrement();

        try {
            while (this.tcpConnection.isRunning) {
                boolean shutdownAfterSending = this.tcpConnection.isServerTerminating;
                boolean flush;

                for (flush = false; this.tcpConnection.sendPacket(); flush = true) {
                }

                try {
                    if (flush && this.tcpConnection.socketOutputStream != null) {
                        this.tcpConnection.socketOutputStream.flush();
                    }
                } catch (IOException e) {
                    if (!this.tcpConnection.isTerminating) {
                        this.tcpConnection.onNetworkError(e);
                    }
                }

                if (shutdownAfterSending) break;
            }
        } finally {
            TcpConnection.field_74469_b.getAndDecrement();
        }
    }
}
