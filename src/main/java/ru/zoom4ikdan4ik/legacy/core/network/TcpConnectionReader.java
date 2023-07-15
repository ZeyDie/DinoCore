package ru.zoom4ikdan4ik.legacy.core.network;

import net.minecraft.network.TcpConnection;

public final class TcpConnectionReader implements Runnable {
    private final TcpConnection tcpConnection;

    public TcpConnectionReader(final TcpConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    @Override
    public synchronized final void run() {
        TcpConnection.field_74471_a.getAndIncrement();

        try {
            while (this.tcpConnection.isRunning && !this.tcpConnection.isServerTerminating) {
                while (this.tcpConnection.readPacket()) {
                }
            }
        } finally {
            TcpConnection.field_74471_a.getAndDecrement();
        }
    }
}
