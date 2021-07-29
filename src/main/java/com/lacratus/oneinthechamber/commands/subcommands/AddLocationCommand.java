package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AddLocationCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public AddLocationCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "AddLocation";
    }

    @Override
    public String getPermission() {
        return "oitc.addlocation";
    }

    @Override
    public String getDescription() {
        return "Add new spawnlocation";
    }

    @Override
    public String getSyntax() {
        return "/oitc addLocation";
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

        // Add a location to the list.
        Location location = player.getLocation();
        main.getSpawnLocations().add(location);
        SendMessage.sendMessage(player, "&8[&bOITC&8] &f Locatie toegevoegd");
        return true;
    }
}
