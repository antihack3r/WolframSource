/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.network.Packet;
import net.wolframclient.compatibility.WMinecraft;

public final class WConnection {
    public static void sendPacket(Packet packet) {
        WMinecraft.getConnection().sendPacket(packet);
    }

    public static void sendPacketBypass(Packet packet) {
        WMinecraft.getConnection().sendPacketBypass(packet);
    }
}

