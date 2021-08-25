package net.lithosmc.shopbroadcast.hooks;

import net.milkbowl.vault.economy.Economy;
import net.lithosmc.shopbroadcast.ShopBroadcastConfig;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBCost;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBHook;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBReadConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements ISBHook, ISBCost, ISBReadConfig {

    private final Economy econ;
    private double adCost = 0;

    public VaultHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp != null ? rsp.getProvider() : null;
    }

    @Override
    public boolean canBuyAd(Player player) {
        if (adCost <= 0 || econ == null) {
            return true;
        }

        if (!econ.has(player, adCost)){
            SBMessages.send(player, "not-enough-money", String.valueOf(adCost));
            return false;
        }

        return true;
    }

    @Override
    public void buyAd(Player player) {
        if (adCost > 0 && econ != null) {
            econ.withdrawPlayer(player, adCost);
            SBMessages.send(player, "take-money", String.valueOf(adCost));
        }
    }

    @Override
    public void readConfig(ShopBroadcastConfig config) {
        adCost = config.getAdvertisementMoneyCost();
    }
}
