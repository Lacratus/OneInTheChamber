package com.lacratus.oneinthechamber.data;

import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DataHandler {

    CompletableFuture<OITCPlayer> getData(Player player);

    void saveData(OITCPlayer playerData);

    void saveArenas(Collection<Arena> arenas);

    CompletableFuture<Map<String, Arena>> getArenas();



}
