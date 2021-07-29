package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {

    private final OneInTheChamberPlugin main;

    public OnJoinListener() {
        this.main = OneInTheChamberPlugin.getInstance();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        OITCPlayer oitcPlayer = new OITCPlayer(event.getPlayer());
        main.getOitcPlayers().put(oitcPlayer.getUuid(), oitcPlayer);
    }
}
