package me.draimlib.modules.configuration;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Configuration extends YamlConfiguration {
    private List<String> mainHeader = Lists.newArrayList();
    private final Map<String, List<String>> headers = Maps.newConcurrentMap();
    private final File file;
    private boolean loadHeaders;

    public Configuration(File file) {
        this.file = file;
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void mainHeader(String... header) {
        mainHeader = Arrays.asList(header);
    }

    public List<String> mainHeader() {
        return mainHeader;
    }

    public void header(String key, String... header) {
        headers.put(key, Arrays.asList(header));
    }

    public List<String> header(String key) {
        return headers.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(get(key));
    }

    /**
     * Перезагрузка конфига файлов
     */
    public void reload() {
        reload(headers.isEmpty() && mainHeader.isEmpty());
    }

    public void reload(boolean loadHeaders) {
        this.loadHeaders = loadHeaders;
        try {
            load(file);
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Не удалось перезагрузить файлы", e);
        }
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        StringBuilder memoryData = new StringBuilder();

        // Разбор заголовков
        final int indentLength = options().indent();
        final String pathSeparator = Character.toString(options().pathSeparator());
        int currentIndents = 0;
        String key = "";
        List<String> headers = Lists.newArrayList();
        for(String line : contents.split("\n")) {
            if(line.isEmpty()) continue; // Пропуск пустых строк
            int indent = getSuccessiveCharCount(line, ' ');
            String subline = indent > 0 ? line.substring(indent) : line;
            if(subline.startsWith("#")) {
                if(!loadHeaders) {
                    // Не загружаются заголовки
                    continue;
                }
                if(subline.startsWith("#>")) {
                    String txt = subline.startsWith("#> ") ? subline.substring(3) : subline.substring(2);
                    mainHeader.add(txt);
                    continue; // Главный заголовок, обрабатываемый bukkit
                }

                // Добавка заголовока в список
                String txt = subline.startsWith("# ") ? subline.substring(2) : subline.substring(1);
                headers.add(txt);
                continue;
            }

            int indents = indent / indentLength;
            if(indents <= currentIndents) {
                // Удаление последнего раздела ключа
                String[] array = key.split(Pattern.quote(pathSeparator));
                int backspace = currentIndents - indents + 1;
                key = join(array, options().pathSeparator(), 0, array.length - backspace);
            }

            // Добавка нового раздела в ключ
            String separator = key.length() > 0 ? pathSeparator : "";
            String lineKey = line.contains(":") ? line.split(Pattern.quote(":"))[0] : line;
            key += separator + lineKey.substring(indent);

            currentIndents = indents;

            memoryData.append(line).append('\n');
            if(!headers.isEmpty()) {
                this.headers.put(key, headers);
                headers = Lists.newArrayList();
            }
        }

        // Разбор оставшегося текста
        super.loadFromString(memoryData.toString());
    }

    /**
     * Сохранение конфигурации в файл
     */
    public void save() {
        if(headers.isEmpty() && mainHeader.isEmpty()) {
            try {
                super.save(file);
            } catch(IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Не удалось сохранить файл", e);
            }
            return;
        }

        // Кастомка
        final int indentLength = options().indent();
        final String pathSeparator = Character.toString(options().pathSeparator());
        String content = saveToString();
        StringBuilder fileData = new StringBuilder(buildHeader());
        int currentIndents = 0;
        String key = "";
        for(String h : mainHeader) {
            // Добавка основного заголовока в начало файла
            fileData.append("#> ").append(h).append('\n');
        }

        for(String line : content.split("\n")) {
            if(line.isEmpty()) continue; // Пропуск пустых строк
            int indent = getSuccessiveCharCount(line, ' ');
            int indents = indent / indentLength;
            String indentText = indent > 0 ? line.substring(0, indent) : "";
            if(indents <= currentIndents) {
                // Удаление последнего раздела ключа
                String[] array = key.split(Pattern.quote(pathSeparator));
                int backspace = currentIndents - indents + 1;
                key = join(array, options().pathSeparator(), 0, array.length - backspace);
            }

            // Добавка нового раздела в ключ
            String separator = key.length() > 0 ? pathSeparator : "";
            String lineKey = line.contains(":") ? line.split(Pattern.quote(":"))[0] : line;
            key += separator + lineKey.substring(indent);

            currentIndents = indents;

            List<String> header = headers.get(key);
            String headerText = header != null ? addHeaderTags(header, indentText) : "";
            fileData.append(headerText).append(line).append('\n');
        }

        // Запись данных в файл
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(fileData.toString());
            writer.flush();
        } catch(IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Не удалось сохранить файл", e);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(IOException e) {
                }
            }
        }
    }

    private String addHeaderTags(List<String> header, String indent) {
        StringBuilder builder = new StringBuilder();
        for(String line : header) {
            builder.append(indent).append("# ").append(line).append('\n');
        }
        return builder.toString();
    }

    private String join(String[] array, char joinChar, int start, int length) {
        String[] copy = new String[length - start];
        System.arraycopy(array, start, copy, 0, length - start);
        return Joiner.on(joinChar).join(copy);
    }

    private int getSuccessiveCharCount(String text, char key) {
        int count = 0;
        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == key) {
                count += 1;
            } else {
                break;
            }
        }
        return count;
    }
}

