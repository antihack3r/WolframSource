/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class Flight
extends ModuleListener {
    public Flight() {
        super("Flight", Module.Category.MOVEMENT, "Basic non-bypassed flight");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        EntityPlayerSP player = WMinecraft.getPlayer();
        float speed = this.getSettings().getFloat("flight_speed");
        WMinecraft.getPlayer().onGround = false;
        if (player.fallDistance > 2.0f) {
            WConnection.sendPacket(new CPacketPlayer(true));
        }
        player.jumpMovementFactor = 1.5f * speed;
        if (player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
        }
        if (player.isSneaking()) {
            player.setSneaking(false);
        }
        WMinecraft.getPlayer().motionX = 0.0;
        WMinecraft.getPlayer().motionY = 0.0;
        WMinecraft.getPlayer().motionZ = 0.0;
        if (Minecraft.getMinecraft().currentScreen == null) {
            float ySpeed = speed;
            if (ySpeed >= 2.8f) {
                ySpeed = 2.8f;
            }
            if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
                WMinecraft.getPlayer().motionY = ySpeed;
            }
            if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
                WMinecraft.getPlayer().motionY = -ySpeed;
            }
        }
    }
}

