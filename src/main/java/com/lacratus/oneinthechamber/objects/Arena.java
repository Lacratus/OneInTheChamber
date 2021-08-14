package com.lacratus.oneinthechamber.objects;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.utils.SendMessage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter @Setter
public class Arena {

    private String name;
    private GameState status;
    private List<OITCPlayer> players;
    private List<Location> locations;
    private List<Sign> signLocations;
    private Location spawnLocation;
    private BossBar bossBarWaitTimer;
    private int Duration;

    public Arena(String name) {
        this.name = name;
        this.status = GameState.WAITING;
        this.players = new CopyOnWriteArrayList<>();
        this.locations = new ArrayList<>();
        this.signLocations = new ArrayList<>();
        this.Duration = OneInTheChamberPlugin.getInstance().getConfig().getInt("Game.StandardDurationGame");
    }


    public boolean addPlayerToArena(OITCPlayer oitcPlayer){
        Player player = oitcPlayer.getPlayer();
        if(oitcPlayer.getArena() != null){
            SendMessage.sendConfigMessage(player,"Message.InGame");
            return false;
        }

        // Check if everything is there what is needed.
        if(spawnLocation == null){
            SendMessage.sendConfigMessage(player,"Message.SpawnLocationNotSet");
            return false;
        }
        if(locations.isEmpty()){
            SendMessage.sendConfigMessage(player,"Message.NoLocationAllocated");
            return false;
        }

        // Add to joined players
        players.add(oitcPlayer);

        // Set arena for player
        oitcPlayer.setArena(this);

        // teleport to lobby
        player.teleport(spawnLocation);

        if(bossBarWaitTimer != null){
            bossBarWaitTimer.addPlayer(oitcPlayer.getPlayer());
        }

        // Check if there are enough players to start the game
        if(players.size() >= 2 && getStatus().equals(GameState.WAITING)){
            Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(this));
        }
        updateSigns();
        return true;
    }

    // Update all the signs of this arena
    public void updateSigns(){
        for(Sign sign : signLocations){
            org.bukkit.material.Sign sign1 = (org.bukkit.material.Sign) sign.getData();
            changeSign(sign);
        }
    }

    public void changeSign(Sign sign){
        // Turns first line into blue
        sign.setLine(0, ChatColor.AQUA + "[OITC]");

        // Check status and give correct colour
        switch(this.getStatus()){
            case STARTED:
                sign.setLine(1, ChatColor.RED + "Started");
                break;
            case WAITING:
                sign.setLine(1, ChatColor.DARK_GREEN + "Waiting");
                break;
            case STARTING:
                sign.setLine(1, ChatColor.GOLD + "Starting");
                break;
            default:
                sign.setLine(1, ChatColor.RED + "Stopped");
        }
        // Give current players in Arena
        sign.setLine(2, ChatColor.GREEN + "Players: " + this.getPlayers().size());
        // Set name of Arena
        sign.setLine(3, ChatColor.BLUE + this.getName());
        sign.update();
    }

    // Open inventory of the Arena with correct information
    public void openArenaMenu(Player player){
        Inventory arenaInventory = Bukkit.createInventory(null, 27, this.getName());

        // Create players itemStack
        ItemStack players = new ItemStack(Material.FEATHER, this.getPlayers().size() > 0 ? this.getPlayers().size() : 1, (short) 1);
        ItemMeta playersMeta = players.getItemMeta();
        playersMeta.setDisplayName("Amount of players: " + this.getPlayers().size());
        players.setItemMeta(playersMeta);

        ItemStack status;
        ItemMeta statusMeta;
        ItemStack join;
        ItemMeta joinMeta;
        // Create Status and Join itemstack
        switch (this.status){
            case WAITING:
                status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                statusMeta = status.getItemMeta();
                statusMeta.setDisplayName(ChatColor.GREEN + "Waiting");
                status.setItemMeta(statusMeta);
                join = new ItemStack(Material.ARROW, 1);
                joinMeta = join.getItemMeta();
                joinMeta.setDisplayName(ChatColor.GREEN + "Join Game");
                join.setItemMeta(joinMeta);
                break;
            case STARTING:
                status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
                statusMeta = status.getItemMeta();
                statusMeta.setDisplayName(ChatColor.GOLD + "Starting");
                status.setItemMeta(statusMeta);
                join = new ItemStack(Material.ARROW, 1);
                joinMeta = join.getItemMeta();
                joinMeta.setDisplayName(ChatColor.GREEN + "Join Game");
                join.setItemMeta(joinMeta);
                break;
            default:
                status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
                statusMeta = status.getItemMeta();
                statusMeta.setDisplayName(ChatColor.RED + "Started");
                status.setItemMeta(statusMeta);
                join = new ItemStack(Material.BARRIER, 1);
                joinMeta = join.getItemMeta();
                joinMeta.setDisplayName(ChatColor.RED + "Game not Joinable");
                join.setItemMeta(joinMeta);
        }
        arenaInventory.setItem(11, players);
        arenaInventory.setItem(13, status);
        arenaInventory.setItem(15, join);

        // Open inventory
        player.openInventory(arenaInventory);


    }


}
