package net.lithosmc.shopbroadcast;

import net.lithosmc.shopbroadcast.command.ShopBroadcastCommand;
import net.lithosmc.shopbroadcast.data.AdDataIO;
import net.lithosmc.shopbroadcast.data.AdDataSource;
import net.lithosmc.shopbroadcast.data.AdsSaveTask;
import net.lithosmc.shopbroadcast.hooks.HookManager;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShopBroadcast extends JavaPlugin {

    private HookManager hookManager;
    private AdDataSource shopAdData;
    private ShopBroadcastConfig config;

    private AdsSaveTask saveTask;
    private AdDisplayTask displayTask;

    @Override
    public void onEnable() {
        if (!isPaper()) {
            getLogger().severe("This plugin requires the PaperMC server software to run! Disabling...");
            setEnabled(false);
            return;
        }

        // Load hooks
        hookManager = new HookManager();

        // Load and pass config to hooks
        configReload();

        shopAdData = new AdDataSource();
        var dataIO = new AdDataIO(this);
        shopAdData.setShopAds(dataIO.load());

        loadMessages();

        // Register commands
        var sbCmd = new ShopBroadcastCommand(this);
        var sbBukkitCmd = getCommand("shopad");
        sbBukkitCmd.setExecutor(sbCmd);
        sbBukkitCmd.setTabCompleter(sbCmd);

        // Schedule display task
        scheduleDisplayTask();

        // Schedule save task
        saveTask = new AdsSaveTask(dataIO, shopAdData);
        saveTask.scheduleTask(this);

    }

    private void loadMessages() {
        // Set up messages
        File messagesFile = new File(getDataFolder(), "messages.properties");
        SBMessages.load(messagesFile, getLogger());
    }

    @Override
    public void onDisable() {
        // Shutdown save task
        if (saveTask != null) {
            saveTask.shutdown(true);
        }
    }

    private void scheduleDisplayTask() {
        if (displayTask != null) {
            displayTask.cancel();
            displayTask = null;
        }

        var displayInterval = config.getMessageInterval() * 20L * 60;
        displayTask = new AdDisplayTask(this);
        displayTask.runTaskTimer(this, displayInterval, displayInterval);
    }

    private void configReload() {
        // Reload config
        config = ShopBroadcastConfig.loadConfig(getDataFolder(), getLogger());
        if (hookManager != null)
            hookManager.hooksReadConfig(config);
    }

    public void reload() {
        // Reload config
        configReload();
        // Reload messages
        SBMessages.getInstance().reload();
        scheduleDisplayTask();
    }

    private boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public AdDataSource getShopAdData() {
        return shopAdData;
    }

    public ShopBroadcastConfig getShopConfig() {
        return config;
    }
}
