/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PostMotionUpdateEvent;
import net.wolframclient.event.events.PreMotionUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class SilentSneak
extends ModuleListener {
    Minecraft mc = Minecraft.getMinecraft();

    public SilentSneak() {
        super("SilentSneak", Module.Category.MOVEMENT, "Packet Sneak");
    }

    @EventTarget
    public void onPreMotionUpdate(PreMotionUpdateEvent event) {
        WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(), CPacketEntityAction.Action.START_SNEAKING));
        WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(), CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @EventTarget
    public void onPostMotionUpdate(PostMotionUpdateEvent event) {
        WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(), CPacketEntityAction.Action.STOP_SNEAKING));
        WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(), CPacketEntityAction.Action.START_SNEAKING));
        if (Wolfram.getWolfram().moduleManager.isEnabled("Sprint") && (WMinecraft.getPlayer().motionX != 0.0 || WMinecraft.getPlayer().motionZ != 0.0 || WMinecraft.getPlayer().moveStrafing != 0.0f) && !Wolfram.getWolfram().moduleManager.isEnabled("FreeCam")) {
            WMinecraft.getPlayer().setSprinting(true);
        }
    }

    @Override
    public void onDisable2() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        WConnection.sendPacket(new CPacketEntityAction(WMinecraft.getPlayer(), CPacketEntityAction.Action.STOP_SNEAKING));
    }
}

