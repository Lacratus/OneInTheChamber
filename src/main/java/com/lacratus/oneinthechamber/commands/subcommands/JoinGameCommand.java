package com.lacratus.oneinthechamber.commands.subcommands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.SubCommand;
import com.lacratus.oneinthechamber.enums.GameState;
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
        return "/oitc joingame";
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

        // If game is started, u can't join
        if (main.getGameState().equals(GameState.STARTED)) {
            SendMessage.sendMessage(player, "&8[&bOITC&8] &f The game is already being played.");
            return true;
        }
        // Join queue
        OITCPlayer oitcPlayer = main.getOitcPlayers().get(player.getUniqueId());
        if (!oitcPlayer.isInGame()) {
            oitcPlayer.setInGame(true);
            SendMessage.sendMessage(player, "&8[&bOITC&8] &f Queue Joined");
            return true;
        }
        // If in queue, can't join queue again
        SendMessage.sendMessage(player, "&8[&bOITC&8] &f U are already in the queue");
        return true;
    }
}
