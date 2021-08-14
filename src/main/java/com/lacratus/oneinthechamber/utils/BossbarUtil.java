package com.lacratus.oneinthechamber.utils;

import com.lacratus.oneinthechamber.OneInTheChamberPlugin;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossbarUtil {

    public static BossBar BuildBossbarTimer(String name, BarColor barColor, double time) {
        BossBar bossBar = Bukkit.createBossBar(name, barColor, BarStyle.SOLID);
        new BukkitRunnable() {
            double seconds = time;

            @Override
            public void run() {
                bossBar.setTitle(name + ": " + (int) seconds);
                if ((seconds--) == 0) {
                    bossBar.removeAll();
                    cancel();
                } else {
                    bossBar.setProgress(seconds / time);
                }
            }
        }.runTaskTimer(OneInTheChamberPlugin.getInstance(), 0, 20);
        bossBar.setVisible(true);
        return bossBar;
    }
}
