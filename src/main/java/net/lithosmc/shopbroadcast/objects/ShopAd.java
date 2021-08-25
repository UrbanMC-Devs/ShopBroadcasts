package net.lithosmc.shopbroadcast.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ShopAd implements Cloneable {

    private final UUID owner;
    private String text;
    private AdTypeData data;
    private long timeCreated;

    public ShopAd(UUID playerOwner, String text) {
        this.owner = playerOwner;
        this.text = text;
        this.data = new AdTypeData();
        this.timeCreated = System.currentTimeMillis();
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public Player getOwnerPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public AdTypeData getTypeData() {
        return data;
    }

    public boolean isType(String type) {
        return data.hasType() && data.getType().equals(type);
    }

    @Override
    public ShopAd clone() {
        var newShopAd = new ShopAd(this.owner, this.text);
        newShopAd.timeCreated = this.timeCreated;

        if (this.data != null)
            newShopAd.data = this.data.clone();

        return newShopAd;
    }
}
