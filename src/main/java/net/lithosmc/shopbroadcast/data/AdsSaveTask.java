package net.lithosmc.shopbroadcast.data;

import net.lithosmc.shopbroadcast.objects.ShopAd;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AdsSaveTask {

    private final AdDataSource adSource;
    private final AdDataIO io;

    private BukkitTask currentTask;

    public AdsSaveTask(AdDataIO io, AdDataSource source) {
        this.io = io;
        this.adSource = source;
    }

    public void scheduleTask(final JavaPlugin plugin) {
        cancelCurrentTask();
        // 5 minute interval in ticks
        var interval = 20L * 60 * 5;
        currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> save(plugin), interval, interval);
    }

    private void save(JavaPlugin plugin) {
        if (adSource.isDirty()) {
            // Initially reset dirty
            var cloneList = adSource.getShopAds().stream()
                                        .map(ShopAd::clone)
                                        .toList();
            adSource.resetDirty();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> io.save(cloneList));
        }
    }

    public void shutdown(boolean forceSave) {
        cancelCurrentTask();
        if (forceSave) {
            io.save(adSource.getShopAds());
        }
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

}
