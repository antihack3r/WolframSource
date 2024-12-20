/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.fun;

import net.minecraft.client.Minecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Twerk
extends ModuleListener {
    public Twerk() {
        super("Twerk", Module.Category.FUN, "Twerk like Miley Cyrus!!");
    }

    @Override
    public void onDisable2() {
        Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed = false;
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed = !Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed;
    }
}

