package me.draimlib.modules.packets;

import com.google.common.collect.Maps;
import me.draimlib.Module;
import me.draimlib.DraimLib;
import me.draimlib.misc.Reflection;
import me.draimlib.misc.Reflection.ClassReflection;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class PacketModule extends Module {
    private Method GET_HANDLE;
    private Field PLAYER_CONNECTION;
    private Method SEND_PACKET;
    private final Map<String, ClassReflection> packetReflectionMap = Maps.newConcurrentMap();

    public PacketModule(DraimLib plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        GET_HANDLE = Reflection.getCBMethod("entity.CraftPlayer", "getHandle");
        PLAYER_CONNECTION = Reflection.getNMSField("EntityPlayer", "playerConnection");
        SEND_PACKET = Reflection.getNMSMethod("PlayerConnection", "sendPacket", Reflection.getNMSClass("Packet"));
    }

    @Override
    public void disable() {
    }

    /**
     * Создание нового пустого пакета по имени.
     *
     */
    public Packet createPacket(String name) {
        ClassReflection reflection = packetReflectionMap.get(name);
        if(reflection == null) {
            reflection = new ClassReflection(Reflection.getNMSClass(name));
            packetReflectionMap.put(name, reflection);
        }

        Object instance = reflection.newInstance();
        return new Packet(reflection, instance);
    }

    /**
     * Отправка пакетов игроку
     *
     */
    public void sendPacket(Player player, Packet packet) {
        Object entityPlayer = Reflection.invokeMethod(GET_HANDLE, player);
        Object playerConnection = Reflection.getFieldValue(PLAYER_CONNECTION, entityPlayer);
        Reflection.invokeMethod(SEND_PACKET, playerConnection, packet.getHandle());
    }

    /**
     * Всеобщий пакет для всех игроков в мире
     *
     */
    public void broadcastPacket(World world, Packet packet) {
        for(Player player : world.getPlayers()) {
            sendPacket(player, packet);
        }
    }
}

