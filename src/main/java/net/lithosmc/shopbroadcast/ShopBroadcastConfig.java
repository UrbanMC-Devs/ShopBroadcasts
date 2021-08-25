package net.lithosmc.shopbroadcast;

import me.Silverwolfg11.CommentConfig.annotations.Comment;
import me.Silverwolfg11.CommentConfig.annotations.Node;
import me.Silverwolfg11.CommentConfig.annotations.SerializableConfig;
import me.Silverwolfg11.CommentConfig.node.ParentConfigNode;
import me.Silverwolfg11.CommentConfig.serialization.ClassDeserializer;
import me.Silverwolfg11.CommentConfig.serialization.ClassSerializer;
import me.Silverwolfg11.CommentConfig.serialization.NodeSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SerializableConfig
@Comment({"--**-- ShopBroadcast Configuration --**--", "by Silverwolfg11", ""})
public class ShopBroadcastConfig {

    private static transient double CONFIG_VERSION = 1.0;

    @Comment("Prefix before every ShopBroadcast advertisement.")
    private String prefix = "&6[&bShopAd&6]";

    public String getPrefix() {
        return prefix;
    }

    @Comment({"", "How often should the advertisements be displayed?", "(In minutes)"})
    @Node("ad-interval")
    private int messageInterval = 5;

    public int getMessageInterval() {
        return messageInterval;
    }

    @Comment({"toHow many vote points should an ad cost?", "(Requires VotingPlugin)"})
    @Node({"ad-cost", "vote-points"})
    private int advertisementCost = 5;

    public int getAdvertisementCost() {
        return advertisementCost;
    }

    @Comment({"", "How much money should an ad cost?", "(Requires Vault)"})
    @Node({"ad-cost", "money-cost"})
    private double advertisementMoneyCost = 0;

    public double getAdvertisementMoneyCost() {
        return advertisementMoneyCost;
    }

    @Comment({"", "How many advertisements can a player buy?", "Set to 0 to disable and -1 for infinite."})
    @Node("per-player-limit")
    private int adsPerPlayer = 1;

    public int getAdsPerPlayer() {
        return adsPerPlayer;
    }

    @Comment({"", "After how much time should an advertisement expire and be removed?", "(In Minutes)",
            "Default is 24hrs.", "Set to 0 to disable."})
    @Node("ad-length")
    private long adLength = 60 * 24; // Default is 24 hrs.

    public long getAdLength() {
        return adLength;
    }

    @Comment({"", "Config Version! Do not touch!!!!!"})
    @Node("config-version")
    private double configVersion = CONFIG_VERSION;

    public static ShopBroadcastConfig loadConfig(final File parentDirectory, final Logger logger) {
        final File file = new File(parentDirectory, "config.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        ShopBroadcastConfig config;
        if (!file.exists()) {
            try {
                file.createNewFile();
                config = new ShopBroadcastConfig();
                saveConfig(config, file, logger);
                return config;
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "Error creating configuration file!", e);
                return null;
            }
        }
        final ClassDeserializer deserializer = new ClassDeserializer();

        try {
            config = deserializer.deserializeClass(file, ShopBroadcastConfig.class);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Error deserializing configuration!", ex);
            return null;
        }

        if (config.configVersion < 1.0) {
            config.configVersion = 1.0;
            saveConfig(config, file, logger);
            logger.info("Updated config to version 1.0");
        }

        return config;
    }

    private static void saveConfig(final ShopBroadcastConfig config, final File file, final Logger errorLogger) {
        final ParentConfigNode configNode = ClassSerializer.serializeClass(config);
        final NodeSerializer serializer = new NodeSerializer();

        try {
            serializer.serializeToFile(file, configNode);
        }
        catch (IOException e) {
            errorLogger.log(Level.WARNING, "Error saving config file!", e);
        }
    }
}
