package com.lacratus.oneinthechamber.listeners;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    OneInTheChamberPlugin main;

    public SignListener() {
        this.main = OneInTheChamberPlugin.getInstance();
    }

    @EventHandler
    public void onSignChance(SignChangeEvent event) {
        for (String locationName : main.getArenas().keySet()) {
            // Check if sign is correctly filled
            if (event.getLine(0).equals("[" + locationName + "]")) {
                Arena arena = main.getArenas().get(locationName);

                // Change sign to correct layout
                Sign sign = (Sign) event.getBlock().getState();
                arena.changeSign(sign);

                // Add sign to signlocations of arena
                arena.getSignLocations().add(sign);
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        // When a sign is broken, it will be removed from the list of a arena.
        if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) event.getBlock().getState();
            for (Arena arena : main.getArenas().values()) {
                arena.getSignLocations().remove(sign);
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if(event.getAction() ==  Action.LEFT_CLICK_BLOCK || event.getClickedBlock() == null){
            return;
        }
        // Check if sign is clicked
        if (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) event.getClickedBlock().getState();

            // Open inventory of arena
            String name = sign.getLine(3);
            name = ChatColor.stripColor(name);
            Arena arena = main.getArenas().get(name);
            if(arena != null){
                arena.openArenaMenu(event.getPlayer());
            }
        }
    }

}
