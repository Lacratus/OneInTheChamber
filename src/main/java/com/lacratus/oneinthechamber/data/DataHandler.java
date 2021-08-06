package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface DataHandler {

    CompletableFuture<OITCPlayer> getData(Player player);

    void saveData(OITCPlayer playerData);

    void addLocation(Location location);



}
