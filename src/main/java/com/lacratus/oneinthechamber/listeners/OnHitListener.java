package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

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



        // If shooter is not a player, do nothing.
        if (!((event.getEntity()).getShooter() instanceof Player)) {
            return;
        }
        
        // Get Shooter of Arrow and the hit person and Arena they are in
        Player shooter = (Player) (event.getEntity()).getShooter();
        OITCPlayer oitcPlayerShooter = main.getOitcPlayers().get(shooter.getUniqueId());
        Arena arena = oitcPlayerShooter.getArena();

        // If arena does not exist, they are not in a game.
        if(arena == null){
            return;
        }

        // If game is not started, do nothing.
        if(!arena.getStatus().equals(GameState.STARTED)){
            return;
        }

        // If hit entity is not a a player, regenerate arrow of shooter
        if (!(event.getHitEntity() instanceof Player)) {
            oitcPlayerShooter.regenerateArrow();
            return;
        }

        Player hitPlayer = (Player) event.getHitEntity();
        OITCPlayer oitcPlayerHitPlayer = main.getOitcPlayers().get(hitPlayer.getUniqueId());

        // If hitplayer isn't part of the same arena, do nothing.
        if(!arena.getPlayers().contains(oitcPlayerHitPlayer)){
            return;
        }



        // If player tries to hit himself, call him a cheater
        if (hitPlayer == (event.getEntity()).getShooter()) {
            hitPlayer.sendTitle("§aTrying to cheat u motherfucker?", "", 10, 25, 10);
            oitcPlayerShooter.regenerateArrow();
            return;
        }


        // Teleport player to random selected location and give death
        oitcPlayerHitPlayer.teleportPlayer();
        oitcPlayerHitPlayer.setDeaths(oitcPlayerHitPlayer.getDeaths() + 1);
        hitPlayer.getScoreboard().getTeam("deaths").setSuffix(oitcPlayerHitPlayer.getDeaths() + "");
        SendMessage.sendMessage(hitPlayer, "Kills: " + oitcPlayerHitPlayer.getKills() + " | Deaths: " + oitcPlayerHitPlayer.getDeaths());

        // Show Screens
        shooter.sendTitle("§aNice shot", "", 10, 25, 10);
        hitPlayer.sendTitle("§bU dead", "", 10, 25, 10);

        // Create Arrow
        ItemStack arrow = new ItemStack(Material.ARROW);

        // Give arrow back and Add stats to killer
        shooter.getInventory().addItem(arrow);
        oitcPlayerShooter.setKills(oitcPlayerShooter.getKills() + 1);
        shooter.getScoreboard().getTeam("kills").setSuffix(oitcPlayerShooter.getKills() + "");
        SendMessage.sendMessage(shooter, "Kills: " + oitcPlayerShooter.getKills() + " | Deaths: " + oitcPlayerShooter.getDeaths());

        // Give items back to death
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
