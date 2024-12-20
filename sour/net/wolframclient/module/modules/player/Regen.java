/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Regen
extends ModuleListener {
    public Regen() {
        super("Regen", Module.Category.PLAYER, "Regenerate faster");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (this.shouldSpam()) {
            this.spamPackets();
        }
    }

    private boolean shouldSpam() {
        return (WMinecraft.getPlayer().isOnLadder() || WMinecraft.getPlayer().isInWater() || WMinecraft.getPlayer().onGround) && WMinecraft.getPlayer().getHealth() < 20.0f;
    }

    private void spamPackets() {
        new Thread(){

            @Override
            public void run() {
                byte i = (byte)Regen.this.getSettings().getInt("regen_speed");
                while (i > 0) {
                    WConnection.sendPacket(new CPacketPlayer());
                    i = (byte)(i - 1);
                }
            }
        }.start();
    }
}

