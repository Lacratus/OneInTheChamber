package com.lacratus.oneinthechamber.commands;

import com.lacratus.oneinthechamber.utils.SendMessage;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    // name of the subcommand
    public abstract String getName();

    // Permission u need to use command
    public abstract String getPermission();

    // ex. "Gives all people who are wanted"
    public abstract String getDescription();

    // How to use command ex. /wanted set <player> <time>
    public abstract String getSyntax();

    // Minimum args required
    public abstract int getMinimumArgs();

    // Maximum args allowed
    public abstract int getMaximumArgs();

    // code for the subcommand
    public boolean perform(Player player, String[] args) {
        return !(args.length < getMinimumArgs() || args.length > getMaximumArgs());
    }
}
