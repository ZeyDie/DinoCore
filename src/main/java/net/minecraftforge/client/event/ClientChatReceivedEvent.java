package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class ClientChatReceivedEvent extends Event
{
    public String message;
    public ClientChatReceivedEvent(final String message)
    {
        this.message = message;
    }
}
