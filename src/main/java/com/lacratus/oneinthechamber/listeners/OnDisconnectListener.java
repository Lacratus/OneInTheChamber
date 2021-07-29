package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnDisconnectListener implements Listener {

    private final OneInTheChamberPlugin main;

    public OnDisconnectListener() {
        this.main = OneInTheChamberPlugin.getInstance();
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){
        // Remove player from Hashmap
        Player player = event.getPlayer();
        main.getOitcPlayers().remove(player.getUniqueId());
    }
}
