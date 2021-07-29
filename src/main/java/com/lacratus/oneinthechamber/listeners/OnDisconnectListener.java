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
        Player player = event.getPlayer();
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());

        main.getOitcPlayers().remove(player.getUniqueId());
    }
}
