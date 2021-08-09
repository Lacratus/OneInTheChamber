package com.lacratus.oneinthechamber.events;

import com.lacratus.oneinthechamber.objects.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Arena arena;


    public GameStartEvent(Arena arena){
        this.arena = arena;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arena getArena() { return arena; }
}
