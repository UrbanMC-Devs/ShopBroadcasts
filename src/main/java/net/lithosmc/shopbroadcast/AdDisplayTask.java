package net.lithosmc.shopbroadcast;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.ShopAd;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class AdDisplayTask extends BukkitRunnable {
    private final ShopBroadcast plugin;

    private int currAdIndex = 0;

    public AdDisplayTask(ShopBroadcast plugin) {
        this.plugin = plugin;
    }

    // Display the shop ads
    @Override
    public void run() {
        // Check that there are shop ads to display
        if (!plugin.getShopAdData().hasShopAds())
            return;

        var shopAd = getNextShopAd();

        // No shop advertisement to display
        if (shopAd == null)
            return;

        // Create shopad text component
        var shopAdText = shopAd.getText();
        // Parse as component
        var adTxtComp = Component.text(shopAdText);
        // Style according to type
        var adStyle = plugin.getHookManager().getAdStyle(shopAd);
        if (adStyle != null)
            adTxtComp = adTxtComp.style(adStyle);

        // Now build the actual display component
        var displayStr = SBMessages.of("shopad-display");
        var displayComp = MiniMessage.get().parse(displayStr, Template.of("message", adTxtComp));

        // Now display the message to everyone
        Bukkit.getServer().sendMessage(displayComp);
    }

    private ShopAd getNextShopAd() {
        var currTime = System.currentTimeMillis();
        var expireInterval = TimeUnit.MINUTES.toMillis(plugin.getShopConfig().getAdLength());

        ShopAd shopAd;
        do {
            if (!plugin.getShopAdData().hasShopAds())
                return null;

            final var shopAds = plugin.getShopAdData().getShopAds();
            // Reset ad index if greater than size.

            if (currAdIndex > shopAds.size())
                currAdIndex = 0;

            shopAd = shopAds.get(currAdIndex);

            // Check if this shop ad has expired
            if (hasShopAdExpired(shopAd, currTime, expireInterval)) {
                plugin.getShopAdData().removeShopAd(shopAd);
                shopAd = null;
            }

        } while (shopAd == null);

        return shopAd;
    }


    private boolean hasShopAdExpired(ShopAd shopAd, long msCurrTime, long msExpirationInterval) {
        if (msExpirationInterval == 0)
            return false;

        var msTimeCreated = shopAd.getTimeCreated();
        return msTimeCreated + msExpirationInterval < msCurrTime;
    }
}
