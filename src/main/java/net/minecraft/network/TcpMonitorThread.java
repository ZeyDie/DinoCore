package net.minecraft.network;

@Deprecated
class TcpMonitorThread extends Thread
{
    final TcpConnection theTcpConnection;

    TcpMonitorThread(final TcpConnection par1TcpConnection)
    {
        this.theTcpConnection = par1TcpConnection;
    }

    public void run()
    {
        try
        {
            Thread.sleep(2000L);

            if (TcpConnection.isRunning(this.theTcpConnection))
            {
                TcpConnection.getWriteThread(this.theTcpConnection).interrupt();
                this.theTcpConnection.networkShutdown("disconnect.closed", new Object[0]);
            }
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
