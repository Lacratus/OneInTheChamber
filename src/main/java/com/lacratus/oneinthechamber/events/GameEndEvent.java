package com.lacratus.oneinthechamber.events;

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

    public GameEndEvent(Player winner, int score){
        this.winner = winner;
        this.score = score;
    }

    public GameEndEvent(){

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
