package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameEndEvent;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameListeners implements Listener {

    OneInTheChamberPlugin main;

    public GameListeners(){
        this.main = OneInTheChamberPlugin.getInstance();
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        SendMessage.broadcast("&8[&bOITC&8] &f Game is starting in 20 seconds! Join queue to play!");
        new BukkitRunnable(){

            @Override
            public void run() {
                for(OITCPlayer player: main.getOitcPlayers().values()){
                    // Teleport player if it is in game.
                    player.teleportPlayer();
                }
                main.setGameState(GameState.STARTED);
                endGameAfterTime(30); // Configureren
                SendMessage.broadcast("&8[&bOITC&8] &f Game started");
            }
        }.runTaskLater(main,20L * 20);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        SendMessage.broadcast("&8[&bOITC&8] &f Game ended");
       // SendMessage.broadcast("&8[&bOITC&8] &f Winner: &8" + event.getWinner().getName() + " &fWith a score of &8" + event.getScore());
    }

    private void endGameAfterTime(int time) {
        new BukkitRunnable(){
            @Override
            public void run() {
                for(OITCPlayer player: main.getOitcPlayers().values()){
                    // Remove players from the game
                    player.removeFromGame();
                }
                main.setGameState(GameState.STOPPED);
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent());
            }


        }.runTaskLater(main,20L * time);
    }
}
