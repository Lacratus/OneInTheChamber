package com.lacratus.oneinthechamber.utils;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.lacratus.oneinthechamber.objects.OITCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

public class ScoreboardUtil {

    public static void buildScoreboard(OITCPlayer oitcPlayer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("Scoreboard", "dummy");
                obj.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "OneInTheChamber");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score blank = obj.getScore(" ");
                blank.setScore(3);

                Team kills = scoreboard.registerNewTeam("kills");
                kills.addEntry(ChatColor.AQUA.toString());
                kills.setPrefix(ChatColor.YELLOW + "Kills: ");
                kills.setSuffix(oitcPlayer.getKills() + "");
                obj.getScore(ChatColor.AQUA.toString()).setScore(2);

                Team deaths = scoreboard.registerNewTeam("deaths");
                deaths.addEntry(ChatColor.RED.toString());
                deaths.setPrefix(ChatColor.YELLOW + "Deaths: ");
                deaths.setSuffix(oitcPlayer.getDeaths() + "");
                obj.getScore(ChatColor.RED.toString()).setScore(1);

                oitcPlayer.getPlayer().setScoreboard(scoreboard);
            }
        }.runTask(OneInTheChamberPlugin.getInstance());

    }
}
