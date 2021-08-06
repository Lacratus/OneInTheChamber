package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class OnHitListener implements Listener {

    private final OneInTheChamberPlugin main;

    public OnHitListener() {
        this.main = OneInTheChamberPlugin.getInstance();
    }
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {

        // If Damager is not an arrow, do nothing.
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        // Remove arrow
        event.getEntity().remove();

        // If game is not started, do nothing.
        if(!main.getGameState().equals(GameState.STARTED)){
            return;
        }

        // If shooter is not a player, do nothing.
        if (!((event.getEntity()).getShooter() instanceof Player)) {
            return;
        }
        
        // Get Shooter of Arrow and the hit person
        Player shooter = (Player) (event.getEntity()).getShooter();
        OITCPlayer oitcPlayerShooter = main.getOitcPlayers().get(shooter.getUniqueId());

        // If shooter is not part of the game, do nothing.
        if(!oitcPlayerShooter.isInGame()){
            return;
        }

        // If hit entity is not a a player, regenerate arrow of shooter
        if (!(event.getHitEntity() instanceof Player)) {
            oitcPlayerShooter.regenerateArrow();
            return;
        }

        Player hitPlayer = (Player) event.getHitEntity();
        OITCPlayer oitcPlayerHitPlayer = main.getOitcPlayers().get(hitPlayer.getUniqueId());

        // If hitplayer is not part of the game, do nothing.
        if(!oitcPlayerHitPlayer.isInGame()){
            return;
        }

        // If player tries to hit himself, call him a cheater
        if (hitPlayer == (event.getEntity()).getShooter()) {
            hitPlayer.sendTitle("§aTrying to cheat u motherfucker?", "", 10, 25, 10);
            oitcPlayerShooter.regenerateArrow();
            return;
        }

        // If no locations appointed, do nothing
        if (main.getSpawnLocations().isEmpty()) {
            Bukkit.getLogger().warning("No locations are defined");
            return;
        }
        // Teleport player to random selected location and give death
        oitcPlayerHitPlayer.teleportPlayer();
        oitcPlayerHitPlayer.setDeaths(oitcPlayerHitPlayer.getDeaths() + 1);
        SendMessage.sendMessage(hitPlayer, "Kills: " + oitcPlayerHitPlayer.getKills() + " | Deaths: " + oitcPlayerHitPlayer.getDeaths());

        // Show Screens
        shooter.sendTitle("§aNice shot", "", 10, 25, 10);
        hitPlayer.sendTitle("§bU dead", "", 10, 25, 10);

        // Create Arrow
        ItemStack arrow = new ItemStack(Material.ARROW);

        // Give arrow back and Add stats to killer
        shooter.getInventory().addItem(arrow);
        oitcPlayerShooter.setKills(oitcPlayerShooter.getKills() + 1);
        SendMessage.sendMessage(shooter, "Kills: " + oitcPlayerShooter.getKills() + " | Deaths: " + oitcPlayerShooter.getDeaths());

        // Give items back to death and Add stats to death
        if (oitcPlayerHitPlayer.getRegenerateArrowTask() != null) {
            oitcPlayerHitPlayer.getRegenerateArrowTask().cancel();
            hitPlayer.setExp(0F);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            event.setCancelled(true);
        }
    }
}
