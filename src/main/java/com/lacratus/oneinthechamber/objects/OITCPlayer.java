package com.lacratus.oneinthechamber.objects;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;
import java.util.UUID;

@Getter
@Setter
public class OITCPlayer {

    private Player player;
    private UUID uuid;
    private int kills;
    private int deaths;
    private boolean isInGame;
    private BukkitTask regenerateArrowTask;

    public OITCPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.kills = 0;
        this.deaths = 0;
        this.isInGame = false;
    }

    public OITCPlayer(Player player, int kills, int deaths) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.kills = kills;
        this.deaths = deaths;
        this.isInGame = false;
    }

    // Give arrow back after
    public void regenerateArrow() {
        player.setExp(0F);
        final int ticks = 20 * OneInTheChamberPlugin.getInstance().getConfig().getInt("Game.RegenerateArrowSeconds"); // Replaced by config later
        final float division = 1F / ticks;

        BukkitTask regenArrow = new BukkitRunnable() {
            @Override
            public void run() {
                float currentExp = player.getExp();
                try {
                    player.setExp(currentExp + division);
                } catch (Exception ex) {
                    player.setExp(1F);
                }
                currentExp = player.getExp();

                // When expbar turns full
                if (currentExp >= 1) {
                    ItemStack arrow = new ItemStack(Material.ARROW);
                    player.getInventory().addItem(arrow);
                    cancel();
                }
            }
        }.runTaskTimer(OneInTheChamberPlugin.getInstance(), 0L, 1L);

        setRegenerateArrowTask(regenArrow);
    }

    // Teleport player to random location and give Bow & Arrow.
    public void teleportPlayer() {
        if (!isInGame) {
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(OneInTheChamberPlugin.getInstance().getSpawnLocations().size());
        Location location = OneInTheChamberPlugin.getInstance().getSpawnLocations().get(randomIndex);
        player.teleport(location);

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemStack bow = new ItemStack(Material.BOW);
        player.getInventory().clear();
        player.getInventory().addItem(bow);
        player.getInventory().addItem(arrow);
    }

    public void removeFromGame() {
        if (isInGame) {
            isInGame = false;
            // Teleport naar spawn
        }
    }
}
