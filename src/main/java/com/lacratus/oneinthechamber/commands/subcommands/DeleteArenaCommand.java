package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class DeleteArenaCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public DeleteArenaCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "DeleteArena";
    }

    @Override
    public String getPermission() {
        return "oitc.deletearena";
    }

    @Override
    public String getDescription() {
        return "Delete an Arena";
    }

    @Override
    public String getSyntax() {
        return "/oitc deleteArena <Name>";
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

        // Check if arena exist
        if (!main.getArenas().containsKey(args[1])) {
            SendMessage.sendConfigMessage(player, "Message.ArenaNotExist");
            return true;
        }

        // Get arena and remove from map
        Arena arena = main.getArenas().get(args[1]);
        main.getArenas().remove(args[1],arena);

        // Remove signs from the arena
        for(Sign sign : arena.getSignLocations()){
            Location location = sign.getLocation();
            location.getBlock().setType(Material.AIR);
        }
        // Remove players from the arena
        for(OITCPlayer oitcPlayer: arena.getPlayers()){
            oitcPlayer.setArena(null);
        }

        SendMessage.sendConfigMessage(player, "Message.ArenaDeleted");
        return true;
    }
}
