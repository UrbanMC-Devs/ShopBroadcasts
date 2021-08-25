package net.lithosmc.shopbroadcast.hooks;

import com.bencodez.votingplugin.user.UserManager;
import net.lithosmc.shopbroadcast.ShopBroadcastConfig;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBCost;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBHook;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBReadConfig;
import org.bukkit.entity.Player;

public class VotingPluginHook implements ISBHook, ISBCost, ISBReadConfig {
    private int votePointCost = -1;

    @Override
    public void readConfig(ShopBroadcastConfig config) {
        votePointCost = config.getAdvertisementCost();
    }

    @Override
    public boolean canBuyAd(Player player) {
        if (votePointCost < 1)
            return true;

        var votingUser = UserManager.getInstance().getVotingPluginUser(player.getUniqueId());

        if (votingUser.getPoints() < votePointCost) {
            SBMessages.send(player, "vp-not-enough-points", votePointCost);
            return false;
        }

        return true;
    }

    @Override
    public void buyAd(Player player) {
        if (votePointCost < 1)
            return;

        var votingUser = UserManager.getInstance().getVotingPluginUser(player.getUniqueId());
        votingUser.removePoints(votePointCost);
        SBMessages.send(player, "vp-bought-ad", votePointCost);
    }
}
