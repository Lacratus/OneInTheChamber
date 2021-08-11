package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.objects.Arena;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.entity.Player;

public class JoinGameCommand extends SubCommand {

    private final OneInTheChamberPlugin main;

    public JoinGameCommand() {
        main = OneInTheChamberPlugin.getInstance();
    }

    @Override
    public String getName() {
        return "JoinGame";
    }

    @Override
    public String getPermission() {
        return "oitc.joingame";
    }

    @Override
    public String getDescription() {
        return "Join a game";
    }

    @Override
    public String getSyntax() {
        return "/oitc joingame <Name>";
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

        if (!main.getArenas().containsKey(arenaName)) {
            SendMessage.sendConfigMessage(player, "Message.ArenaNotExist");
            return true;
        }
        Arena arena = main.getArenas().get(arenaName);
        // If game is started/stopped, u can't join
        if (!(arena.getStatus().equals(GameState.WAITING) || arena.getStatus().equals(GameState.STARTING))) {
            SendMessage.sendConfigMessage(player, "Message.GameStarted");
            return true;
        }
        // Join queue
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());
        if (!arena.addPlayerToArena(oitcPlayer)) {
            return true;
        }
        // Set arena of player
        oitcPlayer.setArena(arena);
        SendMessage.sendConfigMessage(player, "Message.JoinGame");
        return true;
    }
}
