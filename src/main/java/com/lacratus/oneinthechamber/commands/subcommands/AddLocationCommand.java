package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.objects.Arena;
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
        return "Add new locations to the arena, Type 'Spawn' behind command to set spawnlocation." ;
    }

    @Override
    public String getSyntax() {
        return "/oitc addLocation <Arena> [Spawn]";
    }

    @Override
    public int getMinimumArgs() {
        return 2;
    }

    @Override
    public int getMaximumArgs() {
        return 3;
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
        // Get arena and location
        Arena arena = main.getArenas().get(arenaName);
        Location location = player.getLocation();

        // Set spawnlocation of lobby
        if(args.length == 3){
            if(args[2].equalsIgnoreCase("Spawn")) {
                arena.setSpawnLocation(location);
                SendMessage.sendConfigMessage(player,"Message.AddLocation");
                return true;
            }
            
            SendMessage.sendMessage(player, "&8[&bOITC&8] &f Unknown Command - Use /oitc for help");
            return true;
        }

        // Add a location to the list.
        arena.getLocations().add(location);
        SendMessage.sendConfigMessage(player,"Message.AddLocation");
        return true;
    }
}
