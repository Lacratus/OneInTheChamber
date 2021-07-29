package com.lacratus.oneinthechamber;

import com.lacratus.oneinthechamber.commands.CommandManager;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.listeners.GameListeners;
import com.lacratus.oneinthechamber.listeners.OnDisconnectListener;
import com.lacratus.oneinthechamber.listeners.OnJoinListener;
import com.lacratus.oneinthechamber.listeners.OnHitListener;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter @Setter
public final class OneInTheChamberPlugin extends JavaPlugin {

    private static @Getter @Setter OneInTheChamberPlugin instance;

    private List<Location> spawnLocations;
    private Map<UUID, OITCPlayer> oitcPlayers;
    private GameState gameState;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin Enabled");

        // Initialise main
        setInstance(this);

        // Initialise Attributes
        spawnLocations = new ArrayList<>();
        oitcPlayers = new HashMap<>();

        gameState = GameState.INACTIVE;

        // Register Commands
        getCommand("Oitc").setExecutor(new CommandManager());

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new OnHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnDisconnectListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
