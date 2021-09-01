package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameEndEvent;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.BossbarUtil;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;
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
        SendMessage.broadcast("&8[&bOITC&8] &f Game " + arena.getName() + " is starting in 20 seconds! Click sign to play!");

        // Try to start game
        arena.tryStartGame();
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Arena arena = event.getArena();
        SendMessage.broadcast("&8[&bOITC&8] &f Game ended");

        // Start endgame proces
        arena.startEndGame();

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
        if (event.getCurrentItem().getType() != Material.ARROW) {
            return;
        }
        // Add player to arena
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());
        Arena arena = main.getArenas().get(title);

        arena.addPlayerToArena(oitcPlayer);
    }
}

