package me.draimlib.settings;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Settings extends Configuration {
    public Settings(JavaPlugin instance) {
        super("config.yml", instance);
    }
    public String getTag()
    {
        return ChatColor.translateAlternateColorCodes('&', getString("tag"));
    }
    public String getLang()
    {
        return getString("lang");
    }
}
