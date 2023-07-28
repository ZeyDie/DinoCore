package net.minecraft.network;

@Deprecated
public class TcpReaderThread extends Thread {
    final TcpConnection theTcpConnection;

    public TcpReaderThread(final TcpConnection par1TcpConnection, final String par2Str) {
        super(par2Str);
        this.theTcpConnection = par1TcpConnection;
    }

    public void run() {
        TcpConnection.field_74471_a.getAndIncrement();

        try {
            while (TcpConnection.isRunning(this.theTcpConnection) && !TcpConnection.isServerTerminating(this.theTcpConnection)) {
                while (true) {
                    if (!TcpConnection.readNetworkPacket(this.theTcpConnection)) {
                        try {
                            sleep(2L);
                        } catch (final InterruptedException interruptedexception) {
                            ;
                        }
                    }
                }
            }
        } finally {
            TcpConnection.field_74471_a.getAndDecrement();

            //TODO ZoomCodeStart
            this.stop();
            //TODO ZoomCodeEnd

        }
    }
}
