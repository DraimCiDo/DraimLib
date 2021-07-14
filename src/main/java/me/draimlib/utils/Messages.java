package me.draimlib.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Messages {
    public static void sendMessage(MessagesManager mm, Conversable conversable, String string)
    {
        String langString = mm.getLang().getString(string);
        if(langString == null){
            Messages.log(mm.getPlugin(), "Не могу найти перевод для " + string, Level.SEVERE);
            return;
        }
        sendMessageText(mm, conversable, langString);
    }
    public static void sendMessage(MessagesManager mm, Conversable conversable, String string, HashMap<String, String> replace)
    {
        String langString = mm.getLang().getString(string);
        if(langString == null){
            Messages.log(mm.getPlugin(), "Не могу найти перевод для " + string, Level.SEVERE);
            return;
        }        for (Map.Entry<String, String> entry : replace.entrySet()) {
        langString = langString.replace(entry.getKey(), entry.getValue());
    }
        sendMessageText(mm, conversable, langString);
    }
    public static void sendMessageText(MessagesManager mm, Conversable conversable, String text)
    {
        if(conversable instanceof Player)
        {
            Player player = (Player) conversable;
            if(onCooldown(player, mm)) return;
        }
        conversable.sendRawMessage(Formater.formatColor(mm.getSettings().getTag() + text));
    }

    public static boolean onCooldown(Player player, MessagesManager mm)
    {
        if(mm.getAntiSpam().containsKey(player.getUniqueId())) {
            if (mm.getAntiSpam().get(player.getUniqueId()) < System.currentTimeMillis()) {
                mm.getAntiSpam().remove(player.getUniqueId());
                mm.getAntiSpam().put(player.getUniqueId(), System.currentTimeMillis() + 100);
                return false;
            }
        }
        else {
            mm.getAntiSpam().put(player.getUniqueId(), System.currentTimeMillis() + 100);
            return false;
        }
        return true;
    }

    public static void log(JavaPlugin plugin, String text)
    {
        log(plugin, text, Level.INFO);
    }
    public static void log(JavaPlugin plugin, String text, Level level)
    {
        plugin.getLogger().log(level, Formater.formatColor(text));
    }
}
