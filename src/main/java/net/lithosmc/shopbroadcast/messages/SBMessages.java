package net.lithosmc.shopbroadcast.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SBMessages {

    private static SBMessages instance;

    private final File FILE;
    private final Logger errorLogger;
    private final String VERSION = "1.0.0";

    private ResourceBundle bundle;

    private SBMessages(File messagesFile, Logger errorLogger) {
        this.FILE = messagesFile;
        this.errorLogger = errorLogger;

        createFile();
        loadBundle();
        checkVersion();
    }

    public static void load(File messagesFile, Logger errorLogger) {
        instance = new SBMessages(messagesFile, errorLogger);
    }

    public static SBMessages getInstance() {
        if (instance != null)
            return instance;

        throw new RuntimeException("Messages instance was accessed before loaded!");
    }

    public static void send(CommandSender sender, String key, Object... args) {
        var comp = instance.getComponentFromBundle(key, args);
        sender.sendMessage(comp);
    }

    public static String of(String key, Object... args) {
        return instance.getStringFromBundle(key, args);
    }

    private void createFile() {
        if (!FILE.getParentFile().isDirectory()) {
            FILE.getParentFile().mkdir();
        }

        if (!FILE.exists()) {
            try {
                FILE.createNewFile();

                InputStream input = getClass().getClassLoader().getResourceAsStream("messages.properties");

                Files.copy(input, FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                errorLogger.log(Level.SEVERE, "Error creating messages file!", e);
            }
        }
    }

    private void loadBundle() {
        try {
            InputStream input = new FileInputStream(FILE);
            Reader reader = new InputStreamReader(input, "UTF-8");

            bundle = new PropertyResourceBundle(reader);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Component getComponentFromBundle(String key, Object... args) {
        String rawString = instance.getStringFromBundle(key, args);
        // Parse to MiniMessage
        return MiniMessage.get().parse(rawString);
    }

    private String getStringFromBundle(String key, Object... args) {
        try {
            return format(bundle.getString(key), args);
        } catch (Exception e) {
            if (e instanceof MissingResourceException) {
                errorLogger.severe("Missing message for message key '" + key + "' in messages.properties!");
            }
            else {
                errorLogger.log(Level.SEVERE, "Error fetching key '" + key + "' in messages.properties!", e);
            }
            return key;
        }
    }

    private String format(String message, Object... args) {
        message = message.replace("{prefix}", bundle.getString("prefix"));

        if (args != null) {
            message = MessageFormat.format(message, args);
        }

        return message;
    }

    public void reload() {
        createFile();
        loadBundle();
        checkVersion();
    }

    private void checkVersion() {
        String versionStr = null;

        try {
            versionStr = bundle.getString("version");
        } catch (MissingResourceException ignore) {
        }

        if (!VERSION.equals(versionStr))
            errorLogger.severe("The messages.properties file is out of date!" +
                    " Please update the file with the new messages and change the 'version' value to '" + versionStr + "'!");
    }
}
