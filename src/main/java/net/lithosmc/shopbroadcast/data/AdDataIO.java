package net.lithosmc.shopbroadcast.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.lithosmc.shopbroadcast.ShopBroadcast;
import net.lithosmc.shopbroadcast.objects.ShopAd;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdDataIO {

    private final File dataFile;
    private final Type shopAdListType = new TypeToken<List<ShopAd>>() {}.getType();

    private final Logger logger;

    public AdDataIO(ShopBroadcast plugin) {
        dataFile = new File(plugin.getDataFolder(), "ads.json");
        this.logger = plugin.getLogger();
    }

    public void save(List<ShopAd> shopAds) {
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter(dataFile);
             JsonWriter jsonWriter = new JsonWriter(writer)) {
            gson.toJson(shopAds, shopAdListType, jsonWriter);
        }
        catch(IOException ex) {
            logger.log(Level.SEVERE, "Error saving shop ads!", ex);
        }
    }

    public List<ShopAd> load() {
        // Check if parent directory exists
        // Create if not
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdir();
        }

        // Check if file exists
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(dataFile)) {
            List<ShopAd> shopAds = gson.fromJson(reader, shopAdListType);

            if (shopAds != null) {
                return shopAds;
            }
        } catch (IOException ignore) {
        }

        return new ArrayList<>();
    }
}
