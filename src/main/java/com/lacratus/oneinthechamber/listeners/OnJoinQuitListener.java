package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class OnJoinQuitListener implements Listener {

    private final OneInTheChamberPlugin main;

    public OnJoinQuitListener() {
        this.main = OneInTheChamberPlugin.getInstance();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        main.getDataHandler().getData(player).whenComplete(((oitcPlayer, throwable) -> {
            if(throwable != null){
                player.kickPlayer("Server is in onderhoud");
                throwable.printStackTrace();
                return;
            }
            main.getOitcPlayers().put(player.getUniqueId(), oitcPlayer);
        }));
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){
        Player player = event.getPlayer();
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());
        main.getDataHandler().saveData(oitcPlayer);

        // Remove player from Hashmap
        main.getOitcPlayers().remove(player.getUniqueId());
    }
}
