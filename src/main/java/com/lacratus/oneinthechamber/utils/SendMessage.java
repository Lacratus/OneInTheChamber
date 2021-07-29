package com.lacratus.oneinthechamber.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendMessage {

    public static void sendMessage(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',message));
    }
}
