package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CreateArenaCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public CreateArenaCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "CreateArena";
    }

    @Override
    public String getPermission() {
        return "oitc.createarena";
    }

    @Override
    public String getDescription() {
        return "Create new Arena";
    }

    @Override
    public String getSyntax() {
        return "/oitc createArena <Name>";
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

        if (main.getArenas().containsKey(args[1])) {
            SendMessage.sendConfigMessage(player, "Message.ArenaAlreadyExists");
            return true;
        }

        // Add Arena to the map
        Arena arena = new Arena(args[1]);
        main.getArenas().put(args[1],arena);
        SendMessage.sendConfigMessage(player, "Message.ArenaCreated");
        return true;
    }
}
