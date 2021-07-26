package me.draimlib;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class DraimLib extends JavaPlugin {
    private static DraimLib instance;

    private static void setInstance(DraimLib instance) {
        DraimLib.instance = instance;
    }

    public static DraimLib getInstance() {
        return instance;
    }

    protected final Registry registry;

    public DraimLib(Class<? extends Module>... modules) {
        setInstance(this);
        this.registry = new Registry(this, getClassLoader());
        registry.registerModules(true, modules);
    }

    @Override
    public void onEnable() {
        registry.enableModules(true);
        enable();
        registry.enableModules(false);
    }

    @Override
    public void onDisable() {
        registry.disableModules(false);
        disable();
        registry.disableModules(true);
    }

    public abstract void enable();

    public abstract void disable();

    public <T extends Module> T getModule(Class<T> type) {
        return registry.getModule(type);
    }
}
