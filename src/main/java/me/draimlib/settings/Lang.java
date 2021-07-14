package me.draimlib.settings;

import me.draimlib.utils.Formater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;

public class Lang extends Configuration{
    public Lang(String lang, JavaPlugin instance) {
        super(lang + ".yml", instance, "lang");
        FileConfiguration defaultConfig = null;
        if(plugin.getResource("lang/" + lang + ".yml") != null)
        {
            defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("lang/" + lang + ".yml")));
        }
        else
        {
            defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("lang/ru_RU.yml")));
        }
        if(defaultConfig != null)
            for(String key: defaultConfig.getKeys(false))
            {
                if(defaultConfig.isConfigurationSection(key))
                    checkSection(defaultConfig.getConfigurationSection(key));
                if(!configuration.contains(defaultConfig.getCurrentPath() + "." +key))
                    configuration.set(defaultConfig.getCurrentPath() + "." + key, defaultConfig.get(key));
            }

    }
    public void checkSection(ConfigurationSection section)
    {
        for(String key: section.getKeys(false))
        {
            if(section.isConfigurationSection(key))
                checkSection(section.getConfigurationSection(key));
            if(!configuration.contains(section.getCurrentPath() + "." +key))
                configuration.set(section.getCurrentPath() + "." + key, section.get(key));
        }
    }

    public String getString(String path)
    {
        return Formater.formatColor(configuration.getString(path));
    }
}
