/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.hooks;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import net.minecraft.client.multiplayer.ServerData;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;

public class ServerHook {
    private static final List<String> BROKEN_SERVER_SYSTEMS = Arrays.asList("bungeecord", "waterfall", "minebench");

    public static int getProtocolVersion() {
        if (Wolfram.getWolfram().getClientSettings().getInt("mc112x_compatibility") == 2) {
            return 340;
        }
        if (Wolfram.getWolfram().getClientSettings().getInt("mc112x_compatibility") == 1) {
            return 338;
        }
        ServerData server = Wolfram.getWolfram().lastServer;
        NavigableMap<Integer, String> protocols = WMinecraft.PROTOCOLS;
        if (!server.pinged || server.pingToServer < 0L) {
            return (Integer)protocols.lastKey();
        }
        if (!protocols.containsKey(server.version)) {
            return (Integer)protocols.lastKey();
        }
        if (BROKEN_SERVER_SYSTEMS.contains(server.gameVersion.split(" ", 2)[0].toLowerCase())) {
            return (Integer)protocols.lastKey();
        }
        return server.version;
    }
}

