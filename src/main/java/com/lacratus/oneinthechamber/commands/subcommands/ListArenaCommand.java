package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.entity.Player;

public class ListArenaCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public ListArenaCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "ListArena";
    }

    @Override
    public String getPermission() {
        return "oitc.listarena";
    }

    @Override
    public String getDescription() {
        return "List all arena's";
    }

    @Override
    public String getSyntax() {
        return "/oitc Listarena ";
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
        StringBuilder builder = new StringBuilder();

        // List all arenas
        for(String arenaName: main.getArenas().keySet()){
            builder.append(arenaName).append(", ");
        }
        SendMessage.sendMessage(player, builder.toString());
        return true;
    }
}
