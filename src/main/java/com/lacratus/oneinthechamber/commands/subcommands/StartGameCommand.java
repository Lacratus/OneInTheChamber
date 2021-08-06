package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class StartGameCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public StartGameCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "StartGame";
    }

    @Override
    public String getPermission() {
        return "oitc.startgame";
    }

    @Override
    public String getDescription() {
        return "Start a new game";
    }

    @Override
    public String getSyntax() {
        return "/oitc startgame";
    }

    @Override
    public int getMinimumArgs() {
        return 1;
    }

    @Override
    public int getMaximumArgs() {
        return 1;
    }

    @Override
    public boolean perform(Player player, String[] args) {
        if (!super.perform(player, args)) return false;

        // If there are no spawnlocations, u can't start
        if (main.getSpawnLocations().isEmpty()) {
            SendMessage.sendConfigMessage(player,"Message.NoLocationAllocated");
            return true;
        }

        // If a game is started, u can't start again
        if (main.getGameState().equals(GameState.STARTED) || main.getGameState().equals(GameState.STARTING)) {
            SendMessage.sendConfigMessage(player,"Message.GameStarted");
            return true;
        }

        // Start game
        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent());
        main.setGameState(GameState.STARTING);
        SendMessage.sendConfigMessage(player,"Message.StartGame");
        return true;
    }
}
