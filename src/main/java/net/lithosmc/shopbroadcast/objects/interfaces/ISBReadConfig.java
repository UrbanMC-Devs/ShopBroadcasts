package net.lithosmc.shopbroadcast.objects.interfaces;

import net.lithosmc.shopbroadcast.ShopBroadcastConfig;

/**
 * A hook interface that is used
 * when a hook needs access to the
 * shopbroadcast config.
 *
 * Registered hooks simply have to extend
 * this interface to indicate that subscribe
 * to reading the config.
 */
public interface ISBReadConfig {
    /**
     * Read the shopbroadcast config when
     * it is loaded or reloaded.
     *
     *
     * @param config ShopBroadcastConfig to read.
     */
    void readConfig(ShopBroadcastConfig config);
}
