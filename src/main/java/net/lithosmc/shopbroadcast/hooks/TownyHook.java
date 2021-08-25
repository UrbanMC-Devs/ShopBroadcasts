package net.lithosmc.shopbroadcast.hooks;

import com.palmergames.bukkit.towny.TownyAPI;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.ShopAd;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBHook;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBType;

public class TownyHook implements ISBHook, ISBType {
    @Override
    public String getType() {
        return "town";
    }

    // No type data needed
    @Override
    public String setTypeData(ShopAd shopAd, String dataArg) {
        return "";
    }

    @Override
    public Style styleBroadcast(ShopAd shopAd) {
        // Get the resident
        var resident = TownyAPI.getInstance().getResident(shopAd.getOwnerUUID());

        if (resident == null || !resident.hasTown())
            return null;

        var town = resident.getTownOrNull();
        var townName = town.getName();

        var hoverComp = SBMessages.getInstance().getComponentFromBundle("towny-hover", townName);

        return Style.style()
                .hoverEvent(HoverEvent.showText(hoverComp))
                .clickEvent(ClickEvent.runCommand("/t spawn " + townName))
                .build();
    }
}
