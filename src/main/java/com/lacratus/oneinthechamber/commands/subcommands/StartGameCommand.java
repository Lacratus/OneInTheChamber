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

        if (main.getSpawnLocations().isEmpty()) {
            SendMessage.sendMessage(player, "&8[&bOITC&8] &f No locations initialised");
            return true;
        }
        if (main.getGameState().equals(GameState.STARTED) || main.getGameState().equals(GameState.STARTING)) {
            SendMessage.sendMessage(player, "&;8[&bOITC&8] &f The game is already being played.");
            return true;
        }

        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent());
        main.setGameState(GameState.STARTING);
        SendMessage.sendMessage(player, "&8[&bOITC&8] &f U are starting game");
        return true;
    }
}