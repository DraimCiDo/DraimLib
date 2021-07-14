package me.draimlib.updater;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final Version version;
    private final Version spigotVersion;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.version = new Version(this.plugin.getDescription().getVersion());
        this.resourceId = resourceId;
        this.spigotVersion = getVersion();
    }

    public Version getVersion() {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
                return new Version(scanner.next());
            }
        } catch (IOException exception) {
            this.plugin.getLogger().info("Не удается найти обновления: " + exception.getMessage());
        }
        return new Version("X");
    }
    public boolean isUpToDate()
    {
        if(spigotVersion != null)
            return spigotVersion.compareTo(this.version)<0;
        return false;
    }
    public boolean isStable()
    {
        return this.version.type.isStable();
    }
}

