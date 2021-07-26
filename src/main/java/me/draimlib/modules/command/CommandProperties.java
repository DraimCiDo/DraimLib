package me.draimlib.modules.command;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class CommandProperties {
    private final Map<String, Object> properties;

    public CommandProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Получение описание команды.
     *
     */
    public String getDescription() {
        return get("description", "");
    }

    /**
     * Получение альясов (ИЛЬЯСОВ АХАХАХАХ)
     *
     */
    public List<String> getAliases() {
        Object object = properties.get("aliases");
        if(object == null) return Lists.newArrayList();
        if(object instanceof List) {
            return (List<String>) object;
        } else {
            return Lists.newArrayList((String) object);
        }
    }

    /**
     * Получение прав
     *
     */
    public String getPermission() {
        return get("permission", null);
    }

    /**
     * Получение сообщение об ошибке, если у игрока нет разрешения.
     *
     */
    public String getPermissionMessage() {
        return get("permission-message", "");
    }

    /**
     * Получение примера использования команды.
     *
     */
    public String getUsage() {
        return get("usage", "");
    }

    /**
     * Получение свойства из этой команды.
     *
     */
    public <T> T get(String key, Class<T> type) {
        return type.cast(properties.get(key));
    }

    // internal
    private <T> T get(String key, T def) {
        return properties.containsKey(key) ? (T) properties.get(key) : def;
    }
}

