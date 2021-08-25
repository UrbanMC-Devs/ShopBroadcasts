package net.lithosmc.shopbroadcast.objects.interfaces;

import org.bukkit.entity.Player;

public interface ISBCost {
    /**
     * Check whether the player meets the requirements to
     * purchase a shop advertisement.
     *
     * Hooks are responsible for sending messages if
     * the player does not meet the requirement.
     *
     * @param player Player purchasing the advertisement.
     * @return whether the player can purchase the advertisement.
     */
    boolean canBuyAd(Player player);

    /**
     * Process any transactions when a player
     * buys an advertisement.
     *
     * Hooks are responsible for sending messages
     * relating to transactions.
     *
     * @param player Player buying the advertisement.
     */
    void buyAd(Player player);
}
