package net.lithosmc.shopbroadcast.data;

import net.lithosmc.shopbroadcast.objects.ShopAd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdDataSource {

    private List<ShopAd> shopAds = new ArrayList<>();
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    public List<ShopAd> getShopAds() {
        return Collections.unmodifiableList(shopAds);
    }

    public void addShopAd(ShopAd shopAd) {
        shopAds.add(shopAd);
        markDirty();
    }

    public boolean removeShopAd(ShopAd shopAd) {
        var removed = shopAds.remove(shopAd);
        if (removed)
            markDirty();

        return removed;
    }

    public boolean hasShopAds() {
        return !shopAds.isEmpty();
    }

    public Collection<ShopAd> getShopAdsBelongingTo(UUID playerOwner) {
        return shopAds.stream()
                .filter(ad -> ad.getOwnerUUID().equals(playerOwner))
                .toList();
    }

    public void setShopAds(List<ShopAd> ads) {
        this.shopAds = ads;
    }

    // Indicate whether a shopad has been modified
    public void markDirty() {
        dirty.set(true);
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public boolean resetDirty() {
        return dirty.getAndSet(false);
    }

}
