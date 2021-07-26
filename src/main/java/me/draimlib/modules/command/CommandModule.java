package me.draimlib.modules.command;

import me.draimlib.Module;
import me.draimlib.DraimLib;

public class CommandModule extends Module<DraimLib> {
    public CommandModule(DraimLib plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    public void registerCommand(Command command, String... aliases) {
        for(String alias : aliases) {
            plugin.getCommand(alias).setExecutor(command);
        }
    }
}
