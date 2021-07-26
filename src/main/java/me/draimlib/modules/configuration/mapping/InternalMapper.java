package me.draimlib.modules.configuration.mapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.draimlib.misc.Reflection;
import me.draimlib.misc.Reflection.ClassReflection;
import me.draimlib.modules.configuration.AutoSavePolicy;
import me.draimlib.modules.configuration.Configuration;
import java.lang.reflect.Field;
import java.util.*;

public class InternalMapper {
    private final Map<Class<?>, SettingsHolder> holders = Maps.newConcurrentMap();

    public void registerSettingsClass(Class<?> settingsClass, Configuration config, AutoSavePolicy autoSave) {
        SettingsHolder holder = new SettingsHolder(config, autoSave, settingsClass);
        holders.put(settingsClass, holder);
    }

    public void loadSettings(Class<?> byClass, boolean writeDefaults) {
        SettingsHolder holder = holders.get(byClass);
        if(holder == null) {
            throw new IllegalArgumentException("Указанные настройки не зарегистрированы");
        }

        holder.load(writeDefaults);
    }

    public void saveSettings(Class<?> byClass) {
        SettingsHolder holder = holders.get(byClass);
        if(holder == null) {
            throw new IllegalArgumentException("Указанные настройки не зарегистрированы");
        }

        holder.save();
    }

    public void shutdown() {
        for(SettingsHolder holder : holders.values()) {
            if(holder.getAutoSave() != AutoSavePolicy.ON_SHUTDOWN) {
                continue;
            }

            holder.save();
        }
    }

    protected static class SettingsHolder {
        private List<ConfigOption> options = Lists.newArrayList();
        private final Configuration config;
        private final AutoSavePolicy autoSave;

        public SettingsHolder(Configuration config, AutoSavePolicy autoSave, Class<?> settingsClass) {
            this.config = config;
            this.autoSave = autoSave;
            registerOptions(settingsClass);
        }

        public AutoSavePolicy getAutoSave() {
            return autoSave;
        }

        private void registerOptions(Class<?> settingsClass) {
            final String seperator = Character.toString(config.options().pathSeparator());
            for(Field field : settingsClass.getFields()) {
                if(ConfigOption.class.isAssignableFrom(field.getType())) {
                    // Утверждение
                    ConfigOption<?> value = Reflection.getFieldValue(field, null, ConfigOption.class);
                    if(value == null) {
                        continue;
                    }

                    // Регистрация
                    value.setHolder(this);
                    options.add(value);

                    // Заголовок
                    ConfigHeader header = field.getAnnotation(ConfigHeader.class);
                    if(header != null) {
                        String path = header.path().isEmpty() ? value.getPath(seperator) : header.path();
                        config.header(path, header.value());
                    }
                }
            }
        }

        public void load(boolean writeDefaults) {
            config.reload();
            for(ConfigOption<?> option : options) {
                option.loadFromConfig(config);
            }

            if(writeDefaults) {
                // Очистка старго конфига
                for(String key : config.getKeys(false)) {
                    config.set(key, null);
                }

                save(); // Сохранение по умолчанию
            }
        }

        public void save() {
            for(ConfigOption<?> option : options) {
                option.saveToConfig(config);
            }
            config.save();
        }
    }
}

