package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameEndEvent;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListeners implements Listener {

    OneInTheChamberPlugin main;

    public GameListeners() {
        this.main = OneInTheChamberPlugin.getInstance();
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        // Get arena and set status
        Arena arena = event.getArena();
        arena.setStatus(GameState.STARTING);

        // Send Broadcast
        SendMessage.broadcast("&8[&bOITC&8] &f Game is starting in 20 seconds! Click sign to play!");

        // Teleport all joined players to a random location
        new BukkitRunnable() {

            @Override
            public void run() {
                for (OITCPlayer player : arena.getPlayers()) {
                    // Teleport player if it is in game.
                    player.teleportPlayer();
                }
                arena.setStatus(GameState.STARTED);
                endGameAfterTime(arena);
                SendMessage.broadcast("&8[&bOITC&8] &f Game started");
                arena.updateSigns();
            }
        }.runTaskLater(main, 20L * 20);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Arena arena = event.getArena();
        SendMessage.broadcast("&8[&bOITC&8] &f Game ended");
        for (OITCPlayer player : arena.getPlayers()) {
            // Remove players from the game
            player.removeFromGame();
        }
        arena.setStatus(GameState.WAITING);
        arena.updateSigns();
        // SendMessage.broadcast("&8[&bOITC&8] &f Winner: &8" + event.getWinner().getName() + " &fWith a score of &8" + event.getScore());
    }

    // Player drops no items
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }


    // No hunger
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void OnInventoryInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("oitc.useinventory")) {
            event.setCancelled(true);
        }

        // After clicking on a arena sign
        String title = event.getInventory().getTitle();
        // Does title of inventory not belong to an arena, do nothing
        if (!main.getArenas().containsKey(title)) {
            return;
        }
        event.setCancelled(true);

        // When player clicks on "Join", they join the game
        if(event.getCurrentItem().getType() != Material.ARROW){
            return;
        }
        // Add player to arena
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());
        Arena arena = main.getArenas().get(title);

        arena.addPlayerToArena(oitcPlayer);
    }

    private void endGameAfterTime(Arena arena) {

        // Remove all players from game.
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(arena));
            }
        }.runTaskLater(main, 20L * arena.getDuration());
    }
}
