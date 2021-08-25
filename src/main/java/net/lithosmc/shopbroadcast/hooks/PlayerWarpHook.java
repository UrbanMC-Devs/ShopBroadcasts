package net.lithosmc.shopbroadcast.hooks;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.ShopAd;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBHook;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBType;
import org.bukkit.Bukkit;

public class PlayerWarpHook implements ISBHook, ISBType {
    @Override
    public String getType() {
        return "playerwarp";
    }

    // No type data needed
    @Override
    public String setTypeData(ShopAd ad, String dataArg) {
        return "";
    }

    @Override
    public Style styleBroadcast(ShopAd shopAd) {
        var offlinePlayer = Bukkit.getOfflinePlayer(shopAd.getOwnerUUID());
        var playerName = offlinePlayer.getName();
        // Generate command /pwarps go [playerName]
        var clickEvent = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/pwarps go " + playerName);
        // Hover Component
        var hoverComp = SBMessages.getInstance().getComponentFromBundle("playerwarp-hover", playerName);
        var hoverEvent = HoverEvent.showText(hoverComp);

        return Style.style().clickEvent(clickEvent).hoverEvent(hoverEvent).build();
    }
}
