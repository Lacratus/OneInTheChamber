package com.lacratus.oneinthechamber.events;

import com.lacratus.oneinthechamber.objects.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @Setter
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player winner;
    private int score;
    private Arena arena;

    public GameEndEvent(Player winner, int score,Arena arena){
        this.winner = winner;
        this.score = score;
        this.arena = arena;
    }

    public GameEndEvent(Arena arena){
        this.arena = arena;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
