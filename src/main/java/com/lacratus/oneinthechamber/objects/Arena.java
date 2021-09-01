package com.lacratus.oneinthechamber.objects;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.enums.GameState;
import com.lacratus.oneinthechamber.events.GameEndEvent;
import com.lacratus.oneinthechamber.events.GameStartEvent;
import com.lacratus.oneinthechamber.utils.BossbarUtil;
import com.lacratus.oneinthechamber.utils.SendMessage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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

    public void startEndGame() {
        Arena arena = this;
        new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                count--;
                for (OITCPlayer player :arena.getPlayers()) {
                    // Remove players from the game
                    player.removeFromGame();
                    player.getPlayer().getInventory().clear();
                }
                // Create fireworks
                for (Location location : arena.getLocations()) {
                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).flicker(true).build());

                    firework.setFireworkMeta(fireworkMeta);
                }
                if (count <= 0) {
                    // Teleport everyone to lobby
                    for (OITCPlayer player : arena.getPlayers()) {
                        player.getPlayer().teleport(OneInTheChamberPlugin.getInstance().getSpawnLocation());
                    }
                }
                arena.setStatus(GameState.WAITING);
                arena.updateSigns();
                cancel();

            }
        }.runTaskTimer(OneInTheChamberPlugin.getInstance(), 0L, 2 * 20L);
    }

    public void tryStartGame(){
        Arena arena = this;
        // Create Bossbar
        BossBar bossBarQueueTimer = BossbarUtil.BuildBossbarTimer("Starting", BarColor.YELLOW, 20);
        arena.setBossBarWaitTimer(bossBarQueueTimer);
        for (OITCPlayer oitcPlayer : arena.getPlayers()) {
            bossBarQueueTimer.addPlayer(oitcPlayer.getPlayer());
        }
        // Teleport all joined players to a random location and create correct bossbars
        new BukkitRunnable() {
            int count = 20;

            @Override
            public void run() {
                // If arena has less then 2 players, don't start
                if (arena.getPlayers().size() < 2) {
                    arena.setStatus(GameState.WAITING);
                    arena.updateSigns();
                    // Remove bossbar
                    bossBarQueueTimer.removeAll();
                    arena.setBossBarWaitTimer(null);
                    // Stop runnable
                    cancel();
                }
                // If countdown hits 0, start game
                if ((count--) == 0) {
                    // Remove queuetimer
                    bossBarQueueTimer.removeAll();

                    // Create new timer
                    BossBar bossBarGameTimer = BossbarUtil.BuildBossbarTimer("Game", BarColor.RED, arena.getDuration());

                    // Teleport everyone in to the game and add to Timer
                    for (OITCPlayer oitcPlayer : arena.getPlayers()) {
                        oitcPlayer.teleportPlayer();
                        bossBarGameTimer.addPlayer(oitcPlayer.getPlayer());
                    }
                    // Start game
                    arena.setStatus(GameState.STARTED);

                    // Remove all players from game when game ens..
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(arena));
                        }
                    }.runTaskLater(OneInTheChamberPlugin.getInstance(), 20L * arena.getDuration());
                    SendMessage.broadcast("&8[&bOITC&8] &f Game started");
                    // Update signs
                    arena.updateSigns();

                    // Stop runnable
                    cancel();
                }
            }
        }.runTaskTimer(OneInTheChamberPlugin.getInstance(), 0L, 20L);
    }


}
