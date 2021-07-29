package com.lacratus.oneinthechamber.commands;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.commands.subcommands.AddLocationCommand;
import com.lacratus.oneinthechamber.commands.subcommands.JoinGameCommand;
import com.lacratus.oneinthechamber.commands.subcommands.StartGameCommand;
import com.lacratus.oneinthechamber.utils.SendMessage;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandManager implements CommandExecutor {

    @Getter
    private final Collection<SubCommand> subcommands = new ArrayList<>();

    public CommandManager() {
        subcommands.add(new AddLocationCommand());
        subcommands.add(new StartGameCommand());
        subcommands.add(new JoinGameCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0 || getSubCommand(args[0]) == null) {
            player.sendMessage("--------------------------------");
            for (SubCommand sub : subcommands) {
                SendMessage.sendMessage(player, sub.getSyntax() + " - " + sub.getDescription());
            }
            player.sendMessage("--------------------------------");
            return true;
        }
        SubCommand subCommand = getSubCommand(args[0]);
        if (!player.hasPermission(subCommand.getPermission())) {
            SendMessage.sendMessage(player, OneInTheChamberPlugin.getInstance().getConfig().getString("Message.NoPermission"));
            return true;
        }

        if(!subCommand.perform(player, args)){
            SendMessage.sendMessage(player, "&8[&bOITC&8] &f Unknown Command - Use /oitc for help");
        }
        return true;
    }

    public SubCommand getSubCommand(String name) {
        for (SubCommand sub : subcommands) {
            if (name.equalsIgnoreCase(sub.getName()))
                return sub;
        }
        return null;
    }
}

