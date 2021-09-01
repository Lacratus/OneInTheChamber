package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.objects.Arena;
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
        return 2;
    }

    @Override
    public int getMaximumArgs() {
        return 2;
    }

    @Override
    public boolean perform(Player player, String[] args) {
        if (!super.perform(player, args)) return false;

        // Check if arena exists
        String arenaName = args[1];

        if(!main.getArenas().containsKey(arenaName)){
            SendMessage.sendConfigMessage(player,"Message.ArenaNotExist");
            return true;
        }
        Arena arena = main.getArenas().get(arenaName);

        // If there are no spawnlocations, u can't start
        if (arena.getLocations().isEmpty()) {
            SendMessage.sendConfigMessage(player,"Message.NoLocationAllocated");
            return true;
        }
        // If lobbyspawn is not set, u can't start
        if(main.getSpawnLocation() == null){
            SendMessage.sendConfigMessage(player,"Message.NoLobbySpawnSet");
        }
        // If game is started/inactive/stopped, u can't start again
        if (!(arena.getStatus().equals(GameState.WAITING))) {
            SendMessage.sendConfigMessage(player,"Message.GameStarted");
            return true;
        }

        // Start game
        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(arena));
        SendMessage.sendConfigMessage(player,"Message.StartGame");
        arena.updateSigns();
        return true;
    }
}
