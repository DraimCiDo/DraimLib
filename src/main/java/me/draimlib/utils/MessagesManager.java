package me.draimlib.utils;

import me.draimlib.settings.Lang;
import me.draimlib.settings.Settings;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class MessagesManager {
    private final HashMap<UUID, Long> antiSpam = new HashMap<>();
    private Settings settings;
    private Lang lang;
    private JavaPlugin plugin;

    @Deprecated
    public MessagesManager(Settings settings, Lang lang)
    {
        this.settings = settings;
        this.lang = lang;
    }

    public MessagesManager(Settings settings, Lang lang, JavaPlugin plugin)
    {
        this.settings = settings;
        this.lang = lang;
        this.plugin = plugin;
    }

    public HashMap<UUID, Long> getAntiSpam() {
        return antiSpam;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
