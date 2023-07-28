package net.minecraft.network;

@Deprecated
class TcpMasterThread extends Thread {
    final TcpConnection theTcpConnection;

    TcpMasterThread(final TcpConnection par1TcpConnection) {
        this.theTcpConnection = par1TcpConnection;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        try {
            Thread.sleep(5000L);

            if (TcpConnection.getReadThread(this.theTcpConnection).isAlive()) {
                try {
                    TcpConnection.getReadThread(this.theTcpConnection).stop();
                } catch (final Throwable throwable) {
                    ;
                }
            }

            if (TcpConnection.getWriteThread(this.theTcpConnection).isAlive()) {
                try {
                    TcpConnection.getWriteThread(this.theTcpConnection).stop();
                } catch (final Throwable throwable1) {
                    ;
                }
            }
        } catch (final InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();

            //TODO ZoomCodeStart
            this.stop();
            //TODO ZoomCodeEnd

        }
    }
}
