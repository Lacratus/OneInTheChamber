package com.lacratus.oneinthechamber.utils;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
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
     * @param player player who gets a message
     * @param path Path to where we can find the message
     */
    public static void sendConfigMessage(Player player, String path){
        String message = ChatColor.translateAlternateColorCodes('&',
                OneInTheChamberPlugin.getInstance().getConfig().getString(path));
        if(message.equals("")){
            player.sendMessage("No message initialised");
        }
        player.sendMessage(message);
    }

    /**
     *
     * @param message Message that has to be broadcasted
     */
    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    /**
     *
     * @param path Path to where we can find the message
     */
    public static void broadcastConfigMessage(String path){
        String message = ChatColor.translateAlternateColorCodes('&',
                OneInTheChamberPlugin.getInstance().getConfig().getString(path));
        if(message.equals("")){
            Bukkit.getServer().broadcastMessage("No message initialised");
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                OneInTheChamberPlugin.getInstance().getConfig().getString(path)));
    }
}
