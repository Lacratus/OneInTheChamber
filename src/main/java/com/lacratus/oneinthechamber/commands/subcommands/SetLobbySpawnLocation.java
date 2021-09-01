package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawnLocation extends SubCommand {

    private final OneInTheChamberPlugin main;

    public SetLobbySpawnLocation() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "SetLobbySpawn";
    }

    @Override
    public String getPermission() {
        return "oitc.setlobbyspawn";
    }

    @Override
    public String getDescription() {
        return "Set lobbyspawn of the server.";
    }

    @Override
    public String getSyntax() {
        return "/oitc setlobbyspawn";
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

        // Get location of player
        Location location = player.getLocation();

        // Set spawnlocation of lobby
        main.setSpawnLocation(location);
        SendMessage.sendConfigMessage(player, "Message.LobbySpawnCreated");
        return true;
    }
}
