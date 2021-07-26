package me.draimlib.modules.configuration.mapping;


import me.draimlib.modules.configuration.AutoSavePolicy;
import me.draimlib.modules.configuration.mapping.InternalMapper.SettingsHolder;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigOption<T> {
    private final String path;
    private T value;
    private SettingsHolder holder;

    public ConfigOption(String path) {
        this(path, null);
    }

    public ConfigOption(String path, T defaultValue) {
        this.path = path;
        this.value = defaultValue;
    }

    protected String getPath(String seperator) {
        return path;
    }

    protected void setHolder(SettingsHolder holder) {
        this.holder = holder;
    }

    protected void loadFromConfig(ConfigurationSection section) {
        if(!section.contains(path)) return;
        this.value = (T) section.get(path);
    }

    protected void saveToConfig(ConfigurationSection section) {
        section.set(path, value);
    }

    public void set(T value) {
        this.value = value;
        if(holder != null && holder.getAutoSave() == AutoSavePolicy.ON_CHANGE) {
            holder.save();
        }
    }

    public T value() {
        return value;
    }
}
