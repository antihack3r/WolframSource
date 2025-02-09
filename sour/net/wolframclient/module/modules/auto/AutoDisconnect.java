/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.auto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public final class AutoDisconnect
extends ModuleListener {
    public AutoDisconnect() {
        super("AutoDisconnect", Module.Category.AUTO, "Automatically disconnect when you have less than 3 hearts left");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            return;
        }
        if (WMinecraft.getPlayer().getHealth() >= 6.0f) {
            return;
        }
        event.setCancelled(true);
        this.disconnect();
    }

    private void disconnect() {
        WMinecraft.getWorld().sendQuittingDisconnectingPacket();
        Minecraft.getMinecraft().loadWorld(null);
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }
}

