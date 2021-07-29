package com.lacratus.oneinthechamber.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendMessage {

    /**
     *
     * @param player Player who gets a message
     * @param message The message the player will receive
     */
    public static void sendMessage(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    /**
     *
     * @param message Message that has to be broadcasted
     */
    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',message));
    }
}
