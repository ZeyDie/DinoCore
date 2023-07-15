package me.Kepa2012.fastbukkit.model;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface IProxiedEvent {
    void callEvent(Listener paramListener, Event paramEvent);
}
