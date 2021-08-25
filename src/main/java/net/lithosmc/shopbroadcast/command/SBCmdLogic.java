package net.lithosmc.shopbroadcast.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.lithosmc.shopbroadcast.ShopBroadcast;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.ShopAd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class SBCmdLogic {

    private final ShopBroadcast plugin;

    public SBCmdLogic(ShopBroadcast plugin) {
        this.plugin = plugin;
    }

    // Utility
    private boolean isAdmin(CommandSender sender, UUID target) {
        if (!(sender instanceof Player player))
            return true;

        return !player.getUniqueId().equals(target);
    }

    public void buy(Player player, String text) {

        // Check if player can buy shopad
        int adsPerPlayer = plugin.getShopConfig().getAdsPerPlayer();
        if (adsPerPlayer >= 0) {
            var existingShopAds = plugin.getShopAdData().getShopAdsBelongingTo(player.getUniqueId());
            if (existingShopAds.size() >= adsPerPlayer) {
                SBMessages.send(player, "ad-limit-reached");
                return;
            }
        }

        // Check buy requirements
        if (!plugin.getHookManager().canBuyAd(player)) {
            // Hooks are responsible for sending their own error messages
            return;
        }

        plugin.getHookManager().buyAd(player);

        ShopAd newAd = new ShopAd(player.getUniqueId(), text);
        plugin.getShopAdData().addShopAd(newAd);

        SBMessages.send(player, "ad-bought");
    }

    public void buyAdmin(CommandSender sender, UUID target, String text) {
        // Buy the ad
        ShopAd newAd = new ShopAd(target, text);
        plugin.getShopAdData().addShopAd(newAd);

        SBMessages.send(sender, "ad-bought-admin");
    }

    // Utility
    private Optional<ShopAd> getAdFromPlayer(CommandSender sender, UUID target, int adNum) {
        boolean isAdmin = isAdmin(sender, target);

        var currentAds = plugin.getShopAdData().getShopAdsBelongingTo(target)
                .stream()
                .sorted(Comparator.comparingLong(ShopAd::getTimeCreated))
                .toList();

        if (currentAds.isEmpty()) {
            String key = isAdmin ? "player-has-no-ads" : "no-owned-ads";
            SBMessages.send(sender, key);
            return Optional.empty();
        }

        var adjAdIndex = adNum - 1;
        if (adjAdIndex < 0 || adjAdIndex >= currentAds.size()) {
            SBMessages.send(sender, "invalid-ad-index");
            return Optional.empty();
        }

        return Optional.of(currentAds.get(adjAdIndex));
    }

    public void edit(CommandSender sender, UUID target, int adNum, String text) {
        boolean isAdmin = isAdmin(sender, target);

        var optAd = getAdFromPlayer(sender, target, adNum);
        if (optAd.isEmpty())
            return;

        var ad = optAd.get();
        ad.setText(text);
        plugin.getShopAdData().markDirty();

        SBMessages.send(sender, isAdmin ? "ad-edited-other" : "ad-edited");
    }

    public void delete(CommandSender sender, UUID target, int adNum) {
        boolean isAdmin = isAdmin(sender, target);

        var optAd = getAdFromPlayer(sender, target, adNum);
        if (optAd.isEmpty())
            return;

        plugin.getShopAdData().removeShopAd(optAd.get());

        SBMessages.send(sender, isAdmin ? "ad-deleted-other" : "ad-deleted");
    }

    public void set(CommandSender sender, UUID target, int adNum, String type, String data) {
        boolean isAdmin = isAdmin(sender, target);

        var optAd = getAdFromPlayer(sender, target, adNum);
        if (optAd.isEmpty())
            return;

        var shopAd = optAd.get();
        var resetType = type.equalsIgnoreCase("none");

        // Check if valid type
        if (!resetType && !plugin.getHookManager().isValidAdType(type)) {
            var adTypes = String.join(", ", plugin.getHookManager().getAdTypes());
            SBMessages.send(sender, "invalid-ad-type", adTypes);
            return;
        }

        final var adType = type.toLowerCase();

        if (resetType) {
            shopAd.getTypeData().setType(null);
            shopAd.getTypeData().setData(null);
        }
        else {
            shopAd.getTypeData().setType(adType);

            var typeData = plugin.getHookManager().getTypeData(shopAd, data);
            if (typeData == null) {
                // Hooks are responsible for sending type data error messages.
                return;
            }

            shopAd.getTypeData().setData(typeData);
        }
        plugin.getShopAdData().markDirty();

        var msgKey = "ad-set" + (isAdmin ? "-other" : "");
        SBMessages.send(sender, msgKey, adType);
    }

    public void list(CommandSender sender, UUID target) {

        var shopAds = plugin.getShopAdData().getShopAds();
        if (target != null) {
            shopAds = shopAds
                    .stream()
                    .filter(shopAd -> shopAd.getOwnerUUID().equals(target)).toList();

            if (shopAds.isEmpty()) {
                SBMessages.send(sender, "no-ads-target");
                return;
            }
        }

        if (shopAds.isEmpty()) {
            SBMessages.send(sender, "no-ads");
            return;
        }

        var shopAdComps = shopAds.stream()
                .map(shopAd -> {
                    var shopAdTxt = shopAd.getText();
                    var shopAdPlayer = Bukkit.getOfflinePlayer(shopAd.getOwnerUUID()).getName();

                    var itemMsg = SBMessages.of("ad-item");
                    return MiniMessage.get().parse(
                            itemMsg,
                            Template.of("player", shopAdPlayer),
                            Template.of("message", shopAdTxt)
                    );
                })
                .toList();

        var adComp = Component.join(Component.text("\n"), shopAdComps);

        var listHeader = SBMessages.of("ad-list-header");
        var listComp = MiniMessage.get().parse(
                    listHeader,
                    Template.of("ads", adComp)
                );

        sender.sendMessage(listComp);
    }

    public void reload(CommandSender sender) {
        plugin.reload();
        SBMessages.send(sender, "plugin-reload");
    }

}
