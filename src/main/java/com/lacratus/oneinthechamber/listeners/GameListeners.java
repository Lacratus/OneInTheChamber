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

        // Create Bossbar
        BossBar bossBarQueueTimer = BossbarUtil.BuildBossbarTimer("Starting", BarColor.YELLOW,20);
        arena.setBossBarWaitTimer(bossBarQueueTimer);
        for(OITCPlayer oitcPlayer: event.getArena().getPlayers()){
            bossBarQueueTimer.addPlayer(oitcPlayer.getPlayer());
        }
        // Teleport all joined players to a random location
        new BukkitRunnable() {
            int count = 20;
            @Override
            public void run() {
                // If arena has less then 2 players, don't start
                if (arena.getPlayers().size() < 2) {
                    arena.setStatus(GameState.WAITING);
                    arena.updateSigns();
                    // Remove bossbar
                    bossBarQueueTimer.removeAll();
                    arena.setBossBarWaitTimer(null);
                    // Stop runnable
                    cancel();
                }
                // If countdown hits 0, start game
                if ((count--) == 0) {
                    // Remove queuetimer
                    bossBarQueueTimer.removeAll();

                    // Create new timer
                    BossBar bossBarGameTimer = BossbarUtil.BuildBossbarTimer("Game",BarColor.RED,arena.getDuration());

                    // Teleport everyone in to the game and add to Timer
                    for (OITCPlayer oitcPlayer : arena.getPlayers()) {
                        oitcPlayer.teleportPlayer();
                        bossBarGameTimer.addPlayer(oitcPlayer.getPlayer());
                    }

                    // Start game
                    arena.setStatus(GameState.STARTED);
                    endGameAfterTime(arena);
                    SendMessage.broadcast("&8[&bOITC&8] &f Game started");
                    // Update signs
                    arena.updateSigns();

                    // Stop runnable
                    cancel();
                }
            }
        }.runTaskTimer(main, 0L, 20L);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Arena arena = event.getArena();
        SendMessage.broadcast("&8[&bOITC&8] &f Game ended");
        new BukkitRunnable() {
            int count = 10;

            @Override
            public void run() {
                count--;
                for (OITCPlayer player : arena.getPlayers()) {
                    // Remove players from the game
                    player.removeFromGame();
                    player.getPlayer().getInventory().clear();
                }
                // Create fireworks
                for (Location location : arena.getLocations()) {
                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).flicker(true).build());

                    firework.setFireworkMeta(fireworkMeta);
                }
                if (count <= 0) {
                    // Teleport everyone to lobby
                    for (OITCPlayer player : arena.getPlayers()) {
                        // Location of lobby
                    }
                }
                arena.setStatus(GameState.WAITING);
                arena.updateSigns();
                cancel();

            }
        }.runTaskTimer(main, 0L, 2 * 20L);


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
        if (event.getCurrentItem().getType() != Material.ARROW) {
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
