package com.lacratus.oneinthechamber;

import com.lacratus.oneinthechamber.commands.CommandManager;
import com.lacratus.oneinthechamber.data.DataHandler;
import com.lacratus.oneinthechamber.data.MongoDataHandler;
import com.lacratus.oneinthechamber.data.MySQLDataHandler;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.listeners.GameListeners;
import com.lacratus.oneinthechamber.listeners.OnJoinQuitListener;
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

    // Databasehandler
    private DataHandler dataHandler;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin Enabled");

        // Initialise main
        setInstance(this);


        // Initialise Attributes
        spawnLocations = new ArrayList<>();
        oitcPlayers = new HashMap<>();

        gameState = GameState.INACTIVE;

        // Databases
        boolean mySqlEnabled = this.getConfig().getBoolean("DB.Mysql.Enabled");
        boolean mongodbEnabled = this.getConfig().getBoolean("DB.Mongodb.Enabled");

        if(mongodbEnabled && mySqlEnabled){
            Bukkit.getLogger().warning("SQL Datbase AND Mongo Database are enabled. MongoDB will be used.");
        }
        // mySQL Enabled
        if(mySqlEnabled){
            dataHandler = new MySQLDataHandler();
            Bukkit.getLogger().info("SQL Database enabled");
        }
        // MongoDB Enabled
        if(mongodbEnabled){
            dataHandler = new MongoDataHandler();
            Bukkit.getLogger().info("MongoDB enabled");

        }
        // Register Commands
        getCommand("Oitc").setExecutor(new CommandManager());

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new OnHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
