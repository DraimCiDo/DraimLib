package me.draimlib.modules.configuration;

import com.google.common.collect.Lists;
import me.draimlib.misc.Reflection;
import me.draimlib.modules.configuration.mapping.ConfigHeader;
import me.draimlib.modules.configuration.mapping.ConfigKey;
import me.draimlib.modules.configuration.mapping.ConfigMapper;
import java.lang.reflect.Field;
import java.util.List;

public class AbstractConfig {
    private final List<Field> dataFields = Lists.newArrayList();
    private final ConfigMapper mapper;
    private final Configuration config;
    private boolean clearOnSave = false;

    protected AbstractConfig(ConfigurationModule module) {
        this.mapper = getClass().getAnnotation(ConfigMapper.class);
        this.config = module.getConfiguration(mapper.fileName());
        for(Field field : getClass().getDeclaredFields()) {
            ConfigKey key = field.getAnnotation(ConfigKey.class);
            if(key == null) {
                continue;
            }

            // Headers
            String keyPath = key.path().isEmpty() ? toConfigString(field.getName()) : key.path();
            ConfigHeader header = field.getAnnotation(ConfigHeader.class);
            if(header != null) {
                String path = header.path().isEmpty() ? keyPath : header.path();
                config.header(path, header.value());
            }

            field.setAccessible(true);
            dataFields.add(field);
        }
    }

    protected boolean isClearOnSave() {
        return clearOnSave;
    }

    protected void setClearOnSave(boolean flag) {
        this.clearOnSave = flag;
    }

    public void reload() {
        config.reload();
        if(mapper.header().length > 0) {
            config.mainHeader(mapper.header());
        }

        // Загрузка значения
        for(Field field : dataFields) {
            ConfigKey key = field.getAnnotation(ConfigKey.class);
            String path = key.path().isEmpty() ? toConfigString(field.getName()) : key.path();
            if(!config.contains(path)) {
                continue;
            }

            Reflection.setFieldValue(field, this, config.get(path));
        }
    }

    public void save() {
        // Установка значения
        for(Field field : dataFields) {
            ConfigKey key = field.getAnnotation(ConfigKey.class);
            String path = key.path().isEmpty() ? toConfigString(field.getName()) : key.path();
            config.set(path, Reflection.getFieldValue(field, this));
        }

        config.save();
    }

    private String toConfigString(String value) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if(Character.isUpperCase(c)) {
                builder.append('-').append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}

