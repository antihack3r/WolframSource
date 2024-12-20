/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

public final class WMinecraft {
    public static final String VERSION = "1.12";
    public static final boolean REALMS = "".isEmpty();
    public static final boolean COOLDOWN = "".isEmpty();
    public static final NavigableMap<Integer, String> PROTOCOLS;
    private static final Minecraft mc;

    static {
        TreeMap<Integer, String> protocols = new TreeMap<Integer, String>();
        protocols.put(335, VERSION);
        PROTOCOLS = Collections.unmodifiableNavigableMap(protocols);
        mc = Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer() {
        return WMinecraft.mc.player;
    }

    public static WorldClient getWorld() {
        return WMinecraft.mc.world;
    }

    public static NetHandlerPlayClient getConnection() {
        return WMinecraft.getPlayer().connection;
    }

    public static GuiNewChat getChatGui() {
        return WMinecraft.mc.ingameGUI.getChatGUI();
    }
}

