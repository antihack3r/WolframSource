/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Ghost
extends ModuleListener {
    boolean bypassdeath;
    Minecraft mc = Minecraft.getMinecraft();

    public Ghost() {
        super("Ghost", Module.Category.PLAYER, "Stay alive after dying");
    }

    @EventTarget
    public void onTick(PlayerUpdateEvent event) {
        if (WMinecraft.getWorld() == null) {
            return;
        }
        if (WMinecraft.getPlayer().getHealth() == 0.0f) {
            WMinecraft.getPlayer().setHealth(20.0f);
            WMinecraft.getPlayer().isDead = false;
            this.bypassdeath = true;
            this.mc.displayGuiScreen(null);
            WMinecraft.getPlayer().setPositionAndUpdate(WMinecraft.getPlayer().posX, WMinecraft.getPlayer().posY, WMinecraft.getPlayer().posZ);
        }
    }

    public void onPacketSend(NetworkManagerPacketSendEvent packet) {
        if (this.bypassdeath && packet.getPacket() instanceof CPacketPlayer) {
            packet.setCancelled(true);
        }
    }

    @Override
    public void onDisable2() {
        EntityPlayerSP player = WMinecraft.getPlayer();
        if (player != null) {
            player.respawnPlayer();
        }
        this.bypassdeath = false;
    }
}

