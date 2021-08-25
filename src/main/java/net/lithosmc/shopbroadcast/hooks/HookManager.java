package net.lithosmc.shopbroadcast.hooks;

import net.kyori.adventure.text.format.Style;
import net.lithosmc.shopbroadcast.ShopBroadcastConfig;
import net.lithosmc.shopbroadcast.objects.ShopAd;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBCost;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBHook;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBReadConfig;
import net.lithosmc.shopbroadcast.objects.interfaces.ISBType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HookManager {

    private final List<ISBHook> hooks = new ArrayList<>();

    @SuppressWarnings("Convert2MethodRef")
    public HookManager() {
        // Intentionally use the full lambda to prevent class from loading
        // if plugin is not enabled.
        addHook("Vault", () -> new VaultHook());
        addHook("PlayerWarps", () -> new PlayerWarpHook());
        addHook("VotingPlugin", () -> new VotingPluginHook());
        addHook("Towny", () -> new TownyHook());
    }

    public void addHook(String pluginName, Supplier<ISBHook> hookSupplier) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            var hook = hookSupplier.get();
            hooks.add(hook);
        }
    }

    public void hooksReadConfig(ShopBroadcastConfig config) {
        for (ISBHook hook : hooks) {
            if (hook instanceof ISBReadConfig readHook) {
                readHook.readConfig(config);
            }
        }
    }

    public boolean canBuyAd(final Player player) {
        return hooks.stream()
                .filter(hook -> hook instanceof ISBCost)
                .map(hook -> (ISBCost) hook)
                .allMatch(costHook -> costHook.canBuyAd(player));
    }

    public void buyAd(final Player player) {
        for (ISBHook hook : hooks) {
            if (hook instanceof ISBCost costHook) {
                costHook.buyAd(player);
            }
        }
    }

    private Stream<ISBType> getTypedHooks() {
        return hooks.stream()
                // Convert to ISBType Hooks
                .filter(hook -> hook instanceof ISBType)
                .map(hook -> (ISBType) hook);
    }

    public Style getAdStyle(ShopAd shopad) {
        var data = shopad.getTypeData();

        if (!data.hasType())
            return null;

        return getTypedHooks()
                // Check that the type for the hook matches the shopad type
                .filter(tHook -> tHook.getType().equals(data.getType()))
                // Get the click event
                .map(tHook -> tHook.styleBroadcast(shopad))
                // Return the first non-null click event
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public boolean isValidAdType(String type) {
        var typeStr = type.toLowerCase();
        return getTypedHooks()
                .anyMatch(tHook -> tHook.getType().equals(typeStr));
    }

    public Collection<String> getAdTypes() {
        return getTypedHooks()
                .map(ISBType::getType)
                .toList();
    }

    public String getTypeData(ShopAd shopAd, String dataArg) {
        if (!shopAd.getTypeData().hasType())
            return null;

        var shopType = shopAd.getTypeData().getType();

        return getTypedHooks()
                .filter(tHook -> tHook.getType().equals(shopType))
                .map(tHook -> tHook.setTypeData(shopAd, dataArg))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
