package me.draimlib.modules.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public abstract class Command implements CommandExecutor {
    private String usage = null;
    private String permission = null;
    private boolean allowConsole = true;
    private int minArgs = 0;

    // Формат
    private String prefix = "";
    private ChatColor successColor = ChatColor.GREEN;
    private ChatColor errorColor = ChatColor.RED;

    // Инфа
    protected CommandSender sender;
    protected Player player;
    protected boolean isPlayer;
    private String[] args;

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        this.isPlayer = sender instanceof Player;
        this.sender = sender;
        this.args = args;
        if(!allowConsole && !isPlayer) {
            reply(false, "Вы должны быть игроком, чтобы выполнить эту команду!");
            return true;
        }

        if(isPlayer) this.player = (Player) sender;
        if(permission != null && !sender.hasPermission(permission)) {
            reply(false, "У вас нет разрешения на выполнение этой команды!");
            return true;
        }

        if(args.length < minArgs) {
            reply(false, "Для этой команды требуется не менее %s аргументов!", minArgs);
            return true;
        }

        try {
            execute();
        } catch(Exception e) {
            reply(false, "При выполнении этой команды произошла ошибка, пожалуйста, свяжитесь с администратором!");
            Bukkit.getLogger().log(Level.SEVERE, "Ошибка при выполнении команды", e);
        }
        return true;
    }

    public abstract void execute();

    protected void reply(String message, Object... args) {
        reply(true, message, args);
    }

    protected void reply(boolean success, Object message, Object... args) {
        reply(sender, success, message, args);
    }

    protected void reply(CommandSender sender, boolean success, Object message, Object... args) {
        String text = prefix + (success ? successColor : errorColor).toString() +
                ChatColor.translateAlternateColorCodes('&', String.format(message.toString(), args));
        sender.sendMessage(text);
    }

    protected String getArg(int index) {
        return args[index];
    }

    protected int getArgAsInt(int index) {
        return Integer.parseInt(getArg(index));
    }

    protected Player getArgAsPlayer(int index) {
        return Bukkit.getPlayer(getArg(index));
    }

    protected int getArgLength() {
        return args.length;
    }

    protected String getUsage() {
        return usage;
    }

    protected void setUsage(String usage) {
        this.usage = usage;
    }

    protected String getPermission() {
        return permission;
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

    protected int getMinArgs() {
        return minArgs;
    }

    protected void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    protected boolean isAllowConsole() {
        return allowConsole;
    }

    protected void setAllowConsole(boolean allowConsole) {
        this.allowConsole = allowConsole;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected void setPrefix(String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    protected ChatColor getSuccessColor() {
        return successColor;
    }

    protected void setSuccessColor(ChatColor successColor) {
        this.successColor = successColor;
    }

    protected ChatColor getErrorColor() {
        return errorColor;
    }

    protected void setErrorColor(ChatColor errorColor) {
        this.errorColor = errorColor;
    }
}

